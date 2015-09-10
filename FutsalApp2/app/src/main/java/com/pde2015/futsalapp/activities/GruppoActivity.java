package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.Gruppo;
import com.appspot.futsalapp_1008.pdE2015.model.GruppoBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoIscrittoGestisceBean;

import com.appspot.futsalapp_1008.pdE2015.model.PartitaBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.EsciGruppoAT;
import com.pde2015.futsalapp.asynctasks.EstIscrittoAT;
import com.pde2015.futsalapp.asynctasks.GetGruppoAT;
import com.pde2015.futsalapp.asynctasks.IscrizioneGruppoAT;
import com.pde2015.futsalapp.asynctasks.ListaPartiteProposteConfermateAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.EsciGruppoTC;
import com.pde2015.futsalapp.taskcallbacks.EstIscrittoTC;
import com.pde2015.futsalapp.taskcallbacks.GetGruppoTC;
import com.pde2015.futsalapp.taskcallbacks.IscrizioneGruppoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaPartiteProposteConfermateTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListMatches;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GruppoActivity extends AppCompatActivity implements AggiornaStatoTC, SessioneIndietroTC, IscrizioneGruppoTC,
        EstIscrittoTC, ListaPartiteProposteConfermateTC, GetGruppoTC, View.OnClickListener, ListaStatiTC, EsciGruppoTC {

    private static final String TAG = "GruppoActivity";

    String emailUtente;
    private Long idSessione, idGruppo;
    boolean estIscritto = false;
    String nomeGruppo, emailAdmin, citta;
    com.google.api.client.util.DateTime dataCreazione;
    FloatingActionButton fab;
    GruppoActivity daPassareAlDialog = GruppoActivity.this;

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

    TextView nomeGruppoView, dataCreazioneView, adminView, cittaView;
    Button esciGruppoButton, iscrivitiButton;

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;

    LinkedList<Class> listaActivity;
    Menu menu;

    CircularProgressView cpv;
    boolean gone = false;


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

        iscrivitiButton = (Button)findViewById(R.id.group_join);
        iscrivitiButton.setVisibility(View.GONE);

        cpv = (CircularProgressView)findViewById(R.id.progress_view);

        InfoIscrittoGestisceBean iscrittoBean = new InfoIscrittoGestisceBean();
        iscrittoBean.setGruppo(idGruppo);
        iscrittoBean.setGiocatore(emailUtente);

        if(checkNetwork()) new EstIscrittoAT(getApplicationContext(), this, GruppoActivity.this, idSessione, iscrittoBean).execute();

        if(checkNetwork()) new GetGruppoAT(getApplicationContext(), this, GruppoActivity.this, idSessione, idGruppo).execute();

        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this, idGruppo, null).execute();

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
                    cpv.setVisibility(View.VISIBLE);
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
                    cpv.setVisibility(View.VISIBLE);
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
                    cpv.setVisibility(View.VISIBLE);
                    this.nuovoStato = AppConstants.STORICO;
                    PayloadBean p = new PayloadBean();
                    p.setIdSessione(idSessione);
                    p.setNuovoStato(AppConstants.STORICO);
                    new AggiornaStatoAT(getApplicationContext(), this, idSessione, GruppoActivity.this).execute(p);
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
            cpv.setVisibility(View.VISIBLE);
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

    //Metodo per risolvere il problema della lunghezza della lista sugli schermi più piccoli
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    //Done per estIscritto
    public void done(boolean res, boolean estIscritto){
        if(res && estIscritto){

            this.estIscritto = true;
            setContentView(R.layout.activity_gruppo);

            this.esciGruppoButton = (Button)findViewById(R.id.group_exit);
            this.esciGruppoButton.setVisibility(View.GONE);
            this.esciGruppoButton.setOnClickListener(this);

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
                        new AggiornaStatoAT(myList.getContext(), (AggiornaStatoTC) myList.getContext(), idSessione, (GruppoActivity) myList.getContext()).execute(p);
                    }
                    selectedMatch = listaPartite.get(position).getPartita().getId();

                    cpv.setVisibility(View.VISIBLE);

                }
            });

        }
    }

    public void onClick(View view) {
        //Toast.makeText(getApplicationContext(), "Id: "+view.getId(), Toast.LENGTH_LONG);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
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
            case R.id.group_exit:
                Log.e(TAG, "Bottone EsciGruppo cliccato!");
                adb.setTitle("Esci dal Gruppo");
                adb.setMessage("Vuoi veramente uscire dal gruppo?");
                adb.setIcon(R.drawable.alert);
                adb.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkNetwork()) {
                            cpv.setVisibility(View.VISIBLE);
                            InfoIscrittoGestisceBean gestioneBean = new InfoIscrittoGestisceBean();
                            gestioneBean.setGiocatore(emailUtente);
                            gestioneBean.setGruppo(idGruppo);
                            cpv.setVisibility(View.VISIBLE);
                            new EsciGruppoAT(getApplicationContext(), daPassareAlDialog, daPassareAlDialog, idSessione, gestioneBean).execute();
                        }
                    }
                });
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                });
                adb.show();
                break;
            case R.id.group_join:
                adb.setTitle("Iscriviti al Gruppo");
                adb.setMessage("Vuoi veramente iscriverti al gruppo?");
                adb.setIcon(R.drawable.confirm_dialog);
                adb.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkNetwork()) {
                            cpv.setVisibility(View.VISIBLE);
                            if(checkNetwork()) new IscrizioneGruppoAT(getApplicationContext(), daPassareAlDialog, daPassareAlDialog, idSessione, idGruppo).execute();
                        }
                    }
                });
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                adb.show();
                break;
        }
    }

    // Done per IscrizioneGruppo
    public void done(boolean res, DefaultBean response) {
        if(res && response != null) {
            if(response.getHttpCode().equals(AppConstants.OK)) {
                cpv.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Iscrizione avvenuta con successo!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), GruppoActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                startActivity(i);
                this.finish();
            }
            else {
                cpv.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), response.getResult(), Toast.LENGTH_LONG).show();
            }
        }
        else
            cpv.setVisibility(View.GONE);
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
                Intent myIntent = new Intent(getApplicationContext(), InvitaGiocatoreActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            }
            else if(this.nuovoStato.equals(AppConstants.PARTITA)){
                Intent myIntent = new Intent(getApplicationContext(), PartitaActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                myIntent.putExtra("idPartita", selectedMatch);
                startActivity(myIntent);
                this.finish();
            }
            else if(this.nuovoStato.equals(AppConstants.STORICO)){
                Intent myIntent = new Intent(getApplicationContext(), StoricoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            }
            else if(this.nuovoStato.equals(AppConstants.PRINCIPALE)){
                Intent myIntent = new Intent(getApplicationContext(), PrincipaleActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

    //Done per listaPartiteProposteConfermate
    public void done(boolean res, List<PartitaBean> listaPartite){
        if(res){
            if(listaPartite != null) {
                this.listaPartite = listaPartite;

                //Chiamo l'adapter
                adapterMatches = new CustomListMatches(GruppoActivity.this, listaPartite);

                nomiChiPropone = new String[listaPartite.size()];
                datePartite = new com.google.api.client.util.DateTime[listaPartite.size()];
                quote = new Float[listaPartite.size()];
                tipiPartite = new Integer[listaPartite.size()];
                statoPartite = new String[listaPartite.size()];

                for (int i = 0; i < listaPartite.size(); i++) {
                    nomiChiPropone[i] = listaPartite.get(i).getPartita().getPropone();
                    datePartite[i] = listaPartite.get(i).getPartita().getDataOraPartita();
                    quote[i] = listaPartite.get(i).getPartita().getQuota();
                    tipiPartite[i] = listaPartite.get(i).getTipo();
                    //TODO assegna statoPartita
                    statoPartite[i] = listaPartite.get(i).getPartita().getStatoCorrente();
                }
            }
            else {
                this.listaPartite = new ArrayList<PartitaBean>();

                //Chiamo l'adapter
                adapterMatches = new CustomListMatches(GruppoActivity.this, this.listaPartite);
            }

                myList = (ListView)findViewById(R.id.group_lista_partite);
                emptyText = (TextView) findViewById(android.R.id.empty);
                myList.setEmptyView(emptyText);
                myList.setAdapter(adapterMatches);

                setListViewHeightBasedOnItems(myList);

            if(!gone) {
                gone = true;
                cpv = (CircularProgressView)findViewById(R.id.progress_view);
                cpv.setVisibility(View.GONE);
            }

            this.esciGruppoButton.setVisibility(View.VISIBLE);

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
    public void done(boolean res, GruppoBean infoGruppo){
        if(res && infoGruppo != null){
            this.nomeGruppo = infoGruppo.getGruppo().getNome();
            this.emailAdmin = infoGruppo.getEmailAdmin();
            this.citta = infoGruppo.getGruppo().getCitta();
            this.dataCreazione = infoGruppo.getGruppo().getDataCreazione();

            this.nomeGruppoView = (TextView)findViewById(R.id.group_group_name);
            this.dataCreazioneView = (TextView)findViewById(R.id.group_date_creation);
            this.adminView = (TextView)findViewById(R.id.group_admin);
            this.cittaView = (TextView)findViewById(R.id.group_city_name);

            this.iscrivitiButton.setVisibility(View.VISIBLE);
            this.iscrivitiButton.setOnClickListener(this);

            this.nomeGruppoView.setText(this.nomeGruppo);
            this.adminView.setText(emailAdmin);
            //this.dataCreazioneView.setText(dataCreazione.toString().substring(0, 10));
            String dataString = dataCreazione.toString().substring(0,10);
            this.dataCreazioneView.setText(dataString.substring(8)+"-"+dataString.substring(5, 7)+"-"+dataString.substring(0, 4));
            this.cittaView.setText(citta);
            cpv = (CircularProgressView)findViewById(R.id.progress_view);
        }
    }

    public void done(boolean res, List<String> listaStati, String tipoDone) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
            if(!gone) {
                gone = true;
                cpv.setVisibility(View.GONE);
            }
        }
    }

    public void done(boolean res, boolean esito, String tipoAT) {
        if (res && esito) {
            Toast.makeText(getApplicationContext(), "Uscita dal gruppo effettuata con successo.", Toast.LENGTH_LONG).show();
            PayloadBean p = new PayloadBean();
            p.setIdSessione(idSessione);
            //p.setNuovoStato(AppConstants.PRINCIPALE);

            if (checkNetwork()) {
                cpv.setVisibility(View.VISIBLE);
                //this.nuovoStato = AppConstants.PRINCIPALE;
                //new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
                new SessioneIndietroAT(getApplicationContext(), this, this, idSessione).execute(p);
            }
        }
    }

}
