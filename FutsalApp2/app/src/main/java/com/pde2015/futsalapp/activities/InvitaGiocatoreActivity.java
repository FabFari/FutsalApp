package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.Giocatore;
import com.appspot.futsalapp_1008.pdE2015.model.InfoInvitoBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.InvitaGiocatoreAT;
import com.pde2015.futsalapp.asynctasks.ListaGiocatoriAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.InvitaGiocatoreTC;
import com.pde2015.futsalapp.taskcallbacks.ListaGiocatoriTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListGiocatore;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class InvitaGiocatoreActivity extends AppCompatActivity implements ListaStatiTC, ListaGiocatoriTC, SessioneIndietroTC,
        AggiornaStatoTC, InvitaGiocatoreTC {

    private final static String TAG = "InvitaGiocatoreActivity";

    Long idSessione, idGruppo;
    String emailUtente;

    private CustomListGiocatore adapterGiocatori;

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;
    LinkedList<Class> listaActivity;
    CircularProgressView progressView;

    //List rendering stuff
    private List<Giocatore> giocatorilist;

    String[] nomi;
    String[] emails;
    String[] pics;

    private ArrayList<String> partialNames = new ArrayList<String>();
    private ArrayList<String> partialEmails = new ArrayList<String>();
    private ArrayList<String> partialPics = new ArrayList<String>();

    // List of names matching criteria are listed here
    private ListView myList;
    // Field where user enters his search criteria
    private EditText nameCapture;
    // Appare questo fino a quando listaGruppiAT non chiama done()
    TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invita_giocatore);

        progressView = (CircularProgressView)findViewById(R.id.progress_view);

        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");
        idGruppo = (Long)extras.get("idGruppo");

        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        if(checkNetwork()) new ListaGiocatoriAT(getApplicationContext(), this, this).execute();

        nameCapture = (EditText) findViewById(R.id.name);
        nameCapture.setHint("Digita il nome del giocatore da cercare");
        //this.nameCapture.setFocusable(false);
        //this.nameCapture.setClickable(false);
        myList = (ListView) findViewById(R.id.listView1);
        emptyText = (TextView) findViewById(android.R.id.empty);
        myList.setEmptyView(emptyText);
        myList.setAdapter(adapterGiocatori);

        nameCapture.addTextChangedListener(new TextWatcher() {

            // As the user types in the search field, the list is
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                AlterAdapterGiocatori();
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invita_giocatore, menu);
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

    private void AlterAdapterGiocatori() {
        // Se l'utente svuota l'edittext, mostro tutti i gruppi aperti esistenti
        if (nameCapture.getText().toString().isEmpty()) {
            partialNames.clear();
            partialEmails.clear();
            partialPics.clear();

            giocatorilist.clear();

            for(int i = 0; i < nomi.length; i++){
                partialNames.add(i, nomi[i]);
                partialEmails.add(i, emails[i]);
                partialPics.add(i, pics[i]);

                Giocatore g = new Giocatore();
                g.setNome(nomi[i]);
                g.setEmail(emails[i]);
                g.setFotoProfilo(pics[i]);
                giocatorilist.add(g);
            }
            adapterGiocatori.notifyDataSetChanged();
        }
        else {

            partialNames.clear();
            partialEmails.clear();
            partialPics.clear();

            giocatorilist.clear();
            int j = 0;

            for (int i = 0; i < nomi.length; i++) {
                //ToUpperCase per fare ricerca case-insensitive
                if (nomi[i].toString().toUpperCase().contains(nameCapture.getText().toString().toUpperCase())) {
                    partialNames.add(j, nomi[i]);
                    partialEmails.add(j, emails[i]);
                    partialPics.add(j, pics[i]);

                    Giocatore g = new Giocatore();
                    g.setNome(nomi[i]);
                    g.setEmail(emails[i]);
                    g.setFotoProfilo(pics[i]);
                    giocatorilist.add(g);
                    j++;
                }
                else
                    continue;
                adapterGiocatori.notifyDataSetChanged();
            }
        }

        if(adapterGiocatori.getCount()==0){
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("Nessun giocatore trovato.");
        }
    }

    public boolean checkNetwork() {
        cd = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        if (cd.isConnectingToInternet()) {
            return true;
        }else{
            // Internet Connection is not present
            alert.showAlertDialog(InvitaGiocatoreActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

    public void done(boolean res, List<String> listaStati, String tipoDone ) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
            Log.e(TAG, "lista Stati: "+listaStati);
        }
    }

    public void done(boolean res, List<Giocatore> listaGiocatori){
        if( res && listaGiocatori != null ){
            // Rimozione Utente
            Iterator<Giocatore> it = listaGiocatori.iterator();
            //while(!it.next().getEmail().equals(emailUtente))
            int k = 0;
            while(it.hasNext()) {
                Giocatore g = it.next();

                if(g.getEmail().equals(emailUtente))
                    break;

                k++;
            }
            listaGiocatori.remove(k);

            this.giocatorilist = listaGiocatori;

            //Chiamo l'adapter
            adapterGiocatori = new CustomListGiocatore(InvitaGiocatoreActivity.this, giocatorilist);

            nomi = new String[giocatorilist.size()];
            emails = new String[giocatorilist.size()];
            pics = new String[giocatorilist.size()];

            for(int i = 0; i < giocatorilist.size(); i++){
                nomi[i] = giocatorilist.get(i).getNome();
                emails[i] = giocatorilist.get(i).getEmail();
                pics[i] = giocatorilist.get(i).getFotoProfilo();
            }

            myList = (ListView)findViewById(R.id.listView1);
            emptyText = (TextView) findViewById(android.R.id.empty);
            myList.setEmptyView(emptyText);
            myList.setAdapter(adapterGiocatori);

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    progressView.setVisibility(View.VISIBLE);
                    InfoInvitoBean p = new InfoInvitoBean();
                    p.setEmailDestinatario(giocatorilist.get(position).getEmail());
                    p.setEmailMittente(emailUtente);
                    p.setIdGruppo(idGruppo);
                    if (checkNetwork()) new InvitaGiocatoreAT(myList.getContext(), (InvitaGiocatoreTC)myList.getContext(), (InvitaGiocatoreActivity)myList.getContext(), idSessione, p).execute();

                }
            });

        }

        //this.nameCapture.setFocusable(true);
        //this.nameCapture.setClickable(true);
        this.progressView.setVisibility(View.GONE);
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

    public void done(boolean res, boolean result) {
        if(res && result) {
            if(checkNetwork()){
                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.GRUPPO);
                new AggiornaStatoAT(getApplicationContext(), this, idSessione, InvitaGiocatoreActivity.this).execute(p);
            }
        }
        else
            progressView.setVisibility(View.GONE);
    }

    public void done(boolean res) {
        if(res) {
            if(listaActivity.contains(GruppoActivity.class)) {
                Intent i = new Intent(InvitaGiocatoreActivity.this, GruppoActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                startActivity(i);
                this.finish();
            }
        }
    }

    public void done(boolean res, String nuovoStato) {
        if(res) {
            if(nuovoStato.equals(AppConstants.GRUPPO)) {
                Intent i = new Intent(InvitaGiocatoreActivity.this, GruppoActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                startActivity(i);
                this.finish();
            }
        }
    }

}
