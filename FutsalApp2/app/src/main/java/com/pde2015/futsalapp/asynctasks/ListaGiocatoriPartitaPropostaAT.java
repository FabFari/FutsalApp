package com.pde2015.futsalapp.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.appspot.futsalapp_1008.pdE2015.PdE2015;
import com.appspot.futsalapp_1008.pdE2015.model.ListaGiocatoriBean;
import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.taskcallbacks.ListaGiocatoriPartitaTC;
import com.pde2015.futsalapp.utils.AlertDialogManager;

import java.io.IOException;

/**
 * Created by Roberto on 30/07/2015.
 */
public class ListaGiocatoriPartitaPropostaAT extends AsyncTask<Void, Void, ListaGiocatoriBean>
{
    Context context;
    ListaGiocatoriPartitaTC taskCallback;

    Long idSessione, idPartita;

    Activity activity;
    AlertDialogManager alert = new AlertDialogManager();

    public ListaGiocatoriPartitaPropostaAT(Context context, ListaGiocatoriPartitaTC taskCallback, Long idPartita, Long idSessione, Activity activity) {
        this.context = context;
        this.idSessione = idSessione;
        this.idPartita = idPartita;
        this.taskCallback = taskCallback;
        this.activity = activity;
    }

    protected ListaGiocatoriBean doInBackground(Void... unused) {
        PdE2015 apiHandler = AppConstants.getApiServiceHandle(null);
        try {
            PdE2015.Api.ListaGiocatoriPartitaProposta get = apiHandler.api().listaGiocatoriPartitaProposta(idPartita, idSessione);
            ListaGiocatoriBean response = get.execute();

            return response;
        } catch (IOException e) {
            // Toast.makeText(context, "Si e' verificato un problema. Riprovare!", Toast.LENGTH_SHORT).show();
            taskCallback.done(false, null, null, "ListaGiocatoriPartitaProposta", "PROPOSTA");
        }

        return null;
    }

    protected void onPostExecute(ListaGiocatoriBean response) {
        if(response != null)
            taskCallback.done(true, response.getListaGiocatori(), response.getListaAmici(), "ListaGiocatoriPartitaProposta", "PROPOSTA");
        else {
            alert.showAlertDialog(activity,
                    "Attenzione!",
                    "Si e' verificato un problema. Riprovare!", false);
            taskCallback.done(false, null, null, "ListaGiocatoriPartitaProposta", "PROPOSTA");
        }

    }

}

