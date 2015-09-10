package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoPressoBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.CampoActivity;
import com.pde2015.futsalapp.taskcallbacks.ImpostaCampoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 29/07/2015.
 */
public class ImpostaCampoAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    ImpostaCampoTC taskCallback;
    CampoActivity activity;
    Long idSessione;
    InfoPressoBean pressoBean;
    AlertDialogManager alert = new AlertDialogManager();

    public ImpostaCampoAT(Context context, ImpostaCampoTC taskCallback, CampoActivity activity, Long idSessione, InfoPressoBean pressoBean) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.pressoBean = pressoBean;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{


            PdE2015.Api.ImpostaCampo get = apiHandler.api().impostaCampo(idSessione, pressoBean);

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
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, false);
        }

    }
}
