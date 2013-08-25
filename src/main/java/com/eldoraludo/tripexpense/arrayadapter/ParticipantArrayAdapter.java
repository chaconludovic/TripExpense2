package com.eldoraludo.tripexpense.arrayadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eldoraludo.tripexpense.R;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.util.DateHelper;

import java.util.List;

public class ParticipantArrayAdapter extends ArrayAdapter<Participant> {
    private final Context context;
    private final List<Participant> values;

    public ParticipantArrayAdapter(Context context, List<Participant> values) {
        super(context, R.layout.ligne_participant, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View depenseLigneView = inflater.inflate(R.layout.ligne_participant,
                parent, false);
        TextView nomParticipantText = (TextView) depenseLigneView
                .findViewById(R.id.nomParticipant);
        TextView dateText = (TextView) depenseLigneView.findViewById(R.id.date);

        Participant participant = values.get(position);
        nomParticipantText.setText(participant.getNom());
        dateText.setText(new StringBuilder()
                .append("Arrivé le ")
                .append(DateHelper.prettyDate(participant.getDateArrive()))
                .append(" et départ le ")
                .append(DateHelper.prettyDate(participant.getDateDepart())).toString());

        return depenseLigneView;
    }
}