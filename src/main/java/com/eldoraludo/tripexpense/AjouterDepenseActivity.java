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
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.entite.TypeDeDepense;
import com.eldoraludo.tripexpense.util.DateHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AjouterDepenseActivity extends Activity {
    private Integer idDepense;
    private Integer idProjet;
    private Button ajouterOuModifierDepenseButton;
    private EditText nomDepenseText;
    private EditText montantText;
    private TextView tvDisplayDateDebutDepense;
    private Button btnChangeDateDebutDepense;
    private TextView tvDisplayDateFinDepense;
    private Button btnChangeDateFinDepense;
    private Spinner listeParticipant;

    private int anneeDebutDepense;
    private int moisDebutDepense;
    private int jourDebutDepense;
    private int anneeFinDepense;
    private int moisFinDepense;
    private int jourFinDepense;
    private DatabaseHandler databaseHandler;
    static final int DATE_DIALOG_ID_DEBUT_DEPENSE = 999;
    static final int DATE_DIALOG_ID_FIN_DEPENSE = 1999;
    private static final int ERROR_DIALOG_DATE_INCOHERENTE = 45464;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_depense);

        Intent intent = getIntent();
        idDepense = intent.getIntExtra(GestionDepenseActivity.ID_DEPENSE, -1);
        idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
                -1);
        Preconditions.checkState(!idProjet.equals(-1),
                "L'id du projet doit être définit");
        databaseHandler = new DatabaseHandler(this);
        nomDepenseText = (EditText) findViewById(R.id.nomDepenseText);
        montantText = (EditText) findViewById(R.id.montantText);
        ajouterOuModifierDepenseButton = (Button) findViewById(R.id.ajouterOuModifierDepenseButton);

        listeParticipant = (Spinner) findViewById(R.id.listeParticipant);
        List<Participant> list = new ArrayList<Participant>();
        try {
            list = databaseHandler.getAllParticipant(idProjet);
        } catch (Exception e) {
            Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<Participant> dataAdapter = new ArrayAdapter<Participant>(
                this, android.R.layout.simple_spinner_item, list);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listeParticipant.setAdapter(dataAdapter);

        Depense depense = null;
        if (idDepense != -1) {
            try {
                depense = databaseHandler.trouverLaDepense(idDepense);
                Preconditions.checkNotNull(depense,
                        "La dépense n'a pas été trouvée");
                nomDepenseText.setText(depense.getNomDepense());
                montantText.setText(String.valueOf(depense.getMontant()));
                anneeDebutDepense = depense.getDateDebut().getYear();
                moisDebutDepense = depense.getDateDebut().getMonthOfYear();
                jourDebutDepense = depense.getDateDebut().getDayOfMonth();

                anneeFinDepense = depense.getDateFin().getYear();
                moisFinDepense = depense.getDateFin().getMonthOfYear();
                jourFinDepense = depense.getDateFin().getDayOfMonth();
                Participant participant = databaseHandler
                        .trouverLeParticipant(depense.getParticipantId());
                int pos = list.indexOf(participant);
                listeParticipant.setSelection(pos);
                ajouterOuModifierDepenseButton.setText("Modifier");
            } catch (Exception e) {
                Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (depense == null) {
            final Calendar c = Calendar.getInstance();
            anneeDebutDepense = c.get(Calendar.YEAR);
            moisDebutDepense = c.get(Calendar.MONTH) + 1;
            jourDebutDepense = c.get(Calendar.DAY_OF_MONTH);
            anneeFinDepense = c.get(Calendar.YEAR);
            moisFinDepense = c.get(Calendar.MONTH) + 1;
            jourFinDepense = c.get(Calendar.DAY_OF_MONTH) + 1;
        }

        setCurrentDateOnView();
        addListenerOnButton();

    }

    public void onClick(View view) {
        try {
            // If add button was clicked
            if (ajouterOuModifierDepenseButton.isPressed()) {
                // Get entered text
                String nomDepenseTextValue = nomDepenseText.getText().toString();
                if (nomDepenseTextValue == null || nomDepenseTextValue.isEmpty()) {
                    Toast.makeText(this, "Il faut préciser un nom de dépense", Toast.LENGTH_SHORT).show();
                    return;
                }
                String montantDepenseTextValue = montantText.getText().toString();
                if (montantDepenseTextValue == null || montantDepenseTextValue.isEmpty()) {
                    Toast.makeText(this, "Il faut préciser un montant", Toast.LENGTH_SHORT).show();
                    return;
                }
                // verification des dates
                // date de debut avec date de fin
                DateTime dateDebut = DateHelper.convertirIntsToDate(
                        jourDebutDepense, moisDebutDepense, anneeDebutDepense);
                DateTime dateFin = DateHelper.convertirIntsToDate(jourFinDepense,
                        moisFinDepense, anneeFinDepense);
                if (dateDebut.isAfter(dateFin)) {
                    // showDialog(ERROR_DIALOG_DATE_INCOHERENTE);
                    Toast.makeText(this, "La date de début doit être avant la date de fin", Toast.LENGTH_SHORT).show();
                    return;
                }
                // ecart date contient participant
                if (verificationPresenceParticipant(dateDebut, dateFin)) {
                    return;
                }

                nomDepenseText.setText("");
                montantText.setText("");
                Participant participantSelectionne = (Participant) listeParticipant
                        .getSelectedItem();
                this.ajouterDepense(nomDepenseTextValue, montantDepenseTextValue,
                        participantSelectionne.getId(), dateDebut, dateFin);
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                super.finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean verificationPresenceParticipant(DateTime dateDebut, DateTime dateFin) {
        List<Participant> allParticipant = databaseHandler
                .getAllParticipant(idProjet);
        DateTime dateCourante = dateDebut;
        while (!dateCourante.isAfter(dateFin)) {
            final DateTime finalDateCourante = dateCourante;
            if (!Iterables.any(allParticipant, new Predicate<Participant>() {
                @Override
                public boolean apply(Participant participant) {
                    return !participant.getDateArrive().isAfter(finalDateCourante) && !finalDateCourante.isAfter(participant.getDateDepart());
                }
            })) {
                Toast.makeText(this, "Aucun participant n'a été trouvé pour la date " + finalDateCourante.toString("dd/MM/YYYY"), Toast.LENGTH_SHORT).show();
                return true;
            }
            dateCourante = dateCourante.plusDays(1);
        }
        return false;
    }

    private void ajouterDepense(String nomDepenseTextValue,
                                String montantDepenseTextValue, Integer participantId,
                                DateTime dateDebut, DateTime dateFin) {
        // Add text to the database
        databaseHandler.ajouterOuModifierDepense(Depense.newBuilder()
                .withId(idDepense == -1 ? null : idDepense)
                .withNomDepense(nomDepenseTextValue)
                .withMontant(Double.valueOf(montantDepenseTextValue))
                .withDateDebut(dateDebut).withDateFin(dateFin)
                .withParticipantId(participantId)
                .withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(idProjet)
                .build());
    }

    // display current date
    public void setCurrentDateOnView() {
        tvDisplayDateDebutDepense = (TextView) findViewById(R.id.afficherDateDebutDepense);
        tvDisplayDateFinDepense = (TextView) findViewById(R.id.afficherDateFinDepense);
        // set current date into textview
        tvDisplayDateDebutDepense.setText(new StringBuilder().append("   ")
                // Month is 0 based, just add 1
                .append(jourDebutDepense).append("/").append(moisDebutDepense)
                .append("/").append(anneeDebutDepense).append(" "));
        tvDisplayDateFinDepense.setText(new StringBuilder().append("   ")
                // Month is 0 based, just add 1
                .append(jourFinDepense).append("/").append(moisFinDepense)
                .append("/").append(anneeFinDepense).append(" "));
    }

    public void addListenerOnButton() {

        btnChangeDateDebutDepense = (Button) findViewById(R.id.btnChangeDateDebutDepense);

        btnChangeDateDebutDepense.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID_DEBUT_DEPENSE);

            }

        });
        btnChangeDateFinDepense = (Button) findViewById(R.id.btnChangeDateFinDepense);

        btnChangeDateFinDepense.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID_FIN_DEPENSE);

            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID_DEBUT_DEPENSE:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListenerDebutDepense,
                        anneeDebutDepense, moisDebutDepense - 1, jourDebutDepense);
            case DATE_DIALOG_ID_FIN_DEPENSE:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListenerFinDepense,
                        anneeFinDepense, moisFinDepense - 1, jourFinDepense);
        /*    case ERROR_DIALOG_DATE_INCOHERENTE:
                // AlertDialog create = getDialogError("La date de début doit être avant la date de fin");
                return null;*/
        }
        return null;
    }

    /*private AlertDialog getDialogError(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                AjouterDepenseActivity.this);
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

    private DatePickerDialog.OnDateSetListener datePickerListenerDebutDepense = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            anneeDebutDepense = selectedYear;
            moisDebutDepense = selectedMonth + 1;
            jourDebutDepense = selectedDay;

            // set selected date into textview
            tvDisplayDateDebutDepense.setText(new StringBuilder().append("   ")
                    .append(jourDebutDepense).append("/")
                    .append(moisDebutDepense).append("/")
                    .append(anneeDebutDepense).append(" "));

        }
    };

    private DatePickerDialog.OnDateSetListener datePickerListenerFinDepense = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            anneeFinDepense = selectedYear;
            moisFinDepense = selectedMonth + 1;
            jourFinDepense = selectedDay;

            // set selected date into textview
            tvDisplayDateFinDepense.setText(new StringBuilder().append("   ")
                    .append(jourFinDepense).append("/").append(moisFinDepense)
                    .append("/").append(anneeFinDepense).append(" "));

        }
    };

}
