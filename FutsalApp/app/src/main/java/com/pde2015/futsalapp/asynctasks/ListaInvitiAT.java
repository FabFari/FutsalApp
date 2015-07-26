package com.pde2015.futsalapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.ListaInvitiBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.ProfileActivity;
import com.pde2015.futsalapp.taskcallbacks.ListaInvitiTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Fabrizio on 25/07/2015.
 */
public class ListaInvitiAT extends AsyncTask<Void, Void, ListaInvitiBean> {
    Context context;
    ListaInvitiTC taskCallback;
    Long idSessione;
    ProfileActivity activity;
    AlertDialogManager alert = new AlertDialogManager();

    public ListaInvitiAT(Context context, ListaInvitiTC taskCallback, Long idSessione, ProfileActivity activity) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.idSessione = idSessione;
        this.activity = activity;
    }

    protected ListaStatiBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{

            PdE2015.Api.ListaInviti get = apiHandler.api().listaInviti(idSessione);
            ListaInvitiBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si è verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(true, null);
        }

        return null;
    }

    protected void onPostExecute(ListaInvitiBean response) {
        if(response != null && response.getHttpCode().equals(AppConstants.OK))
            taskCallback.done(true, response.getListaInviti());
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si è verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }

    }
}
