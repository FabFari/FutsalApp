package com.pde2015.futsalapp.taskcallbacks;

import com.appspot.futsalapp_1008.pdE2015.model.Giocatore;
import java.util.List;

/**
 * Created by Roberto on 30/07/2015.
 */
public interface ClassificaVotiTC {
    void done(boolean res, List<Giocatore> listaGiocatori, List<Integer> listaVoti);
}
