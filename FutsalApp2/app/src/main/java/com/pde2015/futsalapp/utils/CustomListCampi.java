package com.pde2015.futsalapp.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.futsalapp_1008.pdE2015.model.Campo;
import com.pde2015.futsalapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Roberto on 28/07/2015.
 */
public class CustomListCampi extends ArrayAdapter<Campo>
{
    private final Activity context;
    private final List<Campo> campi;

    public CustomListCampi(Activity context, List<Campo> campi) {
        super(context, R.layout.elemento_lista_campi, campi);
        this.context = context;
        this.campi = campi;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Campo c = campi.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.elemento_lista_campi, null, true);
        TextView campoName = (TextView) rowView.findViewById(R.id.campo_name);
        ImageView campoLogo = (ImageView) rowView.findViewById(R.id.campo_logo);
        TextView citta = (TextView) rowView.findViewById(R.id.campo_city);
        TextView prezzo = (TextView) rowView.findViewById(R.id.campo_price);

        campoName.setText(/*campoName.getText() + */campi.get(position).getNome());
        citta.setText(citta.getText() + campi.get(position).getCitta());
        prezzo.setText(prezzo.getText() + campi.get(position).getPrezzo().toString());

        Picasso.with(getContext())
                .load(R.drawable.campo)
                .transform(new CircleTransform())
                .placeholder(R.drawable.campo)
                .error(R.drawable.campo)
                .into(campoLogo);

        return rowView;
    }
}