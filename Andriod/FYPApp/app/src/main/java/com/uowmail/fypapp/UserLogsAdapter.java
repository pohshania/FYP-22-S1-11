package com.uowmail.fypapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class UserLogsAdapter extends FirestoreRecyclerAdapter<UserLogsModel, UserLogsAdapter.UserLogsViewHolder> {

    private OnListItemClick onListItemClick;

    public UserLogsAdapter(@NonNull FirestoreRecyclerOptions<UserLogsModel> options, OnListItemClick onListItemClick) {
        super(options);

        this.onListItemClick = onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserLogsViewHolder holder, int position, @NonNull UserLogsModel model) {

        if(position != RecyclerView.NO_POSITION){
            // Do your binding here
            holder.doc_id.setText(model.getDocument_id());
        }

    }

    @NonNull
    @Override
    public UserLogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_logs_list_item_single, parent, false);
        return new UserLogsViewHolder(view);
    }

    // Viewholder class for user logs
    protected class UserLogsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView doc_id;

        public UserLogsViewHolder(@NonNull View itemView) {
            super(itemView);

            doc_id = itemView.findViewById(R.id.userLogs_recyView_docId);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onListItemClick.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface OnListItemClick{
        void onItemClick(UserLogsModel snapshot, int position);
    }


}
