package com.pde2015.futsalapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton;

import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.InfoCampoBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.taskcallbacks.CreaCampoTC;
import com.pde2015.futsalapp.asynctasks.CreaCampoAT;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.PlacesAutoCompleteAdapter;
import com.pde2015.futsalapp.utils.SessionManager;
import com.pde2015.futsalapp.utils.StreetAutoCompleteAdapter;

import java.util.LinkedList;
import java.util.List;

public class CreaCampoActivity extends AppCompatActivity implements SessioneIndietroTC, ListaStatiTC, CreaCampoTC,
        View.OnClickListener, AggiornaStatoTC {

    private static final String TAG = "CreaCampoActivity";

    EditText nomeText, telefonoText, prezzoText;
    AutoCompleteTextView cittaText, indirizzoText;
    CheckBox lun_cb, mar_cb, mer_cb, ven_cb, gio_cb, sab_cb, dom_cb;
    Button giorniChiusura;

    Long idSessione, idCampoCreato, idGruppo, idPartita;
    String emailUtente;

    String nome, telefono, citta, indirizzo;
    float prezzo;
    int telefonoInt;
    boolean lun_ck, mar_ck, mer_ck, gio_ck, ven_ck, sab_ck, dom_ck;

    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();
    CircularProgressView cpv;

    LinkedList<Class> listaActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_campo);

        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        idGruppo = (Long)extras.get("idGruppo");
        idPartita = (Long)extras.get("idPartita");
        emailUtente = (String)extras.get("email");

        cpv =(CircularProgressView)findViewById(R.id.progress_view);
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crea_campo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.confirm_create_field) {
            if(listaActivity.contains(CampoActivity.class)) {
                if (nomeText.getText().toString().length() > 0 && telefonoText.getText().toString().length() > 0 &&
                        prezzoText.getText().toString().length() > 0 && cittaText.getText().toString().length() > 0 &&
                        indirizzoText.getText().toString().length() > 0) {
                    try {
                        prezzo = Float.parseFloat(prezzoText.getText().toString());
                        //telefonoInt = Integer.parseInt(telefonoText.getText().toString());
                        if (prezzo > 0.0 /*&& telefonoInt > 0*/) {
                            nome = nomeText.getText().toString();
                            telefono = telefonoText.getText().toString();
                            citta = cittaText.getText().toString();
                            indirizzo = indirizzoText.getText().toString();

                            InfoCampoBean payload = new InfoCampoBean();
                            payload.setNome(nome);
                            payload.setTelefono(telefono);
                            payload.setPrezzo(prezzo);
                            payload.setCitta(citta);
                            payload.setIndirizzo(indirizzo);
                            LinkedList<String> giorniChiusura = new LinkedList<String>();
                            if (lun_ck) giorniChiusura.add("Lunedì");
                            if (mar_ck) giorniChiusura.add("Martedì");
                            if (mer_ck) giorniChiusura.add("Mercoledì");
                            if (gio_ck) giorniChiusura.add("Giovedì");
                            if (ven_ck) giorniChiusura.add("Venerdì");
                            if (sab_ck) giorniChiusura.add("Sabato");
                            if (dom_ck) giorniChiusura.add("Domenica");
                            payload.setGiorniChiusura(giorniChiusura);

                            if (checkNetwork()) {
                                cpv.setVisibility(View.VISIBLE);
                                new CreaCampoAT(getApplicationContext(), this, this, idSessione, payload).execute();
                            }

                        } else
                            Toast.makeText(getApplicationContext(), "Prezzo inserito non valido!", Toast.LENGTH_LONG).show();
                    }
                    catch(NumberFormatException e) {
                        Toast.makeText(getApplicationContext(),"Prezzo inserito non valido!", Toast.LENGTH_LONG).show();
                    }

                }
                else
                    Toast.makeText(getApplicationContext(), "Compilare tutti i campi!", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getApplicationContext(), "Impossibile passare all'Activity 'CampoActivity': Stato non raggiungibile!", Toast.LENGTH_LONG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.giorniChiusura:
                final AlertDialog.Builder giorniChiusuraDialog = new AlertDialog.Builder(CreaCampoActivity.this);
                final LayoutInflater inflater = getLayoutInflater();
                final View convertView = (View) inflater.inflate(R.layout.giorni_chiusura_layout, null);
                giorniChiusuraDialog.setView(convertView);
                giorniChiusuraDialog.setTitle("Giorni Chiusura");

                lun_cb = (CheckBox)convertView.findViewById(R.id.lunedi_cb);
                lun_cb.setChecked(lun_ck);
                lun_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        lun_ck = isChecked;
                    }
                });
                mar_cb = (CheckBox)convertView.findViewById(R.id.martedi_cb);
                mar_cb.setChecked(mar_ck);
                mar_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mar_ck = isChecked;
                    }
                });
                mer_cb = (CheckBox)convertView.findViewById(R.id.mercoledi_cb);
                mer_cb.setChecked(mer_ck);
                mer_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mer_ck = isChecked;
                    }

                });
                gio_cb = (CheckBox)convertView.findViewById(R.id.giovedi_cb);
                gio_cb.setChecked(gio_ck);
                gio_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        gio_ck = isChecked;
                    }

                });
                ven_cb = (CheckBox)convertView.findViewById(R.id.venerdi_cb);
                ven_cb.setChecked(ven_ck);
                ven_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ven_ck = isChecked;
                    }

                });
                sab_cb = (CheckBox)convertView.findViewById(R.id.sabato_cb);
                sab_cb.setChecked(sab_ck);
                sab_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sab_ck = isChecked;
                    }

                });
                dom_cb = (CheckBox)convertView.findViewById(R.id.domenica_cb);
                dom_cb.setChecked(dom_ck);
                dom_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        dom_ck = isChecked;
                    }

                });

                giorniChiusuraDialog.create();
                giorniChiusuraDialog.show();

                break;
        }
    }

    public void onBackPressed(){
        if(checkNetwork()) {
            PayloadBean bean = new PayloadBean();
            bean.setIdSessione(idSessione);
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
            alert.showAlertDialog(CreaCampoActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", false);
            // stop executing code by return
            return false;
        }
    }

    public void done(boolean res, List<String> listaStati, String tipoDone) {
        cpv.setVisibility(View.GONE);
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();

            nomeText = (EditText)findViewById(R.id.insertNome);
            telefonoText = (EditText)findViewById(R.id.insertTelefono);
            prezzoText = (EditText)findViewById(R.id.insertPrezzo);

            giorniChiusura = (Button)findViewById(R.id.giorniChiusura);
            giorniChiusura.setOnClickListener(this);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setLogo(R.drawable.logo_small);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            cittaText = (AutoCompleteTextView) findViewById(R.id.insertCitta);
            cittaText.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
            cittaText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String str = (String) parent.getItemAtPosition(position);
                    Log.d("CITTA", str);
                }
            });

            indirizzoText = (AutoCompleteTextView) findViewById(R.id.insertIndirizzo);
            indirizzoText.setAdapter(new StreetAutoCompleteAdapter(this, R.layout.list_item));
            indirizzoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String str = (String) parent.getItemAtPosition(position);
                    Log.d("INDIRIZZO", str);
                }
            });

        }

    }

    public void done(boolean res, Long idCampo){
        if( res ){
            this.idCampoCreato = idCampo;
            Log.e(TAG, "idCampoCreato: "+idCampoCreato);
            PayloadBean p = new PayloadBean();
            p.setIdSessione(idSessione);
            if(checkNetwork()){
                p.setNuovoStato(AppConstants.CAMPO);
                new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
            }
        }
    }

    public void done(boolean res) {
        cpv.setVisibility(View.GONE);
        if( res ) {
            Intent myIntent = new Intent(CreaCampoActivity.this, CampoActivity.class);
            myIntent.putExtra("idSessione", idSessione);
            myIntent.putExtra("idGruppo", idGruppo);
            myIntent.putExtra("idPartita", idPartita);
            myIntent.putExtra("email", emailUtente);
            myIntent.putExtra("idCampo", idCampoCreato);
            startActivity(myIntent);
            this.finish();
        }
    }

    public void done(boolean res, String nuovoStato) {
        if(res) {
            Intent myIntent = new Intent(CreaCampoActivity.this, RicercaCampoActivity.class);
            myIntent.putExtra("idSessione", idSessione);
            myIntent.putExtra("idGruppo", idGruppo);
            myIntent.putExtra("idPartita", idPartita);
            myIntent.putExtra("email", emailUtente);
            startActivity(myIntent);
            this.finish();
        }
    }

}
