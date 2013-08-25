package com.eldoraludo.tripexpense;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
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

import com.eldoraludo.tripexpense.arrayadapter.EmpruntArrayAdapter;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Emprunt;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestionEmpruntActivity extends ListActivity {
    protected static final int CONTEXTMENU_DELETEITEM = 0;
    protected static final int CONTEXTMENU_MODIFYITEM = 1;
    protected static final String ID_EMPRUNT = "id_emprunt";
    private static final int REQUEST_AJOUTER_EMPRUNT = 0;
    private Integer idProjet;
    private Button ajouterNouvelEmpruntButton;
    private DatabaseHandler databaseHandler;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_emprunt);
        ajouterNouvelEmpruntButton = (Button) findViewById(R.id.ajouterNouvelEmpruntButton);
        databaseHandler = new DatabaseHandler(this);
        Intent intent = getIntent();
        idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
                -1);
        Preconditions.checkState(!idProjet.equals(-1),
                "L'id du projet doit être définit");

        List<Emprunt> values = new ArrayList<Emprunt>();
        try {
            values = Lists.newLinkedList(databaseHandler.getAllEmprunt(idProjet));
            Collections.sort(values);
        } catch (Exception e) {
            Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // Binding resources Array to ListAdapter
        // this.setListAdapter(new ArrayAdapter<Depense>(this,
        // android.R.layout.simple_list_item_1, values));
        EmpruntArrayAdapter adapter = new EmpruntArrayAdapter(this, values);
        this.setListAdapter(adapter);
        lv = getListView();

        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Emprunt empruntAModifier = (Emprunt) lv.getAdapter().getItem(
                        position);
                Intent intent = new Intent(getApplicationContext(),
                        AjouterEmpruntActivity.class);
                intent.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                        idProjet);
                intent.putExtra(ID_EMPRUNT, empruntAModifier.getId());
                startActivityForResult(intent, REQUEST_AJOUTER_EMPRUNT);

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
        // Show the Up button in the action bar.
        setupActionBar();
    }

    public void onClick(View view) {
        // If add button was clicked
        if (ajouterNouvelEmpruntButton.isPressed()) {
            Intent intent = new Intent(this, AjouterEmpruntActivity.class);
            intent.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
            // startActivity(intent);
            startActivityForResult(intent, REQUEST_AJOUTER_EMPRUNT);
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gestion_emprunt, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_AJOUTER_EMPRUNT) {
                try {
                    List<Emprunt> values = databaseHandler.getAllEmprunt(idProjet);
                    // Binding resources Array to ListAdapter
                    EmpruntArrayAdapter adapter = new EmpruntArrayAdapter(this,
                            values);
                    this.setListAdapter(adapter);
                } catch (Exception e) {
                    Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem aItem) {

        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem
                .getMenuInfo();

		/* Switch on the ID of the item, to get what the user selected. */

        switch (aItem.getItemId()) {

            case CONTEXTMENU_DELETEITEM:
                try {
                    ArrayAdapter<Emprunt> adapter = (ArrayAdapter<Emprunt>) getListAdapter();

			/* Get the selected item out of the Adapter by its position. */

                    Emprunt empruntASupprimer = (Emprunt) lv.getAdapter().getItem(
                            menuInfo.position);

                    databaseHandler.deleteEmprunt(empruntASupprimer);
            /* Remove it from the list. */
                    adapter.remove(empruntASupprimer);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true; /* true means: "we handled the event". */
            case CONTEXTMENU_MODIFYITEM:
                try {
                    Emprunt empruntAModifier = (Emprunt) lv.getAdapter().getItem(
                            menuInfo.position);
                    Intent i = new Intent(getApplicationContext(),
                            AjouterEmpruntActivity.class);
                    // sending data to new activity
                    i.putExtra(ID_EMPRUNT, empruntAModifier.getId());
                    i.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
                    startActivityForResult(i, REQUEST_AJOUTER_EMPRUNT);
                } catch (Exception e) {
                    Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true; /* true means: "we handled the event". */
        }

        return false;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.gestion_emprunt_synthese_menu:
                Intent pageSynthese = new Intent(getApplicationContext(),
                        SyntheseActivity.class);
                pageSynthese.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                        idProjet);
                startActivity(pageSynthese);
                super.finish();
                return true;
            case R.id.gestion_emprunt_participant_menu:
                Intent pageDepense = new Intent(getApplicationContext(),
                        GestionParticipantActivity.class);
                pageDepense.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                        idProjet);
                startActivity(pageDepense);
                super.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
