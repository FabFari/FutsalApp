package com.pde2015.futsalapp.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.appspot.futsalapp_1008.pdE2015.model.Giocatore;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaIscrittiGruppoTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListIscritti;

import java.util.LinkedList;
import java.util.List;

public class IscrittiGruppoActivity extends AppCompatActivity implements AggiornaStatoTC, SessioneIndietroTC, ListaIscrittiGruppoTC {

    private final static String TAG = "IscrittiGruppoActivity";
    private final static String statoCorrente = AppConstants.ISCRITTI_GRUPPO;
    String emailUtente;
    private Long idSessione;
    private Long idGruppo;
    private String nuovoStato;

    private List<Giocatore> listaIscritti;
    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;

    LinkedList<Class> listaActivity;

    String[] nomiGiocatori;
    TextView emptyText;
    String selectedPlayer;
    // List of names matching criteria are listed here
    private ListView myList;

    private CustomListIscritti adapterIscritti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iscritti_gruppo);

        myList = (ListView)findViewById(R.id.iscritti_gruppo_list);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PayloadBean p = new PayloadBean();
                p.setIdSessione(idSessione);
                p.setNuovoStato(AppConstants.PROFILO);
                if (checkNetwork()) {
                    nuovoStato = AppConstants.PROFILO;
                    new AggiornaStatoAT(myList.getContext(), (AggiornaStatoTC) myList.getContext(), idSessione, (IscrittiGruppoActivity) myList.getContext()).execute(p);
                }
                selectedPlayer = listaIscritti.get(position).getEmail();

            }
        });
    }

    public boolean checkNetwork() {
        cd = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        if (cd.isConnectingToInternet()) {
            return true;
        }else{
            // Internet Connection is not present
            alert.showAlertDialog(IscrittiGruppoActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_iscritti_gruppo, menu);
        return true;
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

    //Done per aggiornaStato
    public void done(boolean res) {
        if (res) {
            if (this.nuovoStato.equals(AppConstants.PROFILO)) {
                Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("emailProfilo", selectedPlayer);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
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
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            }
        }
    }

    //Done per listaIscrittiGruppo
    public void done(boolean res, List<Giocatore> listaGiocatori ){
        if( res && listaGiocatori != null){
            this.listaIscritti = listaGiocatori;

            //Chiamo l'adapter
            adapterIscritti = new CustomListIscritti(IscrittiGruppoActivity.this, listaGiocatori, this.emailUtente);

            nomiGiocatori = new String[listaGiocatori.size()];

            for(int i = 0; i < listaGiocatori.size(); i++){
                nomiGiocatori[i] = listaGiocatori.get(i).getNome();
            }

            myList = (ListView)findViewById(R.id.listView1);
            emptyText = (TextView) findViewById(android.R.id.empty);
            myList.setEmptyView(emptyText);
            myList.setAdapter(adapterIscritti);
        }
    }
}
