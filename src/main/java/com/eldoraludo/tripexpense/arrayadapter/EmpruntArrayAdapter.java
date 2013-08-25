package com.eldoraludo.tripexpense.arrayadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eldoraludo.tripexpense.R.id;
import com.eldoraludo.tripexpense.R.layout;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Emprunt;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.util.DateHelper;

import java.util.List;

public class EmpruntArrayAdapter extends ArrayAdapter<Emprunt> {
    private final Context context;
    private final List<Emprunt> emprunts;

    public EmpruntArrayAdapter(Context context, List<Emprunt> emprunts) {
        super(context, layout.ligne_emprunt, emprunts);
        this.context = context;
        this.emprunts = emprunts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DatabaseHandler databaseHandler = new DatabaseHandler(this.context);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View empruntLigneView = inflater.inflate(layout.ligne_emprunt,
                parent, false);
        TextView nomEmprunteurText = (TextView) empruntLigneView
                .findViewById(id.nomEmprunteur);
        TextView nomParticipantText = (TextView) empruntLigneView
                .findViewById(id.nomParticipant);
        TextView nomDeLEmpruntText = (TextView) empruntLigneView
                .findViewById(id.nomDeLEmprunt);

        TextView montantEtDateText = (TextView) empruntLigneView
                .findViewById(id.montantEtDateDeLEmprunt);
        Emprunt emprunt = emprunts.get(position);
        Participant emprunteur = databaseHandler.trouverLeParticipant(emprunt
                .getEmprunteurId());
        nomEmprunteurText.setText(emprunteur.getNom());
        montantEtDateText.setText(new StringBuilder()
                .append("a emprunté ")
                .append(emprunt.getMontant())
                .append(" euros le ")
                .append(DateHelper.prettyDate(emprunt.getDateEmprunt())).append(" à ").toString());
        Participant participant = databaseHandler.trouverLeParticipant(emprunt
                .getParticipantId());
        nomParticipantText.setText(participant.getNom());
        nomDeLEmpruntText.setText(new StringBuilder().append("(").append(emprunt.getNomEmprunt()).append(")").toString());

        return empruntLigneView;
    }
}