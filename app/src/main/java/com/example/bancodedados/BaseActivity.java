package com.example.bancodedados;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.Nullable;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        setupBottomNavigation();
    }

    public void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    if (!(BaseActivity.this instanceof MainActivity)) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0); // Sem animação
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_search) {
                    if (!(BaseActivity.this instanceof BusinessActivity)) {
                        Intent intent = new Intent(getApplicationContext(), BusinessActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0); // Sem animação
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    if (!(BaseActivity.this instanceof PerfilActivity)) {
                        Intent intent = new Intent(getApplicationContext(), PerfilActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0); // Sem animação
                        finish();
                    }
                    return true;
                }
                return false;
            });
        } else {
            Log.e("BaseActivity", "BottomNavigationView não encontrado no layout.");
        }
    }


    public void updateBottomNavigationSelection(int menuItemId) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(menuItemId);
        }
    }
}

