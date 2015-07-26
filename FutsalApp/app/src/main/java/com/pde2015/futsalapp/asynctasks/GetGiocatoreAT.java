package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.GiocatoreBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.ProfileActivity;
import com.pde2015.futsalapp.taskcallbacks.GetGiocatoreTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Fabrizio on 23/07/2015.
 */
public class GetGiocatoreAT extends AsyncTask<Void, Void, GiocatoreBean> {
    Context context;
    GetGiocatoreTC taskCallback;
    ProfileActivity activity;
    Long idSessione;
    String email;
    AlertDialogManager alert = new AlertDialogManager();

    public GetGiocatoreAT(Context context, GetGiocatoreTC taskCallback, ProfileActivity activity, Long idSessione, String email) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.email = email;
    }

    protected GiocatoreBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{


            PdE2015.Api.GetGiocatore get = apiHandler.api().getGiocatore(email, idSessione);

            GiocatoreBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si è verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }
        return null;
    }

    protected void onPostExecute(GiocatoreBean response) {
        if(response != null && response.getHttpCode().equals(AppConstants.OK))
            taskCallback.done(true, response);
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si è verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }

    }
}
