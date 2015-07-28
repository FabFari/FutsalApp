package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.ProfileActivity;
import com.pde2015.futsalapp.taskcallbacks.RispondiInvitoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Fabrizio on 27/07/2015.
 */
public class RispondiInvitoAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    RispondiInvitoTC taskCallback;
    ProfileActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    Long idInvito;
    Long idGruppo;
    boolean answer;

    public RispondiInvitoAT(Context context, RispondiInvitoTC taskCallback, ProfileActivity activity,
                            Long idSessione, Long idInvito, Long idGruppo, boolean answer) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.idInvito = idInvito;
        this.idGruppo = idGruppo;
        this.answer = answer;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Api.RispondiInvito post = apiHandler.api().rispondiInvito(idInvito, idSessione, answer);

            DefaultBean response = post.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si è verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null, idGruppo, answer);
        }
        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null) {
            if(response.getHttpCode().equals(AppConstants.OK))
                taskCallback.done(true, response, idGruppo, answer);
            else
                Toast.makeText(activity.getApplicationContext(), response.getResult(), Toast.LENGTH_LONG).show();
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si è verificato un problema. Riprovare!", false);
            taskCallback.done(false, response, idGruppo, answer);
        }
    }

}
