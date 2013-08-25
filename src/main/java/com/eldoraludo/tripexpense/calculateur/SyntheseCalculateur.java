package com.eldoraludo.tripexpense.calculateur;

import com.eldoraludo.tripexpense.dto.SyntheseDTO;
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Emprunt;
import com.eldoraludo.tripexpense.entite.Participant;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SyntheseCalculateur implements Calculateur<List<SyntheseDTO>> {

    private List<Depense> depenses;
    private List<Emprunt> emprunts;
    private List<Participant> participants;

    public SyntheseCalculateur(List<Emprunt> emprunts, List<Depense> depenses,
                               List<Participant> participants) {
        this.depenses = depenses;
        this.emprunts = emprunts;
        this.participants = participants;
    }

    @Override
    public List<SyntheseDTO> getResultat() {
        Map<DateTime, Set<Participant>> dateParticipantMap = this
                .buildDateParticipantMap(participants);

        Map<Depense, Map<Participant, Double>> depenseParticipantsMap = extractionParticipantParDepense(
                depenses, participants, dateParticipantMap);

        Map<Participant, Map<Participant, Double>> dette = extractionDetteParParticipant(
                depenseParticipantsMap, participants);

        Map<Participant, Map<Participant, Double>> detteEtEmprunt = gestionEmprunt(dette, emprunts, participants);

        Map<Participant, Map<Participant, Double>> detteRectifie = nettoyageDesRelationsSymetriques(detteEtEmprunt);

        Map<Participant, Map<Participant, Double>> detteSimplifie = extractionDetteSimplifierParParticipant(detteRectifie);

        return getSyntheseDTO(detteSimplifie);
    }

    public Map<Participant, Map<Participant, Double>> gestionEmprunt(Map<Participant, Map<Participant, Double>> donnees, List<Emprunt> emprunts, List<Participant> participants) {
        if (emprunts == null) {
            return donnees;
        }
        if (donnees == null) {
            donnees = new HashMap<Participant, Map<Participant, Double>>();
        }
        for (Emprunt emprunt : emprunts) {
            Participant emprunteur = trouverLeParticipant(participants, emprunt.getEmprunteurId());
            Preconditions.checkNotNull(emprunteur, "L'emprunteur n'a pas été trouvé dans la liste");
            Participant participant = trouverLeParticipant(participants, emprunt.getParticipantId());
            Preconditions.checkNotNull(participant, "L'emprunteur n'a pas été trouvé dans la liste");
            if (donnees.containsKey(emprunteur)) {
                Double montantDOrigine = 0.0;
                if (donnees.get(emprunteur).containsKey(participant)) {
                    montantDOrigine = donnees.get(emprunteur).get(participant);
                }
                donnees.get(emprunteur).put(participant, montantDOrigine + emprunt.getMontant());
            } else {
                HashMap<Participant, Double> participantEmprunt = new HashMap<Participant, Double>();
                participantEmprunt.put(participant, emprunt.getMontant());
                donnees.put(emprunteur, participantEmprunt);
            }
        }
        return donnees;
    }

    public Map<Participant, Map<Participant, Double>> extractionDetteSimplifierParParticipant(
            Map<Participant, Map<Participant, Double>> dette) {
        Map<Participant, Map<Participant, Double>> detteSimplifier = copieDeLaMap(dette);
        for (int i = 0; i < 4; i++) {
            List<Participant> dettes = new LinkedList<Participant>(detteSimplifier.keySet());
            Collections.shuffle(dettes);
            for (Participant participant : dettes) {
                if (!detteSimplifier.containsKey(participant)) {
                    continue;
                }
                Set<Participant> depenseurs = Sets.newHashSet(detteSimplifier
                        .get(participant).keySet());
                for (Participant depenseur : depenseurs) {
                    if (!detteSimplifier.containsKey(depenseur)) {
                        continue;
                    }
                    if (!detteSimplifier.get(participant)
                            .containsKey(depenseur)) {
                        continue;
                    }
                    Set<Participant> intersection = Sets.newHashSet(Sets
                            .intersection(detteSimplifier.get(depenseur)
                                    .keySet(), depenseurs));
                    if (intersection.size() != 0) {
                        for (Participant participantEnCommun : intersection) {
                            if (!detteSimplifier.get(participant).containsKey(
                                    depenseur)) {
                                continue;
                            }
                            if (!detteSimplifier.get(depenseur).containsKey(
                                    participantEnCommun)) {
                                continue;
                            }
                            if (!detteSimplifier.get(participant).containsKey(
                                    participantEnCommun)) {
                                continue;
                            }
                            Double detteDepenseurAuParticipantEnCommun = detteSimplifier
                                    .get(depenseur).get(participantEnCommun);
                            Double detteParticipantAuDepenseur = detteSimplifier
                                    .get(participant).get(depenseur);
                            if (detteParticipantAuDepenseur < detteDepenseurAuParticipantEnCommun) {
                                this.retirerDepenseurAuParticipant(
                                        detteSimplifier, participant, depenseur);

                                this.ajouterDette(detteSimplifier, participant,
                                        participantEnCommun,
                                        detteParticipantAuDepenseur);
                                Double detteRestante = this.retirerDette(
                                        detteSimplifier, depenseur,
                                        participantEnCommun,
                                        detteParticipantAuDepenseur);
                                if (0 <= detteRestante && detteRestante < 1) {
                                    this.retirerDepenseurAuParticipant(
                                            detteSimplifier, depenseur,
                                            participantEnCommun);
                                }
                            } else {
                                this.ajouterDette(detteSimplifier, participant,
                                        participantEnCommun,
                                        detteDepenseurAuParticipantEnCommun);
                                this.retirerDette(
                                        detteSimplifier,
                                        participant,
                                        depenseur,
                                        detteDepenseurAuParticipantEnCommun);
                                this.retirerDepenseurAuParticipant(
                                        detteSimplifier, depenseur,
                                        participantEnCommun);
                            }

                        }
                    }
                }
            }
        }
        return detteSimplifier;
    }

    private void retirerDepenseurAuParticipant(
            Map<Participant, Map<Participant, Double>> dette,
            Participant participant, Participant depenseur) {
        Preconditions.checkNotNull(dette, "La map de dette ne peut etre null");
        Preconditions.checkNotNull(participant,
                "Le participant ne peut etre null");
        Preconditions.checkNotNull(depenseur, "Le dépenseur ne peut etre null");
        dette.get(participant).remove(depenseur);
    }

    private Double retirerDette(
            Map<Participant, Map<Participant, Double>> dette,
            Participant participant, Participant depenseur,
            Double montantARetirer) {
        Preconditions.checkNotNull(dette, "La map de dette ne peut etre null");
        Preconditions.checkNotNull(participant,
                "Le participant ne peut etre null");
        Preconditions.checkNotNull(depenseur, "Le dépenseur ne peut etre null");
        Preconditions.checkNotNull(montantARetirer,
                "Le montant ne peut etre null");
        dette.get(participant).put(depenseur,
                dette.get(participant).get(depenseur) - montantARetirer);
        return dette.get(participant).get(depenseur);
    }

    private void ajouterDette(Map<Participant, Map<Participant, Double>> dette,
                              Participant participant, Participant depenseur,
                              Double montantAAjouter) {
        Preconditions.checkNotNull(dette, "La map de dette ne peut etre null");
        Preconditions.checkNotNull(participant,
                "Le participant ne peut etre null");
        Preconditions.checkNotNull(depenseur, "Le dépenseur ne peut etre null");
        Preconditions.checkNotNull(montantAAjouter,
                "Le montant ne peut etre null");
        dette.get(participant).put(depenseur,
                dette.get(participant).get(depenseur) + montantAAjouter);
    }

    private List<SyntheseDTO> getSyntheseDTO(
            Map<Participant, Map<Participant, Double>> dette) {
        List<SyntheseDTO> res = new ArrayList<SyntheseDTO>();
        for (Participant participant : dette.keySet()) {
            for (Participant depenseur : dette.get(participant).keySet()) {
                Double montant = dette.get(participant).get(depenseur);
                if (0.0 <= montant.doubleValue() && montant.doubleValue() < 1) {
                    continue;
                }
                montant = Math.round(montant * 100.0) / 100.0;
                // String ress = participant.getNom() + " doit à "
                // + depenseur.getNom() + " la somme de " + montant
                // + " euros";
                SyntheseDTO ress = new SyntheseDTO();
                ress.setParticipant(participant);
                ress.setDepenseur(depenseur);
                ress.setMontant(montant);
                res.add(ress);
            }

        }
        return res;
    }

    public Map<Participant, Map<Participant, Double>> extractionDetteParParticipant(
            Map<Depense, Map<Participant, Double>> depenseParticipantsMap,
            List<Participant> participants) {
        Map<Participant, Map<Participant, Double>> dette = new HashMap<Participant, Map<Participant, Double>>();
        for (Depense depense : depenseParticipantsMap.keySet()) {
            Participant depenseur = this.trouverLeParticipant(participants,
                    depense.getParticipantId());
            Map<Participant, Double> map = depenseParticipantsMap.get(depense);
            for (Participant participant : map.keySet()) {
                if (!dette.containsKey(participant)) {
                    dette.put(participant, new HashMap<Participant, Double>());
                }
                if (!dette.get(participant).containsKey(depenseur)) {
                    dette.get(participant).put(depenseur, 0.0);
                }
                dette.get(participant).put(
                        depenseur,
                        dette.get(participant).get(depenseur)
                                + map.get(participant));
            }
        }
        return dette;
    }

    public Map<Participant, Map<Participant, Double>> nettoyageDesRelationsSymetriques(
            Map<Participant, Map<Participant, Double>> dette) {
        // nettoyage
        Map<Participant, Map<Participant, Double>> detteFinal = copieDeLaMap(dette);
        for (Participant participant : dette.keySet()) {
            if (!detteFinal.containsKey(participant)) {
                continue;
            }
            for (Participant depenseur : dette.get(participant).keySet()) {
                if (!detteFinal.containsKey(depenseur)
                        || !detteFinal.containsKey(participant)) {
                    continue;
                }
                if (detteFinal.get(depenseur).containsKey(participant)
                        && detteFinal.get(participant).containsKey(depenseur)) {
                    Double montantDuParLeDepenseurAuParticipant = detteFinal
                            .get(depenseur).get(participant);
                    if (this.estDetteDuDepenseSuperieurADetteDuParticipant(
                            detteFinal, participant, depenseur,
                            montantDuParLeDepenseurAuParticipant)) {
                        this.recalculDeLaDetteDuDepenseur(detteFinal,
                                detteFinal, participant, depenseur,
                                montantDuParLeDepenseurAuParticipant);
                    } else {
                        this.recalculDeLaDetteDuParticipant(detteFinal,
                                detteFinal, participant, depenseur,
                                montantDuParLeDepenseurAuParticipant);
                    }
                }
            }
        }
        return detteFinal;
    }

    private Participant trouverLeParticipant(List<Participant> participants,
                                             Integer participantId) {
        for (Participant participant : participants) {
            if (participant.getId().equals(participantId)) {
                return participant;
            }
        }
        return null;
    }

    private Map<Participant, Map<Participant, Double>> copieDeLaMap(
            Map<Participant, Map<Participant, Double>> dette) {
        Map<Participant, Map<Participant, Double>> detteFinal = new HashMap<Participant, Map<Participant, Double>>();
        for (Participant participant : dette.keySet()) {
            Map<Participant, Double> mapDepenseur = new HashMap<Participant, Double>();
            for (Participant depenseur : dette.get(participant).keySet()) {
                mapDepenseur.put(depenseur,
                        dette.get(participant).get(depenseur));
            }
            detteFinal.put(participant, mapDepenseur);
        }
        return detteFinal;
    }

    private void recalculDeLaDetteDuParticipant(
            Map<Participant, Map<Participant, Double>> dette,
            Map<Participant, Map<Participant, Double>> detteFinal,
            Participant participant, Participant depenseur,
            Double detteDuDepenseur) {
        Double detteDuParticipant = dette.get(participant).get(depenseur);
        Double detteRecalcule = detteDuParticipant - detteDuDepenseur;
        if (detteRecalcule == 0) {
            retirerDepenseurAuParticipant(detteFinal, participant, depenseur);
        } else {
            detteFinal.get(participant).put(depenseur, detteRecalcule);
        }
        retirerDepenseurAuParticipant(detteFinal, depenseur, participant);
        if (detteFinal.get(depenseur).size() == 0) {
            detteFinal.remove(depenseur);
        }
    }

    private void recalculDeLaDetteDuDepenseur(
            Map<Participant, Map<Participant, Double>> dette,
            Map<Participant, Map<Participant, Double>> detteFinal,
            Participant participant, Participant depenseur,
            Double detteDuDepenseur) {
        Double detteDuParticipant = dette.get(participant).get(depenseur);
        Double detteRecalcule = detteDuDepenseur - detteDuParticipant;
        if (detteRecalcule == 0) {
            retirerDepenseurAuParticipant(detteFinal, depenseur, participant);
        } else {
            detteFinal.get(depenseur).put(participant, detteRecalcule);
        }
        retirerDepenseurAuParticipant(detteFinal, participant, depenseur);
        if (detteFinal.get(participant).size() == 0) {
            detteFinal.remove(participant);
        }
    }

    private boolean estDetteDuDepenseSuperieurADetteDuParticipant(
            Map<Participant, Map<Participant, Double>> dette,
            Participant participant, Participant depenseur,
            Double montantDuParLeDepenseurAuParticipant) {
        return montantDuParLeDepenseurAuParticipant > dette.get(participant)
                .get(depenseur);
    }

    public Map<Depense, Map<Participant, Double>> extractionParticipantParDepense(
            List<Depense> depenses, List<Participant> participants,
            Map<DateTime, Set<Participant>> dateParticipantMap) {
        Map<Depense, Map<Participant, Double>> depenseParticipantsMap = new HashMap<Depense, Map<Participant, Double>>();
        Collections.shuffle(depenses);
        for (Depense depense : depenses) {
            DateTime dateDebut = depense.getDateDebut();
            DateTime dateFin = depense.getDateFin();
            DateTime dateCourante = dateDebut;
            int nombreDeParticipantPourLaDepense = 0;
            while (!dateCourante.isAfter(dateFin)) {
                Set<Participant> participantsDuJour = dateParticipantMap
                        .get(dateCourante);
                if (participantsDuJour != null) {
                    nombreDeParticipantPourLaDepense += participantsDuJour
                            .size();
                }
                dateCourante = dateCourante.plusDays(1);
            }
            Preconditions.checkState(nombreDeParticipantPourLaDepense != 0,
                    "le Nombre de participant entre la date de début de dépense "
                            + dateDebut.toString() + " et la date de fin de dépense "
                            + dateFin.toString() + " est egual à zéro");
            Map<Participant, Double> mapPart = new HashMap<Participant, Double>();
            Double montantParPart = (double) Math.round(depense.getMontant()
                    .longValue()
                    / Double.valueOf(nombreDeParticipantPourLaDepense)
                    .longValue());
            Collections.shuffle(participants);
            for (Participant participant : participants) {
                if (participant.getId().equals(depense.getParticipantId())) {
                    continue;
                }
                dateCourante = dateDebut;
                while (!dateCourante.isAfter(dateFin)) {
                    Set<Participant> participantsDuJour = dateParticipantMap
                            .get(dateCourante);
                    if (participantsDuJour != null
                            && participantsDuJour.contains(participant)) {
                        if (!mapPart.containsKey(participant)) {
                            mapPart.put(participant, 0.0);
                        }
                        mapPart.put(participant, mapPart.get(participant)
                                + montantParPart);
                    }
                    dateCourante = dateCourante.plusDays(1);
                }
            }
            depenseParticipantsMap.put(depense, mapPart);
        }
        return depenseParticipantsMap;
    }

    public Map<DateTime, Set<Participant>> buildDateParticipantMap(
            List<Participant> participants) {
        Map<DateTime, Set<Participant>> map = new TreeMap<DateTime, Set<Participant>>();
        DateTime dateCourante = null;
        Collections.shuffle(participants);
        for (Participant participant : participants) {
            dateCourante = participant.getDateArrive();
            while (!dateCourante.isAfter(participant.getDateDepart())) {
                if (map.isEmpty()) {
                    Set<Participant> list = new HashSet<Participant>();
                    list.add(participant);
                    map.put(dateCourante, list);
                } else {
                    if (!map.containsKey(dateCourante)) {
                        Set<Participant> list = new HashSet<Participant>();
                        map.put(dateCourante, list);
                    }
                    map.get(dateCourante).add(participant);
                }
                dateCourante = dateCourante.plusDays(1);
            }
        }
        return map;
    }

}
