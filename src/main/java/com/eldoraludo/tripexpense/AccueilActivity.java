package com.eldoraludo.tripexpense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Projet;

public class AccueilActivity extends Activity {

    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        databaseHandler = new DatabaseHandler(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.accueil, menu);
        try {
            if (databaseHandler.getProjetsCount() == 0) {
                MenuItem menuSynthese = menu.findItem(R.id.gestion_accueil_synthese_menu);
                menuSynthese.setVisible(false);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gestion_accueil_projet_menu:
                Intent pageGestionProjet = new Intent(getApplicationContext(),
                        GestionProjetActivity.class);
                startActivity(pageGestionProjet);
                return true;
            case R.id.gestion_accueil_synthese_menu:
                try {
                    Intent pageSynthese = new Intent(getApplicationContext(),
                            SyntheseActivity.class);
                    Projet projetCourant = databaseHandler.trouverLeProjetCourant();
                    pageSynthese.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                            projetCourant.getId());
                    startActivity(pageSynthese);
                } catch (Exception e) {
                    Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
