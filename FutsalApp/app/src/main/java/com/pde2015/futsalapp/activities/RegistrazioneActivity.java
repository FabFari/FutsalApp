package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoGiocatoreBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.taskcallbacks.RegistrazioneTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.asynctasks.RegistrazioneAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.pde2015.futsalapp.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class RegistrazioneActivity extends AppCompatActivity implements SessioneIndietroTC, RegistrazioneTC,
            ListaStatiTC {

    private static final String TAG = "RegistrazioneActivity";

    Long idSessione;
    String email, nome, pic, ruolo;
    int telefono;
    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();
    Spinner spinner;
    EditText emailText, nomeText, telefonoText;
    LinkedList<Class> listaActivity;
    CircularProgressView cpv;

    SharedPreferences pref;

    boolean hasDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        nome = (String)extras.get("nome");
        email = (String)extras.get("email");
        pic = (String)extras.get("pic");

        cpv =(CircularProgressView)findViewById(R.id.progress_view);
        cpv.setVisibility(View.VISIBLE);
        Log.e(TAG, "Inizio ottenimento stati");
        // Ottenimento stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        // Costruzione schermata
        setContentView(R.layout.activity_registrazione);
        // Popolamento Spinner
        spinner = (Spinner)findViewById(R.id.ruoli_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ruoli, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Impostazione campi
        emailText = (EditText)findViewById(R.id.insertEmail);
        emailText.setText(email);
        emailText.setClickable(false);
        emailText.setFocusable(false);

        nomeText = (EditText)findViewById(R.id.insertNome);
        nomeText.setText(nome);

        ImageView immagine = (ImageView)findViewById(R.id.immagine);
        Picasso.with(RegistrazioneActivity.this).load(pic).into(immagine);

        telefonoText = (EditText)findViewById(R.id.insertTelefono);

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
        getMenuInflater().inflate(R.menu.menu_registrazione, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        try {
            if(id == R.id.confirm) {
                if(hasDone) {
                    Log.e(TAG, "Test1 OK");
                    if(listaActivity.contains(PrincipaleActivity.class)) {
                        Log.e(TAG, "Test2 OK");
                        if(nomeText.getText().toString().length() > 0 && telefonoText.getText().toString().length() > 0
                                && Integer.parseInt(telefonoText.getText().toString()) > 0) {
                            Log.e(TAG, "Test3 OK");
                            nome = nomeText.getText().toString();
                            telefono = Integer.parseInt(telefonoText.getText().toString());
                            ruolo = spinner.getSelectedItem().toString();

                            InfoGiocatoreBean payload = new InfoGiocatoreBean();
                            payload.setEmail(email);
                            payload.setFotoProfilo(pic);
                            payload.setNome(nome);
                            payload.setRuoloPreferito(ruolo);
                            payload.setTelefono(Integer.toString(telefono));

                            if(checkNetwork()) new RegistrazioneAT(getApplicationContext(), this, this, idSessione, payload).execute();

                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Compilare tutti i campi!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Impossibile passare all'Activity 'PrincipaleActivity': Stato non raggiungibile!", Toast.LENGTH_LONG);
                }
            }
        }
        catch(NumberFormatException e) {
            Toast.makeText(getApplicationContext(),"Inserire un numero telefonico valido!", Toast.LENGTH_LONG).show();
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
                Intent myIntent = new Intent(getApplicationContext(), LoginRegistratiActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

    public void done(boolean res, DefaultBean response) {
        if( res && response.getHttpCode().equals(AppConstants.OK)) {
            pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("EmailUtente", email);
            editor.commit();
            Intent myIntent = new Intent(getApplicationContext(), PrincipaleActivity.class);
            myIntent.putExtra("idSessione", idSessione);
            myIntent.putExtra("email", email);
            startActivity(myIntent);
            this.finish();
        }
    }

    public void done(boolean res, List<String> listaStati, String tipoDone) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
            cpv.setVisibility(View.GONE);
            hasDone = true;
            Log.e(TAG, "ListaStati Finito");
        }

    }

}
