package com.eldoraludo.tripexpense;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.eldoraludo.tripexpense.arrayadapter.ProjetArrayAdapter;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Projet;

import java.util.ArrayList;
import java.util.List;

public class GestionProjetActivity extends ListActivity {
    public static final String ID_PROJET_COURANT = "idProjetCourant";
    protected static final int CONTEXTMENU_DELETEITEM = 0;
    protected static final int CONTEXTMENU_MODIFYITEM = 1;
    private static final int REQUEST_AJOUTER_PROJET = 1;

    // private EditText nouveauProjetText;
    private Button ajouterNouveauProjetButton;
    private DatabaseHandler databaseHandler;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_projet);

        databaseHandler = new DatabaseHandler(this);

        // nouveauProjetText = (EditText) findViewById(R.id.nouveauProjetText);
        ajouterNouveauProjetButton = (Button) findViewById(R.id.ajouterNouveauProjetButton);

        List<Projet> values = new ArrayList<Projet>();
        try {
            values = databaseHandler.getAllProjet();
        } catch (Exception e) {
            Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // Binding resources Array to ListAdapter
        // this.setListAdapter(new ArrayAdapter<Projet>(this,
        // android.R.layout.simple_list_item_1, values));
        ProjetArrayAdapter adapter = new ProjetArrayAdapter(this, values);
        this.setListAdapter(adapter);
        lv = getListView();

        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    Projet projetACharger = (Projet) lv.getAdapter().getItem(
                            position);
                    // mise à projet courant
                    for (Projet projet : databaseHandler.getAllProjet()) {
                        if (projet.estCourant()) {
                            projet.annuleEtatCourant();
                            databaseHandler.ajouterOuModifierProjet(projet);
                        }
                    }
                    projetACharger.definirEnTantQueCourant();
                    databaseHandler.ajouterOuModifierProjet(projetACharger);
                    Intent i = new Intent(getApplicationContext(),
                            FicheProjetActivity.class);
                    // sending data to new activity
                    i.putExtra(ID_PROJET_COURANT, projetACharger.getId());
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenuInfo menuInfo) {
                // TODO Auto-generated method stub
                menu.setHeaderTitle("Que voulez vous faire?");
                menu.add(0, CONTEXTMENU_MODIFYITEM, 0, "Modifier");
                menu.add(0, CONTEXTMENU_DELETEITEM, 0, "Supprimer");
            }

        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem aItem) {

        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem
                .getMenuInfo();

		/* Switch on the ID of the item, to get what the user selected. */

        switch (aItem.getItemId()) {

            case CONTEXTMENU_DELETEITEM:
                try {
                    ArrayAdapter<Projet> adapter = (ArrayAdapter<Projet>) getListAdapter();

			/* Get the selected item out of the Adapter by its position. */

                    Projet projetASupprimer = (Projet) lv.getAdapter().getItem(
                            menuInfo.position);

                    databaseHandler.deleteProjet(projetASupprimer);
            /* Remove it from the list. */
                    adapter.remove(projetASupprimer);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true; /* true means: "we handled the event". */
            case CONTEXTMENU_MODIFYITEM:
                try {
                    Projet projetAModifier = (Projet) lv.getAdapter().getItem(
                            menuInfo.position);
                    Intent i = new Intent(getApplicationContext(),
                            FicheProjetActivity.class);
                    // sending data to new activity
                    i.putExtra(ID_PROJET_COURANT, projetAModifier.getId());
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true; /* true means: "we handled the event". */
        }

        return false;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_AJOUTER_PROJET) {
                // List<Projet> values = databaseHandler.getAllProjet();
                // // Binding resources Array to ListAdapter
                // this.setListAdapter(new ArrayAdapter<Projet>(this,
                // android.R.layout.simple_list_item_1, values));
                List<Projet> values = new ArrayList<Projet>();
                try {
                    values = databaseHandler.getAllProjet();
                } catch (Exception e) {
                    Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                // Binding resources Array to ListAdapter
                // this.setListAdapter(new ArrayAdapter<Projet>(this,
                // android.R.layout.simple_list_item_1, values));
                ProjetArrayAdapter adapter = new ProjetArrayAdapter(this,
                        values);
                this.setListAdapter(adapter);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        // If add button was clicked
        if (ajouterNouveauProjetButton.isPressed()) {
            Intent intent = new Intent(this, AjouterProjetActivity.class);
            startActivityForResult(intent, REQUEST_AJOUTER_PROJET);
        }
    }

}
