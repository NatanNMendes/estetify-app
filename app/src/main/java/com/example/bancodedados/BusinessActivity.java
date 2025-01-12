package com.example.bancodedados;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bancodedados.utils.CardSalonAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessActivity extends BaseActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business);
        setupBottomNavigation();
        updateBottomNavigationSelection(R.id.nav_search);

        // Configurar ajustes de insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Carregar dados do Firestore
        loadSalonDetails();
    }

    private void loadSalonDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Salon")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Map<String, Object>> salons = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("nome");
                            String url = document.getString("url");
                            List<String> categories = (List<String>) document.get("categoria");
                            Double avaliacoes = document.getDouble("avaliacoes"); // Pegar o campo de avaliação

                            if (name == null || name.isEmpty() || url == null || url.isEmpty()) {
                                continue;
                            }

                            Map<String, Object> salon = new HashMap<>();
                            salon.put("name", name);
                            salon.put("url", url);
                            salon.put("categoria", categories);
                            salon.put("avaliacoes", avaliacoes); // Adicionar o campo de avaliação ao mapa
                            salons.add(salon);
                        }

                        if (!salons.isEmpty()) {
                            CardSalonAdapter adapter = new CardSalonAdapter(salons, this);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
    }

}
