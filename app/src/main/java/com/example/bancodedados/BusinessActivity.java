package com.example.bancodedados;

import static com.example.bancodedados.BuildConfig.OPENAI_API_KEY;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bancodedados.utils.CardSalonAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.IOException;
import java.util.*;
import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class BusinessActivity extends BaseActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<Map<String, Object>> allSalons = new ArrayList<>();
    private List<Map<String, Object>> filteredSalons = new ArrayList<>();
    private CardSalonAdapter adapter;
    private LevenshteinDistance levenshtein = new LevenshteinDistance();
    String apiKey = OPENAI_API_KEY;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        setupBottomNavigation();
        updateBottomNavigationSelection(R.id.nav_search);

        searchView = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Buscar Salão...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getSuggestionFromGPT(newText);
                return true;
            }
        });

        loadSalonDetails();
    }

    private void getSuggestionFromGPT(String query) {
        Log.d("GPT_Query", "Consulta enviada para o GPT: " + query); // Log para depuração

        OkHttpClient client = new OkHttpClient();
        JSONObject jsonBody = new JSONObject();
        try {
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", "Corrija ou sugira a melhor forma de buscar por: " + query);

            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", new JSONArray(Collections.singletonList(message)));
            jsonBody.put("max_tokens", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("OpenAI", "Erro: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        Log.d("OpenAI_Response", responseData); // Log para verificar a resposta do GPT

                        JSONObject jsonResponse = new JSONObject(responseData);
                        String suggestion = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        Log.d("GPT_Suggestion", "Sugestão recebida: " + suggestion); // Log para verificar a sugestão
                        runOnUiThread(() -> filterSalonsSmart(suggestion.trim()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void filterSalonsSmart(String query) {
        Log.d("FilterQuery", "Consulta para filtrar: " + query); // Log para verificar a consulta de filtragem

        filteredSalons.clear();
        if (query.isEmpty()) {
            filteredSalons.addAll(allSalons);
        } else {
            for (Map<String, Object> salon : allSalons) {
                String name = (String) salon.get("name");
                if (name != null && isSimilar(name, query)) {
                    filteredSalons.add(salon);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private boolean isSimilar(String text, String query) {
        int distance = levenshtein.apply(text.toLowerCase(), query.toLowerCase());
        Log.d("Levenshtein", "Comparando '" + text + "' com '" + query + "' -> Distância: " + distance); // Log para verificar a distância Levenshtein
        return distance <= 3 || text.toLowerCase().contains(query.toLowerCase()); // Ajuste o limite de distância
    }

    private void loadSalonDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Salon").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                allSalons.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    String name = document.getString("nome");
                    String url = document.getString("url");
                    List<String> categories = (List<String>) document.get("categoria");
                    Double avaliacoes = document.getDouble("avaliacoes");

                    if (name == null || url == null) continue;

                    Map<String, Object> salon = new HashMap<>();
                    salon.put("name", name);
                    salon.put("url", url);
                    salon.put("categoria", categories);
                    salon.put("avaliacoes", avaliacoes);
                    allSalons.add(salon);
                    Log.d("SalonData", "Carregando dados: " + name + ", " + url + ", " + categories); // Log para depuração
                }
                filteredSalons.addAll(allSalons);
                adapter = new CardSalonAdapter(filteredSalons, this);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}


//package com.example.bancodedados;
//
//import android.os.Bundle;
//import androidx.appcompat.widget.SearchView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.bancodedados.utils.CardSalonAdapter;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class BusinessActivity extends BaseActivity {
//
//    private SearchView searchView;
//    private RecyclerView recyclerView;
//    private List<Map<String, Object>> allSalons = new ArrayList<>();
//    private List<Map<String, Object>> filteredSalons = new ArrayList<>();
//    private CardSalonAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_business);
//        setupBottomNavigation();
//        updateBottomNavigationSelection(R.id.nav_search);
//
//        // Configurar ajustes de insets para adaptar a interface ao sistema de barras
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Configurar a SearchBar para abrir ao clicar
//        searchView = findViewById(R.id.search_bar);
//
//        // Configuração de RecyclerView
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//
//        // Tornar o clique na barra de pesquisa mais fluído
//        searchView.setIconifiedByDefault(false); // Garante que a SearchView esteja expandida ao iniciar
//        searchView.setQueryHint("Buscar Salão...");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false; // Não é necessário fazer nada ao submeter a pesquisa
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filterSalons(newText); // Filtra os salões conforme a digitação
//                return true;
//            }
//        });
//
//        // Carregar dados do Firestore
//        loadSalonDetails();
//    }
//
//
//
//    private void filterSalons(String query) {
//        filteredSalons.clear();
//        if (query.isEmpty()) {
//            filteredSalons.addAll(allSalons); // Se não houver texto, mostra todos os salões
//        } else {
//            for (Map<String, Object> salon : allSalons) {
//                String name = (String) salon.get("name");
//                if (name != null && name.toLowerCase().contains(query.toLowerCase())) {
//                    filteredSalons.add(salon); // Adiciona o salão à lista filtrada se o nome corresponder
//                }
//            }
//        }
//        adapter.notifyDataSetChanged(); // Notifica o adapter que a lista foi atualizada
//    }
//
//    private void loadSalonDetails() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("Salon")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        allSalons.clear(); // Limpa a lista antes de adicionar os novos salões
//                        for (DocumentSnapshot document : task.getResult()) {
//                            String name = document.getString("nome");
//                            String url = document.getString("url");
//                            List<String> categories = (List<String>) document.get("categoria");
//                            Double avaliacoes = document.getDouble("avaliacoes");
//
//                            if (name == null || name.isEmpty() || url == null || url.isEmpty()) {
//                                continue;
//                            }
//
//                            Map<String, Object> salon = new HashMap<>();
//                            salon.put("name", name);
//                            salon.put("url", url);
//                            salon.put("categoria", categories);
//                            salon.put("avaliacoes", avaliacoes);
//                            allSalons.add(salon);
//                        }
//
//                        if (!allSalons.isEmpty()) {
//                            filteredSalons.addAll(allSalons); // Inicializa a lista filtrada com todos os salões
//                            adapter = new CardSalonAdapter(filteredSalons, this); // Cria o adapter com a lista filtrada
//                            recyclerView.setAdapter(adapter); // Configura o adapter para o RecyclerView
//                        }
//                    }
//                });
//    }


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
//
//}