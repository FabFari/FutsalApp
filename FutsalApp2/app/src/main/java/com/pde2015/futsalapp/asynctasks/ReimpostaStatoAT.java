package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.SplashscreenActivity;
import com.pde2015.futsalapp.taskcallbacks.ReimpostaStatoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Fabrizio on 08/08/2015.
 */
public class ReimpostaStatoAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    ReimpostaStatoTC taskCallback;
    SplashscreenActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;

    public ReimpostaStatoAT(Context context, ReimpostaStatoTC taskCallback, SplashscreenActivity activity, Long idSessione) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Sessione.ReimpostaStato get = apiHandler.sessione().reimpostaStato(idSessione);

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
        if(response != null) {
            if(response.getHttpCode().equals(AppConstants.OK))
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
