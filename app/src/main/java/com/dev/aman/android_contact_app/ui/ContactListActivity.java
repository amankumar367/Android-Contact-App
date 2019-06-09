package com.dev.aman.android_contact_app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Patterns;

import com.dev.aman.android_contact_app.adapter.ContactListAdapter;
import com.dev.aman.android_contact_app.model.ContactModel;
import com.dev.aman.android_contact_app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ContactListActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private ContactListAdapter adapter;
    private ProgressDialog mProgressBar;
    private ArrayList<ContactModel> arrayList = new ArrayList<>();

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
                    getContactDetails();
                    setRecyclerView();
                }
            },200);

        } else {
            requestPermission();
        }
    }

    private void getContactDetails() {
        HashMap<String, ContactModel> map = new HashMap<String, ContactModel>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            do {
                // get the contact's information
                String id = cursor
                        .getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor
                        .getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Integer hasPhone = cursor
                        .getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                String image = null;
                image = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.Photo.PHOTO_THUMBNAIL_URI));

                // get the user's email address
                String email = null;
                Cursor cursorEmail = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id},
                        null);

                if (cursorEmail != null && cursorEmail.moveToFirst()) {
                    email = cursorEmail.getString(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    cursorEmail.close();
                }

                // get the user's phone number
                String phone = null;
                if (hasPhone > 0) {
                    Cursor cursorPhone = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (cursorPhone != null && cursorPhone.moveToFirst()) {
                        phone = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        cursorPhone.close();
                    }
                }

                // if the user has an email or phone then add it to contacts
                if ((!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        && !email.equalsIgnoreCase(name)) || (!TextUtils.isEmpty(phone))) {
                    ContactModel contactModel = new ContactModel();
                    contactModel.setName(name);
                    contactModel.setNumber(phone);
                    contactModel.setEmail(email);
                    contactModel.setImage(image);

                    if (!map.containsKey(phone))
                        map.put(phone, contactModel);

                }

            } while (cursor.moveToNext());
            cursor.close();
            sortArrayList(map);
        }
    }

    private void setRecyclerView() {
        adapter = new ContactListAdapter(this, arrayList);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        mProgressBar.dismiss();
    }

    private void sortArrayList(HashMap<String, ContactModel> hashmap) {
        arrayList.clear();

        for (Map.Entry<String, ContactModel> map : hashmap.entrySet())
            arrayList.add(map.getValue());

        Collections.sort(arrayList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                ContactModel c1 = (ContactModel) o1;
                ContactModel c2 = (ContactModel) o2;
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
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
