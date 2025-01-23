package com.example.bancodedados.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bancodedados.R;

import java.util.List;
import java.util.Map;

import com.example.bancodedados.SalonPage;

public class AdapterCardSalon extends RecyclerView.Adapter<AdapterCardSalon.ViewHolder> {

    private final List<Map<String, Object>> salonList;
    private final Context context; // Adicionado contexto

    public AdapterCardSalon(List<Map<String, Object>> salonList, Context context) {
        this.salonList = salonList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_salon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> salon = salonList.get(position);

        String name = (String) salon.get("name");
        String imageUrl = (String) salon.get("url");
        List<String> categories = (List<String>) salon.get("categoria");
        Double avaliacoes = (Double) salon.get("avaliacoes");

        holder.salonName.setText(name);

        // Carregar imagem do salão
        Glide.with(context)
                .load(imageUrl)
                .circleCrop()
                .into(holder.salonImage);

        // Configurar o RatingBar
        if (avaliacoes != null) {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating(avaliacoes.floatValue());
        } else {
            holder.ratingBar.setVisibility(View.GONE);
        }

        // Adicionar categorias dinamicamente
        holder.categoryContainer.removeAllViews();
        if (categories != null) {
            for (String category : categories) {
                TextView categoryView = new TextView(context);
                categoryView.setText(category);
                categoryView.setPadding(12, 4, 12, 4);
                categoryView.setTextSize(12);
                categoryView.setTextColor(context.getResources().getColor(android.R.color.white));
                categoryView.setBackground(context.getDrawable(R.drawable.custom_background_category));
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setMargins(8, 8, 8, 8);
                holder.categoryContainer.addView(categoryView, params);
            }
        }

        // Configurar clique no card
        holder.itemView.setOnClickListener(v -> {
            // Intent para a tela do salão
            Intent intent = new Intent(context, SalonPage.class);
            intent.putExtra("salon_name", name); // Passar o nome do salão para a próxima tela
            intent.putExtra("came_from", "MainActivity");
            context.startActivity(intent);
        });
    }






    @Override
    public int getItemCount() {
        return salonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView salonName;
        private final ImageView salonImage;
        private final RatingBar ratingBar; // RatingBar para avaliação
        private final GridLayout categoryContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            salonName = itemView.findViewById(R.id.salonName);
            salonImage = itemView.findViewById(R.id.salonImage);
            ratingBar = itemView.findViewById(R.id.ratingBar); // Inicializar RatingBar
            categoryContainer = itemView.findViewById(R.id.categoryContainer);
        }
    }

}
