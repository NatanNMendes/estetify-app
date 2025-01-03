package com.example.bancodedados;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bancodedados.TableAdapter;
import com.example.bancodedados.utils.CardAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Dados para o Carrossel
        List<String> carouselItems = Arrays.asList("Card 1", "Card 2", "Card 3");

        RecyclerView carouselRecyclerView = findViewById(R.id.carousel_recycler_view);
        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        carouselRecyclerView.setAdapter(new CardAdapter(carouselItems)); // Passe a lista aqui

        // Dados para a Tabela
        List<TableAdapter.RowItem> tableItems = Arrays.asList(
                new TableAdapter.RowItem("Linha 1 - Coluna Grande", "Pequeno 1"),
                new TableAdapter.RowItem("Linha 2 - Coluna Grande", "Pequeno 2"),
                new TableAdapter.RowItem("Linha 3 - Coluna Grande", "Pequeno 3"),
                new TableAdapter.RowItem("Linha 4 - Coluna Grande", "Pequeno 4")
        );

        RecyclerView tableRecyclerView = findViewById(R.id.table_recycler_view);
        tableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tableRecyclerView.setAdapter(new com.example.bancodedados.TableAdapter(tableItems)); // Passe a lista aqui

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
}