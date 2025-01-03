package com.example.bancodedados;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    // Lista para armazenar objetos com duas colunas
    private List<RowItem> items;

    // Construtor para receber os dados
    public TableAdapter(List<RowItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind dos dados para as duas colunas
        RowItem item = items.get(position);
        holder.largeText.setText(item.getLargeColumn());
        holder.smallText.setText(item.getSmallColumn());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView largeText;
        TextView smallText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            largeText = itemView.findViewById(R.id.table_text_large);
            smallText = itemView.findViewById(R.id.table_text_small);
        }
    }

    // Classe para representar os itens da tabela
    public static class RowItem {
        private String largeColumn;
        private String smallColumn;

        public RowItem(String largeColumn, String smallColumn) {
            this.largeColumn = largeColumn;
            this.smallColumn = smallColumn;
        }

        public String getLargeColumn() {
            return largeColumn;
        }

        public String getSmallColumn() {
            return smallColumn;
        }
    }
}
