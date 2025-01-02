package com.example.bancodedados.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Objects;

public class SignUpService {
    private static final String TAG = "SignUpService";
    private static final String[] mensagens = {
            "Preencha todos os campos",
            "Cadastro realizado com sucesso",
            "Erro ao cadastrar usuário",
            "Senha muito curta",
            "As senhas não coincidem"
    };

    private final FirebaseAuth firebaseAuth;
    private final FirebaseUserService firebaseUserService;

    public SignUpService() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUserService = new FirebaseUserService();
    }

    public void createAccount(String email, String password, String confirmPassword, String username, Context context) {
        if (context == null) {
            Log.e(TAG, "Contexto inválido.");
            return;
        }

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast(context, mensagens[0]);
        } else if (!password.equals(confirmPassword)) {
            showToast(context, mensagens[4]);
        } else if (password.length() < 6) {
            showToast(context, mensagens[3]);
        } else {
            registerUser(email, password, username, context);
        }
    }

    private void registerUser(String email, String password, String username, Context context) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseUserService.saveUserData(Objects.requireNonNull(firebaseAuth.getCurrentUser()), context);
            } else {
                handleAuthError(task.getException(), context);
            }
        });
    }

    private void handleAuthError(Exception exception, Context context) {
        String errorMessage = mensagens[2];
        if (exception instanceof FirebaseAuthException) {
            errorMessage = Objects.requireNonNull(((FirebaseAuthException) exception).getLocalizedMessage());
        }
        showToast(context, errorMessage);
        Log.e(TAG, "Erro de autenticação: " + exception);
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
