package com.pde2015.futsalapp.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.futsalapp_1008.pdE2015.model.Giocatore;
import com.pde2015.futsalapp.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Roberto on 30/07/2015.
 */
public class CustomListClassifica  extends ArrayAdapter<Giocatore>
{
    private final Activity context;
    private final List<Giocatore> users;
    private final List<Integer> voti;
    private final String emailUtente;

    public CustomListClassifica(Activity context, List<Giocatore> users, List<Integer> voti, String emailUtente) {
        super(context, R.layout.elemento_lista_iscritti, users);
        this.context = context;
        this.users = users;
        this.voti = voti;
        this.emailUtente = emailUtente;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Giocatore g = users.get(position);
        int v = voti.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.elemento_lista_classifica, null, true);
        TextView userName = (TextView) rowView.findViewById(R.id.elemento_classifica_username);
        ImageView userPic = (ImageView) rowView.findViewById(R.id.elemento_classifica_user_pic);
        TextView you = (TextView) rowView.findViewById(R.id.elemento_classifica_you);
        TextView voto = (TextView) rowView.findViewById(R.id.elemento_classifica_voti);

        userName.setText(users.get(position).getNome());
        voto.setText(voto.getText() + voti.get(position).toString());
        if( users.get(position).getEmail().equals(this.emailUtente) ) you.setText("Tu");

        Picasso.with(getContext())
                .load(users.get(position).getFotoProfilo())
                .transform(new CircleTransform())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(userPic);

        return rowView;
    }
}

