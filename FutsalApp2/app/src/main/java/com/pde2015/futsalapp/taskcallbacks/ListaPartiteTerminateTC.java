package com.pde2015.futsalapp.taskcallbacks;

import com.appspot.futsalapp_1008.pdE2015.model.PartitaBean;

import java.util.List;

/**
 * Created by Roberto on 29/07/2015.
 */
public interface ListaPartiteTerminateTC {
    void done(boolean res, List<PartitaBean> listaPartite);
}
