package com.pde2015.futsalapp.utils;

import java.util.*;

import com.appspot.futsalapp_1008.pdE2015.model.Gruppo;
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
            else if(stato.equals(AppConstants.GRUPPO))
                listaActivity.add(GruppoActivity.class);
            else if(stato.equals(AppConstants.MODIFICA_PROFILO))
                listaActivity.add(ModificaProfiloActivity.class);
            else if(stato.equals(AppConstants.RICERCA_GRUPPO))
                listaActivity.add(RicercaGruppoActivity.class);
            else if(stato.equals(AppConstants.INVITO))
                listaActivity.add(InvitaGiocatoreActivity.class);
            else if(stato.equals(AppConstants.ISCRITTI_GRUPPO))
                listaActivity.add(IscrittiGruppoActivity.class);
            else if(stato.equals(AppConstants.STORICO))
                listaActivity.add(StoricoActivity.class);
            else if(stato.equals(AppConstants.CREA_PARTITA))
                listaActivity.add(CreaPartitaActivity.class);
            else if(stato.equals(AppConstants.PARTITA))
                listaActivity.add(PartitaActivity.class);
            else if(stato.equals(AppConstants.CREA_VOTO))
                listaActivity.add(CreaVotoActivity.class);
            else if(stato.equals(AppConstants.RICERCA_CAMPO))
                listaActivity.add(RicercaCampoActivity.class);
            else if(stato.equals(AppConstants.CAMPO))
                listaActivity.add(CampoActivity.class);
            else if(stato.equals(AppConstants.CREA_CAMPO))
                listaActivity.add(CreaCampoActivity.class);
            //else if(stato.equals(AppConstants.DISPONIBILE_PER_PARTITA))
            //TODO listaActivity.add(DisponibileActivity.class);
            //else if(stato.equals(AppConstants.ESCI_GRUPPO))
            //TODO listaActivity.add(EsciGruppoActivity.class);
            //else if(stato.equals(AppConstants.ANNULLA_PARTITA))
            //TODO listaActivity.add(AnnullaPartitaActivity.class);
            //else if(stato.equals(AppConstants.ELENCO_VOTI))
            //TODO listaActivity.add(CreaCampoActivity.class);
            //else if(stato.equals(AppConstants.MODIFICA_GRUPPO))
            //TODO listaActivity.add(CreaCampoActivity.class);
            //else if(stato.equals(AppConstants.MODIFICA_PARTITA))
            //TODO listaActivity.add(CreaCampoActivity.class);
        }

        return listaActivity;
    }

}
