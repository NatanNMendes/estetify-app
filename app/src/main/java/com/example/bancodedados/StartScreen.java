package com.example.bancodedados;

import android.content.Intent;
import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bancodedados.utils.Navigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartScreen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private Navigation navigation;
    private static final String TAG = "StartScreen";
    private com.example.bancodedados.services.SignInGoogleService signInGoogleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_screen);

        navigation = new Navigation(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        signInGoogleService = new com.example.bancodedados.services.SignInGoogleService(this);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        // Botões de ação
        LinearLayout googleSignInButton = findViewById(R.id.login_google_button);
        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> navigation.navigationToScreen(LoginActivity.class));
        googleSignInButton.setOnClickListener(v -> signInGoogleService.launchSignIn());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            navigation.navigationToScreen(MainActivity.class);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == com.example.bancodedados.services.SignInGoogleService.SIGN_IN_REQUEST_CODE) {
            signInGoogleService.handleSignInResult(requestCode, resultCode, data);
        }
    }
}
