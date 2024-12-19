package com.example.bancodedados.utils;

import static android.content.Intent.getIntent;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bancodedados.StartScreen;


public class Navigation {
    private final Context thisScreen;

    public Navigation(Context thisScreen) {
        this.thisScreen = thisScreen;
    }

    public void navigationToScreen(Class<?> nextScreen) {
        Intent intent = new Intent(thisScreen, nextScreen);
        thisScreen.startActivity(intent);

        if (thisScreen instanceof android.app.Activity) {
            ((android.app.Activity) thisScreen).finish();
        }
    }

    public void navigationToBackScreen(AppCompatActivity currentActivity) {
        Intent intent = currentActivity.getIntent();
        String cameFrom = intent.getStringExtra("came_from");

        if ("PerfilActivity".equals(cameFrom)) {
            Intent startScreenIntent = new Intent(thisScreen, StartScreen.class);
            thisScreen.startActivity(startScreenIntent);
            currentActivity.finish();
        } else {
            currentActivity.finish();
        }
    }

}
