package com.pde2015.futsalapp.taskcallbacks;

import com.appspot.futsalapp_1008.pdE2015.model.InfoGruppoBean;

import java.util.List;

/**
 * Created by Roberto on 23/07/2015.
 */
public interface ListaGruppiIscrittoTC {
    void done(boolean res, List<InfoGruppoBean> listaGruppi);
}
