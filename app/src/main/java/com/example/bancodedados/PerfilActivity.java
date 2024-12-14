package com.example.bancodedados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PerfilActivity extends AppCompatActivity {

    private TextView perfil_nome, perfil_email;
    private Button btn_sair;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Associando variáveis ao layout
        perfil_nome = findViewById(R.id.perfil_nome);
        perfil_email = findViewById(R.id.perfil_email);
        btn_sair = findViewById(R.id.btn_sair);

        btn_sair.setOnClickListener(v -> doLogoff());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DocumentReference documentReference = db.collection("Users").document(usuarioID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        // Loga ou trata o erro
                        System.err.println("Erro ao buscar os dados: " + error.getMessage());
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        perfil_nome.setText(documentSnapshot.getString("nome"));
                        perfil_email.setText(email);
                    }
                }
            });
        }
    }

    private void doLogoff() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
