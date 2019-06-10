package com.dev.aman.android_contact_app.viewmodel;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.dev.aman.android_contact_app.model.ContactModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ContactListViewModel extends AndroidViewModel {

    public MutableLiveData<ArrayList<ContactModel>> contact = new MutableLiveData<>();
    private ArrayList<ContactModel> arrayList = new ArrayList<>();
    private ContentResolver contentResolver = getApplication().getContentResolver();

    public ContactListViewModel(Application application) {
        super(application);
    }


    public void getContactDetails() {
        HashMap<String, ContactModel> map = new HashMap<String, ContactModel>();
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

    private void sortArrayList(HashMap<String, ContactModel> hashmap) {
        arrayList.clear();

        for (Map.Entry<String, ContactModel> map : hashmap.entrySet())
            arrayList.add(map.getValue());
        contact.setValue(arrayList);

        Collections.sort(arrayList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                ContactModel c1 = (ContactModel) o1;
                ContactModel c2 = (ContactModel) o2;
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
    }

}
