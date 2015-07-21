package com.pde2015.futsalapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.*;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.R;

import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.SessionManager;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;

public class LoginRegistratiActivity extends AppCompatActivity implements ListaStatiTC, AggiornaStatoTC {

    String statoCorrente = AppConstants.LOGIN_E_REGISTRAZIONE;
    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;
    Long idSessione;
    LinkedList<Class> listaActivity;
    boolean sessionCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        // Ottenimento stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        //while(!sessionCreated);

    /*    // Setup Schermata
        Button btn_login = (Button)findViewById(R.id.btn_login);
        Button btn_registrazione = (Button)findViewById(R.id.btn_sign_in);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // LOGIN
            }
        });

        btn_registrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listaActivity.contains(RegistrazioneActivity.class)) {
                    PayloadBean p = new PayloadBean();
                    p.setIdSessione(idSessione);
                    p.setNuovoStato(AppConstants.REGISTRAZIONE);
                    if(checkNetwork()) new AggiornaStatoAT(v.getContext(), (AggiornaStatoTC)v.getContext(), idSessione, (LoginRegistratiActivity)v.getContext()).execute(p);
                }
                else
                    Toast.makeText(getApplicationContext(), "Impossibile passare all'Activity 'RegistrazioneActivity': Stato non raggiungibile!", Toast.LENGTH_LONG);
            }
        }); */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_registrati, menu);
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
            alert.showAlertDialog(LoginRegistratiActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", false);
            // stop executing code by return
            return false;
        }
    }

    public void done(boolean res, List<String> listaStati) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
            sessionCreated = true;
            aggiornaGrafica();
        }

    }

    public void done(boolean res)
    {
        if( res ) {
            Intent myIntent = new Intent(getApplicationContext(), RegistrazioneActivity.class);
            myIntent.putExtra("idSessione", idSessione);
            startActivity(myIntent);
            finish();
        }
    }

    private void aggiornaGrafica(){

        setContentView(R.layout.activity_login_registrati);
        // Setup Schermata
        Button btn_login = (Button)findViewById(R.id.btn_login);
        Button btn_registrazione = (Button)findViewById(R.id.btn_sign_in);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // LOGIN
            }
        });
        btn_registrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listaActivity.contains(RegistrazioneActivity.class)) {
                    v.setClickable(false);
                    CircularProgressView cd =(CircularProgressView)((LoginRegistratiActivity)v.getContext()).findViewById(R.id.progress_view);
                    cd.setVisibility(View.VISIBLE);
                    PayloadBean p = new PayloadBean();
                    p.setIdSessione(idSessione);
                    p.setNuovoStato(AppConstants.REGISTRAZIONE);
                    if(checkNetwork()) new AggiornaStatoAT(v.getContext(), (AggiornaStatoTC)v.getContext(), idSessione, (LoginRegistratiActivity)v.getContext()).execute(p);
                }
                else
                    Toast.makeText(getApplicationContext(), "Impossibile passare all'Activity 'RegistrazioneActivity': Stato non raggiungibile!", Toast.LENGTH_LONG);
            }
        });
    }
    /*
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                // LOGIN
                break;
            case R.id.btn_sign_in:
                if(listaActivity.contains(RegistrazioneActivity.class)) {
                    Intent myIntent = new Intent(this, RegistrazioneActivity.class);
                    myIntent.putExtra("idSessione", idSessione);
                    startActivity(myIntent);
                    this.finish();
                }
                break;
        }
    }*/
}
