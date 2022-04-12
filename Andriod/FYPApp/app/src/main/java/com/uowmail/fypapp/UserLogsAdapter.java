package com.uowmail.fypapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.auth.User;

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
            //holder.doc_id.setText(model.getDocument_id());

        }

        holder.doc_id.setText(model.getDocument_id());
        Log.d("POSITION","Position: " + position);

    }

    @NonNull
    @Override
    public UserLogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_logs_list_item_single2, parent, false);
        return new UserLogsViewHolder(view);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        UserLogsFragment.disableProgressBar();
        UserLogsFragment.showDatePickerButton();

        if(getItemCount() == 0)
        {
            UserLogsFragment.showNoDataFoundText();
        }else{
            UserLogsFragment.hideNoDateFoundText();
        }

    }

    @Override
    public void updateOptions(@NonNull FirestoreRecyclerOptions<UserLogsModel> options) {
        super.updateOptions(options);
        UserLogsFragment.hideNoDateFoundText();
        UserLogsFragment.enableProgressBar();
    }

    // Viewholder class for user logs
    protected class UserLogsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView doc_id;

        public UserLogsViewHolder(@NonNull View itemView) {
            super(itemView);

            doc_id = itemView.findViewById(R.id.userLogs_recyView_docId2);


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
