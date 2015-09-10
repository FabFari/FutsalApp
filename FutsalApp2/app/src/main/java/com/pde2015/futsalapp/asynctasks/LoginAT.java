package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.utils.AlertDialogManager;
import com.pde2015.futsalapp.taskcallbacks.LoginTC;
import com.pde2015.futsalapp.activities.LoginRegistratiActivity;

import java.io.IOException;

/**
 * Created by Fabrizio on 22/07/2015.
 */
public class LoginAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    LoginTC taskCallback;
    LoginRegistratiActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    String email;

    public LoginAT(Context context, LoginTC taskCallback, LoginRegistratiActivity activity, Long idSessione, String email) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.email = email;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Api.Login post = apiHandler.api().login(email, idSessione);

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
        if( response != null && response.getHttpCode().equals(AppConstants.OK))
            taskCallback.done(true, response);
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si è verificato un problema. Riprovare!", false);
            taskCallback.done(false, response);
        }

    }

}
