package com.example.bancodedados;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;

import com.example.bancodedados.utils.AdapterTableHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryActivity extends BaseActivity {

    private AdapterTableHistory adapterTableHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        setupBottomNavigation();
        updateBottomNavigationSelection(R.id.nav_favorites);

        RecyclerView tableRecyclerView = findViewById(R.id.table_recycler_view);
        tableRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar o adaptador vazio e atualizá-lo posteriormente
        adapterTableHistory = new AdapterTableHistory(new ArrayList<>());
        tableRecyclerView.setAdapter(adapterTableHistory);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
                            Map<String, List<AdapterTableHistory.RowItem>> groupedProducts = new HashMap<>();
                            Map<String, Date> dateMap = new HashMap<>(); // Map para armazenar as datas reais para ordenação

                            // Iterar sobre os produtos comprados e agrupá-los por mês e ano
                            for (Map<String, Object> produto : produtosComprados) {
                                String nome = (String) produto.get("nome");
                                Object precoObj = produto.get("valor");
                                String dataCompra = (String) produto.get("dataCompra");

                                String preco = "0"; // Valor padrão para preço
                                if (precoObj instanceof Number) {
                                    preco = String.valueOf(precoObj);
                                } else if (precoObj instanceof String) {
                                    preco = (String) precoObj;
                                }

                                if (nome != null && dataCompra != null) {
                                    // Formatar a data de compra para mês e ano
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                        Date date = sdf.parse(dataCompra);
                                        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM 'de' yyyy", Locale.getDefault());
                                        String monthYear = monthYearFormat.format(date);

                                        // Ajustar a capitalização: primeira letra maiúscula e restante minúsculo
                                        monthYear = capitalizeMonthYear(monthYear);

                                        // Salvar a data para ordenação
                                        dateMap.put(monthYear, date);

                                        // Agrupar produtos por mês e ano
                                        groupedProducts.computeIfAbsent(monthYear, k -> new ArrayList<>())
                                                .add(new AdapterTableHistory.RowItem(nome, preco));
                                    } catch (Exception e) {
                                        Log.e("DataCompra", "Erro ao formatar data de compra", e);
                                    }
                                }
                            }

                            // Ordenar os grupos por data (mês e ano mais recente primeiro) usando o map de datas
                            List<Map.Entry<String, List<AdapterTableHistory.RowItem>>> sortedGroups = new ArrayList<>(groupedProducts.entrySet());
                            sortedGroups.sort((entry1, entry2) -> {
                                Date date1 = dateMap.get(entry1.getKey());
                                Date date2 = dateMap.get(entry2.getKey());
                                return date2.compareTo(date1); // Ordem decrescente (mais recente primeiro)
                            });

                            // Converter os grupos ordenados para uma lista para exibição no adapter
                            List<AdapterTableHistory.GroupedRowItem> groupedRowItems = new ArrayList<>();
                            for (Map.Entry<String, List<AdapterTableHistory.RowItem>> entry : sortedGroups) {
                                String header = entry.getKey(); // Mês e ano
                                List<AdapterTableHistory.RowItem> items = entry.getValue();

                                // Adicionar o cabeçalho
                                groupedRowItems.add(new AdapterTableHistory.GroupedRowItem(header));

                                // Adicionar os itens do grupo
                                for (AdapterTableHistory.RowItem item : items) {
                                    groupedRowItems.add(new AdapterTableHistory.GroupedRowItem(item));
                                }
                            }

                            // Atualizar o adaptador com os dados agrupados
                            adapterTableHistory.updateGroupedItems(groupedRowItems);
                        } else {
                            Log.d("Firestore", "O usuário não tem produtos comprados.");
                        }
                    } else {
                        Log.e("Firestore", "Erro ao carregar dados do usuário.", task.getException());
                    }
                });
    }

    // Método auxiliar para ajustar a capitalização do mês e ano
    private String capitalizeMonthYear(String monthYear) {
        if (monthYear == null || monthYear.isEmpty()) {
            return monthYear;
        }

        String[] words = monthYear.split(" ");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (word.length() > 1) {
                capitalized.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
            } else {
                capitalized.append(word.toUpperCase());
            }
            capitalized.append(" ");
        }

        return capitalized.toString().trim();
    }

}