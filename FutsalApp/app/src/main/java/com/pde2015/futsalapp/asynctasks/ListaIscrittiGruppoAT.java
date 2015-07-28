package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.ListaGiocatoriBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.taskcallbacks.ListaIscrittiGruppoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 26/07/2015.
 */
public class ListaIscrittiGruppoAT extends AsyncTask<Void, Void, ListaGiocatoriBean>{

    Context context;
    ListaIscrittiGruppoTC taskCallback;
    Long idSessione, idGruppo;
    Activity activity;
    AlertDialogManager alert = new AlertDialogManager();

    public ListaIscrittiGruppoAT(Context context, ListaIscrittiGruppoTC taskCallback, Long idSessione, Long idGruppo, Activity activity) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.idSessione = idSessione;
        this.idGruppo = idGruppo;
        this.activity = activity;
    }

    protected ListaGiocatoriBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Api.ListaIscrittiGruppo get = apiHandler.api().listaIscrittiGruppo(idSessione, idGruppo);
            ListaGiocatoriBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(true, null);
        }

        return null;
    }

    protected void onPostExecute(ListaGiocatoriBean response) {
        if(response != null && response.getHttpCode().equals(AppConstants.OK))
            taskCallback.done(true, response.getListaGiocatori());
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }

    }

}
