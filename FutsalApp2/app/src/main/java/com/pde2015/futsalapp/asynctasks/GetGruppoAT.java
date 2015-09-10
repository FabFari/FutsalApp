package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.GruppoBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.GruppoActivity;
import com.pde2015.futsalapp.taskcallbacks.GetGruppoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 26/07/2015.
 */
public class GetGruppoAT extends AsyncTask<Void, Void, GruppoBean> {

    Context context;
    GetGruppoTC taskCallback;
    GruppoActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    Long idGruppo;

    public GetGruppoAT(Context context, GetGruppoTC taskCallback, GruppoActivity activity, Long idSessione, Long idGruppo) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.idGruppo = idGruppo;
    }

    protected GruppoBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Api.GetGruppo get = apiHandler.api().getGruppo(idGruppo, idSessione);
            GruppoBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }

        return null;
    }

    protected void onPostExecute(GruppoBean response) {
        if(response != null && response.getHttpCode().equals(AppConstants.OK))
            taskCallback.done(true, response);
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }

    }
}
