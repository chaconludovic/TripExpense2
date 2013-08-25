package com.eldoraludo.tripexpense.dto;

import com.eldoraludo.tripexpense.entite.Participant;

public class SyntheseDTO {

	private Participant participant;
	private Participant depenseur;
	private Double montant;

	public Participant getParticipant() {
		return participant;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	public Participant getDepenseur() {
		return depenseur;
	}

	public void setDepenseur(Participant depenseur) {
		this.depenseur = depenseur;
	}

	public Double getMontant() {
		return montant;
	}

	public void setMontant(Double montant) {
		this.montant = montant;
	}

}
