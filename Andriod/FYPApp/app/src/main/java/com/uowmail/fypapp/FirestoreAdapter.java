package com.uowmail.fypapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FirestoreAdapter extends FirestoreRecyclerAdapter<ProductsModel, FirestoreAdapter.ProductsViewHolder> {

    private OnListItemClick onListItemClick;

    public FirestoreAdapter(@NonNull FirestoreRecyclerOptions<ProductsModel> options, OnListItemClick onListItemClick) {
        super(options);

        this.onListItemClick = onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull ProductsModel model) {
        holder.list_id.setText(model.getItem_id());
        holder.list_name.setText(model.getName());
        holder.list_price.setText(model.getPrice() + "");
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_logs_list_item_single, parent, false);
        return new ProductsViewHolder(view);
    }

    // Viewholder class
    protected class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView list_id;
        private TextView list_name;
        private TextView list_price;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            list_id = itemView.findViewById(R.id.list_id);
            list_name = itemView.findViewById(R.id.list_name);
            list_price = itemView.findViewById(R.id.list_price);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onListItemClick.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface OnListItemClick{
        void onItemClick(ProductsModel snapshot, int position);
    }
}
