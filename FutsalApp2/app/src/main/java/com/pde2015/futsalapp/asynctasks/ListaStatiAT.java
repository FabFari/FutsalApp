package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.PayloadBean;
import com.appspot.futsalapp_1008.pdE2015.model.ListaStatiBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.LoginRegistratiActivity;
import com.pde2015.futsalapp.taskcallbacks.ListaStatiTC;

import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Fabrizio on 21/07/2015.
 */
public class ListaStatiAT extends AsyncTask<Void, Void, ListaStatiBean> {
    Context context;
    ListaStatiTC taskCallback;
    Long idSessione;
    Activity activity;
    String email;
    Long idGruppo;
    Long idPartita;
    AlertDialogManager alert = new AlertDialogManager();

    public ListaStatiAT(Context context, ListaStatiTC taskCallback, Long idSessione, Activity activity) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.idSessione = idSessione;
        this.activity = activity;
    }

    public ListaStatiAT(Context context, ListaStatiTC taskCallback, Long idSessione, Activity activity, String email) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.idSessione = idSessione;
        this.activity = activity;
        this.email = email;
    }

    public ListaStatiAT(Context context, ListaStatiTC taskCallback, Long idSessione, Activity activity, Long idGruppo, Long idPartita) {
        this.context = context;
        this.taskCallback = taskCallback;
        this.idSessione = idSessione;
        this.activity = activity;
        this.idGruppo = idGruppo;
        this.idPartita = idPartita;
    }

    protected ListaStatiBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);

        try{

            PayloadBean bean = new PayloadBean();
            bean.setIdSessione(idSessione);
            bean.setProfiloDaVisitare(email);
            bean.setIdGruppo(idGruppo);
            bean.setIdPartita(idPartita);

            PdE2015.Sessione.ListaStati get = apiHandler.sessione().listaStati(bean);
            ListaStatiBean response = get.execute();

            return response;
        }
        catch(IOException e) {
            // Toast.makeText(context, "Si è verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(true, null, "ListaStatiAT");
        }

        return null;
    }

    protected void onPostExecute(ListaStatiBean response) {
        if(response != null && (response.getHttpCode() == null || !response.getHttpCode().equals(AppConstants.NOT_FOUND)))
            taskCallback.done(true, response.getStatiSuccessivi(), "ListaStatiAT");
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si è verificato un problema. Riprovare!", false);
            taskCallback.done(false, null, "ListaStatiAT");
        }

    }
}
