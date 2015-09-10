package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.appspot.futsalapp_1008.pdE2015.model.Campo;
import com.appspot.futsalapp_1008.pdE2015.model.InfoPressoBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.GetCampoAT;
import com.pde2015.futsalapp.asynctasks.ImpostaCampoAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.GetCampoTC;
import com.pde2015.futsalapp.taskcallbacks.ImpostaCampoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListDays;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CampoActivity extends AppCompatActivity implements ImpostaCampoTC, AggiornaStatoTC, SessioneIndietroTC, GetCampoTC, ListaStatiTC {

    private static final String TAG = "CampoActivity";
    String emailUtente;
    private Long idSessione, idGruppo, idPartita, idCampo;
    Campo campo;

    List<String> listaGiorniChiusura;
    String[] giorniChiusura;

    CustomListDays adapterDays;

    TextView emptyText;
    private ListView myList;

    String nuovoStato;

    TextView nomeCampoView, prezzoView, cittaView, indirizzoView;
    // Continua a girare fino a quando listaGruppiAT non chiama done()
    CircularProgressView progressView;

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;

    LinkedList<Class> listaActivity;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campo);

        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");
        idGruppo = (Long)extras.get("idGruppo");
        idPartita = (Long)extras.get("idPartita");
        idCampo = (Long)extras.get("idCampo");

        Log.e(TAG, "idSessione: "+idSessione);
        Log.e(TAG, "email: "+emailUtente);
        Log.e(TAG, "idGruppo: "+idGruppo);
        Log.e(TAG, "idPartita: "+idPartita);
        Log.e(TAG, "idCampo: "+idCampo);

        progressView = (CircularProgressView)findViewById(R.id.progress_view);

        // Ottenimento stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        // Prendo la lista dal server
        if(checkNetwork()) new GetCampoAT(getApplicationContext(), this, idSessione, idCampo, this).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_campo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.select_campo_confirm) {
            InfoPressoBean pressoBean = new InfoPressoBean();
            pressoBean.setCampo(idCampo);
            pressoBean.setPartita(idPartita);
            if(checkNetwork()){
                this.progressView.setVisibility(View.VISIBLE);
                new ImpostaCampoAT(getApplicationContext(), this, this, idSessione, pressoBean).execute();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean checkNetwork() {
        cd = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        if (cd.isConnectingToInternet()) {
            return true;
        }else{
            // Internet Connection is not present
            alert.showAlertDialog(CampoActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

    public void onBackPressed(){
        // Disabilitare Bottoni
        if(checkNetwork()) {
            this.progressView.setVisibility(View.VISIBLE);
            PayloadBean bean = new PayloadBean();
            bean.setIdSessione(idSessione);
            new SessioneIndietroAT(getApplicationContext(), this, this, idSessione).execute(bean);
        }
    }

    //Done per aggiornaStato
    public void done(boolean res){
        if(res) {
            if (this.nuovoStato.equals(AppConstants.PARTITA)) {
                Intent myIntent = new Intent(getApplicationContext(), PartitaActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                myIntent.putExtra("idPartita", idPartita);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

    //Done per sessioneIndietro
    public void done(boolean res, String nuovoStato){
        if(res) {
            if(nuovoStato.equals(AppConstants.RICERCA_CAMPO)) {
                Intent myIntent = new Intent(getApplicationContext(), RicercaCampoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                myIntent.putExtra("idPartita", idPartita);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

    //Done per getCampo
    public void done(boolean res, Campo campo){
        if(res && campo != null){
            this.campo = campo;

            this.nomeCampoView = (TextView)findViewById(R.id.campo_name);
            this.cittaView = (TextView)findViewById(R.id.campo_city);
            this.indirizzoView = (TextView)findViewById(R.id.campo_address);
            this.prezzoView = (TextView)findViewById(R.id.campo_price);

            this.nomeCampoView.setText(campo.getNome());
            this.cittaView.setText(campo.getCitta());
            this.indirizzoView.setText(campo.getIndirizzo());
            this.prezzoView.setText(campo.getPrezzo().toString());

            adapterDays = new CustomListDays(CampoActivity.this, campo.getGiorniChiusura());
            myList = (ListView)findViewById(R.id.campo_days);
            emptyText = (TextView) findViewById(android.R.id.empty);
            myList.setEmptyView(emptyText);
            myList.setAdapter(adapterDays);

            this.progressView.setVisibility(View.GONE);
        }
    }

    //Done per listaStati
    public void done(boolean res, List<String> listaStati, String tipoDone ) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
        }
    }

    //Done per impostaCampo
    public void done(boolean res, boolean impostato){
        if( res && res ){
            PayloadBean payload = new PayloadBean();
            payload.setIdSessione(idSessione);
            payload.setNuovoStato(AppConstants.PARTITA);
            if(checkNetwork()){
                this.nuovoStato = AppConstants.PARTITA;
                new AggiornaStatoAT(getApplicationContext(), this, idSessione, CampoActivity.this).execute(payload);
            }
        }
    }
}
