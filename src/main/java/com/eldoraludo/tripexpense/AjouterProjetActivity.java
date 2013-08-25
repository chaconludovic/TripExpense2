package com.eldoraludo.tripexpense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Projet;

public class AjouterProjetActivity extends Activity {
    private Button ajouterOuModifierProjetButton;
    private EditText nomProjetText;
    private DatabaseHandler databaseHandler;
    private Integer idProjet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_projet);
        Intent intent = getIntent();
        idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
                -1);
        databaseHandler = new DatabaseHandler(this);

        ajouterOuModifierProjetButton = (Button) findViewById(R.id.ajouterOuModifierProjetButton);
        nomProjetText = (EditText) findViewById(R.id.nomProjetText);
        if (idProjet != -1) {
            Projet projet = databaseHandler.trouverLeProjet(idProjet);
            nomProjetText.setText(projet.getNom());
            ajouterOuModifierProjetButton.setText("Modifier");
        }

    }

    public void onClick(View view) {
        // If add button was clicked
        if (ajouterOuModifierProjetButton.isPressed()) {
            // Get entered text
            String nomProjetTextValue = nomProjetText.getText().toString();
            if (nomProjetTextValue == null || nomProjetTextValue.isEmpty()) {
                Toast.makeText(this, "Il faut préciser un nom de projet", Toast.LENGTH_SHORT).show();
                return;
            }
            nomProjetText.setText("");
            Projet projet = databaseHandler.trouverLeProjet(nomProjetTextValue);
            if (projet != null && (idProjet == -1 || !idProjet.equals(projet.getId()))) {
                Toast.makeText(this, "Le nom du projet " + nomProjetTextValue + " existe déjà", Toast.LENGTH_SHORT).show();
                return;
            }
            // Add text to the database
            databaseHandler.ajouterOuModifierProjet(Projet.newBuilder()
                    .withId(idProjet == -1 ? null : idProjet)
                    .withNom(nomProjetTextValue).withEstProjetCourant(false)
                    .build());
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            super.finish();
        }
    }

}
