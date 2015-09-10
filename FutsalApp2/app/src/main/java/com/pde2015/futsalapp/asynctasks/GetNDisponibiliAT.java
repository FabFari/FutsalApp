package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.NDisponibiliBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.taskcallbacks.GetNDisponibiliTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 30/07/2015.
 */
public class GetNDisponibiliAT extends AsyncTask<Void, Void, NDisponibiliBean> {

    private final static String TAG = "GetNDisponibiliAT";

    Context context;
    GetNDisponibiliTC taskCallback;
    Activity activity;
    Long idPartita;
    AlertDialogManager alert = new AlertDialogManager();

    public GetNDisponibiliAT(Context context, GetNDisponibiliTC taskCallback, Activity activity, Long idPartita) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idPartita = idPartita;
    }

    protected NDisponibiliBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{


            PdE2015.Partita.GetNDisponibili put = apiHandler.partita().getNDisponibili(idPartita);

            NDisponibiliBean response = put.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, -1);
        }
        return null;
    }

    protected void onPostExecute(NDisponibiliBean response) {
        if(response != null && response.getHttpCode().equals(AppConstants.OK)) {
            taskCallback.done(true, response.getNDisponibili());
            Log.e(TAG, response.getResult());
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, -1);
        }

    }
}
