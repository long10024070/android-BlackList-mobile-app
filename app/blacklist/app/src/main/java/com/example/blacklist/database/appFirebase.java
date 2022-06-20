package com.example.blacklist.database;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

public class appFirebase {
    private static final String DEFAULT_PHONE_NUMBER = "DEFAULT";
    private static final String IN_BLACKLIST = "BlackNumber";
    private static final String IN_WHITELIST = "WhiteNumber";
    private static final String SUBCRIBE = "IsSubcribe";
    private static final String UNSUBCRIBE = "NoSubcribe";

    private static appFirebase instance = null;

    public static final FirebaseDatabase database = FirebaseDatabase.getInstance("https://blacklist-49ebb-default-rtdb.asia-southeast1.firebasedatabase.app");
    public static DatabaseReference db = null;
    public static DatabaseReference numberDB = null;
    public static DatabaseReference subcribeDB = null;

    private Context ctx = null;
    private String phone_number = DEFAULT_PHONE_NUMBER;

    private appFirebase(Context context) {
        Log.d("BlackList", "NEW appFirebase Instance");
        ctx = context;
        TelephonyManager tMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) ctx, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE},2);
        }
        phone_number = tMgr.getLine1Number();
        if (phone_number == null || phone_number == "")
            phone_number = DEFAULT_PHONE_NUMBER;
        db = database.getReference("userdata/"+phone_number);
        numberDB = db.child("numbers");
        subcribeDB = db.child("subcribe");

        numberDB.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.w("BlackList", "Firebase" + snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        numberDB.child(number).setValue(IN_BLACKLIST);
    }

    public void addWhiteListNumber(String number) {
        if (phone_number.equals(DEFAULT_PHONE_NUMBER))
            return;
        numberDB.child(number).setValue(IN_WHITELIST);
    }

    public void subcribeUser(String user_number) {
        if (phone_number.equals(DEFAULT_PHONE_NUMBER))
            return;
        subcribeDB.child(user_number).setValue(SUBCRIBE);
    }

    public void unsubcribeUser(String user_number) {
        if (phone_number.equals(DEFAULT_PHONE_NUMBER))
            return;
        subcribeDB.child(user_number).setValue(UNSUBCRIBE);
    }

    private Map<String, String> extractData(String data) {
        Map<String, String> map = new TreeMap<>();
        for (int i=0;i<data.length();++i) {
            if (data.charAt(i) == '=') {
                int l = i - 1;
                int r = i + 1;
                while (data.charAt(l) != '{' && data.charAt(l) != ' ')
                    --l;
                while (data.charAt(r) != '}' && data.charAt(r) != ',')
                    ++r;
                String key = data.substring(l+1, i);
                String value = data.substring(i+1, r);
                map.put(key,value);
            }
        }
        return map;
    }

    public List<String> getBlacklist() {
        Set<String> blacklist = new TreeSet<String>();
        Set<String> whitelist = new TreeSet<String>();
        Set<String> subcribe = new TreeSet<String>();

        numberDB.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    String data = String.valueOf(task.getResult().getValue());
                    Map<String, String> map =  extractData(data);
                    for (Map.Entry<String, String> pair : map.entrySet()) {
                        if (pair.getValue().equals(IN_BLACKLIST))
                            blacklist.add(pair.getKey());
                        if (pair.getValue().equals(IN_WHITELIST))
                            whitelist.add(pair.getKey());
                    }
                    Log.d("firebase BLACKLIST", blacklist.toString());
                }
            }
        });

        subcribeDB.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    String data = String.valueOf(task.getResult().getValue());
                    Map<String, String> map =  extractData(data);
                    for (Map.Entry<String, String> pair : map.entrySet()) {
                        if (pair.getValue().equals(SUBCRIBE))
                            subcribe.add(pair.getKey());
                    }
                }
            }
        });

        for (String number : subcribe) {
            db.child(number).child("numbers").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        String data = String.valueOf(task.getResult().getValue());
                        Map<String, String> map =  extractData(data);
                        for (Map.Entry<String, String> pair : map.entrySet()) {
                            if (!whitelist.contains(pair.getKey()) && pair.getValue().equals(IN_BLACKLIST))
                                blacklist.add(pair.getKey());
                        }
                        Log.d("firebase BLACKLIST", blacklist.toString());
                    }
                }
            });
        }

        Log.w("firebase blacklist", blacklist.toString());
        Log.w("BlackList", blacklist.toString());
        return new ArrayList<String>(blacklist);
    }
}
