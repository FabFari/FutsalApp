package com.pde2015.futsalapp.taskcallbacks;

import com.appspot.futsalapp_1008.pdE2015.model.Giocatore;
import com.appspot.futsalapp_1008.pdE2015.model.ListaGiocatoriBean;

import java.util.List;

/**
 * Created by Roberto on 26/07/2015.
 */
public interface ListaIscrittiGruppoTC {
    void done(boolean res, List<Giocatore> listaIscritti);
}
