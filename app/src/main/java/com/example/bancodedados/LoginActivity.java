package com.example.bancodedados;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView text_tela_cadastro;
    private TextView edit_email, edit_senha;
    private Button btn_entrar;
    private ProgressBar progress_bar;
    String[] mensagens = {"Preencha todos os campos", "Login realizado com sucesso", "Erro ao realizar login"};

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        EditText emailInput = findViewById(R.id.usernameInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        ImageButton togglePasswordButton = findViewById(R.id.togglePasswordButton);
        LinearLayout signUpButton = findViewById(R.id.sign_up_button);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> validarLogin(loginButton));
        togglePasswordButton.setOnClickListener(v -> togglePasswordVisibility(passwordInput,togglePasswordButton));
        signUpButton.setOnClickListener(v -> goToCreateAccount());
    }

    public void validarLogin(View v) {

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast toast = Toast.makeText(LoginActivity.this, mensagens[0], Toast.LENGTH_SHORT);
            Objects.requireNonNull(toast.getView()).setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            TextView text = toast.getView().findViewById(android.R.id.message);
            text.setTextColor(Color.BLACK);
            toast.show();
        } else {
            AutenticarUsuario(v);
        }
    }

    private void AutenticarUsuario(View view) {

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener((new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progress_bar.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IrParaPerfilActivity();
                        }
                    }, 2000);
                } else {
                    String erro;

                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        erro = "Erro ao realizar login";
                    }
                    Toast toast = Toast.makeText(LoginActivity.this, erro, Toast.LENGTH_SHORT);
                    Objects.requireNonNull(toast.getView()).setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    TextView text = toast.getView().findViewById(android.R.id.message);
                    text.setTextColor(Color.BLACK);
                    toast.show();
                }
            }
        }));
    }

    private void IrParaPerfilActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
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
