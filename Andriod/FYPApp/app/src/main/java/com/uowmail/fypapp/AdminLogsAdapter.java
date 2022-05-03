package com.uowmail.fypapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminLogsAdapter extends FirestoreRecyclerAdapter<AdminLogsModel, AdminLogsAdapter.AdminDeleteLogsViewHolder> {

    private OnListItemClick onListItemClick;
    private TextView doc_id, options;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button saveChange, cancelBtn;
    private String email, currAdminEmail, currAdminPassword;
    private FirebaseAuth fAuth;
    private EditText userEmail, adminPassword;

    public AdminLogsAdapter(@NonNull FirestoreRecyclerOptions<AdminLogsModel> options, OnListItemClick onListItemClick) {
        super(options);

        this.onListItemClick = onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdminDeleteLogsViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull AdminLogsModel model) {

        if(position != RecyclerView.NO_POSITION){
            // Do your binding here
            //holder.doc_id.setText(model.getDocument_id());

        }

        holder.doc_id.setText(formatDocumentID(model.getDocument_id()));

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(holder.options.getContext(), holder.options);
                popupMenu.inflate(R.menu.admin_delete_logs_options_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()){
                            case R.id.adminDeleteLogs_menu_goToLogsDetails:
                                Intent intent = new Intent(holder.options.getContext(), AdminLogDetailsActivity.class);
                                intent.putExtra("adminOrgID", "UOW");
                                intent.putExtra("docID", model.getDocument_id().toString());
                                holder.options.getContext().startActivity(intent);
                                break;
                            case R.id.adminDeleteLogs_menu_menu_delete:
                                fAuth = FirebaseAuth.getInstance();

                                dialogBuilder = new AlertDialog.Builder(holder.options.getContext());
                                LayoutInflater inflater= LayoutInflater.from(holder.options.getContext());
                                final View passwordPopupView = inflater.inflate(R.layout.popup_for_password, null);

                                saveChange = (Button) passwordPopupView.findViewById(R.id.saveButton);
                                cancelBtn = (Button) passwordPopupView.findViewById(R.id.cancelButton);

                                dialogBuilder.setView(passwordPopupView);
                                dialog = dialogBuilder.create();
                                dialog.show();

                                cancelBtn.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View v){
                                        // define save button here!
                                        dialog.dismiss();
                                    }
                                });


                                // authentication
                                saveChange.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View v){

                                        currAdminEmail = fAuth.getCurrentUser().getEmail();

                                        adminPassword = (EditText) dialog.findViewById(R.id.admin_password);
                                        currAdminPassword = adminPassword.getText().toString().trim();

                                        if(TextUtils.isEmpty(currAdminPassword)){
                                            adminPassword.setError("Password is required!");
                                            return;
                                        }

                                        Log.d("ADMIN INFO", currAdminEmail + currAdminPassword);


                                        fAuth.signInWithEmailAndPassword(currAdminEmail, currAdminPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                Toast.makeText(holder.options.getContext(), "Log deleted.", Toast.LENGTH_SHORT).show();
                                                getSnapshots().getSnapshot(position).getReference().delete();
                                                notifyItemRemoved(position);
                                                dialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(holder.options.getContext(), "Incorrect Admin password." + e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_logs_list_item_single, parent, false);
        return new AdminDeleteLogsViewHolder(view);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

        notifyDataSetChanged();

        AdminLogsActivity.disableProgressBar();
        AdminLogsActivity.enableFilterButton();

        if(getItemCount() == 0)
        {
            AdminLogsActivity.showNoDataFoundText();
        }else{
            AdminLogsActivity.hideNoDateFoundText();
        }

    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @Override
    public void updateOptions(@NonNull FirestoreRecyclerOptions<AdminLogsModel> options) {
        super.updateOptions(options);

        notifyDataSetChanged();

        AdminLogsActivity.hideNoDateFoundText();
        AdminLogsActivity.enableProgressBar();
        AdminLogsActivity.disableFilterButton();
    }

    // Viewholder class for user logs
    protected class AdminDeleteLogsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView doc_id, options;

        public AdminDeleteLogsViewHolder(@NonNull View itemView) {
            super(itemView);

            doc_id = itemView.findViewById(R.id.adminDeleteLogs_recyclerView_docID);
            options = itemView.findViewById(R.id.adminDeleteLogs_options);

            itemView.setOnClickListener(this);
        }

        private String formatDate(String date){
            //2022-04-20  10:34 to 20220420 10:34

            Date newDate = new Date();
            String output = null;

            //Format of the date defined in the input String
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm");

            //Desired format: 24 hour format: Change the pattern as per the need
            DateFormat outputformat = new SimpleDateFormat("yyyyMMddHHmm");


            try {
                //Converting the input String to Date
                newDate = df.parse(date);
                //Changing the format of date and storing it in String
                output = outputformat.format(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return output;
        }

        @Override
        public void onClick(View view) {
            onListItemClick.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface OnListItemClick{
        void onItemClick(AdminLogsModel snapshot, int position);
    }

}
