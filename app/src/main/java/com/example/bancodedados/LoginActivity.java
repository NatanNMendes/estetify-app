package com.example.bancodedados;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bancodedados.utils.Navigation;
import com.example.bancodedados.utils.PasswordVisibility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText edit_email, edit_senha;
    private Button btn_entrar;
    private ProgressBar progress_bar;
    private Navigation navigation;
    private PasswordVisibility passwordVisibility;
    String[] mensagens = {"Preencha todos os campos", "Login realizado com sucesso", "Erro ao realizar login"};

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        navigation = new Navigation(this);
        passwordVisibility = new PasswordVisibility();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        edit_email = findViewById(R.id.usernameInput);
        edit_senha = findViewById(R.id.passwordInput);
        ImageButton togglePasswordButton = findViewById(R.id.togglePasswordButton);
        ImageButton toBackScreen = findViewById(R.id.backButton);
        LinearLayout signUpButton = findViewById(R.id.sign_up_button);
        Button loginButton = findViewById(R.id.loginButton);
        progress_bar = findViewById(R.id.progress_bar);

        toBackScreen.setOnClickListener(v -> navigation.navigationToBackScreen(this));
        loginButton.setOnClickListener(v -> validarLogin());
        togglePasswordButton.setOnClickListener(v -> passwordVisibility.togglePasswordVisibility(edit_senha, togglePasswordButton));
        signUpButton.setOnClickListener(v ->  navigation.navigationToScreen(CreateAccount.class));
    }

    public void validarLogin() {
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast toast = Toast.makeText(LoginActivity.this, mensagens[0], Toast.LENGTH_SHORT);
            Objects.requireNonNull(toast.getView()).setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            TextView text = toast.getView().findViewById(android.R.id.message);
            text.setTextColor(Color.BLACK);
            toast.show();
        } else {
            progress_bar.setVisibility(View.VISIBLE);
            AutenticarUsuario();
        }
    }

    private void AutenticarUsuario() {
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress_bar.setVisibility(View.GONE); // Oculta a barra de progresso após a resposta
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, mensagens[1], Toast.LENGTH_SHORT).show();
                            navigation.navigationToScreen(PerfilActivity.class);

                        } else {
                            String erro;
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                erro = mensagens[2];
                            }
                            Toast toast = Toast.makeText(LoginActivity.this, erro, Toast.LENGTH_SHORT);
                            Objects.requireNonNull(toast.getView()).setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                            TextView text = toast.getView().findViewById(android.R.id.message);
                            text.setTextColor(Color.BLACK);
                            toast.show();
                        }
                    }
                });
    }

}
