package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.AppConstants;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.pde2015.futsalapp.taskcallbacks.AggiornaStatoTC;
import android.app.Activity;

import java.io.IOException;

/**
 * Created by Fabrizio on 21/07/2015.
 */
public class AggiornaStatoAT extends AsyncTask<PayloadBean, Void, DefaultBean> {

    Context context;
    AggiornaStatoTC taskCallback;
    Activity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;

    public AggiornaStatoAT(Context context, AggiornaStatoTC taskCallback, Long idSessione, Activity activity) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.idSessione = idSessione;
        this.activity = activity;
    }

    protected DefaultBean doInBackground(PayloadBean... payloads){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{


            PdE2015.Sessione.AggiornaStato put = apiHandler.sessione().aggiornaStato(payloads[0]);

            DefaultBean response = put.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si è verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false);
        }
        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response.getHttpCode().equals(AppConstants.OK))
            taskCallback.done(true);
        else {
            alert.showAlertDialog(activity,
            "Attenzione!",
            "Si è verificato un problema. Riprovare!", false);
            taskCallback.done(false);
        }

    }
}
