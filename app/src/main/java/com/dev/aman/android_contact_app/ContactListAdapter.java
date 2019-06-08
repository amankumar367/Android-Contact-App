package com.dev.aman.android_contact_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder> {

    public static final String CONTACT_DETAIL = "contact_details";
    private ArrayList<ContactModel> arrayList;
    private Context context;

    public ContactListAdapter(Context context, ArrayList<ContactModel> list) {
        this.arrayList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactListViewHolder(LayoutInflater.from(context).inflate(R.layout.single_contact_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListViewHolder holder, final int position) {
        holder.bind(arrayList.get(position));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ContactDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(CONTACT_DETAIL, arrayList.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ContactListViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView mName, mNumber;
        ImageView mImage;

        ContactListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            mName = view.findViewById(R.id.tv_name);
            mNumber = view.findViewById(R.id.tv_mobile_number);
        }

        void bind(ContactModel contactModel) {
            mName.setText(contactModel.getName());
            mNumber.setText(contactModel.getNumber());
        }
    }
}
