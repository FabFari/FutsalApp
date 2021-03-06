package com.pde2015.futsalapp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.model.InfoPartitaBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.CreaPartitaAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.CreaPartitaTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import com.google.api.client.util.DateTime;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class CreaPartitaActivity extends AppCompatActivity implements View.OnClickListener, ListaStatiTC, CreaPartitaTC, AggiornaStatoTC, SessioneIndietroTC {

    // Widget GUI
    Button btnCalendar, btnTimePicker;
    EditText txtDate, txtTime;
    RadioGroup matchTypeGroup;
    RadioButton radioCalcio, radioCalciotto, radioCalcetto;
    ImageView matchLogo1, matchLogo2;

    // Variable for storing match info
    private int mYear, mMonth, mDay, mHour, mMinute;
    private int tipoPartita;
    private Long idPartitaCreata;

    //Activity info
    private final String TAG = "CreaGruppoActivity";
    private Long idSessione;
    private Long idGruppo;
    String emailUtente;

    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;

    String statoSuccessivo;
    LinkedList<Class> listaActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_partita);

        // Ottenimento parametri da schermata precedente
        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");
        idGruppo = (Long)extras.get("idGruppo");

        matchTypeGroup = (RadioGroup)findViewById(R.id.create_match_type);
        radioCalcio = (RadioButton)findViewById(R.id.calcio_radio);
        radioCalciotto = (RadioButton)findViewById(R.id.calciotto_radio);
        radioCalcetto = (RadioButton)findViewById(R.id.calcetto_radio);

        matchLogo1 = (ImageView)findViewById(R.id.create_match_icon_1);
        matchLogo2 = (ImageView)findViewById(R.id.create_match_icon_2);

        btnCalendar = (Button)findViewById(R.id.btnCalendar);
        btnCalendar.setOnClickListener(this);
        btnTimePicker = (Button)findViewById(R.id.btnTimePicker);
        btnTimePicker.setOnClickListener(this);

        txtDate = (EditText)findViewById(R.id.txtDate);
        txtTime = (EditText)findViewById(R.id.txtTime);

    }

    @Override
    public void onClick(View v) {
        // Process to get Current Time
        final Calendar c = Calendar.getInstance();
        switch(v.getId()) {
            case R.id.btnCalendar:
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                txtDate.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
                break;
            case R.id.btnTimePicker:
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Display Selected time in textbox
                                txtTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                tpd.show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crea_partita, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.create_match_confirm) {
            if(txtDate.getText().toString().length() > 0 && txtTime.getText().toString().length() > 0) {

                Calendar dataCalendar = new GregorianCalendar();
                dataCalendar.set(this.mYear, this.mMonth, this.mDay, this.mHour, this.mMinute);
                Date dataDate = dataCalendar.getTime();
                DateTime dataOraPartita = new DateTime(dataDate);

                int selectedId = matchTypeGroup.getCheckedRadioButtonId();
                if( selectedId == R.id.calcetto_radio ) tipoPartita = 1;
                else if( selectedId == R.id.calciotto_radio ) tipoPartita = 2;
                else if( selectedId == R.id.calcio_radio ) tipoPartita = 3;

                InfoPartitaBean partitaBean = new InfoPartitaBean();
                partitaBean.setDataOraPartita(dataOraPartita);
                partitaBean.setTipo(tipoPartita);

                if(checkNetwork()) new CreaPartitaAT(getApplicationContext(), this, this, idSessione, idGruppo, partitaBean).execute();
            }
            else {
                Toast.makeText(getApplicationContext(), "Compilare tutti i campi!", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onRadioButtonClicked(View v) {
        switch(v.getId()) {
            case R.id.calcetto_radio:
                matchLogo1.setImageResource(R.drawable.calcetto_icon);
                matchLogo2.setImageResource(R.drawable.calcetto_icon);
                break;
            case R.id.calciotto_radio:
                matchLogo1.setImageResource(R.drawable.calciotto_icon);
                matchLogo2.setImageResource(R.drawable.calciotto_icon);
                break;
            case R.id.calcio_radio:
                matchLogo1.setImageResource(R.drawable.calcio_icon);
                matchLogo2.setImageResource(R.drawable.calcio_icon);
                break;
        }
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
            alert.showAlertDialog(CreaPartitaActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", false);
            // stop executing code by return
            return false;
        }
    }

    /**************************
     *** INTERAZIONE SERVER ***
     **************************/

    // Done per Lista stati - ho dovuto modificarlo
    public void done(boolean res, List<String> listaStati, String tipo) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
        }
    }

    //Done per CreaPartita
    public void done(boolean res, Long idPartitaCreata){
        if( res && idPartitaCreata != null) {
            this.idPartitaCreata = idPartitaCreata;
            this.statoSuccessivo = AppConstants.PARTITA;

            PayloadBean p = new PayloadBean();
            p.setIdSessione(this.idSessione);
            p.setNuovoStato(AppConstants.PARTITA);
            if(checkNetwork()) new AggiornaStatoAT(getApplicationContext(), this, idSessione, CreaPartitaActivity.this).execute(p);
        }
    }

    //Done per AggiornaStato
    public void done(boolean res)
    {
        if( res ) {
            //Che faccio?
            if( statoSuccessivo.equals(AppConstants.PARTITA)) {
                Intent i = new Intent(CreaPartitaActivity.this, PartitaActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idPartita", idPartitaCreata);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                startActivity(i);
                this.finish();
            }
        }
    }

    //Done per sessioneIndietro
    public void done(boolean res, String nuovoStato){
        if(res) {
            if(nuovoStato.equals(AppConstants.GRUPPO)) {
                Intent myIntent = new Intent(getApplicationContext(), GruppoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", this.idGruppo);
                startActivity(myIntent);
                this.finish();
            }
        }
    }
}
