package com.dev.aman.android_contact_app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;

public class ContactDetailsActivity extends AppCompatActivity {

    private TextView mName, mFirstName, mLastName;
    private TextView mEmail;
    private TextView mNubmer;
    private ImageView mProfileImage;
    private ImageView mBack;

    private ContactModel contactModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        init();
        onClicks();
        setContactDetails();
    }

    private void init() {
        mName = findViewById(R.id.profile_name);
        mFirstName = findViewById(R.id.profile_first_name);
        mLastName = findViewById(R.id.profile_last_name);

        mNubmer = findViewById(R.id.profile_phone_number);
        mProfileImage = findViewById(R.id.profile_image);

        mEmail = findViewById(R.id.profile_email);
        mBack = findViewById(R.id.back);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            contactModel = (ContactModel) bundle.getSerializable(ContactListAdapter.CONTACT_DETAIL);
        }
    }

    private void onClicks() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setContactDetails() {
        if(isStringEmpty(contactModel.getName())){
            mName.setText(contactModel.getName());
            setFirstAndLastName(contactModel.getName());
        }else {
            mName.setText("");
            mFirstName.setText("");
            mLastName.setText("");
        }

        if(isStringEmpty(contactModel.getNumber())){
            mNubmer.setText(contactModel.getNumber());
        }
        else {
            mNubmer.setText("");
        }

        if(isStringEmpty(contactModel.getEmail())){
            mEmail.setText(contactModel.getEmail());
        }else {
            mEmail.setText("");
        }

        if(isStringEmpty(contactModel.getImage())){
            try {
                Uri uri = Uri.parse(contactModel.getImage());
                mProfileImage.setImageBitmap(getBitmap(uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isStringEmpty(String isEmptyString) {
        return !TextUtils.isEmpty(isEmptyString);
    }

    private void setFirstAndLastName(String name) {
        String lastName = "";
        String firstName= "";
        if(name.split("\\w+").length>1){

            lastName = name.substring(name.lastIndexOf(" ")+1);
            firstName = name.substring(0, name.lastIndexOf(' '));

            mFirstName.setText(firstName);
            mLastName.setText(lastName);
        }
        else{
            firstName = name;
            mFirstName.setText(firstName);
            mLastName.setText("");
        }

    }

    private Bitmap getBitmap(Uri imageUri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(imageUri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
