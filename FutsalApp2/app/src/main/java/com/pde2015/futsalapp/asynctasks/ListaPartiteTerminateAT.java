package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.ListaPartiteBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.taskcallbacks.ListaPartiteTerminateTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 29/07/2015.
 */
public class ListaPartiteTerminateAT extends AsyncTask<Void, Void, ListaPartiteBean> {

    Context context;
    ListaPartiteTerminateTC taskCallback;
    Long idSessione;
    int tipoPartite;
    Long idGruppo;
    Activity activity;
    AlertDialogManager alert = new AlertDialogManager();

    public ListaPartiteTerminateAT(Context context, ListaPartiteTerminateTC taskCallback, Long idGruppo, Long idSessione, int tipoPartite, Activity activity) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.idGruppo = idGruppo;
        this.idSessione = idSessione;
        this.tipoPartite = tipoPartite;
        this.activity = activity;
    }

    protected ListaPartiteBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Api.ListaPartiteTerminate get = apiHandler.api().listaPartiteTerminate(idGruppo, idSessione, tipoPartite);
            ListaPartiteBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }

        return null;
    }

    protected void onPostExecute(ListaPartiteBean response) {
        if(response != null && response.getHttpCode().equals(AppConstants.OK))
            taskCallback.done(true, response.getListaPartite());
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }

    }
}
