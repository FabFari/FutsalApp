package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoIscrittoGestisceBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.GruppoActivity;
import com.pde2015.futsalapp.taskcallbacks.EstIscrittoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 26/07/2015.
 */
public class EstIscrittoAT extends AsyncTask<Void, Void, DefaultBean> {

    Context context;
    EstIscrittoTC taskCallback;
    GruppoActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    InfoIscrittoGestisceBean payload;

    public EstIscrittoAT(Context context, EstIscrittoTC taskCallback, GruppoActivity activity, Long idSessione, InfoIscrittoGestisceBean payload) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.payload = payload;
    }

    protected DefaultBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Iscritto.EstIscritto get = apiHandler.iscritto().estIscritto(payload);
            DefaultBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, false);
        }

        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null && response.getHttpCode().equals(AppConstants.OK))
            taskCallback.done(true, true);
        else if( response != null && response.getHttpCode().equals(AppConstants.NOT_FOUND) )
            taskCallback.done(true, false);
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, false);
        }

    }
}
