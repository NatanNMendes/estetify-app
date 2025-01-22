package com.example.bancodedados;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bancodedados.AdapterTable;
import com.example.bancodedados.utils.AdapterCard;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private AdapterTable adapterTable;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigation();
        updateBottomNavigationSelection(R.id.nav_home);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        RecyclerView carouselRecyclerView = findViewById(R.id.carousel_recycler_view);
        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        RecyclerView tableRecyclerView = findViewById(R.id.table_recycler_view);
        tableRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapterTable = new com.example.bancodedados.AdapterTable(new ArrayList<>());
        tableRecyclerView.setAdapter(adapterTable);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLocation = location;
                Log.d("Location", "Localização atualizada: " + location);
                RecyclerView recyclerView = findViewById(R.id.carousel_recycler_view);
                List<String> selectedCategories = new ArrayList<>();
                loadSalonDetails(recyclerView, selectedCategories, getApplicationContext(), userLocation);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        setupCategoryButtons(carouselRecyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Verifica se o usuário está autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Obtém o ID do usuário logado
            String usuarioID = user.getUid();

            // Exemplo: Exibe o ID do usuário no log
            Log.d("Auth", "Usuário autenticado. ID: " + usuarioID);

            // Carregar produtos do Firestore
            loadPurchasedProducts(usuarioID);
        } else {
            Log.d("Auth", "Usuário não autenticado.");
        }
    }

    private void loadPurchasedProducts(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Acessar o documento do usuário na coleção "Users"
        db.collection("Users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Verificar se o campo "produtosComprados" existe
                        List<Map<String, Object>> produtosComprados =
                                (List<Map<String, Object>>) task.getResult().get("produtosComprados");

                        if (produtosComprados != null) {
                            List<com.example.bancodedados.AdapterTable.RowItem> products = new ArrayList<>();

                            // Iterar sobre os 5 primeiros produtos comprados e adicionar ao adapter
                            int count = 0; // Contador para limitar os itens
                            for (Map<String, Object> produto : produtosComprados) {
                                if (count >= 5) break; // Interrompe após os 5 primeiros itens

                                String nome = (String) produto.get("nome");

                                // Verificar o tipo do campo "valor" e garantir que é tratado corretamente
                                Object precoObj = produto.get("valor");
                                String preco = "0"; // Valor padrão

                                if (precoObj != null) {
                                    Log.d("PrecoObj", "Tipo de 'precoObj': " + precoObj.getClass().getSimpleName());

                                    // Verificar se o precoObj é um número
                                    if (precoObj instanceof Number) {
                                        preco = String.valueOf(precoObj); // Converte Number para String
                                        Log.d("Preco", "Preco (Number): " + preco);
                                    } else if (precoObj instanceof String) {
                                        preco = (String) precoObj; // Se for String, utiliza diretamente
                                        Log.d("Preco", "Preco (String): " + preco);
                                    } else {
                                        Log.d("Preco", "Tipo de preco desconhecido, utilizando valor padrão.");
                                    }
                                } else {
                                    Log.d("Preco", "Preco é null, utilizando valor padrão.");
                                }

                                if (nome != null && !nome.isEmpty()) {
                                    // Adicionar à lista de produtos
                                    products.add(new com.example.bancodedados.AdapterTable.RowItem(nome, preco));
                                }

                                count++; // Incrementa o contador
                            }

                            // Atualizar o adaptador com os dados carregados
                            adapterTable.updateItems(products);
                        } else {
                            Log.d("Firestore", "O usuário não tem produtos comprados.");
                        }
                    } else {
                        Log.e("Firestore", "Erro ao carregar dados do usuário.", task.getException());
                    }
                });
    }

    private LatLng getLatLngFromCep(Context context, String cep) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(cep, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void loadSalonDetails(RecyclerView recyclerView, List<String> selectedCategories, Context context, Location userLocation) {
        if (userLocation == null) {
            Log.e("Location", "Localização do usuário não está disponível.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Salon")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Map<String, Object>> salons = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("nome");
                            String url = document.getString("url");
                            String cep = document.getString("cep");
                            List<String> categories = (List<String>) document.get("categoria");

                            if (name == null || url == null || cep == null) continue;

                            if (selectedCategories.isEmpty() || (categories != null && !Collections.disjoint(categories, selectedCategories))) {
                                LatLng salonLatLng = getLatLngFromCep(context, cep);
                                if (salonLatLng != null) {
                                    double distance = calculateDistance(userLocation.getLatitude(), userLocation.getLongitude(), salonLatLng.latitude, salonLatLng.longitude);

                                    Map<String, Object> salon = new HashMap<>();
                                    salon.put("name", name);
                                    salon.put("url", url);
                                    salon.put("distance", distance);
                                    salons.add(salon);
                                }
                            }
                        }

                        Collections.sort(salons, (s1, s2) -> Double.compare((double) s1.get("distance"), (double) s2.get("distance")));

                        if (!salons.isEmpty()) {
                            AdapterCard adapter = new AdapterCard(salons);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d("Firestore", "Nenhum salão encontrado para as categorias selecionadas.");
                        }
                    } else {
                        Log.e("Firestore", "Erro ao carregar dados da coleção Salon.", task.getException());
                    }
                });
    }

    private void setupCategoryButtons(RecyclerView recyclerView) {
        List<String> selectedCategories = new ArrayList<>();
        Drawable iconX = ContextCompat.getDrawable(this, R.drawable.ic_check);
        if (iconX != null) {
            iconX.setBounds(0, 0, iconX.getIntrinsicWidth(), iconX.getIntrinsicHeight());
        }

        List<Button> categoryButtons = Arrays.asList(
                findViewById(R.id.button_category1),
                findViewById(R.id.button_category3),
                findViewById(R.id.button_category5),
                findViewById(R.id.button_category6),
                findViewById(R.id.button_category7),
                findViewById(R.id.button_category8)
        );

        for (Button button : categoryButtons) {
            button.setOnClickListener(v -> {
                String category = button.getText().toString();
                if (selectedCategories.contains(category)) {
                    selectedCategories.remove(category);
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BCBCBC")));
                    button.setCompoundDrawables(null, null, null, null);
                } else {
                    selectedCategories.add(category);
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6200EE")));
                    button.setCompoundDrawables(null, null, iconX, null);
                }
                loadSalonDetails(recyclerView, selectedCategories, getApplicationContext(), userLocation);
            });
        }
    }
}


