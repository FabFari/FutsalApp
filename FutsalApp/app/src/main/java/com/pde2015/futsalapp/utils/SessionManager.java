package com.pde2015.futsalapp.utils;

import java.util.*;

import com.pde2015.futsalapp.AppConstants;
import com.pde2015.futsalapp.activities.*;

/**
 * Created by Fabrizio on 21/07/2015.
 */
public class SessionManager {
    private List<String> listaStati;

    public SessionManager(List<String> listaStati) {
        this.listaStati = listaStati;
    }

    public LinkedList<Class> getListaActivity() {
        LinkedList<Class> listaActivity = new LinkedList<Class>();

        Iterator<String> it = listaStati.iterator();
        while(it.hasNext()) {
            String stato = it.next();
                if(stato.equals(AppConstants.LOGIN_E_REGISTRAZIONE))
                    listaActivity.add(LoginRegistratiActivity.class);
                else if(stato.equals(AppConstants.REGISTRAZIONE))
                    listaActivity.add(RegistrazioneActivity.class);
                else if(stato.equals(AppConstants.PRINCIPALE))
                    listaActivity.add(PrincipaleActivity.class);
                else if(stato.equals(AppConstants.PROFILO))
                    listaActivity.add(ProfileActivity.class);
                else if(stato.equals(AppConstants.MODIFICA_PROFILO))
                    listaActivity.add(ModificaProfiloActivity.class);
        }

        return listaActivity;
    }

}
