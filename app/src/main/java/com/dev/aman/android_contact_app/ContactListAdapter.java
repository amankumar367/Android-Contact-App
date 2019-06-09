package com.dev.aman.android_contact_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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
        CircleImageView mImage;

        ContactListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            mName = view.findViewById(R.id.single_name);
            mNumber = view.findViewById(R.id.single_number);
            mImage = view.findViewById(R.id.single_profile_image);
        }

        void bind(ContactModel contactModel) {
            mName.setText(contactModel.getName());
            mNumber.setText(contactModel.getNumber());
            if(contactModel.getImage() != null) {
                try {
                    Uri uri = Uri.parse(contactModel.getImage());
                    mImage.setImageBitmap(getBitmap(uri));
                } catch (IOException e) {
                    e.printStackTrace();
                    mImage.setImageDrawable(context.getDrawable(R.drawable.account_circle_black));
                }
            } else {
                mImage.setImageDrawable(context.getDrawable(R.drawable.account_circle_black));
            }
        }

        private Bitmap getBitmap(Uri imageUri) throws IOException {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(imageUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        }
    }
}
