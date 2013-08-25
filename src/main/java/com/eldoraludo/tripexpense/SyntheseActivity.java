package com.eldoraludo.tripexpense;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.eldoraludo.tripexpense.arrayadapter.SyntheseArrayAdapter;
import com.eldoraludo.tripexpense.calculateur.SyntheseCalculateur;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.dto.SyntheseDTO;
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Emprunt;
import com.eldoraludo.tripexpense.entite.Participant;

import java.util.ArrayList;
import java.util.List;

public class SyntheseActivity extends ListActivity implements SensorEventListener {
    static final int PICK_DATE_REQUEST = 1;
    private static boolean CALCUL_EN_COURS = false;
    private Integer idProjet;
    private DatabaseHandler databaseHandler;
    private ChargementDepensesEnArrierePlan chargementDepensesEnArrierePlan;

    private String TAG = SyntheseActivity.class.getSimpleName();
    private static final int FORCE_THRESHOLD = 800;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;

    private SensorManager mSensorMgr;
    private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
    private long mLastTime;
    private OnShakeListener mShakeListener;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;

    public interface OnShakeListener {
        public void onShake();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synthese);

        Intent intent = getIntent();
        idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
                0);
        databaseHandler = new DatabaseHandler(this);
        // this.setListAdapter(new ArrayAdapter<String>(this,
        // android.R.layout.simple_list_item_1, new ArrayList<String>()));

        SyntheseArrayAdapter adapter = new SyntheseArrayAdapter(this,
                new ArrayList<SyntheseDTO>());
        this.setListAdapter(adapter);

        setupActionBar();
        chargementDepensesEnArrierePlan = new ChargementDepensesEnArrierePlan();
        chargementDepensesEnArrierePlan.execute();
    }

    public class ChargementDepensesEnArrierePlan extends
            AsyncTask<Void, String, List<SyntheseDTO>> {
        ProgressDialog progress;

        @Override
        protected void onPostExecute(List<SyntheseDTO> result) {
            progress.dismiss();
            // SyntheseActivity.this.setListAdapter(new ArrayAdapter<String>(
            // SyntheseActivity.this, android.R.layout.simple_list_item_1,
            // result));
            SyntheseArrayAdapter adapter = new SyntheseArrayAdapter(
                    SyntheseActivity.this, result);
            SyntheseActivity.this.setListAdapter(adapter);
            CALCUL_EN_COURS = false;
        }

        @Override
        protected void onPreExecute() {
            CALCUL_EN_COURS = true;
            progress = ProgressDialog.show(SyntheseActivity.this, "",
                    "Calcul des dépenses en cours", true);
        }

        @Override
        protected List<SyntheseDTO> doInBackground(Void... noParam) {
            return getListeDettes();
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
        getMenuInflater().inflate(R.menu.synthese, menu);
        if (databaseHandler.getParticipantsCount(idProjet) == 0) {
            MenuItem menuDepense = menu.findItem(R.id.gestion_synthese_depense_menu);
            menuDepense.setVisible(false);
            MenuItem menuEmprunt = menu.findItem(R.id.gestion_synthese_emprunt_menu);
            menuEmprunt.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.gestion_synthese_projet_menu:
                Intent pageFicheProjet = new Intent(getApplicationContext(),
                        FicheProjetActivity.class);
                pageFicheProjet.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
                startActivity(pageFicheProjet);
                return true;
            case R.id.gestion_synthese_participant_menu:
                Intent pageGestionParticipant = new Intent(getApplicationContext(),
                        GestionParticipantActivity.class);
                pageGestionParticipant.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
                startActivity(pageGestionParticipant);
                return true;
            case R.id.gestion_synthese_depense_menu:
                Intent pageDepense = new Intent(getApplicationContext(),
                        GestionDepenseActivity.class);
                pageDepense.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                        idProjet);
                startActivity(pageDepense);
                return true;
            case R.id.gestion_synthese_emprunt_menu:
                Intent pageEmprunt = new Intent(getApplicationContext(),
                        GestionEmpruntActivity.class);
                pageEmprunt.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                        idProjet);
                startActivity(pageEmprunt);
                return true;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
    }


    public void setOnShakeListener(OnShakeListener listener) {
        Log.d(TAG, "ShakeListener setOnShakeListener invoked---->");
        mShakeListener = listener;
    }

    public void resume() {
        mSensorMgr = (SensorManager) this
                .getSystemService(Context.SENSOR_SERVICE);
        if (mSensorMgr == null) {
            throw new UnsupportedOperationException("Sensors not supported");
        }
        boolean supported = false;
        try {
            supported = mSensorMgr.registerListener(this,
                    mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_GAME);
        } catch (Exception e) {
            Toast.makeText(this
                    , "Shaking not supported", Toast.LENGTH_LONG)
                    .show();
        }

        if ((!supported) && (mSensorMgr != null))
            mSensorMgr.unregisterListener(this);
    }

    public void pause() {
        if (mSensorMgr != null) {

            mSensorMgr.unregisterListener(this);
            mSensorMgr = null;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
        long now = System.currentTimeMillis();

        if ((now - mLastForce) > SHAKE_TIMEOUT) {
            mShakeCount = 0;
        }

        if ((now - mLastTime) > TIME_THRESHOLD) {
            long diff = now - mLastTime;
            float speed = Math.abs(event.values[SensorManager.DATA_X]
                    + event.values[SensorManager.DATA_Y]
                    + event.values[SensorManager.DATA_Z] - mLastX - mLastY
                    - mLastZ)
                    / diff * 10000;
            if (speed > FORCE_THRESHOLD && !CALCUL_EN_COURS) {
                if ((++mShakeCount >= SHAKE_COUNT)
                        && (now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    mShakeCount = 0;
                    Log.d(TAG, "ShakeListener mShakeListener---->" + mShakeListener);
                    if (mShakeListener != null) {
                        mShakeListener.onShake();
                    }
                    try {
                        chargementDepensesEnArrierePlan = new ChargementDepensesEnArrierePlan();
                        chargementDepensesEnArrierePlan.execute();
                    } catch (IllegalStateException e) {
                        // ok execution en cours
                    }
                }
                mLastForce = now;
            }
            mLastTime = now;
            mLastX = event.values[SensorManager.DATA_X];
            mLastY = event.values[SensorManager.DATA_Y];
            mLastZ = event.values[SensorManager.DATA_Z];
        }
    }
}
