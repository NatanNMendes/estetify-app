package com.example.bancodedados;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccount extends AppCompatActivity {

    private static final String TAG = "CreateAccount";

    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button createAccountButton;
    private ImageButton togglePasswordButton, toggleConfirmPasswordButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private boolean isPasswordVisible = false;

    private static final String[] mensagens = {
            "Preencha todos os campos",
            "Cadastro realizado com sucesso",
            "Erro ao cadastrar usuário",
            "Senha muito curta",
            "As senhas não coincidem"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        setupUI();
        setupWindow();
        setupListeners();
    }

    private void setupUI() {
        // Inicializa os componentes da UI
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        togglePasswordButton = findViewById(R.id.togglePasswordButton);
        toggleConfirmPasswordButton = findViewById(R.id.toggleConfirmPasswordButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        // Inicializa Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void setupWindow() {
        // Configura a barra de status e margens
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupListeners() {
        // Alternar visibilidade da senha
        togglePasswordButton.setOnClickListener(view -> togglePasswordVisibility(passwordInput, togglePasswordButton));
        toggleConfirmPasswordButton.setOnClickListener(view -> togglePasswordVisibility(confirmPasswordInput, toggleConfirmPasswordButton));

        // Botão de criar conta
        createAccountButton.setOnClickListener(view -> {
            String nome = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String senha = passwordInput.getText().toString().trim();
            String confirmSenha = confirmPasswordInput.getText().toString().trim();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmSenha.isEmpty()) {
                showToast(mensagens[0]);
            } else if (!senha.equals(confirmSenha)) {
                showToast(mensagens[4]);
            } else if (senha.length() < 6) {
                showToast(mensagens[3]);
            } else {
                createAccount(email, senha, nome);
            }
        });
    }

    private void togglePasswordVisibility(EditText passwordField, ImageButton toggleButton) {
        // Alterna visibilidade da senha
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_visibility_on);
        } else {
            passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_visibility_off);
        }
        passwordField.setSelection(passwordField.getText().length());
    }

    private void createAccount(String email, String senha, String nome) {
        // Criação de usuário no Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                salvarDadosUsuario(nome);
            } else {
                handleAuthError(task.getException());
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(CreateAccount.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void salvarDadosUsuario(String nome) {
        // Obtém o UID do usuário autenticado
        String usuarioID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        // Adiciona logs para verificar o UID e o nome
        Log.d(TAG, "UID do usuário autenticado: " + usuarioID);
        Log.d(TAG, "Nome do usuário: " + nome);

        // Prepara o documento para salvar no Firestore
        DocumentReference documentReference = firestore.collection("Users").document(usuarioID);

        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("nome", nome);
        usuarios.put("email", firebaseAuth.getCurrentUser().getEmail());
        usuarios.put("dataCriacao", System.currentTimeMillis());

        documentReference.set(usuarios)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Usuário salvo no Firestore com sucesso");
                        showToast("Usuário cadastrado com sucesso!");
                        navigateToMain();
                    } else {
                        Log.e(TAG, "Erro ao salvar no Firestore", task.getException());
                        showToast("Erro ao salvar os dados no Firestore");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao salvar no Firestore", e);
                    showToast("Erro ao salvar os dados no Firestore");
                });
    }


    private void handleAuthError(Exception exception) {
        // Trata erros de autenticação
        String erro = "Erro ao cadastrar usuário";
        if (exception instanceof FirebaseAuthException) {
            erro = Objects.requireNonNull(((FirebaseAuthException) exception).getLocalizedMessage());
        }
        showToast(erro);
    }

    private void navigateToMainActivity() {
        // Redireciona para a tela principal após cadastro
        Intent intent = new Intent(CreateAccount.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        if (toast.getView() != null) {
            Objects.requireNonNull(toast.getView()).setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            TextView text = toast.getView().findViewById(android.R.id.message);
            if (text != null) {
                text.setTextColor(Color.BLACK);
            }
        }
        toast.show();
    }
}
