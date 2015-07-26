package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.model.InfoGruppoBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.activities.GruppoActivity;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.ListaGruppiIscrittoTC;
import com.pde2015.futsalapp.asynctasks.ListaGruppiIscrittoAT;

import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListGroups;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PrincipaleActivity extends AppCompatActivity implements ListaStatiTC, AggiornaStatoTC,
        ListaGruppiIscrittoTC {

    private static final String TAG = "PrincipaleActivity";

    private CustomListGroups adapterGroups;
    private List<InfoGruppoBean> groupslist = new ArrayList<InfoGruppoBean>();
    private Long idSessione;
    private String emailUtente;

    String statoCorrente = AppConstants.PRINCIPALE;
    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;
    LinkedList<Class> listaActivity;

    SharedPreferences pref;

    String[] nomigruppi;
    boolean[] aperti;
    String[] citta;

    TextView emptyText;

    boolean hasClicked;
    String statoSuccessivo;

    long selectedGroup;
    // List of names matching criteria are listed here
    private ListView myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principale);

        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        emailUtente = pref.getString("EmailUtente", null);
        Log.e(TAG, "emailUtente: "+emailUtente);

        myList = (ListView)findViewById(R.id.listView1);
        emptyText = (TextView) findViewById(android.R.id.empty);

        // Ottenimento stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        // Prendo la lista dal server
        if(checkNetwork()) new ListaGruppiIscrittoAT(getApplicationContext(), this, idSessione, this).execute();

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.GRUPPO);
                if(checkNetwork()){
                    statoSuccessivo = AppConstants.GRUPPO;
                    new AggiornaStatoAT(myList.getContext(), (AggiornaStatoTC)myList.getContext(), idSessione, (PrincipaleActivity)myList.getContext()).execute(p);
                }
                selectedGroup = groupslist.get(position).getId();

            }
        });

         /* VISUALIZZO ACTION BAR CON LOGO */
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo_small);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principale, menu);
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

        if( id == R.id.user )
        {
            PayloadBean p = new PayloadBean();
            p.setIdSessione(idSessione);
            p.setNuovoStato(AppConstants.PROFILO);
            if(checkNetwork()){
                statoSuccessivo = AppConstants.PROFILO;

                new AggiornaStatoAT(getApplicationContext(), (AggiornaStatoTC)this, idSessione, (PrincipaleActivity)myList.getContext()).execute(p);
            }
            return true;
        }
        if(id == R.id.group_search){
            PayloadBean p = new PayloadBean();
            p.setIdSessione(idSessione);
            p.setNuovoStato(AppConstants.RICERCA_GRUPPO);
            if(checkNetwork()){
                statoSuccessivo = AppConstants.RICERCA_GRUPPO;

                new AggiornaStatoAT(getApplicationContext(), (AggiornaStatoTC)this, idSessione, (PrincipaleActivity)myList.getContext()).execute();
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
            alert.showAlertDialog(PrincipaleActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

    //Done per ListaStati - ho dovuto aggiungere la stringa perche' altrimenti
    // il done si sarebbe sovrapposto a quello di ListaGruppi
    public void done(boolean res, List<String> listaStati, String tipoDone ) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
        }
    }

    //Done per ListaGruppi
    public void done(boolean res, List<InfoGruppoBean> listaGruppi)
    {
        if( res && listaGruppi != null){
            groupslist = listaGruppi;

            //Chiamo l'adapter
            adapterGroups = new CustomListGroups(PrincipaleActivity.this, groupslist);

            nomigruppi = new String[groupslist.size()];
            aperti = new boolean[groupslist.size()];
            citta = new String[groupslist.size()];

            for(int i = 0; i < groupslist.size(); i++){
                nomigruppi[i] = groupslist.get(i).getNome();
                aperti[i] = groupslist.get(i).getAperto();
                citta[i] = groupslist.get(i).getCitta();
            }

            myList.setEmptyView(emptyText);
            myList.setAdapter(adapterGroups);
        }
    }

    //Done per AggiornaStati
    public void done(boolean res)
    {
        if( res ) {
            //Che faccio?
            if( statoSuccessivo.equals(AppConstants.GRUPPO)) {
                Intent i = new Intent(PrincipaleActivity.this, GruppoActivity.class);
                i.putExtra("idGruppo", selectedGroup);
                i.putExtra("idSessione", idSessione);
                startActivity(i);
            }
            else if(statoSuccessivo.equals(AppConstants.CREA_GRUPPO)){
                //Vai alla schermata Crea Gruppo
            }
            else if(statoSuccessivo.equals(AppConstants.RICERCA_GRUPPO)){
                //Vai alla schermata Ricerca Gruppo
            }
            else if(statoSuccessivo.equals(AppConstants.PROFILO)){
                Intent i = new Intent(PrincipaleActivity.this, ProfileActivity.class);
                i.putExtra("emailProfilo", emailUtente);
                i.putExtra("idSessione", idSessione);
                startActivity(i);
            }
        }
    }
}
