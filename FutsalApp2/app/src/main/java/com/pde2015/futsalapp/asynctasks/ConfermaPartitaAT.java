package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.R;
import com.pde2015.futsalapp.taskcallbacks.ConfermaPartitaTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 30/07/2015.
 */
public class ConfermaPartitaAT extends AsyncTask<Void, Void, DefaultBean>
{

    private final static String TAG = "PartitaActivity";


    Context context;
    ConfermaPartitaTC taskCallback;

    Long idSessione, idPartita;

    Activity activity;
    AlertDialogManager alert = new AlertDialogManager();

    public ConfermaPartitaAT(Context context, ConfermaPartitaTC taskCallback, Long idSessione, Long idPartita, Activity activity) {
        this.context = context;
        this.idPartita = idPartita;
        this.idSessione = idSessione;
        this.taskCallback = taskCallback;
        this.activity = activity;
    }

    protected DefaultBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Api.ConfermaPartita post = apiHandler.api().confermaPartita(idPartita, idSessione);
            DefaultBean response = post.execute();
        return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, false, "ConfermaPartita");
        }

        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null && (response.getHttpCode() == null || response.getHttpCode().equals(AppConstants.OK))) {
            taskCallback.done(true, true, "ConfermaPartita");
            Log.e(TAG, "Partita Confermata!");
        }
        else if(response != null) {
            Toast.makeText(context, response.getResult(), Toast.LENGTH_LONG).show();
            taskCallback.done(true, false, "ConfermaPartita");
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, false, "ConfermaPartita");
        }
    }
}
