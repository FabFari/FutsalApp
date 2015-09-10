package com.pde2015.futsalapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.model.InfoGestionePartiteBean;
import com.appspot.futsalapp_1008.pdE2015.model.PartitaBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.AnnullaDisponibilitaAT;
import com.pde2015.futsalapp.asynctasks.GetPartitaAT;
import com.pde2015.futsalapp.asynctasks.InserisciDisponibilitaAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.ModificaDisponibilitaAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.AnnullaDisponibilitaTC;
import com.pde2015.futsalapp.taskcallbacks.DisponibileTC;
import com.pde2015.futsalapp.taskcallbacks.GetPartitaTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.LinkedList;
import java.util.List;

public class DisponibileActivity extends AppCompatActivity implements View.OnClickListener, DisponibileTC,
        AnnullaDisponibilitaTC, AggiornaStatoTC, SessioneIndietroTC, ListaStatiTC, GetPartitaTC {

    private final static String TAG = "DisponibileActivity";

    Button annullaButton;
    Spinner spinner;

    String emailUtente;
    Long idSessione, idGruppo, idPartita;
    String tipoPartita;
    boolean isDisponibile;

    String nuovoStato;

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;
    LinkedList<Class> listaActivity;

    CircularProgressView cpv;
    DisponibileActivity daPassareAlDialog = DisponibileActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disponibile);

        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");
        idGruppo = (Long)extras.get("idGruppo");
        idPartita = (Long)extras.get("idPartita");
        isDisponibile = (boolean)extras.get("isDisponibile");
        tipoPartita = (String)extras.get("tipoPartita");

        // Ottenimento stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        this.spinner = (Spinner)findViewById(R.id.disponibile_spinner);
        ArrayAdapter<CharSequence> adapter;
        if(tipoPartita.equals("CALCETTO"))
            adapter = ArrayAdapter.createFromResource(this,
                    R.array.amici_calcetto, R.layout.element_spinner_ruoli);
        else if(tipoPartita.equals("CALCIOTTO"))
            adapter = ArrayAdapter.createFromResource(this, R.array.amici_calciotto, R.layout.element_spinner_ruoli);
        else
            adapter = ArrayAdapter.createFromResource(this, R.array.amici_calcio, R.layout.element_spinner_ruoli);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        this.cpv = (CircularProgressView)findViewById(R.id.progress_view);
        this.annullaButton = (Button)findViewById(R.id.disponibile_cancel);
        this.annullaButton.setOnClickListener(this);
        Log.e(TAG, "isDisponibile: " + this.isDisponibile);
        if( !isDisponibile ) annullaButton.setVisibility(View.GONE);
        cpv.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_disponibile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.confirm_disponibile) {
            if( isDisponibile ){
                InfoGestionePartiteBean gestioneBean = new InfoGestionePartiteBean();
                gestioneBean.setIdPartita(idPartita);
                Log.e(TAG, "idPartita: " + idPartita);
                gestioneBean.setEmailGiocatore(emailUtente);
                Log.e(TAG, "email: " + emailUtente);
                gestioneBean.setNAmici(Integer.parseInt(spinner.getSelectedItem().toString()));
                Log.e(TAG, "nAmici: "+spinner.getSelectedItem().toString());
                if(checkNetwork()) new ModificaDisponibilitaAT(getApplicationContext(), this, this, idSessione, gestioneBean ).execute();
            }
            else{
                if(checkNetwork()) new InserisciDisponibilitaAT(getApplicationContext(), this, this, idSessione, idPartita ).execute();            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
        if(v.getId() == R.id.disponibile_cancel){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Annulla Partecipazione");
            adb.setMessage("Stai per annullare la partecipazione: procedere?");
            adb.setIcon(R.drawable.alert);
            adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (checkNetwork()) {
                        cpv.setVisibility(View.VISIBLE);
                        InfoGestionePartiteBean gestioneBean = new InfoGestionePartiteBean();
                        gestioneBean.setEmailGiocatore(emailUtente);
                        gestioneBean.setIdPartita(idPartita);
                        new AnnullaDisponibilitaAT(getApplicationContext(), daPassareAlDialog, daPassareAlDialog, idSessione, gestioneBean).execute();
                    }
                }
            });
            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });
            adb.show();
        }
    }

    public void onBackPressed(){
        // Disabilitare Bottoni
        // Abilitare rondella
        PayloadBean bean = new PayloadBean();
        bean.setIdSessione(idSessione);
        if(checkNetwork()) {
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
            alert.showAlertDialog(DisponibileActivity.this,
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
            if(nuovoStato.equals(AppConstants.PARTITA)) {
                Intent i = new Intent(getApplicationContext(), PartitaActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                i.putExtra("idPartita", idPartita);
                startActivity(i);
                this.finish();
            }
            if(nuovoStato.equals(AppConstants.GRUPPO)) {
                Intent i = new Intent(getApplicationContext(), GruppoActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                startActivity(i);
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
        }
    }

    //Done per inserisciDisponibilita, modificaDisponibile
    public void done(boolean res, boolean esito){
        if( res && esito){
            Toast.makeText(getApplicationContext(), "Partecipazione inserita con successo.", Toast.LENGTH_LONG).show();
            PayloadBean p = new PayloadBean();
            p.setIdSessione(idSessione);
            this.nuovoStato = AppConstants.PARTITA;
            p.setNuovoStato(AppConstants.PARTITA);
            if(checkNetwork()) new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
        }
    }

    //Done per annullaDisponibilita
    public void done(boolean res, boolean esito, String tipoAT){
        if( res && esito ){
            if(checkNetwork())  new GetPartitaAT(getApplicationContext(), this, this, idSessione, idPartita).execute();
        }
    }

    public void done(boolean res, PartitaBean partitaBean) {
        if(res && partitaBean != null) {
            if(partitaBean.getHttpCode().equals(AppConstants.OK)) {
                Toast.makeText(getApplicationContext(), "Partecipazione annullata con successo.", Toast.LENGTH_LONG).show();
                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                this.nuovoStato = AppConstants.PARTITA;
                p.setNuovoStato(AppConstants.PARTITA);
                if(checkNetwork()) new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
            }
            else {
                Toast.makeText(getApplicationContext(), "Partecipazione annullata con successo. Annullamento partita per mancanza di partecipanti.", Toast.LENGTH_LONG).show();
                PayloadBean p = new PayloadBean();
                this.nuovoStato = AppConstants.GRUPPO;
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.GRUPPO);
                if(checkNetwork()) new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
            }
        }
    }
}