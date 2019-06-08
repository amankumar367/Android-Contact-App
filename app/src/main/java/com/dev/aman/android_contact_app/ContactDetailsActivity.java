package com.dev.aman.android_contact_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactDetailsActivity extends AppCompatActivity {

    private TextView mName;
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
        }else {
            mName.setText("");
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
    }

    private boolean isStringEmpty(String isEmptyString) {
        return !TextUtils.isEmpty(isEmptyString);
    }
}
