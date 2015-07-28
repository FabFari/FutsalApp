package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.ListaGruppiBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.taskcallbacks.ListaGruppiTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 28/07/2015.
 */
public class ListaGruppiAT extends AsyncTask<Void, Void, ListaGruppiBean>
{
    Context context;
    ListaGruppiTC taskCallback;

    boolean aperto;
    String citta;

    Activity activity;
    AlertDialogManager alert = new AlertDialogManager();

    public ListaGruppiAT(Context context, ListaGruppiTC taskCallback, boolean aperto, String citta, Activity activity) {
        this.context = context;
        this.aperto = aperto;
        this.citta = citta;
        this.taskCallback = taskCallback;
        this.activity = activity;
    }

    protected ListaGruppiBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Gruppo.ListaGruppi get = apiHandler.gruppo().listaGruppi(aperto, citta);
            ListaGruppiBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }

        return null;
    }

    protected void onPostExecute(ListaGruppiBean response) {
        if(response != null && (response.getHttpCode() == null || response.getHttpCode().equals(AppConstants.OK)))
            taskCallback.done(true, response.getListaGruppi());
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }

    }

}
