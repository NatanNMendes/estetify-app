package com.example.bancodedados;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bancodedados.utils.AdapterCard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private com.example.bancodedados.AdapterTable adapterTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupBottomNavigation();
        updateBottomNavigationSelection(R.id.nav_home);

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        RecyclerView carouselRecyclerView = findViewById(R.id.carousel_recycler_view);
        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        loadSalonDetails(carouselRecyclerView);
        setupCategoryButtons(carouselRecyclerView);

        // Dados para a Tabela
        RecyclerView tableRecyclerView = findViewById(R.id.table_recycler_view);
        tableRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar o adaptador vazio e atualizá-lo posteriormente
        adapterTable = new com.example.bancodedados.AdapterTable(new ArrayList<>());
        tableRecyclerView.setAdapter(adapterTable);

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

    private void loadSalonDetails(RecyclerView recyclerView, List<String> selectedCategories) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        db.collection("Salon")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Map<String, Object>> salons = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("nome");
                            String url = document.getString("url");
                            List<String> categories = (List<String>) document.get("categoria");

                            if (name == null || name.isEmpty() || url == null || url.isEmpty()) {
                                continue; // Ignorar documentos inválidos
                            }

                            if (selectedCategories.isEmpty() || (categories != null && !Collections.disjoint(categories, selectedCategories))) {
                                Map<String, Object> salon = new HashMap<>();
                                salon.put("name", name);
                                salon.put("url", url);
                                salons.add(salon);
                            }
                        }

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
        Drawable iconX = ContextCompat.getDrawable(this, R.drawable.ic_check); // Substitua pelo ícone "x" adequado
        iconX.setBounds(0, 0, iconX.getIntrinsicWidth(), iconX.getIntrinsicHeight());

        Button buttonCategory1 = findViewById(R.id.button_category1);
        Button buttonCategory3 = findViewById(R.id.button_category3);
        Button buttonCategory5 = findViewById(R.id.button_category5);
        Button buttonCategory6 = findViewById(R.id.button_category6);
        Button buttonCategory7 = findViewById(R.id.button_category7);
        Button buttonCategory8 = findViewById(R.id.button_category8);

        List<Button> categoryButtons = Arrays.asList(
                buttonCategory1, buttonCategory3, buttonCategory5,
                buttonCategory6, buttonCategory7, buttonCategory8
        );

        for (Button button : categoryButtons) {
            button.setOnClickListener(v -> {
                String category = button.getText().toString();
                if (selectedCategories.contains(category)) {
                    selectedCategories.remove(category);
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BCBCBC"))); // Cinza
                    button.setCompoundDrawables(null, null, null, null); // Remove o ícone
                } else {
                    selectedCategories.add(category);
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6200EE"))); // Roxo
                    button.setCompoundDrawables(null, null, iconX, null); // Adiciona o ícone no final
                }

                // Recarregar os salões filtrados
                loadSalonDetails(recyclerView, selectedCategories);
            });
        }

        // Carregar todos os salões inicialmente
        loadSalonDetails(recyclerView, selectedCategories);
    }
}



