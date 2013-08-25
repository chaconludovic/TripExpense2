package com.eldoraludo.tripexpense.entite;

import java.util.Date;

import org.joda.time.DateTime;

import com.eldoraludo.tripexpense.util.DateHelper;
import com.google.common.base.Preconditions;

public class Participant extends BaseEntity {
    private String nom;
    private Integer projetId;
    private String contactPhoneId;
    private DateTime dateArrive;
    private DateTime dateDepart;

    private Participant() {
    }

    public String getNom() {
        return nom;
    }

    public Integer getProjetId() {
        return projetId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public DateTime getDateArrive() {
        return dateArrive;
    }

    public DateTime getDateDepart() {
        return dateDepart;
    }

    public String getContactPhoneId() {
        return contactPhoneId;
    }

    public static class Builder {

        private Participant participantPartiel;

        public Builder() {
            participantPartiel = new Participant();
        }

        public Builder withId(Integer id) {
            this.participantPartiel.id = id;
            return this;
        }

        public Builder withNom(String nom) {
            this.participantPartiel.nom = nom;
            return this;
        }

        public Builder withContactPhoneId(String contactPhoneId) {
            this.participantPartiel.contactPhoneId = contactPhoneId;
            return this;
        }

        public Builder withProjetId(Integer projetId) {
            this.participantPartiel.projetId = projetId;
            return this;
        }

        public Builder withDateArrive(DateTime dateArrive) {
            this.participantPartiel.dateArrive = dateArrive;
            return this;
        }

        public Builder withDateDepart(DateTime dateDepart) {
            this.participantPartiel.dateDepart = dateDepart;
            return this;
        }

        public Participant build() {
            checkValues();
            return this.participantPartiel;
        }

        private void checkValues() {
            Preconditions.checkNotNull(participantPartiel.getNom());
            Preconditions.checkNotNull(participantPartiel.getProjetId());
            Preconditions.checkNotNull(participantPartiel.getDateArrive());
            Preconditions.checkNotNull(participantPartiel.getDateDepart());

        }
    }

    @Override
    public String toString() {
        return nom + " (" + DateHelper.prettyDate(dateArrive) + "|"
                + DateHelper.prettyDate(dateDepart) + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((dateArrive == null) ? 0 : dateArrive.hashCode());
        result = prime * result
                + ((dateDepart == null) ? 0 : dateDepart.hashCode());
        result = prime * result + ((nom == null) ? 0 : nom.hashCode());
        result = prime * result
                + ((projetId == null) ? 0 : projetId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Participant other = (Participant) obj;
        if (dateArrive == null) {
            if (other.dateArrive != null)
                return false;
        } else if (!dateArrive.equals(other.dateArrive))
            return false;
        if (dateDepart == null) {
            if (other.dateDepart != null)
                return false;
        } else if (!dateDepart.equals(other.dateDepart))
            return false;
        if (nom == null) {
            if (other.nom != null)
                return false;
        } else if (!nom.equals(other.nom))
            return false;
        if (projetId == null) {
            if (other.projetId != null)
                return false;
        } else if (!projetId.equals(other.projetId))
            return false;
        return true;
    }

}
