package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoGiocatoreBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.ModificaProfiloActivity;
import com.pde2015.futsalapp.taskcallbacks.SetGiocatoreTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Fabrizio on 24/07/2015.
 */
public class SetGiocatoreAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    SetGiocatoreTC taskCallback;
    ModificaProfiloActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    InfoGiocatoreBean payload;

    public SetGiocatoreAT(Context context, SetGiocatoreTC taskCallback, ModificaProfiloActivity activity, Long idSessione, InfoGiocatoreBean payload) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.payload = payload;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Api.SetGiocatore post = apiHandler.api().setGiocatore(idSessione, payload);

            DefaultBean response = post.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si è verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }
        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null) {
            if(response.getHttpCode().equals(AppConstants.OK))
                taskCallback.done(true, response);
            else
                Toast.makeText(activity.getApplicationContext(), response.getResult(), Toast.LENGTH_LONG).show();
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si è verificato un problema. Riprovare!", false);
            taskCallback.done(false, response);
        }
    }

}
