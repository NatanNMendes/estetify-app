package com.example.bancodedados;

import android.content.Context;
import android.content.SharedPreferences;

public class CredentialManagerHelper {

    private static final String PREFS_NAME = "CredentialPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ID_TOKEN = "id_token";

    // Método para salvar as credenciais no SharedPreferences
    public static void saveCredentials(Context context, String email, String idToken) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ID_TOKEN, idToken);
        editor.apply();  // Salva as credenciais
    }

    // Método para recuperar as credenciais salvas
    public static String getEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_EMAIL, null);
    }

    public static String getIdToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ID_TOKEN, null);
    }
}
