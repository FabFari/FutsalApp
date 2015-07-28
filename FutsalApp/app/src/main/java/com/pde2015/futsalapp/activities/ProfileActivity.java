package com.pde2015.futsalapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.GiocatoreBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoInvitoBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.ListaInvitiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaInvitiTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.taskcallbacks.GetGiocatoreTC;
import com.pde2015.futsalapp.asynctasks.GetGiocatoreAT;
import com.pde2015.futsalapp.taskcallbacks.RispondiInvitoTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListInviti;
import com.pde2015.futsalapp.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.*;

public class ProfileActivity extends AppCompatActivity implements GetGiocatoreTC, ListaStatiTC, AggiornaStatoTC,
        SessioneIndietroTC, RispondiInvitoTC, ListaInvitiTC {

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
    String statoSuccessivo;
    Long selectedGroup;
    TextView emptyText;

    // Per Lista Inviti
    CustomListInviti customInviti;
    private List<InfoInvitoBean> invitiList = new ArrayList<InfoInvitoBean>();
    private ListView myList;
    String nomiGruppi[];
    String mittenti[];
    ImageButton acceptButtons[];
    ImageButton declineButtons[];

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

        emptyText = (TextView) findViewById(android.R.id.empty);

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
                if(checkNetwork()) {
                    statoSuccessivo = AppConstants.MODIFICA_PROFILO;
                    new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
                }
            }
        }
        else if(id == R.id.show_invites) {
            if(checkNetwork()) {
                Log.e(TAG, "Bottone Mostra Inviti cliccato!");
                cpv.setVisibility(View.VISIBLE);
                new ListaInvitiAT(getApplicationContext(), this, idSessione, this).execute();
            }
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
            if(statoSuccessivo.equals(AppConstants.MODIFICA_PROFILO)) {
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
            else {
                Log.e(TAG, "Done AggiornaStato!");
                Intent i = new Intent(getApplicationContext(), GruppoActivity.class);
                i.putExtra("idGruppo", selectedGroup);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailProfilo);
                startActivity(i);
                this.finish();
            }
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

    public void done(boolean res, List<InfoInvitoBean> listaInviti) {
        Log.e(TAG, "Done ListaInviti!");
        Log.e(TAG, "res: "+res+" listaInviti: "+listaInviti);
        if(res) {
            Log.e(TAG, "Nell'if di Done ListaInviti!");
            cpv.setVisibility(View.GONE);
            if(listaInviti == null || listaInviti.size() == 0)
                Toast.makeText(getApplicationContext(), "Nessun nuovo invito ricevuto!", Toast.LENGTH_LONG).show();
            else {
                final AlertDialog.Builder invitiDialog = new AlertDialog.Builder(ProfileActivity.this);
                final LayoutInflater inflater = getLayoutInflater();
                final View convertView = (View) inflater.inflate(R.layout.custom_autocomplete_listview, null);
                invitiDialog.setView(convertView);
                //invitiDialog.setTitle("Inviti");

                myList = (ListView) convertView.findViewById(R.id.customlistView);
                emptyText = (TextView) convertView.findViewById(android.R.id.empty);

                invitiList = listaInviti;

                customInviti = new CustomListInviti(ProfileActivity.this, this, invitiList, myList, idSessione);

                nomiGruppi = new String[invitiList.size()];
                mittenti = new String[invitiList.size()];

                for(int i = 0; i < invitiList.size(); i++){
                    nomiGruppi[i] = invitiList.get(i).getNomeGruppo();
                    mittenti[i] = invitiList.get(i).getEmailMittente();
                }

                myList.setEmptyView(emptyText);
                myList.setAdapter(customInviti);

                invitiDialog.create();
                invitiDialog.show();

            }
        }

    }

    public void done(boolean res, DefaultBean response, Long idGruppo, boolean answer) {
        cpv.setVisibility(View.GONE);
        if(res && response.getHttpCode().equals(AppConstants.OK)) {
            Log.e(TAG, "Done RispondiInvito!");
            if(checkNetwork() && answer) {
                cpv.setVisibility(View.VISIBLE);
                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.GRUPPO);
                selectedGroup = idGruppo;
                statoSuccessivo = AppConstants.GRUPPO;
                new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
            }
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
