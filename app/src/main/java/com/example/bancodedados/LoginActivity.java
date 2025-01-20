package com.example.bancodedados;


import android.content.res.ColorStateList;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.bancodedados.services.SignInService;
import com.example.bancodedados.utils.Navigation;
import com.example.bancodedados.utils.PasswordVisibility;

public class LoginActivity extends AppCompatActivity {

    private EditText edit_email, edit_senha;
    private Button btn_entrar;
    private ProgressBar progress_bar;
    private Navigation navigation;
    private SignInService signInService; // Novo serviço de autenticação
    private PasswordVisibility passwordVisibility;
    String[] mensagens = {"Preencha todos os campos", "Login realizado com sucesso", "Erro ao realizar login"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        navigation = new Navigation(this);
        signInService = new SignInService(this, navigation);
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
        loginButton.setOnClickListener(v -> {
            String email = edit_email.getText().toString().trim();
            String senha = edit_senha.getText().toString().trim();

            if (email.isEmpty()) {
                edit_email.setError("Email é obrigatório");
                edit_email.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            } else {
                edit_email.setError(null);
                edit_email.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            }

            if (senha.isEmpty()) {
                edit_senha.setError("Senha é obrigatória");
                edit_senha.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            } else {
                edit_senha.setError(null);
                edit_senha.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            }

            if (!email.isEmpty() && !senha.isEmpty()) {
                signInService.signInWithEmailAndPassword(email, senha);
            } else {
                Toast.makeText(LoginActivity.this, mensagens[0], Toast.LENGTH_SHORT).show();
            }
        });
        togglePasswordButton.setOnClickListener(v -> passwordVisibility.togglePasswordVisibility(edit_senha, togglePasswordButton));
        signUpButton.setOnClickListener(v -> navigation.navigationToScreen(SignUpActivity.class));
    }
}

