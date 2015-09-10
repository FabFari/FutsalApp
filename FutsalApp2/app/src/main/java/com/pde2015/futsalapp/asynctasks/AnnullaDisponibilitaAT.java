package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoGestionePartiteBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.DisponibileActivity;
import com.pde2015.futsalapp.taskcallbacks.AnnullaDisponibilitaTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 30/07/2015.
 */
public class AnnullaDisponibilitaAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    AnnullaDisponibilitaTC taskCallback;
    DisponibileActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    InfoGestionePartiteBean payload;

    public AnnullaDisponibilitaAT(Context context, AnnullaDisponibilitaTC taskCallback, DisponibileActivity activity, Long idSessione, InfoGestionePartiteBean payload) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.payload = payload;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Api.AnnullaDisponibilita put = apiHandler.api().annullaDisponibilita(idSessione, payload);

            DefaultBean response = put.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, false, "AnnullaDisponibilita");
        }
        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null ) {
            if(response.getHttpCode().equals(AppConstants.OK))
                taskCallback.done(true, true, "AnnullaDisponibilita");
            else
                Toast.makeText(activity.getApplicationContext(), response.getResult(), Toast.LENGTH_LONG).show();
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, false, "AnnullaDisponibilita");
        }
    }

}
