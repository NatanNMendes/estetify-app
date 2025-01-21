package com.example.bancodedados;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PerfilActivity extends BaseActivity {
    private TextView perfil_nome, perfil_email, perfil_data_criacao, perfil_localizacao;
    private ImageView perfil_foto;
    private Button btn_sair;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;
    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_perfil);
        setupBottomNavigation();
        updateBottomNavigationSelection(R.id.nav_profile);

        // Oculta a ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Atualizar a localização na interface
                String localizacao = "Lat: " + location.getLatitude() + ", Long: " + location.getLongitude();
                perfil_localizacao.setText("Localização: " + localizacao);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
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
                                    .load(fotoUrl)
                                    .placeholder(R.drawable.ic_person)
                                    .error(R.drawable.ic_person)
                                    .into(perfil_foto);
                        } else {
                            perfil_foto.setImageResource(R.drawable.ic_person);
                        }

                        // Busca o campo 'dataCriacao' e converte para data legível
                        Long dataCriacaoMillis = documentSnapshot.getLong("dataCriacao");
                        if (dataCriacaoMillis != null) {
                            String dataFormatada = converterMillisParaData(dataCriacaoMillis);
                            perfil_data_criacao.setText("Conta criada em: " + dataFormatada);
                        } else {
                            perfil_data_criacao.setText("Data de criação não disponível.");
                        }
                    } else {
                        Log.d("Firestore", "Documento não encontrado.");
                    }
                }
            });
        } else {
            Log.d("Auth", "Usuário não autenticado.");
        }

        // Verificar permissões de localização e obter a localização
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Log para indicar que as permissões não foram concedidas
            Log.d("LocationPermission", "Permissões de localização não concedidas.");

            // Solicita as permissões
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Log para indicar que as permissões foram concedidas
            Log.d("LocationPermission", "Permissões de localização concedidas.");

            // Solicita atualizações de localização
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // Log para indicar que a solicitação de atualizações de localização foi feita
            Log.d("LocationManager", "Solicitação de atualizações de localização feita com sucesso.");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Parar a atualização da localização quando a atividade parar
        locationManager.removeUpdates(locationListener);
    }

    // Converte o timestamp para uma data legível
    private String converterMillisParaData(Long millis) {
        Date date = new Date(millis);
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return formato.format(date);
    }

    private void IniciarComponentes() {
        btn_sair = findViewById(R.id.btn_sair);
        perfil_nome = findViewById(R.id.perfil_nome);
        perfil_email = findViewById(R.id.perfil_email);
        perfil_foto = findViewById(R.id.perfil_foto);
        perfil_data_criacao = findViewById(R.id.perfil_data_criacao); // Novo campo adicionado
        perfil_localizacao = findViewById(R.id.perfil_localizacao);
    }
}
