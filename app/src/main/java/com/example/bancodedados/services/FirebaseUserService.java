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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        DocumentReference documentReference = firestore.collection("Users").document(usuarioID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    // O documento já existe. Preserve o campo 'produtosComprados'.
                    List<Map<String, Object>> produtosCompradosExistentes =
                            (List<Map<String, Object>>) documentSnapshot.get("produtosComprados");

                    if (produtosCompradosExistentes != null) {
                        usuario.put("produtosComprados", produtosCompradosExistentes);
                    }

                    // Atualiza o documento com os dados mesclados
                    documentReference.update(usuario)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Dados do usuário atualizados com sucesso.");
                                showToast(context, "Dados atualizados com sucesso!");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Erro ao atualizar dados do usuário.", e);
                                showToast(context, "Erro ao atualizar os dados no Firestore.");
                            });
                } else {
                    // O documento não existe. Cria um novo documento.
                    documentReference.set(usuario)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Usuário salvo no Firestore com sucesso.");
                                showToast(context, "Dados salvos com sucesso!");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Erro ao salvar os dados no Firestore.", e);
                                showToast(context, "Erro ao salvar os dados no Firestore.");
                            });
                }
            } else {
                Log.e(TAG, "Erro ao verificar existência do documento.", task.getException());
                showToast(context, "Erro ao verificar dados do usuário.");
            }
        });
    }

    private Map<String, Object> createUserDataMap(FirebaseUser user) {
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nome", user.getDisplayName());
        usuario.put("email", user.getEmail());
        usuario.put("fotoPerfil", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        usuario.put("dataCriacao", System.currentTimeMillis());
        List<Map<String, Object>> produtosComprados = new ArrayList<>();
        usuario.put("produtosComprados", produtosComprados);

        Log.d(TAG, "Mapa de dados do usuário criado com sucesso: " + usuario);
        return usuario;
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
