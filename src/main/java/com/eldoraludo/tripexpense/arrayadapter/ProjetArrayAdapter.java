package com.eldoraludo.tripexpense.arrayadapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eldoraludo.tripexpense.R;
import com.eldoraludo.tripexpense.entite.Projet;

public class ProjetArrayAdapter extends ArrayAdapter<Projet> {
	private final Context context;
	private final List<Projet> values;

	public ProjetArrayAdapter(Context context, List<Projet> values) {
		super(context, R.layout.ligne_projet, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View depenseLigneView = inflater.inflate(R.layout.ligne_projet, parent,
				false);
		TextView nomProjetText = (TextView) depenseLigneView
				.findViewById(R.id.projetText);

		Projet projet = values.get(position);
		nomProjetText.setText(projet.getNom());
		return depenseLigneView;
	}
}