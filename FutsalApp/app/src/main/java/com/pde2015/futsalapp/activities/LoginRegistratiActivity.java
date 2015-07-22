package com.pde2015.futsalapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

import java.util.*;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pde2015.futsalapp.R;

import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.taskcallbacks.LoginTC;
import com.pde2015.futsalapp.asynctasks.LoginAT;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.SessionManager;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;

// import per login google
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import android.content.IntentSender.SendIntentException;

public class LoginRegistratiActivity extends AppCompatActivity implements ListaStatiTC, AggiornaStatoTC,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener,
        LoginTC{

    String statoCorrente = AppConstants.LOGIN_E_REGISTRAZIONE;
    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;
    Long idSessione;
    LinkedList<Class> listaActivity;
    boolean sessionCreated = false;

    SharedPreferences pref;

    // Info Giocatore
    String nome;
    String email;
    String pic;

    // Per il Login Google
    private static final String TAG = "LoginRegistratiActivity";

    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    // Flags per il Sign-In
    private boolean mIntentInProgress;
    private boolean mSignInClicked;

    // Flag per distinguere Login da Registrati
    private boolean takenLogin = false;
    private boolean hasClicked = false;

    private Button btnSignIn, btnLogIn;

    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");

        // Ottenimento stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

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

    // Override per Login Google
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        //Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        CircularProgressView cd =(CircularProgressView)findViewById(R.id.progress_view);
        cd.setVisibility(View.VISIBLE);

        // Get user's information
        getProfileInformation();

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
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

    @Override
    public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "Id: "+view.getId(), Toast.LENGTH_LONG);
        switch (view.getId()) {
            case R.id.btn_login:
                //Toast.makeText(getApplicationContext(), "Bottone Cliccato!", Toast.LENGTH_LONG);
                hasClicked = true;
                view.setClickable(false);
                takenLogin = true;
                if(mConnectionResult != null)
                    signInWithGplus();
                break;
            case R.id.btn_sign_in:
                if(listaActivity.contains(RegistrazioneActivity.class)) {
                    // Per evitare duplicati
                    hasClicked = true;
                    view.setClickable(false);
                    //CircularProgressView cd =(CircularProgressView)((LoginRegistratiActivity)v.getContext()).findViewById(R.id.progress_view);
                    //cd.setVisibility(View.VISIBLE);
                    //Login Google per le informazioni utente
                    if(mConnectionResult != null)
                        signInWithGplus();
                    // Aggiornamento Stato Server
                    //PayloadBean p = new PayloadBean();
                    //p.setIdSessione(idSessione);
                    //p.setNuovoStato(AppConstants.REGISTRAZIONE);
                    //if(checkNetwork()) new AggiornaStatoAT(v.getContext(), (AggiornaStatoTC)v.getContext(), idSessione, (LoginRegistratiActivity)v.getContext()).execute(p);
                    //Intent myIntent = new Intent(this, RegistrazioneActivity.class);
                    //myIntent.putExtra("idSessione", idSessione);
                    //startActivity(myIntent);
                    //this.finish();
                }
                else
                    Toast.makeText(getApplicationContext(), "Impossibile passare all'Activity 'RegistrazioneActivity': Stato non raggiungibile!", Toast.LENGTH_LONG);
                break;
        }
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
            //aggiornaGrafica();
            // Aggiornamento Grafica e Impostazione Lisstener per login
            setContentView(R.layout.activity_login_registrati);
            btnSignIn = (Button) findViewById(R.id.btn_sign_in);
            btnLogIn = (Button) findViewById(R.id.btn_login);

            btnSignIn.setOnClickListener(this);
            btnLogIn.setOnClickListener(this);

        }

    }

    public void done(boolean res)
    {
        if( res ) {
            Intent myIntent = new Intent(getApplicationContext(), RegistrazioneActivity.class);
            myIntent.putExtra("idSessione", idSessione);
            myIntent.putExtra("nome", nome);
            myIntent.putExtra("email", email);
            myIntent.putExtra("pic", pic);
            startActivity(myIntent);
            this.finish();
        }
    }

    public void done(boolean res, DefaultBean response) {
        if(res && response.getHttpCode().equals(AppConstants.OK)) {
            Intent myIntent = new Intent(getApplicationContext(), PrincipaleActivity.class);
            myIntent.putExtra("idSessione", idSessione);
            myIntent.putExtra("email", email);
            startActivity(myIntent);
            this.finish();
        }
    }

    /*
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

                    //Login Google per le informazioni utente
                    onSignInClicked();

                    PayloadBean p = new PayloadBean();
                    p.setIdSessione(idSessione);
                    p.setNuovoStato(AppConstants.REGISTRAZIONE);
                    if(checkNetwork()) new AggiornaStatoAT(v.getContext(), (AggiornaStatoTC)v.getContext(), idSessione, (LoginRegistratiActivity)v.getContext()).execute(p);
                }
                else
                    Toast.makeText(getApplicationContext(), "Impossibile passare all'Activity 'RegistrazioneActivity': Stato non raggiungibile!", Toast.LENGTH_LONG);
            }
        });
    }*/


    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            if(mGoogleApiClient.isConnected())
                getProfileInformation();
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                nome = currentPerson.getDisplayName();
                pic = currentPerson.getImage().getUrl();
                email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                pic = pic.substring(0, pic.length() - 6);

                if(hasClicked){
                    if(takenLogin) { //Login
                       takenLogin=false;
                        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("EmailUtente", email);
                        editor.commit();
                       if(checkNetwork()) new LoginAT(getApplicationContext(), this, this, idSessione, email).execute();
                    }
                    else { // Registrati
                       PayloadBean p = new PayloadBean();
                       p.setIdSessione(idSessione);
                       p.setNuovoStato(AppConstants.REGISTRAZIONE);
                       if(checkNetwork()) new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
                    }
                }

            }
            else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
