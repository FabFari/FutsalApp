package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoVotoUomoPartitaBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.CreaVotoActivity;
import com.pde2015.futsalapp.taskcallbacks.CreaVotoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 31/07/2015.
 */
public class CreaVotoAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    CreaVotoTC taskCallback;
    CreaVotoActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    InfoVotoUomoPartitaBean payload;

    public CreaVotoAT(Context context, CreaVotoTC taskCallback, CreaVotoActivity activity, Long idSessione, InfoVotoUomoPartitaBean payload) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.payload = payload;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Api.CreaVoto post = apiHandler.api().creaVoto(idSessione, payload);

            DefaultBean response = post.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, false);
        }
        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null ) {
            if(response.getHttpCode().equals(AppConstants.CREATED))
                taskCallback.done(true, true);
            else
                Toast.makeText(activity.getApplicationContext(), response.getResult(), Toast.LENGTH_LONG).show();
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, false);
        }
    }
}