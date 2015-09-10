package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.DefaultBean;
import com.appspot.futsalapp_1008.pdE2015.model.InfoCampoBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.CreaCampoActivity;
import com.pde2015.futsalapp.taskcallbacks.CreaCampoTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Fabrizio on 28/07/2015.
 */
public class CreaCampoAT extends AsyncTask<Void, Void, DefaultBean> {
    Context context;
    CreaCampoTC taskCallback;
    CreaCampoActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione;
    InfoCampoBean payload;

    public CreaCampoAT(Context context, CreaCampoTC taskCallback, CreaCampoActivity activity,
                       Long idSessione, InfoCampoBean payload) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.payload = payload;
    }

    protected DefaultBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Api.CreaCampo post = apiHandler.api().creaCampo(idSessione, payload);

            DefaultBean response = post.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null);
        }
        return null;
    }

    protected void onPostExecute(DefaultBean response) {
        if(response != null ) {
            if(response.getHttpCode().equals(AppConstants.CREATED))
                taskCallback.done(true, response.getIdCreated());
            else
                Toast.makeText(activity.getApplicationContext(), response.getResult(), Toast.LENGTH_LONG).show();
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }
    }

}
