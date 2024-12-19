package com.example.bancodedados.services;


import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bancodedados.PerfilActivity;
import com.example.bancodedados.utils.Navigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInService {

    private Context context;
    private Navigation navigation;

    public SignInService(Context context, Navigation navigation) {
        this.context = context;
        this.navigation = navigation;
    }

    public void signInWithEmailAndPassword(String email, String senha) {
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                            navigation.navigationToScreen(PerfilActivity.class);
                        } else {
                            String erro;
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                erro = "Erro ao realizar login";
                            }
                            Toast.makeText(context, erro, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

