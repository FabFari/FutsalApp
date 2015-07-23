package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.app.Activity;
import android.util.Log;

import com.pde2015.futsalapp.utils.AlertDialogManager;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.pde2015.futsalapp.AppConstants;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.pde2015.futsalapp.taskcallbacks.SessioneIndietroTC;

import java.io.IOException;

/**
 * Created by Fabrizio on 21/07/2015.
 */
public class SessioneIndietroAT extends AsyncTask<PayloadBean, Void, PayloadBean> {
    Context context;
    SessioneIndietroTC taskCallback;
    Activity activity;
    Long idSessione;
    AlertDialogManager alert = new AlertDialogManager();

    public SessioneIndietroAT(Context context, SessioneIndietroTC taskCallback, Activity activity, Long idSessione) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
    }

    protected PayloadBean doInBackground(PayloadBean... payloads){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{


            PdE2015.Sessione.SessioneIndietro put = apiHandler.sessione().sessioneIndietro(payloads[0]);

            PayloadBean response = put.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si è verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }
        return null;
    }

    protected void onPostExecute(PayloadBean response) {
        if(response != null && response.getNuovoStato() != null)
            taskCallback.done(true, response.getNuovoStato());
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si è verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }

    }
}
