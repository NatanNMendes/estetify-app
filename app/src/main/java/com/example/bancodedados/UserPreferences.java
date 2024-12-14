package com.example.bancodedados;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";

    public static void saveUser(Context context, String email, String uid) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_UID, uid);
        editor.apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_UID, null);
    }
}

