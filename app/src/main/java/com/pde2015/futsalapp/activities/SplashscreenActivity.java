package com.pde2015.futsalapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pde2015.futsalapp.R;

import com.pde2015.futsalapp.taskcallbacks.InserisciSessioneTC;
import com.pde2015.futsalapp.asynctasks.InserisciSessioneAT;

public class SplashscreenActivity extends AppCompatActivity implements InserisciSessioneTC {

    // Id della sessione con il server
    Long idSessione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        new InserisciSessioneAT(getApplicationContext(), this).execute();
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
        }
        else
        Toast.makeText(this, "Si è verificato un problema. Riprovare!", Toast.LENGTH_LONG).show();
    }
}
