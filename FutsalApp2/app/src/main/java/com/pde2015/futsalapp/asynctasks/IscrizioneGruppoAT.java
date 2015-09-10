package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.GruppoActivity;
import com.pde2015.futsalapp.taskcallbacks.IscrizioneGruppoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Fabrizio on 11/08/2015.
 */
public class IscrizioneGruppoAT extends AsyncTask<Void, Void, DefaultBean> {

    Context context;
    IscrizioneGruppoTC taskCallback;
    GruppoActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione, idGruppo;

    public IscrizioneGruppoAT(Context context, IscrizioneGruppoTC taskCallback, GruppoActivity activity, Long idSessione, Long idGruppo) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.idGruppo = idGruppo;
    }

    protected DefaultBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Api.CambiaStatoIscrizione get = apiHandler.api().cambiaStatoIscrizione(idGruppo, idSessione);
            DefaultBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }

        return null;
    }

    protected void onPostExecute(DefaultBean response) {
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
