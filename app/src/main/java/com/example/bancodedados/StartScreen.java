package com.example.bancodedados;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class StartScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private static final String TAG = "StartScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_screen);

        // Configurações de janela
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        // Inicializa o Firebase Auth e Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Configura o Google One Tap Sign-In
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = new BeginSignInRequest.Builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id)) // Certifique-se de ter isso no strings.xml
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .setAutoSelectEnabled(true)
                .build();

        // Botões de ação
        LinearLayout googleSignInButton = findViewById(R.id.login_google_button);
        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> goToLogin());
        googleSignInButton.setOnClickListener(v -> launchSignIn());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null) {
            Intent intent = new Intent(StartScreen.this, PerfilActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(StartScreen.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchSignIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(),
                                1001, null, 0, 0, 0);
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao iniciar One Tap", e);
                        Toast.makeText(StartScreen.this, "Erro ao iniciar o processo de login.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Falha ao autenticar com One Tap: ", e);
                    Toast.makeText(StartScreen.this, "Falha ao autenticar com o One Tap.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken);
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter credenciais", e);
                Toast.makeText(this, "Falha na autenticação", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "Usuário autenticado: " + user.getEmail());
                            salvarDadosUsuario(user);
                        }
                    } else {
                        Log.e(TAG, "Falha na autenticação", task.getException());
                        Toast.makeText(StartScreen.this, "Falha ao autenticar.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void salvarDadosUsuario(FirebaseUser user) {
        // UID do usuário
        String usuarioID = user.getUid();

        // Adiciona logs para depuração
        Log.d(TAG, "UID do usuário autenticado: " + usuarioID);
        Log.d(TAG, "Email do usuário: " + user.getEmail());

        // Prepara os dados para salvar no Firestore
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nome", user.getDisplayName());
        usuario.put("email", user.getEmail());
        usuario.put("fotoPerfil", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        usuario.put("dataCriacao", System.currentTimeMillis());

        // Salva os dados no Firestore
        DocumentReference documentReference = firestore.collection("Users").document(usuarioID);
        documentReference.set(usuario)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Usuário salvo no Firestore com sucesso");
                        startActivity(new Intent(StartScreen.this, PerfilActivity.class));
                        finish();
                    } else {
                        Log.e(TAG, "Erro ao salvar no Firestore", task.getException());
                        Toast.makeText(StartScreen.this, "Erro ao salvar os dados no Firestore.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao salvar no Firestore", e);
                    Toast.makeText(StartScreen.this, "Erro ao salvar os dados no Firestore.", Toast.LENGTH_SHORT).show();
                });
    }
}
