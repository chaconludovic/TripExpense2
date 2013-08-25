package com.eldoraludo.tripexpense.entite;

import com.google.common.base.Preconditions;

public class Projet extends BaseEntity {
	private String nom;
	private Boolean estCourant;

	private Projet() {

	}

	public String getNom() {
		return nom;
	}

	public Boolean estCourant() {
		return estCourant;
	}

	public void definirEnTantQueCourant() {
		this.estCourant = true;
	}

	public void annuleEtatCourant() {
		this.estCourant = false;
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private Projet projetPartiel;

		public Builder() {
			projetPartiel = new Projet();
		}

		public Builder withId(Integer id) {
			this.projetPartiel.id = id;
			return this;
		}

		public Builder withNom(String nom) {
			this.projetPartiel.nom = nom;
			return this;
		}

		public Builder withEstProjetCourant(Boolean estProjetCourant) {
			this.projetPartiel.estCourant = estProjetCourant;
			return this;
		}

		public Projet build() {
			checkValues();
			return this.projetPartiel;
		}

		private void checkValues() {
			Preconditions.checkNotNull(projetPartiel.getNom());
		}
	}

	@Override
	public String toString() {
		return nom;
	}



}
