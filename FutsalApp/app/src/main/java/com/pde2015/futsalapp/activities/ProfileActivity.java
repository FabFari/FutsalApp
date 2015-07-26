package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.GiocatoreBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.taskcallbacks.GetGiocatoreTC;
import com.pde2015.futsalapp.asynctasks.GetGiocatoreAT;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.*;

public class ProfileActivity extends AppCompatActivity implements GetGiocatoreTC, ListaStatiTC, AggiornaStatoTC,
        SessioneIndietroTC{

    private static final String TAG = "ProfileActivity";

    SharedPreferences pref;
    ImageView picImage;
    TextView nomeText, emailText, telefonoText, ruoloText;
    Long idSessione;
    String email, emailProfilo, nome, ruolo, telefono, pic;
    LinkedList<Class> listaActivity;
    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;
    boolean gone = false;
    CircularProgressView cpv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle extras = getIntent().getExtras();
        emailProfilo = (String)extras.get("emailProfilo");
        Log.e(TAG, "emailProfilo: " + emailProfilo);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        idSessione = pref.getLong("IdSessione", -1);
        email = pref.getString("EmailUtente", null);
        Log.e(TAG, "idSessione: "+ idSessione);
        Log.e(TAG, "email: "+email);

        cpv =(CircularProgressView)findViewById(R.id.progress_view);

        if(idSessione == -1 || email == null)
            alert.showAlertDialog(this,
                    "Attenzione!",
                    "Si è verificato un problema. Riprovare!", false);

        /* VISUALIZZO ACTION BAR CON LOGO */
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo_small);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // Ottenimento lista stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this, email).execute();
        if(checkNetwork()) new GetGiocatoreAT(getApplicationContext(), this, this, idSessione, emailProfilo).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(emailProfilo.equals(email))
            getMenuInflater().inflate(R.menu.menu_profile, menu);
        else
            getMenuInflater().inflate(R.menu.menu_profile_visit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.edit_profile) {
            if(listaActivity.contains(ModificaProfiloActivity.class)) {
                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.MODIFICA_PROFILO);
                CircularProgressView cd =(CircularProgressView)findViewById(R.id.progress_view);
                cd.setVisibility(View.VISIBLE);
                if(checkNetwork()) new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
            }
        }
        else if(id == R.id.show_invites) {

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

    public void done(boolean res, List<String> listaStati, String tipoDone) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
            if(!gone) {
                gone = true;
                cpv.setVisibility(View.GONE);
            }
        }

    }

    public void done(boolean res, GiocatoreBean response) {
        if(res && response.getHttpCode().equals(AppConstants.OK) && response.getGiocatore() != null) {
            if(!gone) {
                gone = true;
                cpv.setVisibility(View.GONE);
            }
            picImage = (ImageView)findViewById(R.id.imageView);
            pic = response.getGiocatore().getFotoProfilo();
            Picasso.with(ProfileActivity.this).load(pic).into(picImage);
            nomeText = (TextView)findViewById(R.id.tv_nome);
            nome = response.getGiocatore().getNome();
            nomeText.setText("Nome: " + nome);
            emailText = (TextView)findViewById(R.id.tv_email);
            email = response.getGiocatore().getEmail();
            emailText.setText("Email: " + email);
            ruoloText = (TextView)findViewById(R.id.tv_ruolo);
            ruolo = response.getGiocatore().getRuoloPreferito();
            ruoloText.setText("Ruolo Preferito: " + ruolo);
            telefonoText = (TextView)findViewById(R.id.tv_tel);
            telefono = response.getGiocatore().getTelefono();
            telefonoText.setText("Telefono: " + telefono);
        }
    }

    public void done(boolean res)
    {
        if( res ) {
            Intent myIntent = new Intent(getApplicationContext(), ModificaProfiloActivity.class);
            myIntent.putExtra("idSessione", idSessione);
            myIntent.putExtra("nome", nome);
            myIntent.putExtra("email", email);
            myIntent.putExtra("ruolo", ruolo);
            myIntent.putExtra("telefono", telefono);
            myIntent.putExtra("pic", pic);
            startActivity(myIntent);
            this.finish();
        }
    }

    public void done(boolean res, String nuovoStato) {
        cpv.setVisibility(View.GONE);
        if(res) {
            if(nuovoStato.equals(AppConstants.PRINCIPALE)) {
                Intent myIntent = new Intent(getApplicationContext(), PrincipaleActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", email);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

    public void done(boolean res, List<Invito> listaInviti) {
        if(res && listaInviti != null) {

        }

    }

    public boolean checkNetwork() {
        cd = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        if (cd.isConnectingToInternet()) {
            return true;
        }else{
            // Internet Connection is not present
            alert.showAlertDialog(ProfileActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", false);
            // stop executing code by return
            return false;
        }
    }
}
