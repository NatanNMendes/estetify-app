package com.example.bancodedados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.Nullable;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001; // Código de requisição para o Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configura as opções do Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()  // Solicita o email do usuário
                .requestIdToken("SEU_CLIENT_ID")  // Substitua com seu client ID
                .build();

        // Inicia o Google Sign-In com as opções configuradas
        Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);  // Inicia o processo de login
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica se o código da requisição corresponde ao do Google Sign-In
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Tenta obter o conta do Google do resultado
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();  // Obtém o ID Token para autenticação no backend

                // Chama um método para salvar as credenciais (pode ser uma classe externa, como CredentialManagerHelper)
                com.example.bancodedados.CredentialManagerHelper.saveCredentials(this, account.getEmail(), idToken);

                // Exibe uma mensagem de sucesso
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {
                e.printStackTrace();
                // Em caso de erro, exibe uma mensagem de falha
                Toast.makeText(this, "Falha no Google Sign-In", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
