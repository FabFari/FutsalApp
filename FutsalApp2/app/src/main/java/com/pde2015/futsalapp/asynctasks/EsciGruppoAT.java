package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoIscrittoGestisceBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.GruppoActivity;
import com.pde2015.futsalapp.taskcallbacks.EsciGruppoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 30/07/2015.
 */
public class EsciGruppoAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    EsciGruppoTC taskCallback;
    GruppoActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    InfoIscrittoGestisceBean gestioneBean;

    public EsciGruppoAT(Context context, EsciGruppoTC taskCallback, GruppoActivity activity, Long idSessione, InfoIscrittoGestisceBean gestioneBean) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.gestioneBean = gestioneBean;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Api.EsciGruppo get = apiHandler.api().esciGruppo(idSessione, gestioneBean);

            DefaultBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, false, "EsciIscritto");
        }
        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null) {
            if(response.getHttpCode().equals(AppConstants.OK))
                taskCallback.done(true, true, "EsciIscritto");
            else
                Toast.makeText(activity.getApplicationContext(), "Non e' stato ancora scelto un campo!", Toast.LENGTH_LONG).show();
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, false, "EsciIscritto");
        }
    }

}
