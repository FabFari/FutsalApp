package com.pde2015.futsalapp.taskcallbacks;

import com.appspot.futsalapp_1008.pdE2015.model.Invito;

/**
 * Created by Fabrizio on 25/07/2015.
 */
public interface ListaInvitiTC {
    public void done(boolean res, Lista<Invito> listaInviti, String tipoDone);
}
