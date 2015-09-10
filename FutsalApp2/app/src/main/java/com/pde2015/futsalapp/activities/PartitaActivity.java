package com.pde2015.futsalapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.appspot.futsalapp_1008.pdE2015.model.Campo;
import com.appspot.futsalapp_1008.pdE2015.model.Giocatore;
import com.appspot.futsalapp_1008.pdE2015.model.InfoGestionePartiteBean;
import com.appspot.futsalapp_1008.pdE2015.model.Partita;
import com.appspot.futsalapp_1008.pdE2015.model.PartitaBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.AnnullaPartitaAT;
import com.pde2015.futsalapp.asynctasks.CampoPartitaAT;
import com.pde2015.futsalapp.asynctasks.ConfermaPartitaAT;
import com.pde2015.futsalapp.asynctasks.GetNDisponibiliAT;
import com.pde2015.futsalapp.asynctasks.GetPartitaAT;
import com.pde2015.futsalapp.asynctasks.IsDisponibileAT;
import com.pde2015.futsalapp.asynctasks.ListaGiocatoriPartitaConfermataAT;
import com.pde2015.futsalapp.asynctasks.ListaGiocatoriPartitaPropostaAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.AnnullaPartitaTC;
import com.pde2015.futsalapp.taskcallbacks.CampoPartitaTC;
import com.pde2015.futsalapp.taskcallbacks.ConfermaPartitaTC;
import com.pde2015.futsalapp.taskcallbacks.GetNDisponibiliTC;
import com.pde2015.futsalapp.taskcallbacks.GetPartitaTC;
import com.pde2015.futsalapp.taskcallbacks.IsDisponibileTC;
import com.pde2015.futsalapp.taskcallbacks.ListaGiocatoriPartitaTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListPartecipa;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.LinkedList;
import java.util.List;

