package com.example.blacklist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.role.RoleManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.BlockedNumberContract;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;

import com.example.blacklist.Telephone.BasicMobile;
import com.example.blacklist.Telephone.BlackList;
import com.example.blacklist.ui.Contact.ContactModel;
import com.example.blacklist.ui.callLogModel.CallLogItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.blacklist.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    //private RecyclerView revCallLog ;
    private List<CallLogItem> callLogList  ;
    private ArrayList<ContactModel> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callLogList = new ArrayList<CallLogItem>() ;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Request Default Call app
        requestRoleDialer();

        // Request to access call log
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, PackageManager.PERMISSION_GRANTED);

        // Request to access contact list
        if (ContextCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            //When permission is not granted
            //Request permission
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }

    }

    public void requestRoleDialer() {
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
        startActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> startActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == android.app.Activity.RESULT_OK) {
                        // Your app is now the default dialer app
                    } else {
                        // Your app is not the default dialer app
                    }
                }
            });

    public void blockNumber(View view) {
        EditText phoneNumber = findViewById(R.id.BlockNumber);
        BlackList.getInstance(this).putBlockedNumber(phoneNumber.getText().toString());
        ContentValues values = new ContentValues();
        values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, phoneNumber.getText().toString());
        Uri uri = getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values);
    }

    public void enterBlockNumber(View view) {
        final String defaulContent = "Enter Number to Block";
        EditText phoneNumber = findViewById(R.id.BlockNumber);
        phoneNumber.getText().clear();
    }

    public void callNumber(View view) {
        EditText phoneNumber = findViewById(R.id.BlockNumber);
        BasicMobile.MakeCall(this, phoneNumber.getText().toString());
    }

    public void pushBlockedNumber(View view) {

    }

    public void pullBlockedNumber(View view) {

    }


    public void fetchCallLog() {
        String sortOrder = android.provider.CallLog.Calls.DATE + " DESC" ;
        Cursor cursor  = this.getContentResolver().query(
            CallLog.Calls.CONTENT_URI,
                null,null,null,
                sortOrder);

        callLogList.clear();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String str_number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)) ;
            @SuppressLint("Range") String str_name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)) ;
            str_name = str_name==null || str_name.equals("") ? "Unknown" : str_name;
            callLogList.add(new CallLogItem(str_name,str_number)) ;
        }
    }

    public void getContactList() {
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
                    contactList.add(model);
                    //Close phone cursor
                    phoneCursor.close();
                }
            }
            //Close cursor
            cursor.close();
        }
    }
    public List<CallLogItem> getMyCallLog() {
        return callLogList;
    }

    public ArrayList<ContactModel> getMyContactList() {
        return contactList;
    }

}