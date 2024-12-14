package com.example.bancodedados;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PerfilActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final int PERMISSION_REQUEST_CODE = 100;

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

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        if (currentUser == null) {
            redirectToLogin();
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                saveUserCredentials(currentUser.getEmail(), currentUser.getUid());
                Toast.makeText(this, "Bem-vindo de volta, " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        }

        btn_sair.setOnClickListener(v -> doLogoff());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            usuarioID = currentUser.getUid();

            DocumentReference documentReference = db.collection("Users").document(usuarioID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("PerfilActivity", "Erro ao buscar dados do usuário", error);
                        Toast.makeText(PerfilActivity.this, "Erro ao carregar informações do perfil", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String nome = documentSnapshot.getString("nome");
                        String email = currentUser.getEmail();

                        perfil_nome.setText(nome != null ? nome : "Nome não disponível");
                        perfil_email.setText(email != null ? email : "Email não disponível");
                    }
                }
            });
        } else {
            redirectToLogin();
        }
    }


    private void saveUserCredentials(String email, String uid) {
        UserPreferences.saveUser(this, email, uid);
        Log.d("MainActivity", "Dados armazenados - Email: " + email + ", UID: " + uid);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void doLogoff() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
