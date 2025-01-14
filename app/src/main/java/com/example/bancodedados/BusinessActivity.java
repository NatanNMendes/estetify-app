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

import java.text.Normalizer;
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
    private List<String> suggestions = new ArrayList<>();

    /**
     * Método chamado ao criar a BusinessActivity. Inicializa o layout e configura os elementos da interface.
     * @see         #onCreate
     */
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

        // Inicializa a SearchBar como AutoCompleteTextView
        searchBar = findViewById(R.id.search_bar);

        // Configuração do RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Inicializa o Adapter para sugestões
        suggestionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions);
        searchBar.setAdapter(suggestionAdapter);

        // Carrega os dados dos salões para sugestões
        loadSalonNames();

        // Listener para atualizar sugestões em tempo real
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Quando o usuário seleciona uma sugestão
        searchBar.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSalon = (String) parent.getItemAtPosition(position);
            filterSalons(selectedSalon); // Filtra com base na seleção
        });

        // Carregar os detalhes dos salões
        loadSalonDetails();
    }

    /**
     * A consulta de busca inserida pelo usuário na barra de pesquisa.
     */
    private void updateSuggestions(String query) {
        suggestions.clear();
        String normalizedQuery = normalizeText(query);

        for (String name : salonNames) {
            String normalizedName = normalizeText(name);

            if (normalizedName.contains(normalizedQuery)) {
                suggestions.add(name);
            }
        }

        suggestionAdapter.clear();
        suggestionAdapter.addAll(suggestions);
        suggestionAdapter.notifyDataSetChanged();
    }


    /**
     * Normaliza o texto removendo acentos e espaços.
     */
    private String normalizeText(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[^\\p{ASCII}]", "");
        return normalized.toLowerCase();
    }


    /**
     * Verifica se a query é uma subsequência do nome (ex: "dvn" → "divina").
     */
    private boolean isSubsequence(String query, String target) {
        int queryIndex = 0, targetIndex = 0;

        while (queryIndex < query.length() && targetIndex < target.length()) {
            if (query.charAt(queryIndex) == target.charAt(targetIndex)) {
                queryIndex++;
            }
            targetIndex++;
        }

        return queryIndex == query.length();
    }

    /**
     * Carrega os nomes dos salões do Firestore.
     */
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

    /**
     * Filtra os salões conforme a busca.
     */
    private void filterSalons(String query) {
        filteredSalons.clear();
        String normalizedQuery = normalizeText(query);

        for (Map<String, Object> salon : allSalons) {
            String name = (String) salon.get("name");
            if (name != null && (normalizeText(name).contains(normalizedQuery) || isSubsequence(normalizedQuery, normalizeText(name)))) {
                filteredSalons.add(salon);
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Carrega detalhes completos dos salões.
     */
    private void loadSalonDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Salon")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        allSalons.clear();
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
                            filteredSalons.addAll(allSalons);
                            adapter = new CardSalonAdapter(filteredSalons, this);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
    }
}