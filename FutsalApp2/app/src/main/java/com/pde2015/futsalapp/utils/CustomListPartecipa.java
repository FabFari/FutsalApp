package com.pde2015.futsalapp.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.appspot.futsalapp_1008.pdE2015.model.Giocatore;
import com.pde2015.futsalapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Fabrizio on 09/08/2015.
 */
public class CustomListPartecipa extends ArrayAdapter<Giocatore> {

    private final Activity context;
    private final List<Giocatore> users;
    private final String emailUtente;
    private final List<Integer> nAmici;

    public CustomListPartecipa(Activity context, List<Giocatore> users, List<Integer> nAmici, String emailUtente) {
        super(context, R.layout.elemento_lista_iscritti, users);
        this.context = context;
        this.users = users;
        this.nAmici = nAmici;
        this.emailUtente = emailUtente;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Giocatore g = users.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.elemento_lista_partecipa, null, true);
        TextView userName = (TextView) rowView.findViewById(R.id.elemento_lista_partecipa_username);
        ImageView userPic = (ImageView) rowView.findViewById(R.id.elemento_lista_partecipa_user_pic);
        TextView you = (TextView) rowView.findViewById(R.id.elemento_lista_partecipa_you);
        TextView amici = (TextView) rowView.findViewById(R.id.elemento_lista_partecipa_amici);

        userName.setText(users.get(position).getNome());
        if( users.get(position).getEmail().equals(this.emailUtente) ) you.setText("Tu");
        if(nAmici.get(position) > 0) amici.setText("+"+ nAmici.get(position));

        Picasso.with(getContext())
                .load(users.get(position).getFotoProfilo())
                .transform(new CircleTransform())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(userPic);

        return rowView;
    }

}
