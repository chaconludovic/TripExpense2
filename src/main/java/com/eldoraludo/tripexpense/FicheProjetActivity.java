package com.eldoraludo.tripexpense;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.eldoraludo.tripexpense.calculateur.SyntheseCalculateur;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.dto.SyntheseDTO;
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Emprunt;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.entite.Projet;
import com.eldoraludo.tripexpense.util.DateHelper;

import java.util.ArrayList;
import java.util.List;

public class FicheProjetActivity extends Activity {
    private DatabaseHandler databaseHandler;
    private static final int REQUEST_MODIFIER_PROJET = 1;
    private String TAG = SyntheseActivity.class.getSimpleName();

    private Integer idProjet;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_projet);
        Intent intent = getIntent();
        idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
                -1);
        databaseHandler = new DatabaseHandler(this);
        initialiserLaFiche();
    }

    private void initialiserLaFiche() {
        if (idProjet != -1) {
            Projet projet = databaseHandler.trouverLeProjet(idProjet);
            TextView nomDuProjetText = (TextView) this
                    .findViewById(R.id.nomDuProjet);
            nomDuProjetText.setText(projet.getNom());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fiche_projet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.fiche_projet_ajouter_participant_menu:
                Intent i = new Intent(getApplicationContext(),
                        GestionParticipantActivity.class);
                i.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
                startActivity(i);
                return true;
            case R.id.fiche_projet_modifier_projet_menu:
                Intent pageAjouterProjet = new Intent(getApplicationContext(),
                        AjouterProjetActivity.class);
                pageAjouterProjet.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                        idProjet);
                startActivityForResult(pageAjouterProjet, REQUEST_MODIFIER_PROJET);
                return true;
            case R.id.fiche_projet_envoyer_email_menu:
                String to = this.recupererLaListeDEmail().toString();
                String subject = new StringBuilder().append("Synthèse des dettes du séjour ").append(databaseHandler.trouverLeProjet(idProjet).getNom()).append(" avec TripExpense").toString();
                StringBuilder message = recupererLesInfosDuProjet()
                        .append(recupererLaSynthese())
                        .append(recupererLesDepenses())
                        .append(recupererLesEmprunts())
                        .append(recuperGreetings());
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
                //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, message.toString());

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                return true;
            case R.id.fiche_projet_synthese_menu:
                Intent pageSynthese = new Intent(getApplicationContext(),
                        SyntheseActivity.class);
                pageSynthese.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                        idProjet);
                startActivity(pageSynthese);
                super.finish();
                return true;
        }
        return true;
    }

    private StringBuilder recuperGreetings() {
        return new StringBuilder().append("\nMerci d'avoir utiliser TripExpense et à Bientôt\n\nTripExpense Team");
    }

    private StringBuilder recupererLesInfosDuProjet() {
        Projet projet = databaseHandler.trouverLeProjet(idProjet);
        StringBuilder projetStr = new StringBuilder("Séjour: ");
        projetStr.append(projet.getNom()).append("\n");
        return projetStr;
    }

    private StringBuilder recupererLaSynthese() {
        List<SyntheseDTO> listeDettes = getListeDettes();
        StringBuilder synthese = new StringBuilder("\nLa synthèse:\n");
        for (SyntheseDTO syntheseDTO : listeDettes) {
            synthese.append("-").append(syntheseDTO.getParticipant().getNom()).append(" doit la somme de ").append(syntheseDTO.getMontant()).append(" euros à ").append(syntheseDTO.getDepenseur().getNom()).append("\n");
        }
        return synthese;
    }

    private String recupererLesDepenses() {
        List<Depense> depenses = databaseHandler.getAllDepense(idProjet);
        StringBuilder depenseStr = new StringBuilder("\nLes dépenses:\n");
        for (Depense depense : depenses) {
            Participant participant = databaseHandler.trouverLeParticipant(depense.getParticipantId());
            depenseStr.append("-").append(participant.getNom()).append(" a dépensé ").append(depense.getMontant()).append(" euros entre le ").append(DateHelper.prettyDate(depense.getDateDebut())).append(" et le ").append(DateHelper.prettyDate(depense.getDateFin())).append("\n");
        }
        return depenseStr.toString();
    }

    private String recupererLesEmprunts() {
        List<Emprunt> emprunts = databaseHandler.getAllEmprunt(idProjet);
        StringBuilder empruntsStr = new StringBuilder("\nLes emprunts:\n");
        for (Emprunt emprunt : emprunts) {
            Participant emprunteur = databaseHandler.trouverLeParticipant(emprunt.getEmprunteurId());
            Participant participant = databaseHandler.trouverLeParticipant(emprunt.getParticipantId());
            empruntsStr.append("-").append(emprunteur.getNom()).append(" a emprunté ").append(emprunt.getMontant()).append(" euros le ").append(DateHelper.prettyDate(emprunt.getDateEmprunt())).append(" à ").append(participant.getNom()).append("\n");
        }
        return empruntsStr.toString();
    }

    private List<SyntheseDTO> getListeDettes() {
        List<Depense> depenses = databaseHandler.getAllDepense(idProjet);
        List<Participant> participants = databaseHandler
                .getAllParticipant(idProjet);
        List<Emprunt> emprunts = databaseHandler.getAllEmprunt(idProjet);
        SyntheseCalculateur syntheseCalculateur = new SyntheseCalculateur(emprunts, depenses, participants);
        try {
            return syntheseCalculateur.getResultat();
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
        }
        return new ArrayList<SyntheseDTO>();
    }

    private StringBuilder recupererLaListeDEmail() {
        List<Participant> allParticipant = databaseHandler.getAllParticipant(idProjet);
        StringBuilder emails = new StringBuilder();
        for (Participant participant : allParticipant) {
            if (participant.getContactPhoneId() == null) {
                continue;
            }
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, new StringBuilder().append(ContactsContract.CommonDataKinds.Email.CONTACT_ID).append("=?").toString(), new String[]{participant.getContactPhoneId()}, null);
            if (cursor.moveToFirst()) {
                emails.append(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1))).append(";");
            }
        }
        return emails;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_MODIFIER_PROJET) {
                initialiserLaFiche();
            }
        }
    }
}