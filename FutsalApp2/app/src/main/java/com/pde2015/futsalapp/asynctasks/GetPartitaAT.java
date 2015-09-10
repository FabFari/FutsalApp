package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.PartitaBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.taskcallbacks.GetPartitaTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 30/07/2015.
 */
public class GetPartitaAT extends AsyncTask<Void, Void, PartitaBean> {
    Context context;
    GetPartitaTC taskCallback;
    Activity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione, idPartita;

    public GetPartitaAT(Context context, GetPartitaTC taskCallback, Activity activity, Long idSessione, Long idPartita) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.idPartita = idPartita;
    }

    protected PartitaBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Api.GetPartita get = apiHandler.api().getPartita(idPartita, idSessione);

            PartitaBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }
        return null;
    }

    protected void onPostExecute(PartitaBean response) {
        if(response != null) {
            //if(response.getHttpCode().equals(AppConstants.OK))
                taskCallback.done(true, response);
            /*else
                Toast.makeText(activity.getApplicationContext(), response.getResult(), Toast.LENGTH_LONG).show();*/
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, response);
        }
    }

}

