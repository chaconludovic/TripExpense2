package com.eldoraludo.tripexpense.entite;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

public class Depense extends BaseEntity {
	private Double montant;
	private String nomDepense;
	private DateTime dateDebut;
	private DateTime dateFin;
	private TypeDeDepense typeDeDepense;
	private Integer participantId;
	private Integer projetId;

	private Depense() {

	}

	public Double getMontant() {
		return montant;
	}

	public String getNomDepense() {
		return nomDepense;
	}

	public TypeDeDepense getTypeDeDepense() {
		return typeDeDepense;
	}

	public Integer getParticipantId() {
		return participantId;
	}

	public Object getProjetId() {
		return projetId;
	}

	public DateTime getDateDebut() {
		return dateDebut;
	}

	public DateTime getDateFin() {
		return dateFin;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private Depense depensePartiel;

		public Builder() {
			depensePartiel = new Depense();
		}

		public Builder withId(Integer id) {
			this.depensePartiel.id = id;
			return this;
		}

		public Builder withMontant(Double montant) {
			this.depensePartiel.montant = montant;
			return this;
		}

		public Builder withTypeDeDepense(TypeDeDepense typeDeDepense) {
			this.depensePartiel.typeDeDepense = typeDeDepense;
			return this;
		}

		public Builder withNomDepense(String nom) {
			this.depensePartiel.nomDepense = nom;
			return this;
		}

		public Builder withDateDebut(DateTime dateDebut) {
			this.depensePartiel.dateDebut = dateDebut;
			return this;
		}

		public Builder withDateFin(DateTime dateFin) {
			this.depensePartiel.dateFin = dateFin;
			return this;
		}

		public Builder withParticipantId(Integer participantId) {
			this.depensePartiel.participantId = participantId;
			return this;
		}

		public Builder withProjetId(Integer projetId) {
			this.depensePartiel.projetId = projetId;
			return this;
		}

		public Depense build() {
			checkValues();
			return this.depensePartiel;
		}

		private void checkValues() {
			Preconditions.checkNotNull(depensePartiel.getMontant());
			Preconditions.checkNotNull(depensePartiel.getNomDepense());
			Preconditions.checkNotNull(depensePartiel.getTypeDeDepense());
			Preconditions.checkNotNull(depensePartiel.getParticipantId());
			Preconditions.checkNotNull(depensePartiel.getProjetId());
		}

	}

	@Override
	public String toString() {
		return "Depense [montant=" + montant + ", nomDepense=" + nomDepense
				+ ", dateDebut=" + dateDebut + ", dateFin=" + dateFin
				+ ", typeDeDepense=" + typeDeDepense + "]";
	}

}
