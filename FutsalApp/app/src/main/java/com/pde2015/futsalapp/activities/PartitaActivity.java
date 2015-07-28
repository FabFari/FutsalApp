package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;

public class PartitaActivity extends ActionBarActivity  implements AggiornaStatoTC {

    private static final String TAG = "PartitaActivity";

    Long idSessione, idGruppo, idPartita;
    String emailUtente;

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partita);
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");
        idGruppo = (Long)extras.get("idGruppo");
        idPartita = (Long)extras.get("idPartita");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_partita, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.add_field) {
            PayloadBean p = new PayloadBean();
            p.setIdSessione(idSessione);
            p.setNuovoStato(AppConstants.CAMPO);
            if(checkNetwork()) {
                //cpv.setVisibility(View.VISIBLE);
                new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
            }
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
            alert.showAlertDialog(PartitaActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

    public void done(boolean res) {
        //cpv.setVisibility(View.GONE);
        if( res ) {
            Intent myIntent = new Intent(PartitaActivity.this, RicercaCampoActivity.class);
            myIntent.putExtra("idSessione", idSessione);
            myIntent.putExtra("idGruppo", idGruppo);
            myIntent.putExtra("idPartita", idPartita);
            myIntent.putExtra("email", emailUtente);
            startActivity(myIntent);
            this.finish();
        }
    }

}
