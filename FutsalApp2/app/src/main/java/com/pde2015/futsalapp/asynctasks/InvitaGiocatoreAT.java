package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoInvitoBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.InvitaGiocatoreActivity;
import com.pde2015.futsalapp.taskcallbacks.InvitaGiocatoreTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Fabrizio on 31/07/2015.
 */
public class InvitaGiocatoreAT extends AsyncTask<Void, Void, DefaultBean>{

    private static final String TAG = "InvitaGiocatoreActivity";

    Context context;
    InvitaGiocatoreTC taskCallback;
    InvitaGiocatoreActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    InfoInvitoBean payload;

    public InvitaGiocatoreAT(Context context, InvitaGiocatoreTC taskCallback, InvitaGiocatoreActivity activity, Long idSessione, InfoInvitoBean payload) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.payload = payload;
    }

    protected DefaultBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Api.InvitaGiocatore post = apiHandler.api().invitaGiocatore(idSessione, payload);
            DefaultBean response = post.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, false);
        }

        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null && response.getHttpCode().equals(AppConstants.CREATED))
            taskCallback.done(true, true);
        else if( response != null && response.getHttpCode().equals(AppConstants.NOT_FOUND) )
            taskCallback.done(true, false);
        else if( response != null && response.getHttpCode().equals(AppConstants.PRECONDITION_FAILED)) {
            Toast.makeText(context, response.getResult(), Toast.LENGTH_LONG).show();
            taskCallback.done(true, false);
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, false);
        }

    }
}
