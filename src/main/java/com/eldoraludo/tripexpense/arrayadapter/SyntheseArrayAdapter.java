package com.eldoraludo.tripexpense.arrayadapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eldoraludo.tripexpense.R;
import com.eldoraludo.tripexpense.dto.SyntheseDTO;
import com.eldoraludo.tripexpense.entite.Participant;

import java.io.InputStream;
import java.util.List;

public class SyntheseArrayAdapter extends ArrayAdapter<SyntheseDTO> {
    private final Context context;
    private final List<SyntheseDTO> values;

    public SyntheseArrayAdapter(Context context, List<SyntheseDTO> values) {
        super(context, R.layout.ligne_synthese, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View depenseLigneView = inflater.inflate(R.layout.ligne_synthese,
                parent, false);
        SyntheseDTO syntheseDTO = values.get(position);
        ImageView photoParticipant = (ImageView) depenseLigneView.findViewById(R.id.photo_participant);
        this.definirPhoto(syntheseDTO.getParticipant(), photoParticipant);
        TextView syntheseText = (TextView) depenseLigneView
                .findViewById(R.id.syntheseText);
        syntheseText.setText(new StringBuilder()
                .append(syntheseDTO.getParticipant().getNom())
                .append(" doit la somme de ")
                .append(syntheseDTO.getMontant())
                .append(" euros à ").append(syntheseDTO.getDepenseur().getNom()).toString());
        ImageView photoDepenseur = (ImageView) depenseLigneView.findViewById(R.id.photo_depenseur);
        this.definirPhoto(syntheseDTO.getDepenseur(), photoDepenseur);
        return depenseLigneView;
    }

    private void definirPhoto(Participant participant, ImageView photoImage) {
        String contactPhoneId = participant.getContactPhoneId();
        if (contactPhoneId == null) {
            photoImage.setVisibility(View.INVISIBLE);
            return;
        }
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contactPhoneId));
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                contactUri);
        if (inputStream != null) {
            try {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream), 220, 220, false);
                photoImage.setImageBitmap(scaledBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            photoImage.setVisibility(View.INVISIBLE);
        }
    }


}