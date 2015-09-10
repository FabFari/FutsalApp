package com.pde2015.futsalapp.taskcallbacks;

import com.appspot.futsalapp_1008.pdE2015.model.Giocatore;

import java.util.List;

/**
 * Created by Roberto on 30/07/2015.
 */
public interface ListaGiocatoriPartitaTC {
    void done(boolean res, List<Giocatore> lista, List<Integer> amici, String tipoAT, String statoPartita);
}
