package com.example.blacklist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.role.RoleManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.blacklist.Telephone.BasicMobile;
import com.example.blacklist.Telephone.BlackList;
import com.example.blacklist.ui.callLogModel.CallLogItem;
import com.example.blacklist.ui.notifications.NotificationsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacklist.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    //private RecyclerView revCallLog ;
    private List<CallLogItem> callLogList  ;

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
    }

    public void requestRoleDialer() {
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
        ActivityResultLauncher<Intent> startActivityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
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
        startActivityResultLauncher.launch(intent);
    }

    // Post operation for request Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case BasicMobile.CALL_PHONE_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    public void blockNumber(View view) {
        EditText phoneNumber = findViewById(R.id.BlockNumber);
        BlackList.putBlockedNumber(this, phoneNumber.getText().toString());
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

    public List<CallLogItem> getMyCallLog() {
        return callLogList;
    }
}