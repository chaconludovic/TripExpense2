package com.eldoraludo.tripexpense.entite;

import com.google.common.base.Preconditions;

import org.joda.time.DateTime;

public class Emprunt extends BaseEntity implements Comparable<Emprunt> {
    private Double montant;
    private String nomEmprunt;
    private DateTime dateEmprunt;
    private Integer emprunteurId;
    private Integer participantId;
    private Integer projetId;

    private Emprunt() {

    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        montant = montant;
    }

    public String getNomEmprunt() {
        return nomEmprunt;
    }

    public void setNomEmprunt(String nomEmprunt) {
        this.nomEmprunt = nomEmprunt;
    }

    public Integer getEmprunteurId() {
        return emprunteurId;
    }

    public void setEmprunteurId(Integer emprunteurId) {
        this.emprunteurId = emprunteurId;
    }

    public Integer getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Integer participantId) {
        this.participantId = participantId;
    }

    public Integer getProjetId() {
        return projetId;
    }

    public void setProjetId(Integer projetId) {
        this.projetId = projetId;
    }


    public DateTime getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(DateTime dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public int compareTo(Emprunt emprunt) {
        return this.dateEmprunt.compareTo(emprunt.getDateEmprunt());
    }

    public static class Builder {

        private Emprunt empruntPartiel;

        public Builder() {
            empruntPartiel = new Emprunt();
        }

        public Builder withId(Integer id) {
            this.empruntPartiel.id = id;
            return this;
        }

        public Builder withMontant(Double montant) {
            this.empruntPartiel.montant = montant;
            return this;
        }


        public Builder withNomEmprunt(String nom) {
            this.empruntPartiel.nomEmprunt = nom;
            return this;
        }

        public Builder withDateEmprunt(DateTime dateEmprunt) {
            this.empruntPartiel.dateEmprunt = dateEmprunt;
            return this;
        }

        public Builder withEmprunteurId(Integer emprunteurId) {
            this.empruntPartiel.emprunteurId = emprunteurId;
            return this;
        }

        public Builder withParticipantId(Integer participantId) {
            this.empruntPartiel.participantId = participantId;
            return this;
        }

        public Builder withProjetId(Integer projetId) {
            this.empruntPartiel.projetId = projetId;
            return this;
        }

        public Emprunt build() {
            checkValues();
            return this.empruntPartiel;
        }

        private void checkValues() {
            Preconditions.checkNotNull(empruntPartiel.getMontant());
            Preconditions.checkNotNull(empruntPartiel.getNomEmprunt());
            Preconditions.checkNotNull(empruntPartiel.getDateEmprunt());
            Preconditions.checkNotNull(empruntPartiel.getEmprunteurId());
            Preconditions.checkNotNull(empruntPartiel.getParticipantId());
            Preconditions.checkNotNull(empruntPartiel.getProjetId());
        }

    }

    @Override
    public String toString() {
        return "Depense [montant=" + montant + ", nomEmprunt" + nomEmprunt
                + ", emprunteur=" + emprunteurId + ", participant=" + participantId + "]";
    }

}
