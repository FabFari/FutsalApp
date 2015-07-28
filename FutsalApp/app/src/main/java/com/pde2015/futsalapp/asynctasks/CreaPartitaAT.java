package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoPartitaBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.CreaPartitaActivity;
import com.pde2015.futsalapp.taskcallbacks.CreaPartitaTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 27/07/2015.
 */
public class CreaPartitaAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    CreaPartitaTC taskCallback;
    CreaPartitaActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione, idGruppo;
    InfoPartitaBean payload;

    public CreaPartitaAT(Context context, CreaPartitaTC taskCallback, CreaPartitaActivity activity, Long idSessione, Long idGruppo, InfoPartitaBean payload) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.idGruppo = idGruppo;
        this.payload = payload;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Api.CreaPartita post = apiHandler.api().creaPartita(idGruppo, idSessione, payload);

            DefaultBean response = post.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }
        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null ) {
            if(response.getHttpCode().equals(AppConstants.CREATED))
                taskCallback.done(true, response.getIdCreated());
            else
                Toast.makeText(activity.getApplicationContext(), response.getResult(), Toast.LENGTH_LONG).show();
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }
    }
}