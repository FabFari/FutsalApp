package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.ListaGruppiBean;
import com.appspot.futsalapp_1008.pdE2015.model.ListaStatiBean;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.LoginRegistratiActivity;
import com.pde2015.futsalapp.taskcallbacks.ListaGruppiIscrittoTC;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;

import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 23/07/2015.
 */
public class ListaGruppiIscrittoAT extends AsyncTask<Void, Void, ListaGruppiBean>
{
    Context context;
    ListaGruppiIscrittoTC taskCallback;
    Long idSessione;
    Activity activity;
    AlertDialogManager alert = new AlertDialogManager();

    public ListaGruppiIscrittoAT(Context context, ListaGruppiIscrittoTC taskCallback, Long idSessione, Activity activity) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.idSessione = idSessione;
        this.activity = activity;
    }

    protected ListaGruppiBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{
            PdE2015.Api.ListaGruppiIscritto get = apiHandler.api().listaGruppiIscritto(idSessione);
            ListaGruppiBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(true, null);
        }

        return null;
    }

    protected void onPostExecute(ListaGruppiBean response) {
        if(response != null && (response.getHttpCode() == null || response.getHttpCode().equals(AppConstants.OK)))
            taskCallback.done(true, response.getListaGruppi());
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null);
        }

    }

}
