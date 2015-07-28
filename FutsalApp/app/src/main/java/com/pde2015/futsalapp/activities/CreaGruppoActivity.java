package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.pde2015.futsalapp.R;

import com.appspot.futsalapp_1008.pdE2015.model.InfoGruppoBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.CreaGruppoAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.CreaGruppoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.LinkedList;
import java.util.List;

public class CreaGruppoActivity extends AppCompatActivity implements ListaStatiTC, CreaGruppoTC, AggiornaStatoTC, SessioneIndietroTC {

    private static final String TAG = "CreaGruppoActivity";

    String statoCorrente = AppConstants.CREA_GRUPPO;
    private Long idSessione;
    String emailUtente;

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;

    LinkedList<Class> listaActivity;

    Long idGruppoCreato;
    EditText groupNameText, cityText;
    RadioButton groupOpen, groupClosed;
    RadioGroup groupType;
    ImageView groupLogo;
    TextView groupTypeDescr;
    String nome, city;
    boolean aperto = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_gruppo);

        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");
        Log.e(TAG, "EmailUtente: "+emailUtente);

        // Ottenimento stati
        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this).execute();

        groupNameText = (EditText)findViewById(R.id.create_group_name);
        cityText = (EditText)findViewById(R.id.create_group_city);
        groupType = (RadioGroup)findViewById(R.id.create_group_type);
        groupOpen = (RadioButton)findViewById(R.id.group_open_radio);
        groupClosed = (RadioButton)findViewById(R.id.group_closed_radio);
        groupLogo = (ImageView)findViewById(R.id.create_group_logo);
        groupTypeDescr = (TextView)findViewById(R.id.group_type_description);
    /*
        groupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                // TODO Auto-generated method stub
                // checkedId is the RadioButton selected

                if( checkedId == R.id.group_closed_radio ){
                    groupLogo.setBackgroundResource(R.drawable.group_closed);
                    groupTypeDescr.setText(R.string.group_closed_description);
                }
                else{
                    groupLogo.setBackgroundResource(R.drawable.group_open);
                    groupTypeDescr.setText(R.string.group_open_description);
                }
            }
        }); */
    }

    public void onRadioButtonClicked(View v){
        if( v.getId() == R.id.group_closed_radio ){
            groupLogo.setBackgroundResource(R.drawable.group_closed);
            groupLogo.setBackgroundColor(R.color.color_primary);
            groupTypeDescr.setText(R.string.group_closed_description);
            Log.e(TAG, "Cliccato: "+v.getId()+"! Setto: "+ R.drawable.group_closed+", "+ R.color.color_primary+", "+R.string.group_closed_description);
        }
        else if(v.getId() == R.id.group_open_radio){
            groupLogo.setBackgroundResource(R.drawable.group_open);
            groupLogo.setBackgroundColor(R.color.color_primary);
            groupTypeDescr.setText(R.string.group_open_description);
            Log.e(TAG, "Cliccato: "+v.getId()+"! Setto: "+ R.drawable.group_open+", "+ R.color.color_primary+", "+R.string.group_open_description);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crea_gruppo, menu);
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

        if(id == R.id.confirm_create_group) {
            if(groupNameText.getText().toString().length() > 0 && cityText.getText().toString().length() > 0) {
                nome = groupNameText.getText().toString();
                city = cityText.getText().toString();
                int selectedId = groupType.getCheckedRadioButtonId();
                if( selectedId == R.id.group_closed_radio ) aperto = false;

                InfoGruppoBean gruppoBean = new InfoGruppoBean();
                gruppoBean.setNome(nome);
                gruppoBean.setCitta(city);
                gruppoBean.setAperto(aperto);
                if(checkNetwork()) new CreaGruppoAT(getApplicationContext(), this, this, idSessione, gruppoBean).execute();

            }
            else {
                Toast.makeText(getApplicationContext(), "Compilare tutti i campi!", Toast.LENGTH_LONG).show();
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
            alert.showAlertDialog(CreaGruppoActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

    //Done per ListaStati - ho dovuto aggiungere la stringa perche' altrimenti
    // il done si sarebbe sovrapposto a quello di ListaGruppi
    public void done(boolean res, List<String> listaStati, String tipoDone ) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
        }
    }

    // Done per creaGruppo; se è andato tutto a buon fine, chiamo aggiornaStato
    public void done(boolean res, Long idGruppo){
        if( res ){
            this.idGruppoCreato = idGruppo;
            Log.e(TAG, "idGruppoCreato: "+idGruppoCreato);
            PayloadBean p = new PayloadBean();
            p.setIdSessione(idSessione);
            if(checkNetwork()){
                p.setNuovoStato(AppConstants.GRUPPO);
                new AggiornaStatoAT(getApplicationContext(), (AggiornaStatoTC)this, idSessione, (CreaGruppoActivity)this).execute(p);
            }
        }
    }

    //Done per AggiornaStato
    public void done(boolean res)
    {
        if( res ) {

            Intent i = new Intent(CreaGruppoActivity.this, GruppoActivity.class);
            i.putExtra("idGruppo", idGruppoCreato);
            i.putExtra("idSessione", idSessione);
            i.putExtra("email", emailUtente);
            Log.e(TAG, "EMAIL="+emailUtente);
            startActivity(i);

        }
    }

    //Done per sessioneIndietro
    public void done(boolean res, String nuovoStato) {
        if(res) {
            if(nuovoStato.equals(AppConstants.PRINCIPALE)) {
                Intent myIntent = new Intent(getApplicationContext(), PrincipaleActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                startActivity(myIntent);
                this.finish();
            }
        }
    }
}
