package com.example.bancodedados.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.bancodedados.LoginActivity;
import com.example.bancodedados.PerfilActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUserService {
    private static final String TAG = "FirebaseService";
    private final FirebaseFirestore firestore;

    public FirebaseUserService() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void saveUserData(FirebaseUser user, Context context) {
        if (user == null || context == null) {
            Log.e(TAG, "Usuário ou contexto inválido.");
            return;
        }

        String usuarioID = user.getUid();
        Map<String, Object> usuario = createUserDataMap(user);

        saveToFirestore(usuarioID, usuario, context);
    }

    private Map<String, Object> createUserDataMap(FirebaseUser user) {
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nome", user.getDisplayName());
        usuario.put("email", user.getEmail());
        usuario.put("fotoPerfil", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        usuario.put("dataCriacao", System.currentTimeMillis());
        Log.d(TAG, "Mapa de dados do usuário criado com sucesso: " + usuario);
        return usuario;
    }

    private void saveToFirestore(String userId, Map<String, Object> userData, Context context) {
        DocumentReference documentReference = firestore.collection("Users").document(userId);
        documentReference.set(userData)
                .addOnCompleteListener(task -> handleFirestoreResponse(task.isSuccessful(), context))
                .addOnFailureListener(e -> handleFirestoreError(e, context));
    }

    private void handleFirestoreResponse(boolean success, Context context) {
        if (success) {
            Log.d(TAG, "Usuário salvo no Firestore com sucesso");
            showToast(context, "Dados salvos com sucesso!");
        } else {
            Log.e(TAG, "Erro ao salvar no Firestore");
            showToast(context, "Erro ao salvar os dados no Firestore.");
        }
    }

    private void handleFirestoreError(Exception e, Context context) {
        Log.e(TAG, "Erro ao salvar no Firestore", e);
        showToast(context, "Erro ao salvar os dados no Firestore.");
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void performLogout(Context context) {
        if (context == null) {
            Log.e(TAG, "Contexto inválido. Não é possível realizar logout.");
            return;
        }
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("came_from", "PerfilActivity");

        context.startActivity(intent);

        if (context instanceof PerfilActivity) {
            ((PerfilActivity) context).finish();
        }
    }
}
