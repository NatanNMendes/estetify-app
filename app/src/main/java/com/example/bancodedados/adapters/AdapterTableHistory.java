package com.example.bancodedados.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bancodedados.R;
import java.util.List;
import java.util.ArrayList;


public class AdapterTableHistory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<GroupedRowItem> groupedItems;

    public AdapterTableHistory(List<GroupedRowItem> groupedItems) {
        this.groupedItems = groupedItems != null ? groupedItems : new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return groupedItems.get(position).isHeader() ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GroupedRowItem item = groupedItems.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).headerText.setText(item.getHeader());
        } else if (holder instanceof ItemViewHolder) {
            RowItem rowItem = item.getRowItem();
            if (rowItem != null) {
                ((ItemViewHolder) holder).nameText.setText(rowItem.getNome());
                ((ItemViewHolder) holder).priceText.setText("R$ " + rowItem.getPreco());
            }
        }
    }

    @Override
    public int getItemCount() {
        return groupedItems.size();
    }

    public void updateGroupedItems(List<GroupedRowItem> groupedItems) {
        this.groupedItems = groupedItems != null ? groupedItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Classe para os itens de dados
    public static class RowItem {
        private String nome;
        private String preco;

        public RowItem(String nome, String preco) {
            this.nome = nome;
            this.preco = preco;
        }

        public String getNome() {
            return nome;
        }

        public String getPreco() {
            return preco;
        }
    }

    // Classe para agrupar cabeçalhos e itens
    public static class GroupedRowItem {
        private String header;
        private RowItem rowItem;
        private List<RowItem> items;

        // Construtor para cabeçalhos simples
        public GroupedRowItem(String header) {
            this.header = header;
            this.rowItem = null;
            this.items = null;
        }

        // Construtor para itens individuais
        public GroupedRowItem(RowItem rowItem) {
            this.rowItem = rowItem;
            this.header = null;
            this.items = null;
        }

        // Construtor para cabeçalhos com lista de itens
        public GroupedRowItem(String header, List<RowItem> items) {
            this.header = header;
            this.items = items;
            this.rowItem = null;
        }

        public String getHeader() {
            return header;
        }

        public RowItem getRowItem() {
            return rowItem;
        }

        public List<RowItem> getItems() {
            return items;
        }

        public boolean isHeader() {
            return header != null;
        }
    }


    // ViewHolder para cabeçalhos
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.header_text);
        }
    }

    // ViewHolder para itens de dados
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, priceText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.name_text);
            priceText = itemView.findViewById(R.id.price_text);
        }
    }
}



