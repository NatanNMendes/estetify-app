package com.example.bancodedados;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bancodedados.utils.Navigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SalonPage extends BaseActivity {

    private LinearLayout servicesTableContainer;
    private LinearLayout productsTableContainer;
    private Button tabProducts;
    private Handler handler = new Handler();
    private Runnable runnable;
    private Button tabServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_page);
        setupBottomNavigation();
        updateBottomNavigationSelection(R.id.nav_search);

        ImageButton backButton = findViewById(R.id.back_button);
        Navigation navigation = new Navigation(this);

        backButton.setOnClickListener(v -> navigation.navigationToBackScreen(SalonPage.this));

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
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Erro ao buscar dados!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        // Atualizar UI com os dados do salão
                        username.setText(document.getString("nome"));

                        Double avaliacoes = document.getDouble("avaliacoes");
                        if (avaliacoes != null) {
                            ratingBar.setRating(avaliacoes.floatValue());
                        } else {
                            ratingBar.setRating(0f); // Valor padrão
                        }

                        // Carregar URL da imagem do salão
                        String imageUrl = document.getString("url");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUrl) // Carregar a URL da imagem
                                    .circleCrop() // Imagem circular
                                    .into((ImageView) findViewById(R.id.salonImage)); // Definir no ImageView
                        }

                        // Verificar horário de funcionamento
                        Map<String, Map<String, String>> horarioFuncionamento =
                                (Map<String, Map<String, String>>) document.get("horarioFuncionamento");

                        if (horarioFuncionamento != null) {
                            verificarHorarioAtual(horarioFuncionamento, status);
                        }else {
                            Log.d("Firestore", "Horário de funcionamento não encontrado para o salão " + salonName);
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
            View row = LayoutInflater.from(this).inflate(R.layout.item_table_row_buy, container, false);

            // Configurar os dados nas colunas
            TextView nameCell = row.findViewById(R.id.product_name);
            TextView valueCell = row.findViewById(R.id.product_price);
            ImageButton buyButton = row.findViewById(R.id.action_button);

            nameCell.setText(nome);
            valueCell.setText("R$ " + valor);

            buyButton.setOnClickListener(v -> addProductToUserCart(nome, valor));

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

//    private void addProductToUserCart(String nome, String valor) {
//        // Verificar se o status do salão é "FECHADO"
//        TextView status = findViewById(R.id.status);
//        if ("FECHADO".equalsIgnoreCase(status.getText().toString())) {
//            // Exibir alerta
//            new androidx.appcompat.app.AlertDialog.Builder(this)
//                    .setTitle("Salão Fechado")
//                    .setMessage("O salão está fechado no momento. As compras só podem ser realizadas durante o horário de funcionamento.")
//                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                    .show();
//            return; // Impedir a execução da compra
//        }
//
//        // Continuar com o processo de adicionar ao carrinho se o salão estiver aberto
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (currentUser != null) {
//            String userId = currentUser.getUid();
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            // Garantir que o valor seja tratado corretamente
//            double valorDouble = 0.0;
//            try {
//                // Substituir vírgula por ponto, caso o valor esteja formatado como "10,00"
//                String valorFormatado = valor.replace(",", ".");
//                valorDouble = Double.parseDouble(valorFormatado);
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Erro no formato do valor: " + valor, Toast.LENGTH_SHORT).show();
//                return; // Parar a execução caso o valor seja inválido
//            }
//
//            // Obter a data atual
//            String dataCompra = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//                    .format(Calendar.getInstance().getTime());
//
//            // Criar o item com nome, valor e data da compra
//            Map<String, Object> novoItem = new HashMap<>();
//            novoItem.put("nome", nome);
//            novoItem.put("valor", valorDouble); // Salvar como número (double)
//            novoItem.put("dataCompra", dataCompra); // Adicionar a data de compra
//
//            // Atualizar o array no Firestore (adiciona no topo)
//            db.collection("Users").document(userId).get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        List<Map<String, Object>> produtosExistentes =
//                                (List<Map<String, Object>>) documentSnapshot.get("produtosComprados");
//                        if (produtosExistentes == null) {
//                            produtosExistentes = new ArrayList<>();
//                        }
//
//                        // Adicionar o novo item no topo da lista
//                        produtosExistentes.add(0, novoItem);
//
//                        db.collection("Users").document(userId)
//                                .update("produtosComprados", produtosExistentes)
//                                .addOnSuccessListener(aVoid -> Toast.makeText(this, nome + " adicionado ao carrinho!", Toast.LENGTH_SHORT).show())
//                                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao adicionar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//                    })
//                    .addOnFailureListener(e -> Toast.makeText(this, "Erro ao acessar o usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//        } else {
//            Toast.makeText(this, "Usuário não está logado.", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void addProductToUserCart(String nome, String valor) {
        // Verificar se o status do salão é "FECHADO"
        TextView status = findViewById(R.id.status);
        if ("FECHADO".equalsIgnoreCase(status.getText().toString())) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Salão Fechado")
                    .setMessage("O salão está fechado no momento. As compras só podem ser realizadas durante o horário de funcionamento.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }

        // Continuar com o processo de adicionar ao carrinho se o salão estiver aberto
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            double valorDouble;
            try {
                String valorFormatado = valor.replace(",", ".");
                valorDouble = Double.parseDouble(valorFormatado);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro no formato do valor: " + valor, Toast.LENGTH_SHORT).show();
                return;
            }

            String dataCompra = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Calendar.getInstance().getTime());

            Map<String, Object> novoItem = new HashMap<>();
            novoItem.put("nome", nome);
            novoItem.put("valor", valorDouble);
            novoItem.put("dataCompra", dataCompra);

            db.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        List<Map<String, Object>> produtosExistentes =
                                (List<Map<String, Object>>) documentSnapshot.get("produtosComprados");
                        if (produtosExistentes == null) {
                            produtosExistentes = new ArrayList<>();
                        }

                        produtosExistentes.add(0, novoItem);

                        db.collection("Users").document(userId)
                                .update("produtosComprados", produtosExistentes)
                                .addOnSuccessListener(aVoid -> {
                                    showConfirmationDialog(nome, valorDouble, "Nome do Salão", dataCompra);
                                    Toast.makeText(this, nome + " adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao adicionar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erro ao acessar o usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Usuário não está logado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmationDialog(String nome, double valor, String nomeSalao, String dataCompra) {
        String message = String.format(
                "Produto: %s\nPreço: R$ %.2f\nSalão: %s\nData: %s",
                nome, valor, nomeSalao, dataCompra
        );

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Compra realizada com sucesso!")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void verificarHorarioAtual(Map<String, Map<String, String>> horarioFuncionamento, TextView status) {
        // Obter o dia da semana atual
        Calendar calendar = Calendar.getInstance();
        int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
        String diaAtual = "";

        // Mapear o dia da semana para os nomes no Firebase
        switch (diaSemana) {
            case Calendar.SUNDAY: diaAtual = "domingo"; break;
            case Calendar.MONDAY: diaAtual = "segunda"; break;
            case Calendar.TUESDAY: diaAtual = "terca"; break;
            case Calendar.WEDNESDAY: diaAtual = "quarta"; break;
            case Calendar.THURSDAY: diaAtual = "quinta"; break;
            case Calendar.FRIDAY: diaAtual = "sexta"; break;
            case Calendar.SATURDAY: diaAtual = "sabado"; break;
        }

        // Obter os horários do dia atual
        Map<String, String> horarioDoDia = horarioFuncionamento.get(diaAtual);

        // Exibir o dia e a hora atual para debug
        String horaAtualFormatada = new SimpleDateFormat("HH:mm").format(calendar.getTime());
        Log.d("Horário Atual", "Dia: " + diaAtual + " | Hora Atual: " + horaAtualFormatada);

        if (horarioDoDia != null) {
            String abre = horarioDoDia.get("abre");
            String fecha = horarioDoDia.get("fecha");

            // Se o horário estiver definido como "Fechado" em qualquer parte, o salão está fechado
            if ("Fechado".equalsIgnoreCase(abre) || "Fechado".equalsIgnoreCase(fecha)) {
                status.setText("FECHADO");
                status.setTextColor(status.getContext().getResources().getColor(android.R.color.holo_red_dark));
                return;
            }

            // Formatar horário para LocalTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime horaAtual = LocalTime.now();
            LocalTime horaAbre = LocalTime.parse(abre, formatter);
            LocalTime horaFecha = LocalTime.parse(fecha, formatter);

            // Caso o horário de fechamento seja após a meia-noite (por exemplo, 23:00 - 01:00)
            if (horaFecha.compareTo(horaAbre) < 0) {
                // Funcionamento atravessa a meia-noite
                if ((horaAtual.compareTo(horaAbre) >= 0) || horaAtual.compareTo(horaFecha) < 0) {
                    status.setText("Hoje é " + diaAtual + ", " + horaAtualFormatada + ": ABERTO");
                    status.setTextColor(status.getContext().getResources().getColor(android.R.color.holo_green_dark));
                    return;
                }
            } else {
                // Funcionamento normal, sem atravessar a meia-noite
                if (horaAtual.compareTo(horaAbre) >= 0 && horaAtual.compareTo(horaFecha) <= 0) {
                    status.setText("Hoje é " + diaAtual + ", " + horaAtualFormatada + ": ABERTO");
                    status.setTextColor(status.getContext().getResources().getColor(android.R.color.holo_green_dark));
                    return;
                }
            }

            // Se não estiver dentro do horário de funcionamento
            status.setText("Hoje é " + diaAtual + ", " + horaAtualFormatada + ": FECHADO");
            status.setTextColor(status.getContext().getResources().getColor(android.R.color.holo_red_dark));

        } else {
            // Se não houver horários definidos para o dia
            status.setText("Hoje é " + diaAtual + ": FECHADO");
            status.setTextColor(status.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }
    }
}

