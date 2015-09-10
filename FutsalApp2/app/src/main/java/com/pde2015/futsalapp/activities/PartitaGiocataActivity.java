package com.pde2015.futsalapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.Campo;
import com.appspot.futsalapp_1008.pdE2015.model.Giocatore;
import com.appspot.futsalapp_1008.pdE2015.model.ListaVotiBean;
import com.appspot.futsalapp_1008.pdE2015.model.Partita;
import com.appspot.futsalapp_1008.pdE2015.model.PartitaBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.appspot.futsalapp_1008.pdE2015.model.VotoUomoPartita;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.asynctasks.AggiornaStatoAT;
import com.pde2015.futsalapp.asynctasks.CampoPartitaAT;
import com.pde2015.futsalapp.asynctasks.ClassificaVotiAT;
import com.pde2015.futsalapp.asynctasks.ElencoVotiAT;
import com.pde2015.futsalapp.asynctasks.GetPartitaAT;
import com.pde2015.futsalapp.asynctasks.HasVotatoAT;
import com.pde2015.futsalapp.asynctasks.ListaStatiAT;
import com.pde2015.futsalapp.asynctasks.SessioneIndietroAT;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import com.pde2015.futsalapp.taskcallbacks.CampoPartitaTC;
import com.pde2015.futsalapp.taskcallbacks.ClassificaVotiTC;
import com.pde2015.futsalapp.taskcallbacks.ElencoVotiTC;
import com.pde2015.futsalapp.taskcallbacks.GetPartitaTC;
import com.pde2015.futsalapp.taskcallbacks.HasVotatoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;
import com.pde2015.futsalapp.utils.CustomListClassifica;
import com.pde2015.futsalapp.utils.CustomListVoti;
import com.pde2015.futsalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PartitaGiocataActivity extends AppCompatActivity implements ListaStatiTC, AggiornaStatoTC,
        SessioneIndietroTC, ClassificaVotiTC, ElencoVotiTC, GetPartitaTC, HasVotatoTC, CampoPartitaTC {

    private final static String TAG = "PartitaGiocataActivity";
    String emailUtente;
    private Long idSessione, idPartita, idGruppo;
    private String statoSuccessivo;
    Partita partita;

    private List<Giocatore> listaGiocatori;
    private List<Integer> listaVoti;
    AlertDialogManager alert = new AlertDialogManager();
    ConnectionDetector cd;

    LinkedList<Class> listaActivity;

    TextView tipoPartitaText, proponeText, dataText, statoText, campoText;

    String[] nomiGiocatori;
    Integer[] votiGiocatori;
    TextView emptyText;
    String selectedPlayer;
    // List of names matching criteria are listed here
    private ListView myList;

    private CustomListClassifica adapterClassifica;
    CircularProgressView progressView;

    Menu menu;

    // Per Lista Voti
    CustomListVoti customVoti;
    private List<VotoUomoPartita> votiList = new ArrayList<VotoUomoPartita>();
    private ListView myList_voti;
    String mittenti[];
    String destinatari[];
    String commenti[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partita_giocata);

        Bundle extras = getIntent().getExtras();
        idSessione = (Long)extras.get("idSessione");
        emailUtente = (String)extras.get("email");
        idGruppo = (Long)extras.get("idGruppo");
        idPartita = (Long)extras.get("idPartita");

        tipoPartitaText = (TextView)findViewById(R.id.match_type);
        dataText = (TextView)findViewById(R.id.match_data);
        proponeText = (TextView)findViewById(R.id.match_propone);
        statoText = (TextView)findViewById(R.id.match_status);
        campoText = (TextView)findViewById(R.id.match_campo);

        progressView = (CircularProgressView)findViewById(R.id.progress_view);
        myList = (ListView)findViewById(R.id.match_players);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressView.setVisibility(View.VISIBLE);
                /* lancio dialog elencoVoti */
                Log.e(TAG, "position: " + position + " listaGiocatori[position]: " + listaGiocatori.get(position));
                selectedPlayer = listaGiocatori.get(position).getEmail();

                if(checkNetwork()) new ElencoVotiAT(getApplicationContext(), (ElencoVotiTC)myList.getContext(), (PartitaGiocataActivity)myList.getContext(), idSessione, idPartita, selectedPlayer).execute();
            }
        });

        if(checkNetwork()) new ListaStatiAT(getApplicationContext(), this, idSessione, this, null, idPartita).execute();

        if(checkNetwork()) new GetPartitaAT(getApplicationContext(), this, this, idSessione, idPartita).execute();

        if(checkNetwork()) new CampoPartitaAT(getApplicationContext(), this, this, idSessione, idPartita).execute();

        if(checkNetwork()) new ClassificaVotiAT(getApplicationContext(), this, this, idSessione, idPartita).execute();

        if(checkNetwork()) new HasVotatoAT(getApplicationContext(), this, this, idSessione, idPartita).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_partita_giocata, menu);
        menu.getItem(0).setEnabled(false);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.vote) {
            PayloadBean p = new PayloadBean();
            p.setIdSessione(idSessione);
            p.setNuovoStato(AppConstants.CREA_VOTO);
            if(checkNetwork()){
                progressView.setVisibility(View.VISIBLE);
                this.statoSuccessivo = AppConstants.CREA_VOTO;
                new AggiornaStatoAT(getApplicationContext(), this, idSessione, this).execute(p);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Metodo per risolvere il problema della lunghezza della lista sugli schermi più piccoli
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public void onBackPressed(){
        // Disabilitare Bottoni
        progressView.setVisibility(View.VISIBLE);
        if(checkNetwork()) {
            this.progressView.setVisibility(View.VISIBLE);
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
            alert.showAlertDialog(PartitaGiocataActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", null);
            // stop executing code by return
            return false;
        }
    }

    //Done per sessioneIndietro
    public void done(boolean res, String nuovoStato){
        Log.e(TAG, "res:  "+res);
        if(res) {
            Log.e(TAG, "nuovoStato: "+nuovoStato);
            if(nuovoStato.equals(AppConstants.STORICO)){
                Log.e(TAG, "idSessione: "+idSessione+" email: "+emailUtente+" idGruppo: "+idGruppo);
                Intent myIntent = new Intent(getApplicationContext(), StoricoActivity.class);
                myIntent.putExtra("idSessione", idSessione);
                myIntent.putExtra("email", emailUtente);
                myIntent.putExtra("idGruppo", idGruppo);
                startActivity(myIntent);
                this.finish();
            }
        }
        else progressView.setVisibility(View.GONE);
    }

    //Done per AggiornaStato
    public void done(boolean res)
    {
        if( res ) {
            if( statoSuccessivo.equals(AppConstants.CREA_VOTO)) {
                Intent i = new Intent(PartitaGiocataActivity.this, CreaVotoActivity.class);
                i.putExtra("idGruppo", idGruppo);
                i.putExtra("idSessione", idSessione);
                i.putExtra("email", emailUtente);
                i.putExtra("idPartita", idPartita);
                startActivity(i);
            }
        }
        else progressView.setVisibility(View.GONE);
    }

    //Done per ListaStati
    public void done(boolean res, List<String> listaStati, String tipoDone ) {
        if(res && listaStati != null) {
            SessionManager sm = new SessionManager(listaStati);
            listaActivity = sm.getListaActivity();
        }
    }

    //Done per ClassificaVoti
    public void done(boolean res, List<Giocatore> listaGiocatori, List<Integer> listaVoti){
        if( res && listaGiocatori != null && listaVoti != null ) {
            progressView.setVisibility(View.GONE);
            this.listaGiocatori = listaGiocatori;
            this.listaVoti = listaVoti;

            //Chiamo l'adapter
            adapterClassifica = new CustomListClassifica(PartitaGiocataActivity.this, listaGiocatori, listaVoti, emailUtente);

            this.nomiGiocatori = new String[listaGiocatori.size()];
            this.votiGiocatori = new Integer[listaVoti.size()];

            for(int i = 0; i < listaGiocatori.size(); i++){
                nomiGiocatori[i] = listaGiocatori.get(i).getNome();
                votiGiocatori[i] = listaVoti.get(i);
            }

            myList = (ListView)findViewById(R.id.match_players);
            emptyText = (TextView) findViewById(android.R.id.empty);
            myList.setEmptyView(emptyText);
            myList.setAdapter(adapterClassifica);

            setListViewHeightBasedOnItems(myList);
        }
    }

    //Done per ElencoVoti
    public void done(boolean res, ListaVotiBean listaVotiBean){
        if( res && listaVotiBean != null && listaVotiBean.getHttpCode().equals(AppConstants.OK)){
            List<VotoUomoPartita> listaVotiGiocatore = listaVotiBean.getListaVoti();
            progressView.setVisibility(View.GONE);

            //Metto i voti nel Dialog.
            if(listaVotiGiocatore == null || listaVotiGiocatore.size() == 0)
                Toast.makeText(getApplicationContext(), "Nessun voto per questo giocatore!", Toast.LENGTH_LONG).show();
            else {
                final AlertDialog.Builder votiDialog = new AlertDialog.Builder(PartitaGiocataActivity.this);
                final LayoutInflater inflater = getLayoutInflater();
                final View convertView = (View) inflater.inflate(R.layout.layout_lista_voti, null);
                votiDialog.setView(convertView);
                //invitiDialog.setTitle("Inviti");

                myList_voti = (ListView) convertView.findViewById(R.id.voti_list);
                emptyText = (TextView) convertView.findViewById(android.R.id.empty);

                customVoti = new CustomListVoti(PartitaGiocataActivity.this, listaVotiGiocatore);

                mittenti = new String[listaVotiGiocatore.size()];
                destinatari = new String[listaVotiGiocatore.size()];
                commenti = new String[listaVotiGiocatore.size()];

                for(int i = 0; i < listaVotiGiocatore.size(); i++){
                    destinatari[i] = listaVotiGiocatore.get(i).getVotatoUP();
                    mittenti[i] = listaVotiGiocatore.get(i).getVotanteUP();
                    commenti[i] = listaVotiGiocatore.get(i).getCommento();
                }

                myList_voti.setEmptyView(emptyText);
                myList_voti.setAdapter(customVoti);

                votiDialog.create();
                votiDialog.show();
            }
        }
    }

    public void done(boolean res, boolean hasVotato){
        if( res && !hasVotato){
            menu.getItem(0).setEnabled(true);
        }
        else
            menu.getItem(0).setVisible(false);
    }

    //Done per GetPartita
    public void done(boolean res, PartitaBean partitaBean){
        if(res && partitaBean != null){
            this.partita = partitaBean.getPartita();

            //Impostazione grafica
            this.dataText.setText(partitaBean.getDataString());
            if( partitaBean.getTipo() == 1 ) this.tipoPartitaText.setText("Partita di Calcetto");
            if( partitaBean.getTipo() == 2 ) this.tipoPartitaText.setText("Partita di Calciotto");
            if( partitaBean.getTipo() == 3 ) this.tipoPartitaText.setText("Partita di Calcio");
            this.proponeText.setText(partita.getPropone());
            this.statoText.setText(partita.getStatoCorrente());

            progressView.setVisibility(View.GONE);
        }
    }

    //Done per CampoPartita
    public void done(boolean res, Campo campoPartita){
        if( res && campoPartita != null ){
            this.campoText.setText(campoPartita.getNome());
        }
    }
}