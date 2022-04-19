package com.uowmail.fypapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminDeleteLogsAdapter extends FirestoreRecyclerAdapter<AdminDeleteLogsModel, AdminDeleteLogsAdapter.AdminDeleteLogsViewHolder> {

    private OnListItemClick onListItemClick;

    public AdminDeleteLogsAdapter(@NonNull FirestoreRecyclerOptions<AdminDeleteLogsModel> options, OnListItemClick onListItemClick) {
        super(options);

        this.onListItemClick = onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdminDeleteLogsViewHolder holder, int position, @NonNull AdminDeleteLogsModel model) {

        if(position != RecyclerView.NO_POSITION){
            // Do your binding here
            //holder.doc_id.setText(model.getDocument_id());

        }

        //holder.doc_id.setText(model.getDocument_id());
        //holder.doc_id.setText("HEEHEE");
        holder.doc_id.setText(formatDocumentID(model.getDocument_id()));
        Log.d("POSITION","Position: " + position);

    }

    private String formatDocumentID(String modelDocID){
        Date date = new Date();
        String output = null;

        //Format of the date defined in the input String
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");

        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd  HH:mm");


        try {
            //Converting the input String to Date
            date = df.parse(modelDocID);
            //Changing the format of date and storing it in String
            output = outputformat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return output;
    }

    @NonNull
    @Override
    public AdminDeleteLogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_delete_logs_list_item_single, parent, false);
        return new AdminDeleteLogsViewHolder(view);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        AdminDeleteLogsActivity.disableProgressBar();
        AdminDeleteLogsActivity.enableFilterButton();
        AdminDeleteLogsActivity.enableDeleteButton();

        if(getItemCount() == 0)
        {
            AdminDeleteLogsActivity.showNoDataFoundText();
        }else{
            AdminDeleteLogsActivity.hideNoDateFoundText();
        }

    }

    @Override
    public void updateOptions(@NonNull FirestoreRecyclerOptions<AdminDeleteLogsModel> options) {
        super.updateOptions(options);
        AdminDeleteLogsActivity.hideNoDateFoundText();
        AdminDeleteLogsActivity.enableProgressBar();
        AdminDeleteLogsActivity.disableFilterButton();
        AdminDeleteLogsActivity.disableDeleteButton();
    }

    // Viewholder class for user logs
    protected class AdminDeleteLogsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView doc_id;

        public AdminDeleteLogsViewHolder(@NonNull View itemView) {
            super(itemView);

            doc_id = itemView.findViewById(R.id.adminDeleteLogs_recyclerView_docID);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onListItemClick.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface OnListItemClick{
        void onItemClick(AdminDeleteLogsModel snapshot, int position);
    }


}
