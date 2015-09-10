package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.model.Giocatore;
import com.appspot.futsalapp_1008.pdE2015.model.InfoVotoUomoPartitaBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.CreaVotoAT;
import com.pde2015.futsalapp.asynctasks.ListaGiocatoriPartitaConfermataAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.CreaVotoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaGiocatoriPartitaTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListIscritti;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CreaVotoActivity extends AppCompatActivity implements ListaStatiTC, AggiornaStatoTC, SessioneIndietroTC, ListaGiocatoriPartitaTC, CreaVotoTC{

    private CustomListIscritti adapterGiocatori;

    //Dati Intent
    private Long idSessione, idGruppo, idPartita;
    String emailUtente;

    private static final String TAG = "RicercaGruppoActivity";

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;
    LinkedList<Class> listaActivity;

    CircularProgressView cpv;
    String[] nomiGiocatori;
    TextView emptyText;
    String selectedPlayer;
    private String nuovoStato;

    //List rendering stuff
    private List<Giocatore> listaGiocatori = new ArrayList<Giocatore>();
    ListView myList;
    EditText votatoView;
    TextView commentoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_voto);

        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");
        idGruppo = (Long)extras.get("idGruppo");
        idPartita = (Long)extras.get("idPartita");

        cpv = (CircularProgressView)findViewById(R.id.progress_view);
        votatoView = (EditText)findViewById(R.id.vote_name);
        commentoText = (TextView)findViewById(R.id.vote_comment);
        myList = (ListView)findViewById(R.id.listView1);
        emptyText = (TextView) findViewById(android.R.id.empty);
        myList.setEmptyView(emptyText);
        myList.setAdapter(adapterGiocatori);

        // Ottenimento lista stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        if(checkNetwork()) new ListaGiocatoriPartitaConfermataAT(getApplicationContext(), this, idPartita, idSessione, this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crea_voto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.confirm_vote) {
            if(this.votatoView.getText().length()>0 && !this.votatoView.getText().equals("Nessun giocatore scelto")){
                cpv.setVisibility(View.VISIBLE);
                InfoVotoUomoPartitaBean votoBean = new InfoVotoUomoPartitaBean();
                votoBean.setVotante(emailUtente);
                votoBean.setVotato(selectedPlayer);
                votoBean.setLinkVotoPerPartita(idPartita);
                votoBean.setCommento(commentoText.getText().toString());

                if(checkNetwork()) new CreaVotoAT(getApplicationContext(), this, this, idSessione, votoBean).execute();
            }
            else Toast.makeText(getApplicationContext(), "Non hai ancora scelto un giocatore!", Toast.LENGTH_LONG).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        // Disabilitare Bottoni
        this.nuovoStato = AppConstants.PARTITA;
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
            alert.showAlertDialog(CreaVotoActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
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
            if( nuovoStato.equals(AppConstants.PARTITA)) {
                Intent i = new Intent(CreaVotoActivity.this, PartitaGiocataActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                i.putExtra("idPartita", idPartita);
                startActivity(i);
                this.finish();
            }
        }
        else cpv.setVisibility(View.GONE);
    }

    //Done per sessioneIndietro
    public void done(boolean res, String nuovoStato){
        if(res) {
            if(nuovoStato.equals(AppConstants.PARTITA)){
                Intent myIntent = new Intent(getApplicationContext(), PartitaGiocataActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                myIntent.putExtra("idPartita", idPartita);
                startActivity(myIntent);
                this.finish();
            }
        }
        else cpv.setVisibility(View.GONE);
    }

    //Done per ListaGiocatori
    public void done(boolean res, final List<Giocatore> listaGiocatori, List<Integer> amici, String tipoDone, String statoPartita){
        cpv.setVisibility(View.GONE);
        if( res && listaGiocatori != null){
            this.listaGiocatori = listaGiocatori;

            //Chiamo l'adapter
            adapterGiocatori = new CustomListIscritti(CreaVotoActivity.this, listaGiocatori, this.emailUtente);

            nomiGiocatori = new String[listaGiocatori.size()];

            for(int i = 0; i < listaGiocatori.size(); i++){
                nomiGiocatori[i] = listaGiocatori.get(i).getNome();
            }

            myList = (ListView)findViewById(R.id.listView1);
            emptyText = (TextView) findViewById(android.R.id.empty);
            myList.setEmptyView(emptyText);
            myList.setAdapter(adapterGiocatori);

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedPlayer = listaGiocatori.get(position).getEmail();
                    votatoView.setText(listaGiocatori.get(position).getNome());
                }
            });
        }
    }

    //Done per creaVoto
    public void done(boolean res, boolean esito){
        cpv.setVisibility(View.GONE);
        if( res && esito ){
            PayloadBean p = new PayloadBean();
            p.setIdSessione(idSessione);
            p.setNuovoStato(AppConstants.PARTITA);
            if(checkNetwork()){
                this.nuovoStato = AppConstants.PARTITA;
                new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Errore durante l'invio del voto!", Toast.LENGTH_LONG).show();
        }
    }
}
