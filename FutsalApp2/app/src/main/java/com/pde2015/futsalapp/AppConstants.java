package com.pde2015.futsalapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import com.appspot.futsalapp_1008.pdE2015.*;
import com.appspot.futsalapp_1008.pdE2015.model.*;

import javax.annotation.Nullable;

import android.accounts.Account;
import android.accounts.AccountManager;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Created by Fabrizio on 20/07/2015.
 */

public class AppConstants {
    public static final String WEB_CLIENT_ID = "748960067112-vrbnki8tc7ke0m6lsei9vl4hvlg57cbj.apps.googleusercontent.com";
    public static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;

    // Codici server
    public static final String CREATED = "201 Created";
    public static final String OK = "200 OK";
    public static final String PRECONDITION_FAILED = "412 Precondition Failed";
    public static final String CONFLICT = "409 Conflict";
    public static final String UNAUTHORIZED = "401 Unauthorized";
    public static final String INTERNAL_SERVER_ERROR = "500 Internal Server Error";
    public static final String BAD_REQUEST = "400 Bad Request";
    public static final String NOT_FOUND = "404 Not Found";


    // Stati
    public static final String LOGIN_E_REGISTRAZIONE = "LOGIN_E_REGISTRAZIONE";
    public static final String REGISTRAZIONE = "REGISTRAZIONE";
    public static final String PRINCIPALE = "PRINCIPALE";
    public static final String GRUPPO = "GRUPPO";
    public static final String PROFILO = "PROFILO";
    public static final String MODIFICA_PROFILO = "MODIFICA_PROFILO";
    public static final String RICERCA_GRUPPO = "RICERCA_GRUPPO";
    public static final String INVITO = "INVITO";
    public static final String ISCRITTI_GRUPPO = "ISCRITTI_GRUPPO";
    public static final String STORICO = "STORICO";
    public static final String CREA_PARTITA = "CREA_PARTITA";
    public static final String PARTITA = "PARTITA";
    public static final String CREA_VOTO = "CREA_VOTO";
    public static final String RICERCA_CAMPO = "RICERCA_CAMPO";
    public static final String DISPONIBILE_PER_PARTITA = "DISPONIBILE_PER_PARTITA";
    public static final String CAMPO = "CAMPO";
    public static final String CREA_CAMPO = "CREA_CAMPO";
    public static final String CREA_GRUPPO = "CREA_GRUPPO";
    public static final String EXIT = "EXIT";
    public static final String ESCI_GRUPPO = "ESCI_GRUPPO";
    public static final String ANNULLA_PARTITA = "ANNULLA_PARTITA";
    public static final String ELENCO_VOTI = "ELENCO_VOTI";
    public static final String MODIFICA_GRUPPO = "MODIFICA_GRUPPO";
    public static final String MODIFICA_PARTITA = "MODIFICA_PARTITA";

    /*
    public static enum StatoSessione	{LOGIN_E_REGISTRAZIONE, REGISTRAZIONE,
        PRINCIPALE, GRUPPO, PROFILO, MODIFICA_PROFILO,
        RICERCA_GRUPPO, INVITO, ISCRITTI_GRUPPO, STORICO,
        CREA_PARTITA, PARTITA,
        CREA_VOTO, RICERCA_CAMPO, DISPONIBILE_PER_PARTITA,
        CAMPO, CREA_CAMPO, CREA_GRUPPO, EXIT, ESCI_GRUPPO,
        ANNULLA_PARTITA, ELENCO_VOTI, MODIFICA_GRUPPO,
        MODIFICA_PARTITA
    };
    */

    /**
     * Class instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();

    /**
     * Class instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();


    /**
     * Retrieve a Helloworld api service handle to access the API.
     */

    public static PdE2015 getApiServiceHandle(@Nullable GoogleAccountCredential credential) {
        // Use a builder to help formulate the API request.
        PdE2015.Builder helloWorld = new PdE2015.Builder(AppConstants.HTTP_TRANSPORT,
                AppConstants.JSON_FACTORY,credential);
        return helloWorld.build();
    }

    public static int countGoogleAccounts(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts == null || accounts.length < 1) {
            return 0;
        } else {
            return accounts.length;
        }
    }

    public static boolean checkGooglePlayServicesAvailable(Activity activity) {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
            return false;
        }
        return true;
    }

    public static void showGooglePlayServicesAvailabilityErrorDialog(final Activity activity,
                                                                     final int connectionStatusCode) {
        final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, activity, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }





}