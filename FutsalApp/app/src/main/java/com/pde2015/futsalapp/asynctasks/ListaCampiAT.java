package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.ListaCampiBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.taskcallbacks.ListaCampiTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 28/07/2015.
 */
public class ListaCampiAT extends AsyncTask<Void, Void, ListaCampiBean>
{
    Context context;
    ListaCampiTC taskCallback;

    boolean aperto;
    String citta;

    Activity activity;
    AlertDialogManager alert = new AlertDialogManager();

    public ListaCampiAT(Context context, ListaCampiTC taskCallback, Activity activity) {
        this.context = context;
        this.aperto = aperto;
        this.citta = citta;
        this.taskCallback = taskCallback;
        this.activity = activity;
    }

    protected ListaCampiBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Campo.ListaCampi get = apiHandler.campo().listaCampi();
            ListaCampiBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }

        return null;
    }

    protected void onPostExecute(ListaCampiBean response) {
        if(response != null)
            taskCallback.done(true, response.getListaCampi());
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }

    }

}
