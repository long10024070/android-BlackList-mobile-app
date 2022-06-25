package com.example.blacklist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.role.RoleManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.BlockedNumberContract;
import android.util.Log;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;

import com.example.blacklist.Service.BlackListService;
import com.example.blacklist.Telephone.BasicMobile;
import com.example.blacklist.Telephone.BlackList;
import com.example.blacklist.ui.Contact.ContactModel;
import com.example.blacklist.ui.CallLogModel.CallLogItem;
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
    private List<ContactModel> contactList;

    private BlackListService mService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("BlackList", "ON CREATE");

        super.onCreate(savedInstanceState);

        callLogList = new ArrayList<CallLogItem>() ;
        contactList = new ArrayList<ContactModel>() ;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_my_black_list)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Request Default Call app
        requestRoleDialer();

        // Request to access call log
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_NUMBERS}, PackageManager.PERMISSION_GRANTED);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
//                finish();
            requestPermissionLauncher.launch(Manifest.permission.READ_CALL_LOG);
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
            requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_NUMBERS);
        }

        // Start Blacklist auto config with Database at every moment (if not started)
        Intent serviceIntent = new Intent(this, BlackListService.class);
        this.startForegroundService(serviceIntent);
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
                        finish();
                    }
                }
            });

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    finish();
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
            @SuppressLint("Range") String str_call_type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)) ;

            switch (Integer.parseInt(str_call_type)) {
                case CallLog.Calls.INCOMING_TYPE:
                    str_call_type = "Incoming" ;
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    str_call_type = "Outgoing";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    str_call_type = "Missed";
                    break;
                case CallLog.Calls.VOICEMAIL_TYPE:
                    str_call_type = "Voicemail";
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    str_call_type = "Rejected";
                    break;
                case CallLog.Calls.BLOCKED_TYPE:
                    str_call_type = "Blocked";
                    break;
                case CallLog.Calls.ANSWERED_EXTERNALLY_TYPE:
                    str_call_type = "Externally Answered";
                    break;
                default:
                    str_call_type = "NA";
            }
            callLogList.add(new CallLogItem(str_name,str_number,str_call_type)) ;
        }
    }

    public void getContactList() {
        // Init uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //Sort by ascending
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        // Init cursor
        Cursor cursor = getContentResolver().query(
                uri, null, null, null, sort
        );
        contactList.clear();
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
                    //ContactModel model = new ContactModel();
                    //Set name
                    //model.setName(name);
                    //Set number
                    //model.setNumber(number);
                    //Add model in array list
                    contactList.add(new ContactModel(name,number));
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

    public List<ContactModel> getMyContactList() {
        return contactList;
    }

}