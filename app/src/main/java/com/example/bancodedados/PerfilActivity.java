package com.example.bancodedados;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PerfilActivity extends AppCompatActivity {
    private TextView perfil_nome, perfil_email;
    private ImageView perfil_foto;
    private Button btn_sair;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_perfil);
        IniciarComponentes();

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                intent.putExtra("came_from", "PerfilActivity");
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Verifica se o usuário está autenticado
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Busca o documento do usuário no Firestore
            DocumentReference documentReference = db.collection("Users").document(usuarioID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        // Log de erro
                        Log.e("FirestoreError", error.getMessage());
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Atualiza os dados na UI
                        perfil_nome.setText(documentSnapshot.getString("nome"));
                        perfil_email.setText(email);

                        // Carrega a foto de perfil se existir
                        String fotoUrl = documentSnapshot.getString("fotoPerfil");
                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            Glide.with(PerfilActivity.this)
                                    .load(fotoUrl) // URL da foto
                                    .placeholder(R.drawable.ic_person) // Imagem padrão enquanto carrega
                                    .error(R.drawable.ic_person) // Imagem caso ocorra erro
                                    .into(perfil_foto);
                        } else {
                            // Se não houver foto, define a imagem padrão
                            perfil_foto.setImageResource(R.drawable.ic_person);
                        }
                    } else {
                        Log.d("Firestore", "Documento não encontrado.");
                    }
                }
            });
        } else {
            Log.d("Auth", "Usuário não autenticado.");
        }
    }


    private void IniciarComponentes(){
        btn_sair = findViewById(R.id.btn_sair);
        perfil_nome = findViewById(R.id.perfil_nome);
        perfil_email = findViewById(R.id.perfil_email);
        perfil_foto = findViewById(R.id.perfil_foto);
    }
}