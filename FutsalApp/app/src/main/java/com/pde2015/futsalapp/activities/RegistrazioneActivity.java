package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;

public class RegistrazioneActivity extends AppCompatActivity implements SessioneIndietroTC{

    Long idSessione;
    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registrazione, menu);
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
            alert.showAlertDialog(RegistrazioneActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", false);
            // stop executing code by return
            return false;
        }
    }

    public void done(boolean res, String nuovoStato) {
        if(res) {
            if(nuovoStato.equals(AppConstants.LOGIN_E_REGISTRAZIONE)) {
                Intent myIntent = new Intent(this, LoginRegistratiActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

}
