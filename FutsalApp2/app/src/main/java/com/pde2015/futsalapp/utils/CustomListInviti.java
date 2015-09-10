package com.pde2015.futsalapp.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.R;
import com.appspot.futsalapp_1008.pdE2015.model.InfoInvitoBean;
import com.pde2015.futsalapp.activities.ProfileActivity;
import com.pde2015.futsalapp.asynctasks.RispondiInvitoAT;
import com.pde2015.futsalapp.taskcallbacks.RispondiInvitoTC;
import com.pde2015.futsalapp.utils.ConnectionDetector;

import java.util.List;

/**
 * Created by Fabrizio on 27/07/2015.
 */
public class CustomListInviti extends ArrayAdapter<InfoInvitoBean> {
    private static final String TAG = "CustomListInviti";
    private final ProfileActivity context;
    private final View convertView;
    private final RispondiInvitoTC taskCallback;
    private final List<InfoInvitoBean> inviti;
    private AlertDialogManager alert = new AlertDialogManager();
    private ListView myList;
    private Long idSessione;
    private ConnectionDetector cd;
    private CircularProgressView cpv;

    public CustomListInviti(ProfileActivity context, RispondiInvitoTC taskCallback, List<InfoInvitoBean> inviti, ListView myList, Long idSessione, View convertView) {
        super(context, R.layout.invite_list_item, inviti);
        this.context = context;
        this.taskCallback = taskCallback;
        this.inviti = inviti;
        this.myList = myList;
        this.idSessione = idSessione;
        this.convertView = convertView;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.e(TAG, "Nella GetView con position: "+position);
        final InfoInvitoBean i = inviti.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.invite_list_item, null, true);

        TextView nomeGruppo = (TextView)rowView.findViewById(R.id.nome_gruppo);
        nomeGruppo.setText(i.getNomeGruppo());
        TextView mittente = (TextView)rowView.findViewById(R.id.mittente_invito);
        mittente.setText(i.getEmailMittente());
        cpv = (CircularProgressView)convertView.findViewById(R.id.progress_view);


        ImageButton accept = (ImageButton)rowView.findViewById(R.id.accetta_invito);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = myList.getPositionForView(v);
                if (position != ListView.INVALID_POSITION) {
                    InfoInvitoBean inv = i.clone();
                    //cpv = (CircularProgressView)context.findViewById(R.id.progress_view);
                    cpv.setVisibility(View.VISIBLE);
                    if(checkNetwork()) new RispondiInvitoAT(context.getApplicationContext(), taskCallback, context, idSessione, inv.getIdInvito(), inv.getIdGruppo(), true).execute();
                }
            }
        });

        ImageButton decline = (ImageButton)rowView.findViewById(R.id.rifiuta_invito);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = myList.getPositionForView(v);
                if (position != ListView.INVALID_POSITION) {
                    InfoInvitoBean inv = i.clone();
                    //cpv = (CircularProgressView)context.findViewById(R.id.progress_view);
                    cpv.setVisibility(View.VISIBLE);
                    if(checkNetwork()) new RispondiInvitoAT(context.getApplicationContext(), taskCallback, context, idSessione, inv.getIdInvito(), inv.getIdGruppo(), false).execute();
                    remove(inv);
                    //TODO cancellare anche l'item dalla ListView (remove(v)?)
                }
            }
        });

        return rowView;
    }

    public boolean checkNetwork() {
        cd = new ConnectionDetector(context.getApplicationContext());
        // Check if Internet present
        if (cd.isConnectingToInternet()) {
            return true;
        }else{
            // Internet Connection is not present
            alert.showAlertDialog(context.getApplicationContext(),
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

}
