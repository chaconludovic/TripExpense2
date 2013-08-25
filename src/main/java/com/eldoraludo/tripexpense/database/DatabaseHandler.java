package com.eldoraludo.tripexpense.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Emprunt;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.entite.Projet;
import com.eldoraludo.tripexpense.entite.TypeDeDepense;
import com.eldoraludo.tripexpense.util.DateHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tripExpenseDB";

    // Contacts table name
    private static final String TABLE_PROJET = "projet";
    private static final String TABLE_PARTICIPANT = "participant";
    private static final String TABLE_DEPENSE = "depense";
    private static final String TABLE_EMPRUNT = "emprunt";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // DROP table
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPENSE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJET);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPRUNT);

            creationTableProjet(db);
            creationTableParticipant(db);
            creationTableDepense(db);
            creationTableEmprunt(db);
        } finally {
            db.close();
        }

    }

    private void creationTableProjet(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_PROJET
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, est_courant INTEGER)");
    }

    private void creationTableParticipant(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_PARTICIPANT
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, nom_participant TEXT, projet_id INTEGER,date_arrive TEXT,date_depart TEXT ,contact_phone_id TEXT ,FOREIGN KEY (projet_id) REFERENCES PROJET (id))");
    }

    private void creationTableDepense(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DEPENSE
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT,nom_depense TEXT, "
                + " montant DOUBLE, date_debut TEXT,date_fin TEXT ,type TEXT, "
                + " participant_id INTEGER, projet_id INTEGER, "
                + " FOREIGN KEY (participant_id) REFERENCES PARTICIPANT (id), "
                + " FOREIGN KEY (projet_id) REFERENCES PROJET (id))");
    }

    private void creationTableEmprunt(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EMPRUNT
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT,nom_emprunt TEXT, "
                + " montant DOUBLE,  "
                + " date_emprunt TEXT , "
                + " emprunteur_id INTEGER, "
                + " participant_id INTEGER, "
                + " projet_id INTEGER, "
                + " FOREIGN KEY (emprunteur_id) REFERENCES PARTICIPANT (id), "
                + " FOREIGN KEY (participant_id) REFERENCES PARTICIPANT (id), "
                + " FOREIGN KEY (projet_id) REFERENCES PROJET (id))");
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new projet
    public Projet ajouterOuModifierProjet(Projet projet) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("nom", projet.getNom());
            values.put("est_courant", projet.estCourant() ? 1 : 0);
            if (projet.getId() == null) {
                long insertId = db.insert(TABLE_PROJET, null, values);
                projet.definirLId(insertId);
            } else {
                db.update(TABLE_PROJET, values, "id = ?",
                        new String[]{String.valueOf(projet.getId())});
            }
        } finally {
            db.close();
        }
        return projet;
    }

    // Adding new participant
    public Participant ajouterOuModifierParticipant(Participant participant) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("nom_participant", participant.getNom());
            values.put("projet_id", participant.getProjetId());
            values.put("date_arrive",
                    DateHelper.convertirDateToString(participant.getDateArrive()));
            values.put("date_depart",
                    DateHelper.convertirDateToString(participant.getDateDepart()));
            values.put("contact_phone_id", participant.getContactPhoneId());
            if (participant.getId() == null) {
                long insertId = db.insert(TABLE_PARTICIPANT, null, values);
                participant.definirLId(insertId);
            } else {
                db.update(TABLE_PARTICIPANT, values, "id = ?",
                        new String[]{String.valueOf(participant.getId())});
            }
        } finally {
            db.close();
        }
        return participant;
    }

    // Adding new depense
    public Depense ajouterOuModifierDepense(Depense depense) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("nom_depense", depense.getNomDepense());
            values.put("montant", depense.getMontant());
            values.put("type", depense.getTypeDeDepense().name());
            values.put("date_debut",
                    DateHelper.convertirDateToString(depense.getDateDebut()));
            values.put("date_fin",
                    DateHelper.convertirDateToString(depense.getDateFin()));
            values.put("projet_id", String.valueOf(depense.getProjetId()));
            values.put("participant_id", String.valueOf(depense.getParticipantId()));
            if (depense.getId() == null) {
                long insertId = db.insert(TABLE_DEPENSE, null, values);
                depense.definirLId(insertId);
            } else {
                db.update(TABLE_DEPENSE, values, "id = ?",
                        new String[]{String.valueOf(depense.getId())});
            }
        } finally {
            db.close();
        }
        return depense;
    }

    // Adding new Emprunt
    public Emprunt ajouterOuModifierEmprunt(Emprunt emprunt) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("nom_emprunt", emprunt.getNomEmprunt());
            values.put("montant", emprunt.getMontant());
            values.put("date_emprunt",
                    DateHelper.convertirDateToString(emprunt.getDateEmprunt()));
            values.put("projet_id", String.valueOf(emprunt.getProjetId()));
            values.put("emprunteur_id", String.valueOf(emprunt.getEmprunteurId()));
            values.put("participant_id", String.valueOf(emprunt.getParticipantId()));
            if (emprunt.getId() == null) {
                long insertId = db.insert(TABLE_EMPRUNT, null, values);
                emprunt.definirLId(insertId);
            } else {
                db.update(TABLE_EMPRUNT, values, "id = ?",
                        new String[]{String.valueOf(emprunt.getId())});
            }
        } finally {
            db.close();
        }
        return emprunt;
    }

    // Getting single projet
    public Projet trouverLeProjet(Integer projetId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Projet projet;
        try {
            Cursor cursor = db.query(TABLE_PROJET, new String[]{"id", "nom",
                    "est_courant"}, "id=?",
                    new String[]{String.valueOf(projetId)}, null, null, null,
                    null);
            projet = null;
            if (cursor.moveToFirst()) {
                projet = getProjet(cursor);
            }
        } finally {
            db.close();
        }
        return projet;
    }

    public Participant trouverLeParticipant(Integer idParticipant) {
        SQLiteDatabase db = this.getReadableDatabase();
        Participant participant;
        try {
            Cursor cursor = db.query(TABLE_PARTICIPANT, new String[]{"id",
                    "nom_participant", "date_arrive", "date_depart", "projet_id", "contact_phone_id"},
                    "id=?", new String[]{String.valueOf(idParticipant)}, null,
                    null, null, null);
            participant = null;
            if (cursor.moveToFirst()) {
                participant = getParticipant(cursor);
            }
        } finally {
            db.close();
        }
        return participant;
    }

    public Participant trouverLeParticipant(Integer idProjet, String nom) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PARTICIPANT, new String[]{"id",
                "nom_participant", "date_arrive", "date_depart", "projet_id", "contact_phone_id"},
                "nom_participant=? and projet_id = ?", new String[]{nom,String.valueOf(idProjet)}, null,
                null, null, null);
        Participant participant = null;
        if (cursor.moveToFirst()) {
            participant = getParticipant(cursor);
        }
        db.close();
        return participant;
    }

    private Participant getParticipant(Cursor cursor) {
        return Participant
                .newBuilder()
                .withId(cursor.getInt(0))
                .withNom(cursor.getString(1))
                .withDateArrive(
                        DateHelper.convertirStringToDate(cursor
                                .getString(2)))
                .withDateDepart(
                        DateHelper.convertirStringToDate(cursor
                                .getString(3)))
                .withProjetId(cursor.getInt(4))
                .withContactPhoneId(cursor.getString(5))
                .build();
    }

    public Depense trouverLaDepense(Integer idDepense) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DEPENSE, new String[]{"id",
                "nom_depense", " montant", "date_debut", "date_fin", "type",
                "participant_id", "projet_id"}, "id=?",
                new String[]{String.valueOf(idDepense)}, null, null, null,
                null);
        Depense depense = null;
        if (cursor.moveToFirst()) {
            depense = getDepense(cursor);
        }
        db.close();
        return depense;
    }

    public Emprunt trouverLEmprunt(Integer idEmprunt) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EMPRUNT, new String[]{"id",
                "nom_emprunt", " montant", "date_emprunt",
                "emprunteur_id", "participant_id", "projet_id"}, "id=?",
                new String[]{String.valueOf(idEmprunt)}, null, null, null,
                null);
        Emprunt emprunt = null;
        if (cursor.moveToFirst()) {
            emprunt = getEmprunt(cursor);
        }
        db.close();
        return emprunt;
    }

    private Depense getDepense(Cursor cursor) {
        return Depense
                .newBuilder()
                .withId(cursor.getInt(0))
                .withNomDepense(cursor.getString(1))
                .withMontant(Double.valueOf(cursor.getString(2)))
                .withDateDebut(
                        DateHelper.convertirStringToDate(cursor
                                .getString(3)))
                .withDateFin(
                        DateHelper.convertirStringToDate(cursor
                                .getString(4)))
                .withTypeDeDepense(
                        TypeDeDepense.valueOf(cursor.getString(5)))
                .withParticipantId(cursor.getInt(6))
                .withProjetId(cursor.getInt(7)).build();
    }

    private Emprunt getEmprunt(Cursor cursor) {
        return Emprunt
                .newBuilder()
                .withId(cursor.getInt(0))
                .withNomEmprunt(cursor.getString(1))
                .withMontant(Double.valueOf(cursor.getString(2)))
                .withDateEmprunt(
                        DateHelper.convertirStringToDate(cursor
                                .getString(3)))
                .withEmprunteurId(cursor.getInt(4))
                .withParticipantId(cursor.getInt(5))
                .withProjetId(cursor.getInt(6)).build();
    }

    // Getting single projet
    public Projet trouverLeProjetCourant() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROJET, new String[]{"id", "nom",
                "est_courant"}, "est_courant=1", new String[]{}, null, null,
                null, null);
        Projet projet = null;
        if (cursor.moveToFirst()) {
            projet = getProjet(cursor);
        }
        db.close();
        return projet;
    }

    private Projet getProjet(Cursor cursor) {
        return Projet.newBuilder().withId(cursor.getInt(0))
                .withNom(cursor.getString(1))
                .withEstProjetCourant((cursor.getInt(2) == 0) ? false : true)
                .build();
    }

    public Projet trouverLeProjet(String nom) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROJET, new String[]{"id", "nom",
                "est_courant"}, "nom=?", new String[]{nom}, null, null,
                null, null);
        Projet projet = null;
        if (cursor.moveToFirst()) {
            projet = getProjet(cursor);

        }
        db.close();
        return projet;
    }

    // Deleting single projet
    public void deleteProjet(Projet projet) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROJET, "id = ?",
                new String[]{String.valueOf(projet.getId())});
        db.close();
    }

    public void deleteParticipant(Participant participant) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PARTICIPANT, "id = ?",
                new String[]{String.valueOf(participant.getId())});
        db.close();
    }

    public void deleteDepense(Depense depense) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DEPENSE, "id = ?",
                new String[]{String.valueOf(depense.getId())});
        db.close();
    }

    public void deleteEmprunt(Emprunt emprunt) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EMPRUNT, "id = ?",
                new String[]{String.valueOf(emprunt.getId())});
        db.close();
    }

    // Getting projets Count
    public int getProjetsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PROJET;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    // Getting participant Count
    public int getParticipantsCount(Integer idProjet) {
        String countQuery = "SELECT  * FROM " + TABLE_PARTICIPANT
                + " WHERE projet_id=" + idProjet;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getDepensesCount(Integer projetId, Integer participantId) {
        String countQuery = "SELECT  * FROM " + TABLE_DEPENSE
                + " WHERE projet_id=" + projetId + " and participant_id=" + participantId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getEmpruntsCount(Integer projetId, Integer participantId) {
        String countQuery = "SELECT  * FROM " + TABLE_EMPRUNT
                + " WHERE projet_id=" + projetId + " and (participant_id=" + participantId + " or emprunteur_id=" + participantId + ")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public List<Participant> getAllParticipant(Integer projetId) {
        List<Participant> participantList = new ArrayList<Participant>();
        // String selectQuery = "SELECT  * FROM " + TABLE_PARTICIPANT;
        SQLiteDatabase db = this.getReadableDatabase();
        // Cursor cursor = db.rawQuery(selectQuery, null);

        Cursor cursor = db.query(TABLE_PARTICIPANT, new String[]{"id",
                "nom_participant", "date_arrive", "date_depart", "projet_id", "contact_phone_id"},
                "projet_id=?", new String[]{String.valueOf(projetId)}, null,
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                participantList.add(Participant
                        .newBuilder()
                        .withId(cursor.getInt(0))
                        .withNom(cursor.getString(1))
                        .withDateArrive(
                                DateHelper.convertirStringToDate(cursor
                                        .getString(2)))
                        .withDateDepart(
                                DateHelper.convertirStringToDate(cursor
                                        .getString(3)))
                        .withProjetId(cursor.getInt(4))
                        .withContactPhoneId(cursor.getString(5))
                        .build());
            } while (cursor.moveToNext());
        }
        db.close();
        return participantList;
    }

    public List<Depense> getAllDepense(Integer projetId) {
        List<Depense> depenseList = new ArrayList<Depense>();
        // String selectQuery = "SELECT  * FROM " + TABLE_PARTICIPANT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DEPENSE, new String[]{"id",
                "nom_depense", " montant", "date_debut", "date_fin", "type",
                "participant_id", "projet_id"}, "projet_id=?",
                new String[]{String.valueOf(projetId)}, null, null, null,
                null);

        if (cursor.moveToFirst()) {
            do {
                depenseList.add(Depense
                        .newBuilder()
                        .withId(cursor.getInt(0))
                        .withNomDepense(cursor.getString(1))
                        .withMontant(Double.valueOf(cursor.getString(2)))
                        .withDateDebut(
                                DateHelper.convertirStringToDate(cursor
                                        .getString(3)))
                        .withDateFin(
                                DateHelper.convertirStringToDate(cursor
                                        .getString(4)))
                        .withTypeDeDepense(
                                TypeDeDepense.valueOf(cursor.getString(5)))
                        .withParticipantId(cursor.getInt(6))
                        .withProjetId(cursor.getInt(7)).build());
            } while (cursor.moveToNext());
        }
        db.close();
        return depenseList;
    }

    public List<Emprunt> getAllEmprunt(Integer projetId) {
        List<Emprunt> empruntList = new ArrayList<Emprunt>();
        // String selectQuery = "SELECT  * FROM " + TABLE_PARTICIPANT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EMPRUNT, new String[]{"id",
                "nom_emprunt", " montant", "date_emprunt",
                "emprunteur_id", "participant_id", "projet_id"}, "projet_id=?",
                new String[]{String.valueOf(projetId)}, null, null, null,
                null);

        if (cursor.moveToFirst()) {
            do {
                empruntList.add(Emprunt
                        .newBuilder()
                        .withId(cursor.getInt(0))
                        .withNomEmprunt(cursor.getString(1))
                        .withMontant(Double.valueOf(cursor.getString(2)))
                        .withDateEmprunt(DateHelper.convertirStringToDate(cursor
                                .getString(3)))
                        .withEmprunteurId(cursor.getInt(4))
                        .withParticipantId(cursor.getInt(5))
                        .withProjetId(cursor.getInt(6)).build());
            } while (cursor.moveToNext());
        }
        db.close();
        return empruntList;
    }

    public List<Depense> trouverToutesLesDepensesDuParticipant(Integer projetId, Integer participantId) {
        List<Depense> depenseList = new ArrayList<Depense>();
        // String selectQuery = "SELECT  * FROM " + TABLE_PARTICIPANT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DEPENSE, new String[]{"id",
                "nom_depense", " montant", "date_debut", "date_fin", "type",
                "participant_id", "projet_id"}, "projet_id=? and participant_id=? ",
                new String[]{String.valueOf(projetId), String.valueOf(participantId)}, null, null, null,
                null);

        if (cursor.moveToFirst()) {
            do {
                depenseList.add(Depense
                        .newBuilder()
                        .withId(cursor.getInt(0))
                        .withNomDepense(cursor.getString(1))
                        .withMontant(Double.valueOf(cursor.getString(2)))
                        .withDateDebut(
                                DateHelper.convertirStringToDate(cursor
                                        .getString(3)))
                        .withDateFin(
                                DateHelper.convertirStringToDate(cursor
                                        .getString(4)))
                        .withTypeDeDepense(
                                TypeDeDepense.valueOf(cursor.getString(5)))
                        .withParticipantId(cursor.getInt(6))
                        .withProjetId(cursor.getInt(7)).build());
            } while (cursor.moveToNext());
        }
        db.close();
        return depenseList;
    }

    // Getting All projets
    public List<Projet> getAllProjet() {
        List<Projet> projetList = new ArrayList<Projet>();
        String selectQuery = "SELECT  * FROM " + TABLE_PROJET;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                projetList
                        .add(Projet
                                .newBuilder()
                                .withId(cursor.getInt(0))
                                .withNom(cursor.getString(1))
                                .withEstProjetCourant(
                                        (cursor.getInt(2) == 0) ? false : true)
                                .build());
            } while (cursor.moveToNext());
        }
        db.close();
        return projetList;
    }

}
