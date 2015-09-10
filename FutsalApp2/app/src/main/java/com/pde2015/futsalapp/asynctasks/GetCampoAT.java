package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.CampoBean;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.taskcallbacks.GetCampoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 29/07/2015.
 */
public class GetCampoAT extends AsyncTask<Void, Void, CampoBean>
{
    Context context;
    GetCampoTC taskCallback;

    Long idSessione, idCampo;

    Activity activity;
    AlertDialogManager alert = new AlertDialogManager();

    public GetCampoAT(Context context, GetCampoTC taskCallback, Long idSessione, Long idCampo, Activity activity) {
        this.context = context;
        this.idCampo = idCampo;
        this.idSessione = idSessione;
        this.taskCallback = taskCallback;
        this.activity = activity;
    }

    protected CampoBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Api.GetCampo get = apiHandler.api().getCampo(idCampo, idSessione);
            CampoBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }

        return null;
    }

    protected void onPostExecute(CampoBean response) {
        if(response != null && (response.getHttpCode() == null || response.getHttpCode().equals(AppConstants.OK)))
            taskCallback.done(true, response.getCampo());
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }

    }

}
