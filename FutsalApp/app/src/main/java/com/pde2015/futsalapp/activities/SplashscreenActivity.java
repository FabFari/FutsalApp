package com.pde2015.futsalapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Intent;

import com.pde2015.futsalapp.R;

import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.utils.ConnectionDetector;

import com.pde2015.futsalapp.taskcallbacks.InserisciSessioneTC;
import com.pde2015.futsalapp.asynctasks.InserisciSessioneAT;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

public class SplashscreenActivity extends AppCompatActivity implements InserisciSessioneTC {

    // Id della sessione con il server
    Long idSessione;

    // Controllo connessione
    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        if(checkNetwork()) new InserisciSessioneAT(getApplicationContext(), SplashscreenActivity.this ,this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splashscreen, menu);
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

    public void done(boolean response, Long idSessione) {
        if(response && idSessione != null) {
            this.idSessione = idSessione;
            Intent myIntent = new Intent(this, LoginRegistratiActivity.class);
            myIntent.putExtra("idSessione", idSessione);
            startActivity(myIntent);
            this.finish();
        }
        //else
            //Toast.makeText(getApplicationContext(), "Si è verificato un problema. Riprovare!", Toast.LENGTH_LONG).show();
    }

    public boolean checkNetwork() {
        cd = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        if (cd.isConnectingToInternet()) {
            return true;
        }else{
            // Internet Connection is not present
            alert.showAlertDialog(SplashscreenActivity.this,
                    "Connessione Internet assente",
                    "Controlla la tua connessione internet!", false);
            // stop executing code by return
            return false;
        }
    }

}
