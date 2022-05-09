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

public class UserIntrusionLogsAdapter extends FirestoreRecyclerAdapter<UserIntrusionLogsModel, UserIntrusionLogsAdapter.UserNotificationViewHolder> {

    private OnListItemClick onListItemClick;

    public UserIntrusionLogsAdapter(@NonNull FirestoreRecyclerOptions<UserIntrusionLogsModel> options, OnListItemClick onListItemClick) {
        super(options);

        this.onListItemClick = onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserNotificationViewHolder holder, int position, @NonNull UserIntrusionLogsModel model) {

        if(position != RecyclerView.NO_POSITION){
            // Do your binding here
            //holder.doc_id.setText(model.getDocument_id());

        }

        //holder.doc_id.setText(model.getDocument_id());
        //holder.doc_id.setText("HEEHEE");
        holder.doc_id.setText(this.formatDocumentID(model.getDocument_id()));
        //holder.doc_id.setText(model.getDocument_id());
        Log.d("POSITION","Position: " + position);

    }

    @NonNull
    @Override
    public UserNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_notifications_list_item_single, parent, false);
        return new UserNotificationViewHolder(view);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

        notifyDataSetChanged();

        UserIntrusionLogsFragment.disableProgressBar();

        if(getItemCount() == 0)
        {
            UserIntrusionLogsFragment.showNoDataFoundText();
        }else{
            UserIntrusionLogsFragment.hideNoDataFoundText();
        }
    }

    @Override
    public void updateOptions(@NonNull FirestoreRecyclerOptions<UserIntrusionLogsModel> options) {
        super.updateOptions(options);

        UserIntrusionLogsFragment.hideNoDataFoundText();
        UserIntrusionLogsFragment.enableProgressBar();
    }

    private String formatDocumentID(String modelDocID){
        Date d = new Date();
        String output = null;

        Log.d("===========MODELDOCID1=========", modelDocID);

        //Format of the date defined in the input String
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");

        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


        try {
            //Converting the input String to Date

            //Date d = df.parse("202204291230");
            //d = df.parse(modelDocID);
//            if(modelDocID.trim().equals("202204291230")){
//                d = df.parse("202204291230");
//                Log.d("=======HEY======", "WTF");
//            }else{
//                d = df.parse(modelDocID);
//                Log.d("=======HEY======", "HAHAHAHAA");
//            }
            d = df.parse(modelDocID.trim());
            //Changing the format of date and storing it in String
            output = outputformat.format(d);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return output;
    }

    protected class UserNotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView doc_id;

        public UserNotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            doc_id = itemView.findViewById(R.id.userNotifications_recyclerView_docID);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onListItemClick.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface OnListItemClick{
        void onItemClick(UserIntrusionLogsModel snapshot, int position);
    }


}
