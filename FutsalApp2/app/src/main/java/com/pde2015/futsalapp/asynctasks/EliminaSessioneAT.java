package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.ProfileActivity;
import com.pde2015.futsalapp.taskcallbacks.EliminaSessioneTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Fabrizio on 05/08/2015.
 */
public class EliminaSessioneAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    EliminaSessioneTC taskCallback;
    ProfileActivity activity;
    Long idSessione;
    AlertDialogManager alert = new AlertDialogManager();

    public EliminaSessioneAT(Context context, ProfileActivity activity,EliminaSessioneTC taskCallback, Long idSessione) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
    }

    protected DefaultBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Sessione.EliminaSessione post = apiHandler.sessione().eliminaSessione(idSessione);

            DefaultBean response = post.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si è verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(true, null);
        }

        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null) {
            //Toast.makeText(context, "IdSessione: " + response.getIdSessione(), Toast.LENGTH_LONG).show();
            taskCallback.done(true, response);
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si è verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }
    }
}
