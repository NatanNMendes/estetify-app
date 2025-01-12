package com.example.bancodedados.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bancodedados.R;

import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bancodedados.R;
import com.example.bancodedados.SalonPage;

import java.util.List;
import java.util.Map;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final List<Map<String, Object>> salonList;

    public CardAdapter(List<Map<String, Object>> salonList) {
        this.salonList = salonList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
//        Map<String, Object> salon = salonList.get(position);
//
//        // Obter os dados do salão
//        String name = (String) salon.get("name");
//        String imageUrl = (String) salon.get("url");
//
//        // Definir o nome no TextView
//        holder.cardText.setText(name);
//
//        // Usar Glide para carregar a imagem e aplicar a transformação circular
//        Glide.with(holder.itemView.getContext())
//                .load(imageUrl)
//                .circleCrop() // Torna a imagem circular
//                .into(holder.cardImage);
//    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Map<String, Object> salon = salonList.get(position);

        // Obter os dados do salão
        String name = (String) salon.get("name");
        String imageUrl = (String) salon.get("url");

        // Definir o nome no TextView
        holder.cardText.setText(name);

        // Usar Glide para carregar a imagem e aplicar a transformação circular
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .circleCrop() // Torna a imagem circular
                .into(holder.cardImage);

        // Configurar o clique no card para abrir a tela SalonPage
        holder.itemView.setOnClickListener(v -> {
            // Intent para abrir a SalonPage
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, SalonPage.class);
            intent.putExtra("came_from", "BusinessActivity");
            intent.putExtra("salon_name", name);
            intent.putExtra("salon_url", imageUrl);
            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return salonList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImage;
        TextView cardText;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.card_image);
            cardText = itemView.findViewById(R.id.card_text);
        }
    }
}


