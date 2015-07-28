package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import com.appspot.futsalapp_1008.pdE2015.model.Gruppo;
import com.appspot.futsalapp_1008.pdE2015.model.InfoIscrittoGestisceBean;

import com.appspot.futsalapp_1008.pdE2015.model.PartitaBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.EstIscrittoAT;
import com.pde2015.futsalapp.asynctasks.GetGruppoAT;
import com.pde2015.futsalapp.asynctasks.ListaPartiteProposteConfermateAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.EstIscrittoTC;
import com.pde2015.futsalapp.taskcallbacks.GetGruppoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaPartiteProposteConfermateTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListMatches;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GruppoActivity extends AppCompatActivity implements AggiornaStatoTC, SessioneIndietroTC,
        EstIscrittoTC, ListaPartiteProposteConfermateTC, GetGruppoTC, View.OnClickListener {

    private static final String TAG = "GruppoActivity";

    private final static String statoCorrente = AppConstants.GRUPPO;
    String emailUtente;
    private Long idSessione;
    private Long idGruppo;
    boolean estIscritto = false;
    String nomeGruppo, emailAdmin, citta;
    com.google.api.client.util.DateTime dataCreazione;
    FloatingActionButton fab;

    List<PartitaBean> listaPartite = new ArrayList<PartitaBean>();

    String[] nomiChiPropone;
    com.google.api.client.util.DateTime[] datePartite;
    Float[] quote;
    Integer[] tipiPartite;

    long selectedMatch;
    CustomListMatches adapterMatches;

    TextView emptyText;
    private ListView myList;

    String nuovoStato;

    TextView nomeGruppoView, dataCreazioneView, adminView, cittaView;

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;

    LinkedList<Class> listaActivity;
    Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gruppo_non_iscritto);

        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        idGruppo = (Long)extras.get("idGruppo");
        emailUtente = (String)extras.get("email");
        Log.e(TAG, "emailUtente: "+emailUtente);

        InfoIscrittoGestisceBean iscrittoBean = new InfoIscrittoGestisceBean();
        iscrittoBean.setGruppo(idGruppo);
        iscrittoBean.setGiocatore(emailUtente);

        if(checkNetwork()) new EstIscrittoAT(getApplicationContext(), this, GruppoActivity.this, idSessione, iscrittoBean).execute();

        if(checkNetwork()) new GetGruppoAT(getApplicationContext(), this, GruppoActivity.this, idSessione, idGruppo).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_gruppo_non_iscritto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if( estIscritto ){
            if( id == R.id.add_user ){
                if(checkNetwork()){
                    PayloadBean p = new PayloadBean();
                    p.setIdSessione(idSessione);
                    p.setNuovoStato(AppConstants.INVITO);
                    this.nuovoStato = AppConstants.INVITO;
                    new AggiornaStatoAT(getApplicationContext(), this, idSessione, GruppoActivity.this).execute(p);
                }
                return true;
            }
            if( id == R.id.lista_iscritti_gruppo){
                if(checkNetwork()){
                    this.nuovoStato = AppConstants.ISCRITTI_GRUPPO;
                    PayloadBean p = new PayloadBean();
                    p.setIdSessione(idSessione);
                    p.setNuovoStato(AppConstants.ISCRITTI_GRUPPO);
                    new AggiornaStatoAT(getApplicationContext(), this, idSessione, GruppoActivity.this).execute(p);
                }
                return true;
            }
            if( id == R.id.storico_partite){
                if(checkNetwork()){
                    this.nuovoStato = AppConstants.STORICO;
                    PayloadBean p = new PayloadBean();
                    p.setIdSessione(idSessione);
                    p.setNuovoStato(AppConstants.STORICO);
                    new AggiornaStatoAT(getApplicationContext(), this, idSessione, GruppoActivity.this).execute();
                }
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        // Disabilitare Bottoni
        // Abilitare rondella
        if(checkNetwork()) {
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
            alert.showAlertDialog(GruppoActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

    //Done per estIscritto
    public void done(boolean res, boolean estIscritto){
        if(res && estIscritto){

            this.estIscritto = true;
            setContentView(R.layout.activity_gruppo);
            getMenuInflater().inflate(R.menu.menu_gruppo, this.menu);

             /* VISUALIZZO ACTION BAR CON LOGO */
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setLogo(R.drawable.logo_small);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            myList = (ListView)findViewById(R.id.group_lista_partite);
            fab = (FloatingActionButton)findViewById(R.id.group_create_match);
            fab.setOnClickListener(this);

            if(checkNetwork()) new ListaPartiteProposteConfermateAT(getApplicationContext(), this, idGruppo, idSessione, 0, GruppoActivity.this).execute();

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    PayloadBean p = new PayloadBean();
                    p.setIdSessione(idSessione);
                    p.setNuovoStato(AppConstants.PARTITA);
                    if (checkNetwork()) {
                        nuovoStato = AppConstants.PARTITA;
                        new AggiornaStatoAT(myList.getContext(), (AggiornaStatoTC) myList.getContext(), idSessione, (PrincipaleActivity) myList.getContext()).execute(p);
                    }
                    selectedMatch = listaPartite.get(position).getPartita().getId();

                }
            });
        }
    }

    public void onClick(View view) {
        //Toast.makeText(getApplicationContext(), "Id: "+view.getId(), Toast.LENGTH_LONG);
        switch (view.getId()) {
            case R.id.group_create_match:
                Log.e(TAG, "Bottone Crea Partita cliccato!");
                this.nuovoStato = AppConstants.CREA_PARTITA;
                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.CREA_PARTITA);
                if (checkNetwork())
                    new AggiornaStatoAT(getApplicationContext(), this, idSessione, GruppoActivity.this).execute(p);
                break;
        }
    }

    //Done per aggiornaStato
    public void done(boolean res){
        if(res){
            if(this.nuovoStato.equals(AppConstants.CREA_PARTITA)){
                Log.e(TAG, "Nel done di Aggiorna Stato per Crea Partita");
                Intent myIntent = new Intent(getApplicationContext(), CreaPartitaActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            }
            else if(this.nuovoStato.equals(AppConstants.ISCRITTI_GRUPPO)){
                Intent myIntent = new Intent(getApplicationContext(), IscrittiGruppoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                myIntent.putExtra("nomeGruppo", nomeGruppo);
                startActivity(myIntent);
                this.finish();
            }
            else if(this.nuovoStato.equals(AppConstants.INVITO)){
                Intent myIntent = new Intent(getApplicationContext(), InvitoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            }/*
            else if(this.nuovoStato.equals(AppConstants.PARTITA)){
                Intent myIntent = new Intent(getApplicationContext(), PartitaActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                //TODO: devo passare l'id della partita
                startActivity(myIntent);
                this.finish();
            }*/
            else if(this.nuovoStato.equals(AppConstants.STORICO)){
                Intent myIntent = new Intent(getApplicationContext(), StoricoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            }
            //TODO vedi esci_gruppo
        }
    }

    //Done per listaPartiteProposteConfermate
    public void done(boolean res, List<PartitaBean> listaPartite){
        if( res && listaPartite != null){
            this.listaPartite = listaPartite;

            //Chiamo l'adapter
            adapterMatches = new CustomListMatches(GruppoActivity.this, listaPartite);

            nomiChiPropone = new String[listaPartite.size()];
            datePartite = new com.google.api.client.util.DateTime[listaPartite.size()];
            quote = new Float[listaPartite.size()];
            tipiPartite = new Integer[listaPartite.size()];

            for(int i = 0; i < listaPartite.size(); i++){
                nomiChiPropone[i] = listaPartite.get(i).getPartita().getPropone();
                datePartite[i] = listaPartite.get(i).getPartita().getDataOraPartita();
                quote[i] = listaPartite.get(i).getPartita().getQuota();
                tipiPartite[i] = listaPartite.get(i).getTipo();
            }

            myList = (ListView)findViewById(R.id.group_lista_partite);
            emptyText = (TextView) findViewById(android.R.id.empty);
            myList.setEmptyView(emptyText);
            myList.setAdapter(adapterMatches);
        }
    }

    //Done per sessioneIndietro
    public void done(boolean res, String nuovoStato){
        if(res) {
            if(nuovoStato.equals(AppConstants.RICERCA_GRUPPO)) {
                Intent myIntent = new Intent(getApplicationContext(), RicercaGruppoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                startActivity(myIntent);
                this.finish();
            }
            if(nuovoStato.equals(AppConstants.PRINCIPALE)){
                Intent myIntent = new Intent(getApplicationContext(), PrincipaleActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

    //Done per getGruppo
    public void done(boolean res, Gruppo infoGruppo){
        if(res && infoGruppo != null){
            this.nomeGruppo = infoGruppo.getNome();
            //TODO: come la prendiamo la mail dell'admin?
            this.emailAdmin = "Nome Admin";
            this.citta = infoGruppo.getCitta();
            this.dataCreazione = infoGruppo.getDataCreazione();

            this.nomeGruppoView = (TextView)findViewById(R.id.group_group_name);
            this.dataCreazioneView = (TextView)findViewById(R.id.group_date_creation);
            this.adminView = (TextView)findViewById(R.id.group_admin);
            this.cittaView = (TextView)findViewById(R.id.group_city_name);

            this.nomeGruppoView.setText(this.nomeGruppo);
            this.adminView.setText(emailAdmin);
            this.dataCreazioneView.setText(dataCreazione.toString());
            this.cittaView.setText(citta);
        }
    }
}
