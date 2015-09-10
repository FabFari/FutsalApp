package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.ListaVotiBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.PartitaGiocataActivity;
import com.pde2015.futsalapp.taskcallbacks.ElencoVotiTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 30/07/2015.
 */
public class ElencoVotiAT extends AsyncTask<Void, Void, ListaVotiBean> {

    Context context;
    ElencoVotiTC taskCallback;
    PartitaGiocataActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione, idPartita;
    String emailVotato;

    public ElencoVotiAT(Context context, ElencoVotiTC taskCallback, PartitaGiocataActivity activity, Long idSessione, Long idPartita, String emailVotato) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.idPartita = idPartita;
        this.emailVotato = emailVotato;
    }

    protected ListaVotiBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Api.ElencoVotiUomoPartita get = apiHandler.api().elencoVotiUomoPartita(emailVotato, idPartita, idSessione);
            ListaVotiBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }

        return null;
    }

    protected void onPostExecute(ListaVotiBean response) {
        if(response != null && response.getHttpCode().equals(AppConstants.OK))
            taskCallback.done(true, response);
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }

    }
}