public class PartitaActivity extends AppCompatActivity implements ListaStatiTC, CampoPartitaTC,
        ListaGiocatoriPartitaTC, GetPartitaTC, AggiornaStatoTC, SessioneIndietroTC,
        IsDisponibileTC, ConfermaPartitaTC, AnnullaPartitaTC, GetNDisponibiliTC {

    private final static String TAG = "PartitaActivity";

    String emailUtente, tipoPartita;
    private Long idSessione, idPartita, idGruppo;
    private String statoSuccessivo;
    Partita partita;
    boolean isDisponibile = false;

    private List<Giocatore> listaPartecipa;
    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;
    CircularProgressView cpv;

    LinkedList<Class> listaActivity;

    TextView tipoPartitaText, proponeText, quotaText, dataText, nDisponibiliText, separText, nPartecipantiText, statoText, campoText;

    String[] nomiGiocatori;
    TextView emptyText;
    String selectedPlayer;
    // List of names matching criteria are listed here
    private ListView myList;

    private CustomListPartecipa adapterPartecipa;

    Menu menu;
    PartitaActivity daPassareAlDialog = PartitaActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partita_proposta_confermata);

        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");
        idGruppo = (Long)extras.get("idGruppo");
        idPartita = (Long)extras.get("idPartita");

        tipoPartitaText = (TextView)findViewById(R.id.match_type);
        quotaText = (TextView)findViewById(R.id.match_quota);
        dataText = (TextView)findViewById(R.id.match_data);
        proponeText = (TextView)findViewById(R.id.match_propone);
        nDisponibiliText = (TextView)findViewById(R.id.match_number);
        separText = (TextView)findViewById(R.id.match_number_sep);
        nPartecipantiText = (TextView)findViewById(R.id.match_number_tot);
        Log.e(TAG, "R: 0x7f0e00d9 R.id.match_status: "+R.id.match_status);
        statoText = (TextView)findViewById(R.id.match_status);
        Log.e(TAG, "statoText: "+statoText);
        campoText = (TextView)findViewById(R.id.match_campo);

        nPartecipantiText.setVisibility(View.GONE);
        separText.setVisibility(View.GONE);
        nDisponibiliText.setVisibility(View.GONE);

        cpv =(CircularProgressView)findViewById(R.id.progress_view);

        // Ottenimento lista stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this, null, idPartita).execute();

        // Ottenimento partita
        if(checkNetwork())  new GetPartitaAT(getApplicationContext(), this, this, idSessione, idPartita).execute();

        // Ottenimento nDisponibili
        if(checkNetwork())  new GetNDisponibiliAT(getApplicationContext(), this, this, idPartita).execute();

        // Ottenimento Campo
        if(checkNetwork()) new CampoPartitaAT(getApplicationContext(), this, this, idSessione, idPartita).execute();

        // isDisponibile
        if(checkNetwork()) new IsDisponibileAT(getApplicationContext(), this, idSessione, idPartita, this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_partita, menu);
        this.menu = menu;
        menu.getItem(0).setEnabled(false);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setEnabled(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setEnabled(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setEnabled(false);
        menu.getItem(3).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.add_campo) {
            PayloadBean p = new PayloadBean();
            p.setIdSessione(idSessione);
            p.setNuovoStato(AppConstants.RICERCA_CAMPO);
            if(checkNetwork()){
                cpv.setVisibility(View.VISIBLE);
                this.statoSuccessivo = AppConstants.RICERCA_CAMPO;
                new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
            }
            return true;
        }
        if (id == R.id.match_confirm ) {
            if( this. emailUtente.equals(this.partita.getPropone()) ) {
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("Conferma Partita");
                adb.setMessage("Stai per confermare la partita: procedere?");
                adb.setIcon(R.drawable.confirm_dialog);
                adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkNetwork()) {
                            cpv.setVisibility(View.VISIBLE);
                            new ConfermaPartitaAT(getApplicationContext(), daPassareAlDialog , idSessione, idPartita, daPassareAlDialog).execute();
                        }
                    }
                });
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                });
                adb.show();
                return true;
            }

            else Toast.makeText(getApplicationContext(), "Solo chi ha proposto la partita puo' confermarla!", Toast.LENGTH_LONG);
            return true;
        }
        if (id == R.id.match_cancel) {
            if(checkNetwork()){
                if(this.emailUtente.equals(this.partita.getPropone())){
                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    adb.setTitle("Annulla Partita");
                    adb.setMessage("Stai per annullare la partita: procedere?");
                    adb.setIcon(R.drawable.alert);
                    adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (checkNetwork()) {
                                cpv.setVisibility(View.VISIBLE);
                                InfoGestionePartiteBean gestioneBean = new InfoGestionePartiteBean();
                                gestioneBean.setEmailGiocatore(emailUtente);
                                gestioneBean.setIdPartita(idPartita);
                                new AnnullaPartitaAT(getApplicationContext(), daPassareAlDialog, idSessione, gestioneBean, daPassareAlDialog).execute();
                            }
                        }
                    });
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    adb.show();
                    return true;
                }
                else Toast.makeText(getApplicationContext(), "Solo chi ha proposto la partita puo' annullarla!", Toast.LENGTH_LONG).show();
            }

            return true;
        }
        else if(id == R.id.partecipa ){
            PayloadBean p = new PayloadBean();
            p.setIdSessione(idSessione);
            p.setNuovoStato(AppConstants.DISPONIBILE_PER_PARTITA);
            if(checkNetwork()){
                cpv.setVisibility(View.VISIBLE);
                this.statoSuccessivo = AppConstants.DISPONIBILE_PER_PARTITA;
                new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        // Disabilitare Bottoni
        cpv.setVisibility(View.VISIBLE);
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
            alert.showAlertDialog(PartitaActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

    //Done per ListaStati
    public void done(boolean res, List<String> listaStati, String tipoDone ) {
       if (res && listaStati != null) {
            SessionManager sm = new SessionManager((List<String>)listaStati);
            listaActivity = sm.getListaActivity();
       }
    }

    //Done per AggiornaStato
    public void done(boolean res)
    {
        cpv.setVisibility(View.GONE);
        if( res ) {
            if( statoSuccessivo.equals(AppConstants.RICERCA_CAMPO)) {
                Intent i = new Intent(PartitaActivity.this, RicercaCampoActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                i.putExtra("idPartita", idPartita);
                startActivity(i);
                finish();
            }
            else if( statoSuccessivo.equals(AppConstants.PROFILO)){
                Intent i = new Intent(PartitaActivity.this, ProfileActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idSessione", idSessione);
                i.putExtra("emailProfilo", selectedPlayer);
                i.putExtra("idPartita", idPartita);
                startActivity(i);
                finish();
            }
            else if( statoSuccessivo.equals(AppConstants.DISPONIBILE_PER_PARTITA) ){
                Log.e(TAG,"isDisponibile: "+this.isDisponibile);
                Intent i = new Intent(PartitaActivity.this, DisponibileActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                i.putExtra("idPartita", idPartita);
                i.putExtra("isDisponibile", this.isDisponibile);
                i.putExtra("tipoPartita", tipoPartita);
                startActivity(i);
                finish();
            }
        }
    }

    //Done per sessioneIndietro
    public void done(boolean res, String nuovoStato){
        if(res) {
            Log.e(TAG, "nuovoStato: "+nuovoStato);
            if(nuovoStato.equals(AppConstants.GRUPPO)){
                Log.e(TAG, "Torno indietro.");
                Intent myIntent = new Intent(getApplicationContext(), GruppoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            }
            else if(nuovoStato.equals(AppConstants.STORICO)){
                Intent myIntent = new Intent(getApplicationContext(), StoricoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            }
        }
        else cpv.setVisibility(View.GONE);
    }

    //Done per AnnullaPartita, ConfermaPartita
    public void done(boolean res, boolean esito, String tipoAT){
        Log.e(TAG, "esito: " + esito);
        if(res && esito) {
            Log.e(TAG, "tipoAT: " + tipoAT);
            if (tipoAT.equals("AnnullaPartita")) {
                /*if (checkNetwork()) {
                    PayloadBean bean = new PayloadBean();
                    bean.setIdSessione(idSessione);
                    this.statoSuccessivo = AppConstants.GRUPPO;
                    Log.e(TAG, "Lancio SessioneIndietroAT");
                    new SessioneIndietroAT(getApplicationContext(), this, this, idSessione).execute(bean);
                }*/
                Intent myIntent = new Intent(getApplicationContext(), GruppoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            } else if (tipoAT.equals("ConfermaPartita")) {

                //Chiamo l'intent perche' devo ricaricare tutto: listaGiocatori, stato...
                Intent myIntent = new Intent(getApplicationContext(), PartitaActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idPartita", idPartita);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            }
        }
        else
            cpv.setVisibility(View.GONE);
    }

    //Done per GetNDisponibilita
    public void done(boolean res, int nDisponibili){
        if( res ){
            this.nDisponibiliText.setText(Integer.toString(nDisponibili));
        }
    }

    //Done per isDisponibile
    public void done(boolean res, boolean isDisponibile){
        if( res && isDisponibile ){
            this.isDisponibile = true;
        }
    }

    //Done per CampoPartita
    public void done(boolean res, Campo campoPartita){
        if( res && campoPartita != null ){
            this.campoText.setText(campoPartita.getNome());
        }
    }

    //Done per GetPartita
    public void done(boolean res, PartitaBean partitaBean){
        if(res && partitaBean != null){
            this.partita = partitaBean.getPartita();
            if(this.partita.getStatoCorrente().equals("PROPOSTA")){
                nDisponibiliText.setVisibility(View.VISIBLE);
                nPartecipantiText.setVisibility(View.VISIBLE);
                separText.setVisibility(View.VISIBLE);

                menu.getItem(0).setEnabled(true);
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setEnabled(true);
                menu.getItem(1).setVisible(true);

                if(this.partita.getPropone().equals(emailUtente)){
                    menu.getItem(2).setEnabled(true);
                    menu.getItem(2).setVisible(true);
                    menu.getItem(3).setEnabled(true);
                    menu.getItem(3).setVisible(true);

                }

                if(checkNetwork()) new ListaGiocatoriPartitaPropostaAT(getApplicationContext(), this , idPartita, idSessione, this).execute();
            }
            else
                if(checkNetwork()) new ListaGiocatoriPartitaConfermataAT(getApplicationContext(), this , idPartita, idSessione, this).execute();

            //Impostazione grafica
            this.dataText.setText(partitaBean.getDataString());
            if( partitaBean.getTipo() == 1 ) {
                this.tipoPartitaText.setText("Partita di Calcetto");
                this.tipoPartita = "CALCETTO";
            }
            if( partitaBean.getTipo() == 2 ) {
                this.tipoPartitaText.setText("Partita di Calciotto");
                this.tipoPartita = "CALCIOTTO";
            }
            if( partitaBean.getTipo() == 3 ) {
                this.tipoPartitaText.setText("Partita di Calcio");
                this.tipoPartita = "CALCIO";
            }
            this.proponeText.setText(partita.getPropone());
            this.quotaText.setText(partita.getQuota().toString());
            Log.e(TAG, "StatoCorrente: " + partita.getStatoCorrente());
            this.statoText.setText(partita.getStatoCorrente());
            this.nPartecipantiText.setText(partita.getNpartecipanti().toString());

        }
    }

    //Done per ListaGiocatori
    public void done(boolean res, List<Giocatore> listaGiocatori, List<Integer> amici, String tipoDone, String statoPartita){
        cpv.setVisibility(View.GONE);
        if( res && listaGiocatori != null){
            this.listaPartecipa = listaGiocatori;

            //Chiamo l'adapter
            adapterPartecipa = new CustomListPartecipa(PartitaActivity.this, listaGiocatori, amici, this.emailUtente);

            nomiGiocatori = new String[listaGiocatori.size()];

            for(int i = 0; i < listaGiocatori.size(); i++){
                nomiGiocatori[i] = listaGiocatori.get(i).getNome();
            }

            myList = (ListView)findViewById(R.id.match_players);
            emptyText = (TextView) findViewById(android.R.id.empty);
            myList.setEmptyView(emptyText);
            myList.setAdapter(adapterPartecipa);

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    cpv.setVisibility(View.VISIBLE);
                    PayloadBean p = new PayloadBean();
                    p.setIdSessione(idSessione);
                    p.setNuovoStato(AppConstants.PROFILO);
                    if (checkNetwork()) {
                        statoSuccessivo = AppConstants.PROFILO;
                        new AggiornaStatoAT(myList.getContext(), (AggiornaStatoTC) myList.getContext(), idSessione, (PartitaActivity) myList.getContext()).execute(p);
                    }
                    selectedPlayer = listaPartecipa.get(position).getEmail();
                }
            });

            cpv.setVisibility(View.GONE);
        }
    }
}
