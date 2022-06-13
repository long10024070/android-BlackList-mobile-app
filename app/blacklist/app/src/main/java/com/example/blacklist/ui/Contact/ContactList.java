package com.example.blacklist.ui.Contact;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacklist.MainActivity;
import com.example.blacklist.R;

import java.util.ArrayList;

public class ContactList extends MainActivity {
    RecyclerView recyclerView;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void checkPermission() {
        //Check condition

        if (ContextCompat.checkSelfPermission(ContactList.this
                , Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            //When permission is not granted
            //Request permission
            ActivityCompat.requestPermissions(ContactList.this
                    , new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            //When permission is granted
            //Create method
            getContactList();
        }
    }

    private void getContactList() {
        // Init uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //Sort by ascending
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "ASC";
        // Init cursor
        Cursor cursor = getContentResolver().query(
                uri, null, null, null, sort
        );
        //Check condition
        if (cursor.getCount() > 0) {
            //When count is greater than 0
            //Use while loop
            while (cursor.moveToNext()) {
                //Cursor move to next
                //Get contact id
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));
                //Get contact name
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));
                //Init phone uri
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                //Init selection
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                        + " =?";
                //Init phone cursor
                Cursor phoneCursor = getContentResolver().query(
                        uriPhone, null, selection
                        , new String[]{id}, null
                );
                //Check condition
                if (phoneCursor.moveToNext()) {
                    //When phone cursor move to next
                    @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));
                    // Init contact model
                    ContactModel model = new ContactModel();
                    //Set name
                    model.setName(name);
                    //Set number
                    model.setNumber(number);
                    //Add model in array list
                    arrayList.add(model);
                    //Close phone cursor
                    phoneCursor.close();
                }
            }
            //Close cursor
            cursor.close();
        }
        //Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Init adapter
        adapter = new ContactAdapter(this, arrayList);
        //Set adapter
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check condition
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {
            getContactList();
        } else {
            //Display toast
            Toast.makeText(ContactList.this, "Permission Denied."
                    , Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }
}