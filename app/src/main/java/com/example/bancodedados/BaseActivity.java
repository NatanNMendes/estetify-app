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
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_home) {
                        if (!(BaseActivity.this instanceof MainActivity)) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish(); // Finaliza a Activity atual para evitar loop
                        }
                        return true;
                    } else if (itemId == R.id.nav_search) {
                        if (!(BaseActivity.this instanceof SalonPage)) {
                            startActivity(new Intent(getApplicationContext(), SalonPage.class));
                            finish(); // Finaliza a Activity atual para evitar loop
                        }
                        return true;
                    } else if (itemId == R.id.nav_profile) {
                        if (!(BaseActivity.this instanceof PerfilActivity)) {
                            startActivity(new Intent(getApplicationContext(), PerfilActivity.class));
                            finish(); // Finaliza a Activity atual para evitar loop
                        }
                        return true;
                    }
                    return false;
                }
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

