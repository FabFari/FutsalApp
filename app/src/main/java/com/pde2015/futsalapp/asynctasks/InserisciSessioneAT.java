package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.pde2015.futsalapp.AppConstants;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.pde2015.futsalapp.taskcallbacks.InserisciSessioneTC;

import java.io.IOException;

/**
 * Created by Fabrizio on 20/07/2015.
 */
public class InserisciSessioneAT extends AsyncTask<Void, Void, PayloadBean> {
    Context context;
    InserisciSessioneTC taskCallback;

    public InserisciSessioneAT(Context context, InserisciSessioneTC taskCallback) {
        this.context = context;
        this.taskCallback = taskCallback;
    }

    protected PayloadBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Sessione.InserisciSessione get = apiHandler.sessione().inserisciSessione();

            PayloadBean response = get.execute();

            if(response.getIdSessione() != null)
                return response;
        }
        catch(IOException e) {
            Toast.makeText(context, "Si è verificato un problema. Riprovare!", Toast.LENGTH_LONG).show();
            taskCallback.done(true, null);
        }

        return null;
    }

    protected void onPostExecute(PayloadBean response) {
        if(response != null) {
            Toast.makeText(context, "IdSessione" + response.getIdSessione(), Toast.LENGTH_LONG).show();
            taskCallback.done(true, response.getIdSessione());
        }
    }
}
