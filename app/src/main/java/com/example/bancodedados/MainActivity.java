package com.example.bancodedados;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bancodedados.TableAdapter;
import com.example.bancodedados.utils.CardAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TableAdapter tableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        RecyclerView carouselRecyclerView = findViewById(R.id.carousel_recycler_view);
        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadSalonDetails(carouselRecyclerView);

        // Dados para a Tabela
        RecyclerView tableRecyclerView = findViewById(R.id.table_recycler_view);
        tableRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar o adaptador vazio e atualizá-lo posteriormente
        tableAdapter = new TableAdapter(new ArrayList<>());
        tableRecyclerView.setAdapter(tableAdapter);

        // Configurar BottomNavigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case 1: // Substitua por valores fixos para depuração
                    // Lógica para Home
                    return true;
                case 2:
                    // Lógica para Buscar
                    return true;
                case 3:
                    // Lógica para Favoritos
                    return true;
                case 4:
                    // Lógica para Perfil
                    return true;
                default:
                    return false;
            }

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
                            List<TableAdapter.RowItem> products = new ArrayList<>();

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
                                    products.add(new TableAdapter.RowItem(nome, preco));
                                }

                                count++; // Incrementa o contador
                            }

                            // Atualizar o adaptador com os dados carregados
                            tableAdapter.updateItems(products);
                        } else {
                            Log.d("Firestore", "O usuário não tem produtos comprados.");
                        }
                    } else {
                        Log.e("Firestore", "Erro ao carregar dados do usuário.", task.getException());
                    }
                });
    }

//TODO: Adicionar essa logica em uma pagina especifica para exibir o historico detalhado das compras

//    private void loadPurchasedProducts(String userId) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // Acessar o documento do usuário na coleção "Users"
//        db.collection("Users").document(userId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        // Verificar se o campo "produtosComprados" existe
//                        List<Map<String, Object>> produtosComprados =
//                                (List<Map<String, Object>>) task.getResult().get("produtosComprados");
//
//                        if (produtosComprados != null) {
//                            List<TableAdapter.RowItem> products = new ArrayList<>();
//
//                            // Iterar sobre os produtos comprados e adicionar ao adapter
//                            for (Map<String, Object> produto : produtosComprados) {
//                                String nome = (String) produto.get("nome");
//
//                                // Verificar o tipo do campo "preco" e garantir que é tratado corretamente
//                                Object precoObj = produto.get("valor");
//                                String preco = "0"; // Valor padrão
//
//                                if (precoObj != null) {
//                                    Log.d("PrecoObj", "Tipo de 'precoObj': " + precoObj.getClass().getSimpleName());
//
//                                    // Verificar se o precoObj é um número
//                                    if (precoObj instanceof Number) {
//                                        preco = String.valueOf(precoObj); // Converte Number para String
//                                        Log.d("Preco", "Preco (Number): " + preco);
//                                    } else if (precoObj instanceof String) {
//                                        preco = (String) precoObj; // Se for String, utiliza diretamente
//                                        Log.d("Preco", "Preco (String): " + preco);
//                                    } else {
//                                        Log.d("Preco", "Tipo de preco desconhecido, utilizando valor padrão.");
//                                    }
//                                } else {
//                                    Log.d("Preco", "Preco é null, utilizando valor padrão.");
//                                }
//
//                                if (nome != null && !nome.isEmpty()) {
//                                    // Adicionar à lista de produtos
//                                    products.add(new TableAdapter.RowItem(nome, preco));
//                                }
//                            }
//
//                            // Atualizar o adaptador com os dados carregados
//                            tableAdapter.updateItems(products);
//                        } else {
//                            Log.d("Firestore", "O usuário não tem produtos comprados.");
//                        }
//                    } else {
//                        Log.e("Firestore", "Erro ao carregar dados do usuário.", task.getException());
//                    }
//                });
//    }

    private void loadSalonDetails(RecyclerView recyclerView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        db.collection("Salon")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Map<String, Object>> salons = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            // Alterado para buscar "nome" em vez de "name"
                            String name = document.getString("nome");
                            String url = document.getString("url");

                            if (name == null || name.isEmpty()) {
                                Log.e("Firestore", "Nome do salão ausente ou vazio para o documento: " + document.getId());
                                continue; // Ignorar este documento
                            }

                            if (url == null || url.isEmpty()) {
                                Log.e("Firestore", "URL do salão ausente ou vazio para o documento: " + document.getId());
                                continue; // Ignorar este documento
                            }

                            Log.d("Firestore", "Carregado: " + name + " - " + url);

                            // Adicionar os dados à lista
                            Map<String, Object> salon = new HashMap<>();
                            salon.put("name", name); // Internamente ainda usaremos "name" como chave
                            salon.put("url", url);
                            salons.add(salon);
                        }

                        if (!salons.isEmpty()) {
                            CardAdapter adapter = new CardAdapter(salons);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d("Firestore", "Nenhum salão encontrado.");
                        }
                    } else {
                        Log.e("Firestore", "Erro ao carregar dados da coleção Salon.", task.getException());
                    }
                });
    }


}



