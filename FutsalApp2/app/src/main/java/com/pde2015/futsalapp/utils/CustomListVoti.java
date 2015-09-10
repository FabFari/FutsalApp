package com.pde2015.futsalapp.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.appspot.futsalapp_1008.pdE2015.model.VotoUomoPartita;
import com.pde2015.futsalapp.R;

import java.util.List;

/**
 * Created by Roberto on 02/08/2015.
 */
public class CustomListVoti extends ArrayAdapter<VotoUomoPartita> {

    private final Activity context;
    private final List<VotoUomoPartita> voti;
    private static final String noCommentText = "Nessun commento inserito.";

    public CustomListVoti(Activity context, List<VotoUomoPartita> voti) {
        super(context, R.layout.elemento_lista_gruppi, voti);
        this.context = context;
        this.voti = voti;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        VotoUomoPartita p = voti.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.elemento_lista_voti, null, true);
        TextView mittView = (TextView) rowView.findViewById(R.id.voto_from);
        TextView destView = (TextView) rowView.findViewById(R.id.voto_to);
        TextView commView = (TextView)rowView.findViewById(R.id.voto_comment);

        mittView.setText(voti.get(position).getVotanteUP());
        destView.setText(voti.get(position).getVotatoUP());

        if(p.getCommento() == null || p.getCommento().equals(""))
            commView.setText(noCommentText);
        else
            commView.setText("<<"+voti.get(position).getCommento()+">>");

        return rowView;
    }
}
