package com.example.bancodedados;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    // Lista para armazenar objetos com dados dos produtos
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
        // Vincula os dados para as duas colunas
        RowItem item = items.get(position);

        // Exibir nome do produto
        holder.largeText.setText(item.getLargeColumn());

        // Formatar e exibir o preço
        String formattedPrice = formatPrice(item.getSmallColumn());
        holder.smallText.setText(formattedPrice);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Método para atualizar os dados dinamicamente
    public void updateItems(List<RowItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // Método para formatar os preços
    private String formatPrice(String price) {
        try {
            double value = Double.parseDouble(price);
            return String.format("R$ %.2f", value);
        } catch (NumberFormatException e) {
            return "Preço Inválido";
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView largeText;
        TextView smallText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            largeText = itemView.findViewById(R.id.product_name);
            smallText = itemView.findViewById(R.id.product_price);
        }
    }

    // Classe para representar os itens da tabela
    public static class RowItem {
        private String largeColumn; // Nome do produto
        private String smallColumn; // Preço do produto

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
