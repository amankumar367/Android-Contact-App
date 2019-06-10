package com.dev.aman.android_contact_app.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.aman.android_contact_app.R;
import com.dev.aman.android_contact_app.adapter.ContactListAdapter;
import com.dev.aman.android_contact_app.model.ContactModel;
import com.dev.aman.android_contact_app.viewmodel.ContactListViewModel;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private ArrayList<ContactModel> arrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactListAdapter adapter;
    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        init();
        loadContactList();

    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView);

        mProgressBar = new ProgressDialog(this);
        mProgressBar.setMessage("Loading Contact List . . .");
        mProgressBar.setCanceledOnTouchOutside(false);
    }

    private void loadContactList() {

        if (hasContactReadPermission()) {
            mProgressBar.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fatchContactList();
                    setRecyclerView();
                }
            },200);

        } else {
            requestPermission();
        }
    }

    private void fatchContactList() {
        ContactListViewModel contactListViewModel =
                ViewModelProviders.of(this).get(ContactListViewModel.class);

        contactListViewModel.getContactDetails();
        contactListViewModel.contact.observe(this, new Observer<ArrayList<ContactModel>>() {
            @Override
            public void onChanged(ArrayList<ContactModel> contactModels) {
                arrayList = contactModels;
            }
        });
    }


    private void setRecyclerView() {
        adapter = new ContactListAdapter(this, arrayList);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        mProgressBar.dismiss();
    }

    private boolean hasContactReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mProgressBar.show();
                loadContactList();
            }  else {
                requestPermission();
            }
        }
    }
}
