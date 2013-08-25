package com.eldoraludo.tripexpense.arrayadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eldoraludo.tripexpense.R;
import com.eldoraludo.tripexpense.R.id;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.util.DateHelper;

import java.util.List;

public class DepenseArrayAdapter extends ArrayAdapter<Depense> {
    private final Context context;
    private final List<Depense> values;

    public DepenseArrayAdapter(Context context, List<Depense> values) {
        super(context, R.layout.ligne_depense, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DatabaseHandler databaseHandler = new DatabaseHandler(this.context);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View depenseLigneView = inflater.inflate(R.layout.ligne_depense,
                parent, false);
        TextView nomParticipantText = (TextView) depenseLigneView
                .findViewById(R.id.nomParticipant);
        TextView montantEtDatesText = (TextView) depenseLigneView
                .findViewById(id.montantEtDatesDeLaDepense);
        TextView nomDeLaDepenseText = (TextView) depenseLigneView
                .findViewById(R.id.nomDeLaDepense);

        Depense depense = values.get(position);
        Participant participant = databaseHandler.trouverLeParticipant(depense
                .getParticipantId());
        nomParticipantText.setText(participant.getNom());
        montantEtDatesText.setText(new StringBuilder()
                .append("a dépensé ")
                .append(depense.getMontant())
                .append(" euros entre le ")
                .append(DateHelper.prettyDate(depense.getDateDebut()))
                .append(" et le ")
                .append(DateHelper.prettyDate(depense.getDateFin())).toString());
        nomDeLaDepenseText.setText(new StringBuilder().append("(").append(depense.getNomDepense()).append(")").toString());
        return depenseLigneView;
    }
}