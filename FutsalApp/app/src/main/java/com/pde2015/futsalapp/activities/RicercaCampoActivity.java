package com.pde2015.futsalapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;

import com.appspot.futsalapp_1008.pdE2015.model.Campo;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.ListaCampiAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaCampiTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListCampi;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RicercaCampoActivity extends AppCompatActivity implements ListaStatiTC, AggiornaStatoTC, SessioneIndietroTC, ListaCampiTC, View.OnClickListener{

    private CustomListCampi adapterCampi;

    //Dati Intent
    private Long idSessione, idGruppo, idPartita;
    String emailUtente;

    private static final String TAG = "RicercaGruppoActivity";
    private static final String fromAllCities = "";

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;
    LinkedList<Class> listaActivity;

    //List rendering stuff
    private List<Campo> campilist = new ArrayList<Campo>();

    String[] nomicampi;
    String[] citta;
    Float[] prezzi;
    Long[] ids;

    // Filtered list of names and urls
    private ArrayList<String> partialNames = new ArrayList<String>();
    private ArrayList<Float> partialPrices = new ArrayList<Float>();
    private ArrayList<String> partialCities = new ArrayList<String>();
    private ArrayList<Long> partialIds = new ArrayList<Long>();

    // List of names matching criteria are listed here
    private ListView myList;
    // Field where user enters his search criteria
    private EditText nameCapture;
    // Appare questo fino a quando listaGruppiAT non chiama done()
    TextView emptyText;
    // Continua a girare fino a quando listaGruppiAT non chiama done()
    CircularProgressView progressView;

    FloatingActionButton creaCampoButton;

    boolean hasClicked;
    String statoSuccessivo;

    long selectedCampo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_campo);

        progressView = (CircularProgressView)findViewById(R.id.progress_view);

        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");
        idGruppo = (Long)extras.get("idGruppo");
        idPartita = (Long)extras.get("idPartita");

        /// Ottenimento stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        // Prendo la lista dal server
        if(checkNetwork()) new ListaCampiAT(getApplicationContext(), this, this).execute();

        myList = (ListView)findViewById(R.id.listView1);

        nameCapture = (EditText) findViewById(R.id.name);
        nameCapture.setHint("Digita il nome del gruppo da cercare");
        //this.nameCapture.setFocusable(false);
        //this.nameCapture.setClickable(false);
        emptyText = (TextView) findViewById(android.R.id.empty);
        myList.setEmptyView(emptyText);
        myList.setAdapter(adapterCampi);

        creaCampoButton = (FloatingActionButton)findViewById(R.id.create_campo);

        nameCapture.addTextChangedListener(new TextWatcher() {

            // As the user types in the search field, the list is
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                AlterAdapterCampi();
            }

            // Not used for this program
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            // Not uses for this program
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // Auto-generated method stub
            }
        });

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* lancio activity dettagli pianta */
                Log.e(TAG, "position: " + position + " grouplist[position]: " + campilist.get(position));
                selectedCampo = campilist.get(position).getId();

                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.CAMPO);
                if(checkNetwork()){
                    statoSuccessivo = AppConstants.CAMPO;
                    new AggiornaStatoAT(myList.getContext(), (AggiornaStatoTC)myList.getContext(), idSessione, (Activity)myList.getContext()).execute(p);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ricerca_campo, menu);
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
        this.statoSuccessivo = AppConstants.PARTITA;
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
            alert.showAlertDialog(RicercaCampoActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

    private void AlterAdapterCampi() {
        // Se l'utente svuota l'edittext, mostro tutti i gruppi aperti esistenti
        if (nameCapture.getText().toString().isEmpty()) {
            partialNames.clear();
            partialPrices.clear();
            partialCities.clear();
            partialIds.clear();

            campilist.clear();

            for(int i = 0; i < nomicampi.length; i++){
                partialNames.add(i, nomicampi[i]);
                partialCities.add(i, citta[i]);
                partialPrices.add(i, prezzi[i]);
                partialIds.add(i, ids[i]);

                Campo c = new Campo();
                c.setNome(nomicampi[i]);
                c.setPrezzo(prezzi[i]);
                c.setCitta(citta[i]);
                c.setId(ids[i]);
                campilist.add(c);
            }
            adapterCampi.notifyDataSetChanged();
        }
        else {

            partialNames.clear();
            partialPrices.clear();
            partialCities.clear();
            partialIds.clear();

            campilist.clear();
            int j = 0;
            for (int i = 0; i < nomicampi.length; i++) {
                //ToUpperCase per fare ricerca case-insensitive
                if (nomicampi[i].toString().toUpperCase().contains(nameCapture.getText().toString().toUpperCase())) {
                    partialNames.add(j, nomicampi[i]);
                    partialCities.add(j, citta[i]);
                    partialPrices.add(j, prezzi[i]);
                    partialIds.add(j, ids[i]);

                    Campo c = new Campo();
                    c.setNome(nomicampi[i]);
                    c.setPrezzo(prezzi[i]);
                    c.setCitta(citta[i]);
                    c.setId(ids[i]);
                    campilist.add(c);
                    j++;
                }
                else
                    continue;
                adapterCampi.notifyDataSetChanged();
            }
        }

        if(adapterCampi.getCount()==0){
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("Nessun campo trovato.");
        }
    }

    /**************************
     *** INTERAZIONE SERVER ***
     **************************/

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
            if( statoSuccessivo.equals(AppConstants.CAMPO)) {
                Intent myIntent = new Intent(RicercaCampoActivity.this, CampoActivity.class);
                myIntent.putExtra("idCampo", selectedCampo);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("idPartita", idPartita);
                myIntent.putExtra("idGruppo", idGruppo);
                myIntent.putExtra("email", emailUtente);
                startActivity(myIntent);
                this.finish();
            }
            if(statoSuccessivo.equals(AppConstants.CREA_CAMPO)) {
                Intent myIntent = new Intent(RicercaCampoActivity.this, CreaCampoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("idPartita", idPartita);
                myIntent.putExtra("idGruppo", idGruppo);
                myIntent.putExtra("email", emailUtente);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

    //Done per sessioneIndietro
    public void done(boolean res, String nuovoStato){
        if(res) {
            if(nuovoStato.equals(AppConstants.PARTITA)){
                Intent myIntent = new Intent(getApplicationContext(), PartitaActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                myIntent.putExtra("idPartita", idPartita);
                startActivity(myIntent);
                this.finish();
            }
            if(nuovoStato.equals(AppConstants.CREA_CAMPO)) {
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

    //Done per listaCampi
    public void done(boolean res, List<Campo> listaCampi){
        if( res && listaCampi != null ){
            this.campilist = listaCampi;

            //Chiamo l'adapter
            adapterCampi = new CustomListCampi(RicercaCampoActivity.this, campilist);

            nomicampi = new String[campilist.size()];
            prezzi = new Float[campilist.size()];
            citta = new String[campilist.size()];
            ids = new Long[campilist.size()];

            for(int i = 0; i < campilist.size(); i++){
                nomicampi[i] = campilist.get(i).getNome();
                prezzi[i] = campilist.get(i).getPrezzo();
                citta[i] = campilist.get(i).getCitta();
                ids[i] = campilist.get(i).getId();
            }

            myList = (ListView)findViewById(R.id.listView1);
            emptyText = (TextView) findViewById(android.R.id.empty);
            myList.setEmptyView(emptyText);
            myList.setAdapter(adapterCampi);
        }

        //this.nameCapture.setFocusable(true);
        //this.nameCapture.setClickable(true);
        this.progressView.setVisibility(View.GONE);

        //L'ho messo qui e non in onCreate() per evitare che l'utente possa avviare creaCampo
        // prima che listaCampi abbia terminato, per evitare eventuali danni.
        creaCampoButton = (FloatingActionButton)findViewById(R.id.create_campo);
        creaCampoButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        //Toast.makeText(getApplicationContext(), "Id: "+view.getId(), Toast.LENGTH_LONG);
        switch (view.getId()) {
            case R.id.create_campo:
                Log.e(TAG, "Bottone Crea Campo cliccato!");
                this.statoSuccessivo = AppConstants.CREA_CAMPO;
                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.CREA_CAMPO);
                if (checkNetwork())
                    new AggiornaStatoAT(getApplicationContext(), this, idSessione, RicercaCampoActivity.this).execute(p);
                break;
        }
    }
}
