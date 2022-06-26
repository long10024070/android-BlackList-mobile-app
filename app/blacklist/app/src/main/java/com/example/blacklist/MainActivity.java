package com.example.blacklist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;

import com.example.blacklist.Service.BlackListService;
import com.example.blacklist.Telephone.BlackList;
import com.example.blacklist.database.appFirebase;
import com.example.blacklist.ui.Contact.ContactModel;
import com.example.blacklist.ui.CallLogModel.CallLogItem;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.blacklist.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_CODE = 7777777;

    private ActivityMainBinding binding;

    private List<CallLogItem> callLogList;
    private List<ContactModel> contactList;

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

        // Request permission
        List<String> permissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.READ_CALL_LOG);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.READ_CONTACTS);
        if (permissions.size() > 0) {
            String[] permission_strings = new String[permissions.size()];
            ActivityCompat.requestPermissions(
                    this,
                    permissions.toArray(permission_strings),
                    REQUEST_CODE
            );
        }

        // Request Default Call app
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        if (!roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
            requestRoleDialer();
        }

        // Start Service
        if (FullPermissionRole()) {
            Intent serviceIntent = new Intent(this, BlackListService.class);
            this.startForegroundService(serviceIntent);
        }

        while (!FullPermissionRole()) {}
        RequestPhoneNumberIFNEEDDED();
    }

    public boolean FullPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            return false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }

    public boolean FullRole() {
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        if (!roleManager.isRoleHeld(RoleManager.ROLE_DIALER))
            return false;
        return true;
    }

    public boolean FullPermissionRole() {
        return FullPermission() && FullRole();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted. Continue the action or workflow
                // in your app.
                if (FullPermissionRole()) {
                    Context ctx = MainActivity.this;
                    Intent serviceIntent = new Intent(ctx, BlackListService.class);
                    ctx.startForegroundService(serviceIntent);
                }
            } else {
                // Explain to the user that the feature is unavailable because
                // the features requires a permission that the user has denied.
                // At the same time, respect the user's decision. Don't link to
                // system settings in an effort to convince the user to change
                // their decision.
                finish();
            }
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    public void requestRoleDialer() {
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
        startActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> startActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == android.app.Activity.RESULT_OK) {
                        // Your app is now the default dialer app
                        Context ctx = MainActivity.this;
                        if (FullPermissionRole()) {
                            Intent serviceIntent = new Intent(ctx, BlackListService.class);
                            ctx.startForegroundService(serviceIntent);
                        }
                    } else {
                        // Your app is not the default dialer app
                        finish();
                    }
                }
            });

    public void blockNumber(View view) {
        EditText phoneNumber = findViewById(R.id.BlockNumber);
        BlackList.getInstance(this).putBlockedNumber(phoneNumber.getText().toString());
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

    private void RequestPhoneNumber() {
        GetPhoneNumberHintIntentRequest request = GetPhoneNumberHintIntentRequest.builder().build();

        ActivityResultLauncher<IntentSenderRequest> phoneNumberHintIntentResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartIntentSenderForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                try {
                                    String phoneNumber = Identity.getSignInClient(MainActivity.this).getPhoneNumberFromIntent(result.getData());
                                    Log.d("BlackList", "Phone Number Hint " + phoneNumber);
                                    appFirebase db = appFirebase.getInstance(MainActivity.this);
                                    db.setPhone_number(phoneNumber);

                                } catch (Exception e) {
                                    Log.e("BlackList", "Phone Number Hint failed");
                                }
                            }
                        });

        Identity.getSignInClient(this)
                .getPhoneNumberHintIntent(request)
                .addOnSuccessListener( result -> {
                    try {
                        IntentSenderRequest isd = new IntentSenderRequest.Builder(result.getIntentSender()).build();
                        phoneNumberHintIntentResultLauncher.launch(isd);
                    } catch(Exception e) {
                        Log.e("BlackList", "Launching the PendingIntent failed", e);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("BlackList", "Phone Number Hint failed", e);
                });;
    }

    public void RequestPhoneNumberIFNEEDDED() {
        appFirebase db = appFirebase.getInstance(this);
        if (db.getPhone_number().equals(appFirebase.DEFAULT_PHONE_NUMBER))
            RequestPhoneNumber();
    }
}