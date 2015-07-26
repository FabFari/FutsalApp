package com.pde2015.futsalapp.utils;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.futsalapp_1008.pdE2015.model.InfoGruppoBean;
import com.pde2015.futsalapp.R;

import com.appspot.futsalapp_1008.pdE2015.model.Gruppo;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roberto on 23/07/2015.
 */
public class CustomListGroups extends ArrayAdapter<InfoGruppoBean>
{
    private final Activity context;
    private final List<InfoGruppoBean> groups;

    public CustomListGroups(Activity context, List<InfoGruppoBean> groups) {
        super(context, R.layout.elemento_lista_gruppi, groups);
        this.context = context;
        this.groups = groups;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        InfoGruppoBean g = groups.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.elemento_lista_gruppi, null, true);
        TextView groupName = (TextView) rowView.findViewById(R.id.group_name);
        ImageView groupLogo = (ImageView) rowView.findViewById(R.id.group_logo);
        TextView citta = (TextView) rowView.findViewById(R.id.group_city);

        groupName.setText(groups.get(position).getNome());
        citta.setText(groups.get(position).getCitta());

        if( g.getAperto()) {
            Picasso.with(getContext())
                    .load(R.drawable.group_open)
                    .transform(new CircleTransform())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(groupLogo);
        }
        else{
            Picasso.with(getContext())
                    .load(R.drawable.group_closed)
                    .transform(new CircleTransform())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(groupLogo);
        }
        return rowView;
    }
}