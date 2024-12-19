package com.example.bancodedados;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bancodedados.utils.Navigation;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInGoogleService {
    private static final String TAG = "SignInGoogleService";
    public static final int SIGN_IN_REQUEST_CODE = 1001;
    private AppCompatActivity activity;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private Navigation navigation;

    public SignInGoogleService(AppCompatActivity activity) {
        this.activity = activity;
        this.navigation = navigation;

        oneTapClient = Identity.getSignInClient(activity);


        signInRequest = new BeginSignInRequest.Builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(activity.getString(R.string.default_web_client_id))
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .setAutoSelectEnabled(true)
                .build();
    }

    public void launchSignIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(activity, result -> {
                    try {
                        activity.startIntentSenderForResult(result.getPendingIntent().getIntentSender(),
                                SIGN_IN_REQUEST_CODE, null, 0, 0, 0);
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao iniciar One Tap", e);
                        Toast.makeText(activity, "Erro ao iniciar o processo de login.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(TAG, "Falha ao autenticar com One Tap: ", e);
                    Toast.makeText(activity, "Falha ao autenticar com o One Tap.", Toast.LENGTH_SHORT).show();
                });
    }

    public void handleSignInResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken);
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter credenciais", e);
                Toast.makeText(activity, "Falha na autenticação", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Usuário autenticado: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        activity.startActivity(new Intent(activity, PerfilActivity.class));
                        activity.finish();
                    } else {
                        Log.e(TAG, "Falha na autenticação", task.getException());
                        Toast.makeText(activity, "Falha ao autenticar.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
