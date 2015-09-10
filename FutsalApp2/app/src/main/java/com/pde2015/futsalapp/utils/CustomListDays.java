package com.pde2015.futsalapp.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pde2015.futsalapp.R;

import java.util.List;

/**
 * Created by Roberto on 29/07/2015.
 */
public class CustomListDays extends ArrayAdapter<String>
{
    private final Activity context;
    private final List<String> days;

    public CustomListDays(Activity context, List<String> days) {
        super(context, R.layout.elemento_lista_giorni, days);
        this.context = context;
        this.days = days;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        String d = days.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.elemento_lista_giorni, null, true);
        TextView day = (TextView) rowView.findViewById(R.id.day);

        day.setText(d);

        return rowView;
    }
}