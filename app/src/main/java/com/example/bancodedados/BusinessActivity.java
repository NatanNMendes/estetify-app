package com.example.bancodedados;

import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

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

    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<Map<String, Object>> allSalons = new ArrayList<>();
    private List<Map<String, Object>> filteredSalons = new ArrayList<>();
    private CardSalonAdapter adapter;

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
        searchView = findViewById(R.id.search_bar);

        // Configuração de RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Tornar o clique na barra de pesquisa mais fluído
        searchView.setIconifiedByDefault(false); // Garante que a SearchView esteja expandida ao iniciar
        searchView.setQueryHint("Buscar Salão...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Não é necessário fazer nada ao submeter a pesquisa
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSalons(newText); // Filtra os salões conforme a digitação
                return true;
            }
        });

        // Carregar dados do Firestore
        loadSalonDetails();
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


//    private void loadSalonDetails() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("Salon")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<Map<String, Object>> salons = new ArrayList<>();
//                        for (DocumentSnapshot document : task.getResult()) {
//                            String name = document.getString("nome");
//                            String url = document.getString("url");
//                            List<String> categories = (List<String>) document.get("categoria");
//                            Double avaliacoes = document.getDouble("avaliacoes"); // Pegar o campo de avaliação
//
//                            if (name == null || name.isEmpty() || url == null || url.isEmpty()) {
//                                continue;
//                            }
//
//                            Map<String, Object> salon = new HashMap<>();
//                            salon.put("name", name);
//                            salon.put("url", url);
//                            salon.put("categoria", categories);
//                            salon.put("avaliacoes", avaliacoes); // Adicionar o campo de avaliação ao mapa
//                            salons.add(salon);
//                        }
//
//                        if (!salons.isEmpty()) {
//                            CardSalonAdapter adapter = new CardSalonAdapter(salons, this);
//                            recyclerView.setAdapter(adapter);
//                        }
//                    }
//                });
//    }

}
