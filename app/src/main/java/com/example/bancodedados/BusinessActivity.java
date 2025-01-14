package com.example.bancodedados;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.activity.EdgeToEdge;
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

    private AutoCompleteTextView searchBar;
    private RecyclerView recyclerView;
    private List<Map<String, Object>> allSalons = new ArrayList<>();
    private List<Map<String, Object>> filteredSalons = new ArrayList<>();
    private CardSalonAdapter adapter;
    private ArrayAdapter<String> suggestionAdapter;
    private List<String> salonNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business);
        setupBottomNavigation();
        updateBottomNavigationSelection(R.id.nav_search);

        // Configurar ajustes de insets para adaptar a interface ao sistema de barras
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar a SearchBar para abrir ao clicar
        searchBar = findViewById(R.id.search_bar);

        // Configuração de RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Configura o adaptador de sugestões
        suggestionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, salonNames);
        searchBar.setAdapter(suggestionAdapter);

        // Carregar os dados dos salões para sugestões
        loadSalonNames();

        // Listener para filtrar salões conforme a digitação
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSalons(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Quando o usuário seleciona uma sugestão
        searchBar.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSalon = (String) parent.getItemAtPosition(position);
            filterSalons(selectedSalon); // Filtra com base na seleção
        });

        // Carregar dados do Firestore
        loadSalonDetails();
    }

    private void loadSalonNames() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Salon")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        salonNames.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("nome");
                            if (name != null && !name.isEmpty()) {
                                salonNames.add(name);
                            }
                        }
                        suggestionAdapter.notifyDataSetChanged(); // Atualiza sugestões
                    }
                });
    }

    private void filterSalons(String query) {
        filteredSalons.clear();
        if (query.isEmpty()) {
            filteredSalons.addAll(allSalons); // Se não houver texto, mostra todos os salões
        } else {
            for (Map<String, Object> salon : allSalons) {
                String name = (String) salon.get("name");
                if (name != null && name.toLowerCase().contains(query.toLowerCase())) {
                    filteredSalons.add(salon); // Adiciona o salão à lista filtrada se o nome corresponder
                }
            }
        }
        adapter.notifyDataSetChanged(); // Notifica o adapter que a lista foi atualizada
    }

    private void loadSalonDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Salon")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        allSalons.clear(); // Limpa a lista antes de adicionar os novos salões
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("nome");
                            String url = document.getString("url");
                            List<String> categories = (List<String>) document.get("categoria");
                            Double avaliacoes = document.getDouble("avaliacoes");

                            if (name == null || name.isEmpty() || url == null || url.isEmpty()) {
                                continue;
                            }

                            Map<String, Object> salon = new HashMap<>();
                            salon.put("name", name);
                            salon.put("url", url);
                            salon.put("categoria", categories);
                            salon.put("avaliacoes", avaliacoes);
                            allSalons.add(salon);
                        }

                        if (!allSalons.isEmpty()) {
                            filteredSalons.addAll(allSalons); // Inicializa a lista filtrada com todos os salões
                            adapter = new CardSalonAdapter(filteredSalons, this); // Cria o adapter com a lista filtrada
                            recyclerView.setAdapter(adapter); // Configura o adapter para o RecyclerView
                        }
                    }
                });
    }
}