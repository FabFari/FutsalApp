package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.app.Activity;

import com.appspot.futsalapp_1008.pdE2015.model.InfoGruppoBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.ListaGruppiAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaGruppiTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListGroups;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

public class RicercaGruppoActivity extends AppCompatActivity implements ListaStatiTC, AggiornaStatoTC, SessioneIndietroTC, ListaGruppiTC{

    private CustomListGroups adapterGroups;

    //Dati Intent
    private Long idSessione;
    String emailUtente;

    private static final String TAG = "RicercaGruppoActivity";
    private static final String fromAllCities = "";

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;
    LinkedList<Class> listaActivity;

    //List rendering stuff
    private List<InfoGruppoBean> groupslist = new ArrayList<InfoGruppoBean>();

    String[] nomigruppi;
    boolean[] aperti;
    String[] citta;
    Long[] ids;

    // Filtered list of names and urls
    private ArrayList<String> partialNames = new ArrayList<String>();
    private ArrayList<Boolean> partialTypes = new ArrayList<Boolean>();
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

    boolean hasClicked;
    String statoSuccessivo;

    long selectedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_gruppo);

        progressView = (CircularProgressView)findViewById(R.id.progress_view);

        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");

        // Ottenimento stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        // Prendo la lista dal server
        if(checkNetwork()) new ListaGruppiAT(getApplicationContext(), this, true, fromAllCities, this).execute();

        myList = (ListView)findViewById(R.id.listView1);

        nameCapture = (EditText) findViewById(R.id.name);
        nameCapture.setHint("Digita il nome del gruppo da cercare");
        //this.nameCapture.setFocusable(false);
        //this.nameCapture.setClickable(false);
        emptyText = (TextView) findViewById(android.R.id.empty);
        myList.setEmptyView(emptyText);
        myList.setAdapter(adapterGroups);

        nameCapture.addTextChangedListener(new TextWatcher() {

            // As the user types in the search field, the list is
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                AlterAdapterGroups();
            }

            // Not used for this program
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            // Not uses for this program
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
        });

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* lancio activity dettagli pianta */
                Log.e(TAG, "position: "+position+" grouplist[position]: "+groupslist.get(position));
                selectedGroup = groupslist.get(position).getId();

                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.GRUPPO);
                if(checkNetwork()){
                    statoSuccessivo = AppConstants.GRUPPO;
                    new AggiornaStatoAT(myList.getContext(), (AggiornaStatoTC)myList.getContext(), idSessione, (Activity)myList.getContext()).execute(p);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ricerca_gruppo, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public boolean checkNetwork() {
        cd = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        if (cd.isConnectingToInternet()) {
            return true;
        }else{
            // Internet Connection is not present
            alert.showAlertDialog(RicercaGruppoActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
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

    //Done per ListaGruppiAT
    public void done(boolean res, List<InfoGruppoBean> listaGruppi)
    {
        if( res && listaGruppi != null){
            groupslist = listaGruppi;

            //Chiamo l'adapter
            adapterGroups = new CustomListGroups(RicercaGruppoActivity.this, groupslist);

            nomigruppi = new String[groupslist.size()];
            aperti = new boolean[groupslist.size()];
            citta = new String[groupslist.size()];
            ids = new Long[groupslist.size()];

            for(int i = 0; i < groupslist.size(); i++){
                nomigruppi[i] = groupslist.get(i).getNome();
                aperti[i] = groupslist.get(i).getAperto();
                citta[i] = groupslist.get(i).getCitta();
                ids[i] = groupslist.get(i).getId();
            }

            myList = (ListView)findViewById(R.id.listView1);
            emptyText = (TextView) findViewById(android.R.id.empty);
            myList.setEmptyView(emptyText);
            myList.setAdapter(adapterGroups);
        }

        //this.nameCapture.setFocusable(true);
        //this.nameCapture.setClickable(true);
        this.progressView.setVisibility(View.GONE);
    }

    //Done per AggiornaStato
    public void done(boolean res)
    {
        if( res ) {
            //Che faccio?
            if( statoSuccessivo.equals(AppConstants.GRUPPO)) {
                Intent i = new Intent(RicercaGruppoActivity.this, GruppoActivity.class);
                i.putExtra("idGruppo", selectedGroup);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                startActivity(i);
            }
        }
    }

    //Done per sessioneIndietro
    public void done(boolean res, String nuovoStato){
        if(res) {
            if(nuovoStato.equals(AppConstants.PRINCIPALE)){
                Intent myIntent = new Intent(getApplicationContext(), PrincipaleActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

    private void AlterAdapterGroups() {
        // Se l'utente svuota l'edittext, mostro tutti i gruppi aperti esistenti
        if (nameCapture.getText().toString().isEmpty()) {
            partialNames.clear();
            partialTypes.clear();
            partialCities.clear();
            partialIds.clear();

            groupslist.clear();

            for(int i = 0; i < nomigruppi.length; i++){
                partialNames.add(i, nomigruppi[i]);
                partialCities.add(i, citta[i]);
                partialTypes.add(i, aperti[i]);
                partialIds.add(i, ids[i]);

                InfoGruppoBean g = new InfoGruppoBean();
                g.setNome(nomigruppi[i]);
                g.setAperto(aperti[i]);
                g.setCitta(citta[i]);
                g.setId(ids[i]);
                groupslist.add(g);
            }
            adapterGroups.notifyDataSetChanged();
        }
        else {

            partialNames.clear();
            partialTypes.clear();
            partialCities.clear();
            partialIds.clear();

            groupslist.clear();
            int j = 0;
            for (int i = 0; i < nomigruppi.length; i++) {
                //ToUpperCase per fare ricerca case-insensitive
                if (nomigruppi[i].toString().toUpperCase().contains(nameCapture.getText().toString().toUpperCase())) {
                    partialNames.add(j, nomigruppi[i]);
                    partialCities.add(j, citta[i]);
                    partialTypes.add(j, aperti[i]);
                    partialIds.add(j, ids[i]);

                    InfoGruppoBean g = new InfoGruppoBean();
                    g.setNome(nomigruppi[i]);
                    g.setAperto(aperti[i]);
                    g.setCitta(citta[i]);
                    g.setId(ids[i]);
                    groupslist.add(g);
                    j++;
                }
                else
                    continue;
                adapterGroups.notifyDataSetChanged();
            }
        }

        if(adapterGroups.getCount()==0){
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("Nessun gruppo aperto trovato.");
        }
    }
}
