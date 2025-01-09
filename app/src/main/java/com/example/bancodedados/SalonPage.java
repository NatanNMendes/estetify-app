package com.example.bancodedados;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class SalonPage extends AppCompatActivity {

    private LinearLayout servicesTableContainer;
    private LinearLayout productsTableContainer;
    private Button tabProducts;

    private Button tabServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_page);

        // Referências para os elementos do layout
        TextView username = findViewById(R.id.username);
        TextView status = findViewById(R.id.status);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        servicesTableContainer = findViewById(R.id.services_table_container);
        productsTableContainer = findViewById(R.id.products_table_container);
        tabProducts = findViewById(R.id.tab_products);
        tabServices = findViewById(R.id.tab_services);

        // Configurar botão de produtos
        tabProducts.setOnClickListener(v -> switchTab(false));
        tabServices.setOnClickListener(v -> switchTab(true));

        // Obter o nome do salão do Intent
        String salonName = getIntent().getStringExtra("salon_name");

        // Buscar dados do Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Salon")
                .whereEqualTo("nome", salonName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Atualizar UI com os dados do salão
                        username.setText(document.getString("nome"));
                        status.setText("ABERTO"); // Você pode adicionar lógica para verificar o status

                        Double avaliacoes = document.getDouble("avaliacoes");
                        if (avaliacoes != null) {
                            ratingBar.setRating(avaliacoes.floatValue());
                        } else {
                            ratingBar.setRating(0f); // Valor padrão
                        }

                        // Carregar dados dinâmicos
                        List<Map<String, Object>> produtos = (List<Map<String, Object>>) document.get("produtos");
                        List<Map<String, Object>> servicos = (List<Map<String, Object>>) document.get("servicos");

                        if (produtos != null) {
                            populateTable(productsTableContainer, produtos);
                        }

                        if (servicos != null) {
                            populateTable(servicesTableContainer, servicos);
                        }

                        // Exibir serviços por padrão
                        switchTab(true);
                    }
                });
    }

    private void populateTable(LinearLayout container, List<Map<String, Object>> items) {
        container.removeAllViews(); // Limpa a tabela antes de preencher

        for (Map<String, Object> item : items) {
            String nome = (String) item.get("nome");
            String valor = String.valueOf(item.get("valor"));

            // Inflar a linha usando o layout fornecido
            View row = LayoutInflater.from(this).inflate(R.layout.item_table_row, container, false);

            // Configurar os dados nas colunas
            TextView nameCell = row.findViewById(R.id.product_name);
            TextView valueCell = row.findViewById(R.id.product_price);

            nameCell.setText(nome);
            valueCell.setText("R$ " + valor);

            // Adicionar a linha ao container
            container.addView(row);
        }
    }

    private void switchTab(boolean showServices) {
        if (showServices) {
            // Exibir tabela de serviços e esconder produtos
            servicesTableContainer.setVisibility(View.VISIBLE);
            productsTableContainer.setVisibility(View.GONE);

            // Alterar cores dos botões
            tabServices.setBackgroundColor(getResources().getColor(R.color.purple)); // Botão Serviços colorido
            tabServices.setTextColor(getResources().getColor(android.R.color.white));     // Texto branco

            tabProducts.setBackgroundColor(getResources().getColor(android.R.color.darker_gray)); // Botão Produtos cinza
            tabProducts.setTextColor(getResources().getColor(android.R.color.black));     // Texto preto
        } else {
            // Exibir tabela de produtos e esconder serviços
            servicesTableContainer.setVisibility(View.GONE);
            productsTableContainer.setVisibility(View.VISIBLE);

            // Alterar cores dos botões
            tabProducts.setBackgroundColor(getResources().getColor(R.color.purple)); // Botão Produtos colorido
            tabProducts.setTextColor(getResources().getColor(android.R.color.white));     // Texto branco

            tabServices.setBackgroundColor(getResources().getColor(android.R.color.darker_gray)); // Botão Serviços cinza
            tabServices.setTextColor(getResources().getColor(android.R.color.black));     // Texto preto
        }
    }

    // Exibir serviços por padrão e configurar botão
    private void initializeTabs() {
        // Exibir tabela de serviços ao abrir a página
        switchTab(true);

        // Configurar botões para alternar entre serviços e produtos
        tabServices.setOnClickListener(v -> switchTab(true));
        tabProducts.setOnClickListener(v -> switchTab(false));
    }

}

