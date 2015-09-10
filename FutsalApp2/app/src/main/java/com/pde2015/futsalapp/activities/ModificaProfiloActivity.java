package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoGiocatoreBean;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.asynctasks.SetGiocatoreAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.taskcallbacks.SetGiocatoreTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.*;

public class ModificaProfiloActivity extends AppCompatActivity implements ListaStatiTC, SetGiocatoreTC, SessioneIndietroTC, AggiornaStatoTC {

    Long idSessione;
    String email, nome, ruolo, telefono, pic;
    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();
    Spinner spinner;
    EditText emailText, nomeText, telefonoText;
    LinkedList<Class> listaActivity;
    CircularProgressView cpv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_profilo);

        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        email = (String)extras.get("email");
        nome = (String)extras.get("nome");
        ruolo = (String)extras.get("ruolo");
        telefono = (String)extras.get("telefono");
        pic = (String)extras.get("pic");

        cpv =(CircularProgressView)findViewById(R.id.progress_view);
        cpv.setVisibility(View.VISIBLE);
        // Ottenimento stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        // Costruzione schermata

        // Popolamento Spinner
        spinner = (Spinner)findViewById(R.id.ruoli_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ruoli, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(ruolo);
        spinner.setSelection(spinnerPosition);

        // Impostazione campi
        emailText = (EditText) findViewById(R.id.insertEmail);
        emailText.setText(email);
        emailText.setClickable(false);
        emailText.setFocusable(false);

        nomeText = (EditText)findViewById(R.id.insertNome);
        nomeText.setText(nome);

        ImageView immagine = (ImageView)findViewById(R.id.immagine);
        Picasso.with(ModificaProfiloActivity.this).load(pic).into(immagine);

        telefonoText = (EditText)findViewById(R.id.insertTelefono);
        telefonoText.setText(telefono);

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
        getMenuInflater().inflate(R.menu.menu_modifica_profilo, menu);
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
                if(listaActivity.contains(ProfileActivity.class)) {
                    if(nomeText.getText().toString().length() > 0 && telefonoText.getText().toString().length() > 0
                            && Integer.parseInt(telefonoText.getText().toString()) > 0) {
                        nome = nomeText.getText().toString();
                        telefono = telefonoText.getText().toString();
                        ruolo = spinner.getSelectedItem().toString();

                        InfoGiocatoreBean payload = new InfoGiocatoreBean();
                        payload.setEmail(email);
                        payload.setFotoProfilo(pic);
                        payload.setNome(nome);
                        payload.setRuoloPreferito(ruolo);
                        payload.setTelefono(telefono);

                        cpv =(CircularProgressView)findViewById(R.id.progress_view);
                        cpv.setVisibility(View.VISIBLE);

                        if(checkNetwork()) new SetGiocatoreAT(getApplicationContext(), this, this, idSessione, payload).execute();

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Compilare tutti i campi!", Toast.LENGTH_LONG).show();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "Impossibile passare all'Activity 'ProfileActivity': Stato non raggiungibile!", Toast.LENGTH_LONG);
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

            cpv =(CircularProgressView)findViewById(R.id.progress_view);
            cpv.setVisibility(View.VISIBLE);

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
            alert.showAlertDialog(ModificaProfiloActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", false);
            // stop executing code by return
            return false;
        }
    }

    public void done(boolean res, String nuovoStato) {
        cpv.setVisibility(View.GONE);
        if(res) {
            if(nuovoStato.equals(AppConstants.PROFILO)) {
                Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("emailProfilo", email);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

    public void done(boolean res, List<String> listaStati, String tipoDone) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
            cpv.setVisibility(View.GONE);
        }

    }

    public void done(boolean res, DefaultBean response) {
        if( res && response.getHttpCode().equals(AppConstants.OK)) {
            if(listaActivity.contains(ModificaProfiloActivity.class)) {
                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.PROFILO);
                cpv =(CircularProgressView)findViewById(R.id.progress_view);
                cpv.setVisibility(View.VISIBLE);
                if(checkNetwork()) new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
            }
        }
    }

    public void done(boolean res) {
        cpv.setVisibility(View.GONE);
        if(res) {
            Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
            myIntent.putExtra("idSessione", idSessione);
            myIntent.putExtra("emailProfilo", email);
            startActivity(myIntent);
            this.finish();
        }
    }

}
