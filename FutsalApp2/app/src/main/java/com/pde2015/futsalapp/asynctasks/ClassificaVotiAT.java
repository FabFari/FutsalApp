package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.ClassificaVotiBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.PartitaGiocataActivity;
import com.pde2015.futsalapp.taskcallbacks.ClassificaVotiTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 30/07/2015.
 */
public class ClassificaVotiAT extends AsyncTask<Void, Void, ClassificaVotiBean> {
    Context context;
    ClassificaVotiTC taskCallback;
    PartitaGiocataActivity activity;
    AlertDialogManager alert = new AlertDialogManager();
    Long idSessione, idPartita;

    public ClassificaVotiAT(Context context, ClassificaVotiTC taskCallback, PartitaGiocataActivity activity, Long idSessione, Long idPartita) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.activity = activity;
        this.idSessione = idSessione;
        this.idPartita = idPartita;
    }

    protected ClassificaVotiBean doInBackground(Void... unused){
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try {

            PdE2015.Api.ClassificaVoti get = apiHandler.api().classificaVoti(idPartita, idSessione);

            ClassificaVotiBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null, null);
        }
        return null;
    }

    protected void onPostExecute(ClassificaVotiBean response) {
        if(response != null) {
            if(response.getHttpCode().equals(AppConstants.OK))
                taskCallback.done(true, response.getListaGiocatori(), response.getListaNumeroVoti());
            else
                Toast.makeText(activity.getApplicationContext(), "Non e' stato ancora scelto un campo!", Toast.LENGTH_LONG).show();
        }
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null, null);
        }
    }

}
