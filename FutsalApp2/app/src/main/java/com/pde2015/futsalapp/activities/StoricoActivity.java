package com.pde2015.futsalapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.appspot.futsalapp_1008.pdE2015.model.PartitaBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.ListaPartiteTerminateAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaPartiteTerminateTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListMatches;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StoricoActivity extends AppCompatActivity implements ListaStatiTC, ListaPartiteTerminateTC, AggiornaStatoTC, SessioneIndietroTC {

    //Dati Intent
    private Long idSessione, idGruppo;
    String emailUtente;

    private static final String TAG = "RicercaGruppoActivity";
    private static final int allTypes = 0;

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;
    LinkedList<Class> listaActivity;

    List<PartitaBean> listaPartite = new ArrayList<PartitaBean>();

    String[] nomiChiPropone;
    com.google.api.client.util.DateTime[] datePartite;
    Float[] quote;
    Integer[] tipiPartite;
    String[] statoPartite;

    long selectedMatch;
    CustomListMatches adapterMatches;

    TextView emptyText;
    private ListView myList;

    String nuovoStato;

    CircularProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storico);

        progressView = (CircularProgressView)findViewById(R.id.progress_view);

        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");
        idGruppo = (Long)extras.get("idGruppo");

        /// Ottenimento stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        // Prendo la lista dal server
        if(checkNetwork()) new ListaPartiteTerminateAT(getApplicationContext(), this, idGruppo, idSessione, allTypes, this).execute();

        myList = (ListView)findViewById(R.id.storico_list);

        emptyText = (TextView) findViewById(android.R.id.empty);
        myList.setEmptyView(emptyText);
        myList.setAdapter(adapterMatches);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* lancio activity dettagli pianta */
                Log.e(TAG, "position: " + position + " grouplist[position]: " + listaPartite.get(position));
                selectedMatch = listaPartite.get(position).getPartita().getId();

                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.PARTITA);
                if (checkNetwork()) {
                    nuovoStato = AppConstants.PARTITA;
                    new AggiornaStatoAT(myList.getContext(), (AggiornaStatoTC) myList.getContext(), idSessione, (Activity) myList.getContext()).execute(p);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_storico, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        // Disabilitare Bottoni
        // Abilitare rondella
        if(checkNetwork()) {
            this.progressView.setVisibility(View.VISIBLE);
            PayloadBean bean = new PayloadBean();
            bean.setIdSessione(idSessione);
            new SessioneIndietroAT(getApplicationContext(), this, this, idSessione).execute(bean);
        }
    }

    public boolean checkNetwork() {
        cd = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        if (cd.isConnectingToInternet()) {
            return true;
        }else{
            // Internet Connection is not present
            alert.showAlertDialog(StoricoActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

    //Done per sessioneIndietro
    public void done(boolean res, String nuovoStato){
        if(res) {
            if(nuovoStato.equals(AppConstants.GRUPPO)){
                Intent myIntent = new Intent(getApplicationContext(), GruppoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

    //Done per listaPartiteGiocate
    public void done(boolean res, List<PartitaBean> listaPartite){
        progressView.setVisibility(View.GONE);
        if( res && listaPartite != null){
            this.listaPartite = listaPartite;

            //Chiamo l'adapter
            adapterMatches = new CustomListMatches(StoricoActivity.this, listaPartite);

            nomiChiPropone = new String[listaPartite.size()];
            datePartite = new com.google.api.client.util.DateTime[listaPartite.size()];
            quote = new Float[listaPartite.size()];
            tipiPartite = new Integer[listaPartite.size()];
            statoPartite = new String[listaPartite.size()];

            for(int i = 0; i < listaPartite.size(); i++){
                nomiChiPropone[i] = listaPartite.get(i).getPartita().getPropone();
                datePartite[i] = listaPartite.get(i).getPartita().getDataOraPartita();
                quote[i] = listaPartite.get(i).getPartita().getQuota();
                tipiPartite[i] = listaPartite.get(i).getTipo();
                statoPartite[i] = listaPartite.get(i).getPartita().getStatoCorrente();
            }

            myList = (ListView)findViewById(R.id.storico_list);
            emptyText = (TextView) findViewById(android.R.id.empty);
            myList.setEmptyView(emptyText);
            myList.setAdapter(adapterMatches);
        }
    }

    //Done per ListaStati
    public void done(boolean res, List<String> listaStati, String tipoDone ) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
        }
    }

    //Done per AggiornaStato
    public void done(boolean res)
    {
        if( res ) {
            //Che faccio?
            if( nuovoStato.equals(AppConstants.PARTITA)) {
                Log.e(TAG, "idSessione: "+idSessione+" email: "+emailUtente+" idGruppo: "+idGruppo);
                Intent i = new Intent(StoricoActivity.this, PartitaGiocataActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                i.putExtra("idPartita", selectedMatch);
                startActivity(i);
                this.finish();
            }
        }
    }

}
