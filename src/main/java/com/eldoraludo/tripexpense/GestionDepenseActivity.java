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

import com.eldoraludo.tripexpense.arrayadapter.DepenseArrayAdapter;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Depense;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class GestionDepenseActivity extends ListActivity {
    protected static final int CONTEXTMENU_DELETEITEM = 0;
    protected static final int CONTEXTMENU_MODIFYITEM = 1;
    protected static final String ID_DEPENSE = "id_depense";
    private static final int REQUEST_AJOUTER_DEPENSE = 0;
    private Integer idProjet;
    private Button ajouterNouveauDepenseButton;
    private DatabaseHandler databaseHandler;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_depense);
        ajouterNouveauDepenseButton = (Button) findViewById(R.id.ajouterNouveauDepenseButton);
        databaseHandler = new DatabaseHandler(this);
        Intent intent = getIntent();
        idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
                -1);
        Preconditions.checkState(!idProjet.equals(-1),
                "L'id du projet doit être définit");

        List<Depense> values = new ArrayList<Depense>();
        try {
            values = databaseHandler.getAllDepense(idProjet);
        } catch (Exception e) {
            Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // Binding resources Array to ListAdapter
        // this.setListAdapter(new ArrayAdapter<Depense>(this,
        // android.R.layout.simple_list_item_1, values));
        DepenseArrayAdapter adapter = new DepenseArrayAdapter(this, values);
        this.setListAdapter(adapter);
        lv = getListView();

        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Depense depenseAModifier = (Depense) lv.getAdapter().getItem(
                        position);
                Intent intent = new Intent(getApplicationContext(),
                        AjouterDepenseActivity.class);
                intent.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                        idProjet);
                intent.putExtra(ID_DEPENSE, depenseAModifier.getId());
                startActivityForResult(intent, REQUEST_AJOUTER_DEPENSE);

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
        if (ajouterNouveauDepenseButton.isPressed()) {
            Intent intent = new Intent(this, AjouterDepenseActivity.class);
            intent.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
            // startActivity(intent);
            startActivityForResult(intent, REQUEST_AJOUTER_DEPENSE);
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
        getMenuInflater().inflate(R.menu.gestion_depense, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_AJOUTER_DEPENSE) {
                List<Depense> values = new ArrayList<Depense>();
                try {
                    values = databaseHandler.getAllDepense(idProjet);
                } catch (Exception e) {
                    Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                // Binding resources Array to ListAdapter
                DepenseArrayAdapter adapter = new DepenseArrayAdapter(this,
                        values);
                this.setListAdapter(adapter);
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
                    ArrayAdapter<Depense> adapter = (ArrayAdapter<Depense>) getListAdapter();

			/* Get the selected item out of the Adapter by its position. */

                    Depense depenseASupprimer = (Depense) lv.getAdapter().getItem(
                            menuInfo.position);

                    databaseHandler.deleteDepense(depenseASupprimer);
            /* Remove it from the list. */
                    adapter.remove(depenseASupprimer);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(this, "Une erreur est arrivée: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true; /* true means: "we handled the event". */
            case CONTEXTMENU_MODIFYITEM:
                try {
                    Depense depenseAModifier = (Depense) lv.getAdapter().getItem(
                            menuInfo.position);
                    Intent i = new Intent(getApplicationContext(),
                            AjouterDepenseActivity.class);
                    // sending data to new activity
                    i.putExtra(ID_DEPENSE, depenseAModifier.getId());
                    i.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
                    startActivityForResult(i, REQUEST_AJOUTER_DEPENSE);
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
            case R.id.gestion_depense_synthese_menu:
                Intent pageSynthese = new Intent(getApplicationContext(),
                        SyntheseActivity.class);
                pageSynthese.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                        idProjet);
                startActivity(pageSynthese);
                super.finish();
                return true;
            case R.id.gestion_depense_participant_menu:
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
