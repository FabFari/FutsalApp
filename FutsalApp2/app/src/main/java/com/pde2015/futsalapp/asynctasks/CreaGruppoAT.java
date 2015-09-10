package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoGruppoBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.CreaGruppoActivity;
import com.pde2015.futsalapp.taskcallbacks.CreaGruppoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 25/07/2015.
 */
public class CreaGruppoAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    CreaGruppoTC taskCallback;
    CreaGruppoActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    InfoGruppoBean payload;

    public CreaGruppoAT(Context context, CreaGruppoTC taskCallback, CreaGruppoActivity activity, Long idSessione, InfoGruppoBean payload) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.payload = payload;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Api.CreaGruppo post = apiHandler.api().creaGruppo(idSessione, payload);

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
