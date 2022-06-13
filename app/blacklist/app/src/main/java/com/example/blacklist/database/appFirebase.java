package com.example.blacklist.database;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class appFirebase {
    private static final String DEFAULT_PHONE_NUMBER = "DEFAUT";

    private static appFirebase instance = null;

    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference db = null;
    private static final Map<String, String> myBlacklist = new HashMap<>();
    private static final Map<String, String> myWhitelist = new HashMap<>();
    private static final Map<String, String> mySubcribe = new HashMap<>();

    private Context ctx = null;
    private String phone_number = DEFAULT_PHONE_NUMBER;

    private appFirebase(Context context) {
        ctx = context;
        TelephonyManager tMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) ctx, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE},2);
        }
        phone_number = tMgr.getLine1Number();
        if (phone_number == null || phone_number == "")
            phone_number = DEFAULT_PHONE_NUMBER;
        db = database.getReference("userdata/"+phone_number);
    }

    public static appFirebase getInstance(Context context) {
        if (instance == null) {
            instance = new appFirebase(context);
        }
        return instance;
    }

    public void addBlackListNumber(String number) {
        if (phone_number.equals(DEFAULT_PHONE_NUMBER))
            return;
        if (myWhitelist.containsKey(number)) {
            db.child("Whitelist").child(myWhitelist.get(number)).removeValue();
            myWhitelist.remove(number);
        }
        if (!myBlacklist.containsKey(number)) {
            String key = db.child("Blacklist").push().getKey();
            db.child("Blacklist").child(key).setValue(number);
            myBlacklist.put(number, key);
        }
    }

    public void addWhiteListNumber(String number) {
        if (phone_number.equals(DEFAULT_PHONE_NUMBER))
            return;
        if (myBlacklist.containsKey(number)) {
            db.child("Blacklist").child(myBlacklist.get(number)).removeValue();
            myBlacklist.remove(number);
        }
        if (!myWhitelist.containsKey(number)) {
            String key = db.child("Whitelist").push().getKey();
            db.child("Whitelist").child(key).setValue(number);
            myWhitelist.put(number, key);
        }
    }

    public void subcribeUser(String user_number) {
        if (!mySubcribe.containsKey(user_number)) {
            String key = db.child("Subcribe").push().getKey();
            db.child("Subcribe").child(key).setValue(user_number);
            mySubcribe.put(user_number, key);
        }
    }

    public void unsubcribeUser(String user_number) {
        if (mySubcribe.containsKey(user_number)) {
            db.child("Subcribe").child(mySubcribe.get(user_number)).removeValue();
            mySubcribe.remove(user_number);
        }
    }
}
