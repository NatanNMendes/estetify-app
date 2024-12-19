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

import com.example.bancodedados.utils.Navigation;
import com.example.bancodedados.utils.PasswordVisibility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
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
    private PasswordVisibility passwordVisibility;
    private static final String[] mensagens = {
            "Preencha todos os campos",
            "Cadastro realizado com sucesso",
            "Erro ao cadastrar usuário",
            "Senha muito curta",
            "As senhas não coincidem"
    };
    private Navigation navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        passwordVisibility = new PasswordVisibility();
        navigation = new Navigation(this);
        setupUI();
        setupWindow();
        setupListeners();
    }

    private void setupUI() {
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        togglePasswordButton = findViewById(R.id.togglePasswordButton);
        toggleConfirmPasswordButton = findViewById(R.id.toggleConfirmPasswordButton);
        createAccountButton = findViewById(R.id.createAccountButton);

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
        togglePasswordButton.setOnClickListener(view -> passwordVisibility.togglePasswordVisibility(passwordInput, togglePasswordButton));
        toggleConfirmPasswordButton.setOnClickListener(view -> passwordVisibility.togglePasswordVisibility(confirmPasswordInput, toggleConfirmPasswordButton));
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

    private void createAccount(String email, String senha, String nome) {
        // Criação de usuário no Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                salvarDadosUsuario(Objects.requireNonNull(firebaseAuth.getCurrentUser()));
            } else {
                handleAuthError(task.getException());
            }
        });
    }

    private void salvarDadosUsuario(FirebaseUser user) {
        String usuarioID = user.getUid();

        Log.d(TAG, "UID do usuário autenticado: " + usuarioID);
        Log.d(TAG, "Email do usuário: " + user.getEmail());

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nome", user.getDisplayName());
        usuario.put("email", user.getEmail());
        usuario.put("fotoPerfil", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        usuario.put("dataCriacao", System.currentTimeMillis());

        DocumentReference documentReference = firestore.collection("Users").document(usuarioID);
        documentReference.set(usuario)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Usuário salvo no Firestore com sucesso");
                        navigation.navigationToScreen(PerfilActivity.class);
                        finish();
                    } else {
                        Log.e(TAG, "Erro ao salvar no Firestore", task.getException());
                        Toast.makeText(CreateAccount.this, "Erro ao salvar os dados no Firestore.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao salvar no Firestore", e);
                    Toast.makeText(CreateAccount.this, "Erro ao salvar os dados no Firestore.", Toast.LENGTH_SHORT).show();
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
