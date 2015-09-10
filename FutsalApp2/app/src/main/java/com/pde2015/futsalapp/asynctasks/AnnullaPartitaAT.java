package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoGestionePartiteBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.taskcallbacks.AnnullaPartitaTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 30/07/2015.
 */
public class AnnullaPartitaAT extends AsyncTask<Void, Void, DefaultBean> {

    private final static String TAG = "AnnullaPartitaAT";

    Context context;
    AnnullaPartitaTC taskCallback;
    Activity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    InfoGestionePartiteBean gestioneBean;

    public AnnullaPartitaAT(Context context, AnnullaPartitaTC taskCallback, Long idSessione, InfoGestionePartiteBean gestioneBean, Activity activity) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.idSessione = idSessione;
        this.gestioneBean = gestioneBean;
        this.activity = activity;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Api.AnnullaPartita put = apiHandler.api().annullaPartita(idSessione, gestioneBean);

            DefaultBean response = put.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, false, "AnnullaPartita");
        }
        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null && response.getHttpCode().equals(AppConstants.OK)) {
            taskCallback.done(true, true, "AnnullaPartita");
            Log.e(TAG, "Partita Annullata!");
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, false, "AnnullaPartita");
        }

    }
}
