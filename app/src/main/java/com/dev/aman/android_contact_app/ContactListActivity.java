package com.dev.aman.android_contact_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class ContactListActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private ContactListAdapter adapter;
    private ArrayList<ContactModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        init();
        loadContactList();
        onClicks();

    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void loadContactList() {
        HashMap<String, ContactModel> map = new HashMap<String, ContactModel>();

        if (hasContactReadPermission()) {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {

                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                String name = phones.getString(
                        phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                String phoneNumber = phones.getString(
                        phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        .replaceAll("[^0-9+]", "");

                String image = phones.getString(
                        phones.getColumnIndex(ContactsContract.CommonDataKinds.Photo.CONTACT_ID));

                String email = null;
                Cursor emailCursor = getContentResolver()
                        .query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id},
                                null);
                if (emailCursor != null && emailCursor.moveToFirst()) {
                    email = emailCursor.getString(
                            emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                    emailCursor.close();
                }

                ContactModel contactModel = new ContactModel();
                contactModel.setName(name);
                contactModel.setNumber(phoneNumber);
                if(!TextUtils.isEmpty(email))
                    contactModel.setEmail(email);

                if (!map.containsKey(phoneNumber))
                    map.put(phoneNumber, contactModel);

            }

            phones.close();
            sortArrayList(map);
            setRecyclerView();

        } else {
            requestPermission();
        }
    }

    private void setRecyclerView() {
        adapter = new ContactListAdapter(this, arrayList);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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

    private void onClicks() {

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
                loadContactList();
            }
        } else {
            requestPermission();
        }
    }
}
