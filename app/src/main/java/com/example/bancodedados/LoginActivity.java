package com.example.bancodedados;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        EditText passwordInput = findViewById(R.id.passwordInput);
        ImageButton togglePasswordButton = findViewById(R.id.togglePasswordButton);
        LinearLayout signUpButton = findViewById(R.id.sign_up_button);


        signUpButton.setOnClickListener(v -> goToCreateAccount());
    }

    public void goToBackScreen(View view) {
        finish();
    }

    public void goToCreateAccount() {
        Intent intent = new Intent(LoginActivity.this, CreateAccount.class);
        startActivity(intent);
    }

    private void togglePasswordVisibility(EditText passwordField, ImageButton toggleButton) {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            // Tornar a senha visível
            passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_visibility_on); // Ícone para senha visível
        } else {
            // Tornar a senha oculta
            passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_visibility_off); // Ícone para senha oculta
        }
        // Movendo o cursor para o final do texto
        passwordField.setSelection(passwordField.getText().length());
    }
}
