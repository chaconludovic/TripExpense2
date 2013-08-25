package com.eldoraludo.tripexpense;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Emprunt;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.util.DateHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AjouterEmpruntActivity extends Activity {
    private Integer idEmprunt;
    private Integer idProjet;
    private Button ajouterOuModifierEmpruntButton;
    private EditText nomEmpruntText;
    private EditText montantText;
    private TextView tvDisplayDateEmprunt;
    private Button btnChangeDateEmprunt;
    private Spinner listeEmprunteur;
    private Spinner listeParticipant;

    private int anneeEmprunt;
    private int moisEmprunt;
    private int jourEmprunt;
    private DatabaseHandler databaseHandler;
    static final int DATE_DIALOG_ID_DEBUT_EMPRUNT = 999;
    static final int DATE_DIALOG_ID_FIN_EMPRUNT = 1999;
    private static final int ERROR_DIALOG_DATE_INCOHERENTE = 45464;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_emprunt);

        Intent intent = getIntent();
        idEmprunt = intent.getIntExtra(GestionEmpruntActivity.ID_EMPRUNT, -1);
        idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
                -1);
        Preconditions.checkState(!idProjet.equals(-1),
                "L'id du projet doit être définit");
        databaseHandler = new DatabaseHandler(this);
        nomEmpruntText = (EditText) findViewById(R.id.nomEmpruntText);
        montantText = (EditText) findViewById(R.id.montantText);
        ajouterOuModifierEmpruntButton = (Button) findViewById(R.id.ajouterOuModifierEmpruntButton);

        listeEmprunteur = (Spinner) findViewById(R.id.listeEmprunteur);
        listeParticipant = (Spinner) findViewById(R.id.listeParticipant);
        List<Participant> list = new ArrayList<Participant>();
        try {
            list = databaseHandler.getAllParticipant(idProjet);
        } catch (Exception e) {
            Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<Participant> emprunteurArrayAdapter = new ArrayAdapter<Participant>(
                this, android.R.layout.simple_spinner_item, list);
        emprunteurArrayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listeEmprunteur.setAdapter(emprunteurArrayAdapter);
        ArrayAdapter<Participant> participantArrayAdapter = new ArrayAdapter<Participant>(
                this, android.R.layout.simple_spinner_item, list);
        participantArrayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listeParticipant.setAdapter(participantArrayAdapter);

        Emprunt emprunt = null;
        if (idEmprunt != -1) {
            try {
                emprunt = databaseHandler.trouverLEmprunt(idEmprunt);
                Preconditions.checkNotNull(emprunt,
                        "La dépense n'a pas été trouvée");
                nomEmpruntText.setText(emprunt.getNomEmprunt());
                montantText.setText(String.valueOf(emprunt.getMontant()));
                anneeEmprunt = emprunt.getDateEmprunt().getYear();
                moisEmprunt = emprunt.getDateEmprunt().getMonthOfYear();
                jourEmprunt = emprunt.getDateEmprunt().getDayOfMonth();

                Participant emprunteur = databaseHandler
                        .trouverLeParticipant(emprunt.getEmprunteurId());
                Participant participant = databaseHandler
                        .trouverLeParticipant(emprunt.getParticipantId());
                int posEmprunteur = list.indexOf(emprunteur);
                listeEmprunteur.setSelection(posEmprunteur);
                int posParticipant = list.indexOf(participant);
                listeParticipant.setSelection(posParticipant);
                ajouterOuModifierEmpruntButton.setText("Modifier");
            } catch (Exception e) {
                Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (emprunt == null) {
            final Calendar c = Calendar.getInstance();
            anneeEmprunt = c.get(Calendar.YEAR);
            moisEmprunt = c.get(Calendar.MONTH) + 1;
            jourEmprunt = c.get(Calendar.DAY_OF_MONTH);
        }

        setCurrentDateOnView();
        addListenerOnButton();

    }

    public void onClick(View view) {
        try {
            // If add button was clicked
            if (ajouterOuModifierEmpruntButton.isPressed()) {
                // Get entered text
                String nomEmpruntTextValue = nomEmpruntText.getText().toString();
                if (nomEmpruntTextValue == null || nomEmpruntTextValue.isEmpty()) {
                    Toast.makeText(this, "Il faut préciser un nom de dépense", Toast.LENGTH_SHORT).show();
                    return;
                }
                String montantEmpruntTextValue = montantText.getText().toString();
                if (montantEmpruntTextValue == null || montantEmpruntTextValue.isEmpty()) {
                    Toast.makeText(this, "Il faut préciser un montant", Toast.LENGTH_SHORT).show();
                    return;
                }
                Participant emprunteurSelectionne = (Participant) listeEmprunteur
                        .getSelectedItem();
                Participant participantSelectionne = (Participant) listeParticipant
                        .getSelectedItem();
                if (emprunteurSelectionne.getId().equals(participantSelectionne.getId())) {
                    Toast.makeText(this, "Il faut que l'emprunteur soit différent de l'emprunter", Toast.LENGTH_SHORT).show();
                    return;
                }

                DateTime dateEmprunt = DateHelper.convertirIntsToDate(
                        jourEmprunt, moisEmprunt, anneeEmprunt);
                // ecart date contient participant
                if (verificationPresenceParticipant(dateEmprunt)) {
                    return;
                }

                nomEmpruntText.setText("");
                montantText.setText("");
                this.ajouterEmprunt(nomEmpruntTextValue, montantEmpruntTextValue,
                        emprunteurSelectionne.getId(), participantSelectionne.getId(), dateEmprunt);
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                super.finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean verificationPresenceParticipant(final DateTime dateEmprunt) {
        List<Participant> allParticipant = databaseHandler
                .getAllParticipant(idProjet);
        if (!Iterables.any(allParticipant, new Predicate<Participant>() {
            @Override
            public boolean apply(Participant participant) {
                return !participant.getDateArrive().isAfter(dateEmprunt) && !dateEmprunt.isAfter(participant.getDateDepart());
            }
        })) {
            Toast.makeText(this, "Aucun participant n'a été trouvé pour la date " + dateEmprunt.toString("dd/MM/YYYY"), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void ajouterEmprunt(String nomEmpruntTextValue,
                                String montantEmpruntTextValue, Integer emprunteurId, Integer participantId,
                                DateTime dateDebut) {
        // Add text to the database
        databaseHandler.ajouterOuModifierEmprunt(Emprunt.newBuilder()
                .withId(idEmprunt == -1 ? null : idEmprunt)
                .withNomEmprunt(nomEmpruntTextValue)
                .withMontant(Double.valueOf(montantEmpruntTextValue))
                .withDateEmprunt(dateDebut)
                .withEmprunteurId(emprunteurId)
                .withParticipantId(participantId).withProjetId(idProjet)
                .build());
    }

    // display current date
    public void setCurrentDateOnView() {
        tvDisplayDateEmprunt = (TextView) findViewById(R.id.afficherDateEmprunt);
        // set current date into textview
        tvDisplayDateEmprunt.setText(new StringBuilder().append("   ")
                // Month is 0 based, just add 1
                .append(jourEmprunt).append("/").append(moisEmprunt)
                .append("/").append(anneeEmprunt).append(" "));

    }

    public void addListenerOnButton() {

        btnChangeDateEmprunt = (Button) findViewById(R.id.btnChangeDateEmprunt);

        btnChangeDateEmprunt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID_DEBUT_EMPRUNT);

            }

        });


    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID_DEBUT_EMPRUNT:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListenerEmprunt,
                        anneeEmprunt, moisEmprunt - 1, jourEmprunt);
        }
        return null;
    }

    /*private AlertDialog getDialogError(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                AjouterEmpruntActivity.this);
        builder.setTitle("Une erreur est arrivé");
        builder.setIcon(R.drawable.ic_action_error);
        // Add the buttons
        builder.setNeutralButton(errorMessage,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog create = builder.create();
        return create;
    }*/

    private DatePickerDialog.OnDateSetListener datePickerListenerEmprunt = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            anneeEmprunt = selectedYear;
            moisEmprunt = selectedMonth + 1;
            jourEmprunt = selectedDay;

            // set selected date into textview
            tvDisplayDateEmprunt.setText(new StringBuilder().append("   ")
                    .append(jourEmprunt).append("/")
                    .append(moisEmprunt).append("/")
                    .append(anneeEmprunt).append(" "));

        }
    };


}
