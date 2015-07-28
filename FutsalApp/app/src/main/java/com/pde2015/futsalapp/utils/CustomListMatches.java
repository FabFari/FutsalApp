package com.pde2015.futsalapp.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.futsalapp_1008.pdE2015.model.Partita;
import com.appspot.futsalapp_1008.pdE2015.model.PartitaBean;
import com.pde2015.futsalapp.R;

import java.util.List;

/**
 * Created by Roberto on 26/07/2015.
 */
public class CustomListMatches extends ArrayAdapter<PartitaBean> {

    private final Activity context;
    private final List<PartitaBean> matches;

    public CustomListMatches(Activity context, List<PartitaBean> matches) {
        super(context, R.layout.elemento_lista_gruppi, matches);
        this.context = context;
        this.matches = matches;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        PartitaBean p = matches.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.elemento_lista_partite, null, true);
        TextView nomeChiProponeView = (TextView) rowView.findViewById(R.id.match_view_nome_propone);
        TextView quotaView = (TextView) rowView.findViewById(R.id.match_view_quota);
        TextView dataView = (TextView)rowView.findViewById(R.id.match_view_data);
        TextView tipoPartitaView = (TextView)rowView.findViewById(R.id.match_view_type);

        if( p.getTipo() == 1 ) tipoPartitaView.setText("Partita di Calcetto");
        else if( p.getTipo() == 2 ) tipoPartitaView.setText("Partita di Calciotto");
        else if( p.getTipo() == 3 ) tipoPartitaView.setText("Partita di Calcio");

        nomeChiProponeView.setText(matches.get(position).getPartita().getPropone());
        dataView.setText(matches.get(position).getPartita().getDataOraPartita().toString());
        quotaView.setText(matches.get(position).getPartita().getQuota().toString());


        return rowView;
    }
}
