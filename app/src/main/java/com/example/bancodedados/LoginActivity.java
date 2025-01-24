package com.example.bancodedados;


import android.content.res.ColorStateList;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
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


import com.example.bancodedados.services.SignInService;
import com.example.bancodedados.utils.Navigation;
import com.example.bancodedados.utils.PasswordVisibility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText edit_email, edit_senha;
    private Button btn_entrar;
    private ProgressBar progress_bar;
    private Navigation navigation;
    private SignInService signInService; // Novo serviço de autenticação
    private PasswordVisibility passwordVisibility;
    private FirebaseAuth mAuth;
    String[] mensagens = {"Preencha todos os campos", "Login realizado com sucesso", "Erro ao realizar login"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        navigation = new Navigation(this);
        signInService = new SignInService(this, navigation);
        passwordVisibility = new PasswordVisibility();

        mAuth = FirebaseAuth.getInstance();

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
                edit_email.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            } else {
                edit_email.setError(null);
                edit_email.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            }

            if (senha.isEmpty()) {
                edit_senha.setError("Senha é obrigatória");
                edit_senha.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            } else {
                edit_senha.setError(null);
                edit_senha.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            }

            if (!email.isEmpty() && !senha.isEmpty()) {
                signInService.signInWithEmailAndPassword(email, senha);
            } else {
                Toast.makeText(LoginActivity.this, mensagens[0], Toast.LENGTH_SHORT).show();
            }
        });
        togglePasswordButton.setOnClickListener(v -> passwordVisibility.togglePasswordVisibility(edit_senha, togglePasswordButton));
        signUpButton.setOnClickListener(v -> navigation.navigationToScreen(SignUpActivity.class));

        configurarBotaoEsqueceuSenha();
    }

    private void configurarBotaoEsqueceuSenha() {
        TextView botaoEsqueceuSenha = findViewById(R.id.botao_esqueceu_senha);
        botaoEsqueceuSenha.setOnClickListener(v -> showAlertDialogRecoverPassword());
    }

    /**
     * Mostra um diálogo com campo de email para recuperação de senha
     */
    private void showAlertDialogRecoverPassword() {
        // Criar o EditText para o email
        EditText campoEmailRecuperar = new EditText(this);
        campoEmailRecuperar.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        campoEmailRecuperar.setHint("Digite seu e-mail");
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        campoEmailRecuperar.setPadding(padding, padding, padding, padding);

        // Criar e configurar o diálogo
        new MaterialAlertDialogBuilder(this)
                .setTitle("Recuperar Senha")
                .setMessage("Digite seu e-mail para receber o link de recuperação de senha")
                .setView(campoEmailRecuperar)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String email = campoEmailRecuperar.getText().toString().trim();
                    if (!email.isEmpty()) {
                        enviarEmailRecuperacao(email);
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Por favor, digite seu e-mail",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    /**
     * Envia o email de recuperação de senha usando o Firebase
     * @param email Email do usuário
     */
    private void enviarEmailRecuperacao(String email) {
        progress_bar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progress_bar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,
                                "Email de recuperação enviado com sucesso",
                                Toast.LENGTH_LONG).show();
                    } else {
                        String erro = "Erro ao enviar email de recuperação";
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            erro = "Não existe uma conta com este e-mail";
                        }
                        Toast.makeText(LoginActivity.this, erro, Toast.LENGTH_LONG).show();
                    }
                });
    }
}

