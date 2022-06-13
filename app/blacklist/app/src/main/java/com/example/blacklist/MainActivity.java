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
import android.view.View;
import android.widget.EditText;

import com.example.blacklist.Telephone.BasicMobile;
import com.example.blacklist.Telephone.BlackList;
import com.example.blacklist.ui.callLogModel.CallLogItem;
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

    public List<CallLogItem> getMyCallLog() {
        return callLogList;
    }

}