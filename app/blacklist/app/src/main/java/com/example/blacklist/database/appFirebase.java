package com.example.blacklist.database;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.blacklist.Telephone.BlackList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class appFirebase {
    public static final String DEFAULT_PHONE_NUMBER = "DEFAULT";
    private static final String IN_BLACKLIST = "BlackNumber";
    private static final String IN_WHITELIST = "WhiteNumber";
    private static final String SUBCRIBE = "IsSubcribe";
    private static final String UNSUBCRIBE = "NoSubcribe";

    private static appFirebase instance = null;

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance("https://blacklist-49ebb-default-rtdb.asia-southeast1.firebasedatabase.app");
    private static DatabaseReference db = null;
    private static DatabaseReference numberDB = null;
    private static DatabaseReference subcribeDB = null;
    private static Map<String, ValueEventListener> listenerMap;

    private static BlackList blackList = null;
    private static Set<String> myblacklist;
    private static Set<String> mywhitelist;
    private static Map<String, Set<String>> subcribeBlacklist;
    private static Map<String, Integer> subcribeBlacklistcount;

    private Context ctx = null;
    private String phone_number = DEFAULT_PHONE_NUMBER;

    private appFirebase(Context context) {
        Log.d("BlackList", "NEW appFirebase Instance");
        ctx = context;
        TelephonyManager tMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        phone_number = tMgr.getLine1Number();
        if (phone_number == null || phone_number.equals(""))
            phone_number = DEFAULT_PHONE_NUMBER;

        phone_number = phone_number.replace("+","");
        database.setPersistenceEnabled(true);
        db = database.getReference("userdata/" + phone_number);
        db.keepSynced(true);
        numberDB = db.child("numbers");
        subcribeDB = db.child("subcribe");
        listenerMap = new TreeMap<>();

        blackList = BlackList.getInstance(ctx);
        myblacklist = new TreeSet<>();
        mywhitelist = new TreeSet<>();
        subcribeBlacklist = new TreeMap<>();
        subcribeBlacklistcount = new TreeMap<>();

        numberDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.w("BlackList", ""+snapshot.getValue());
                Map<String, String> map = extractData(""+snapshot.getValue());
                for (Map.Entry<String, String> pair : map.entrySet()) {
                    if (pair.getValue().equals(IN_BLACKLIST)) {
                        if (!myblacklist.contains(pair.getKey())) {
                            mywhitelist.remove(pair.getKey());
                            myblacklist.add(pair.getKey());
                            blackList.noDBputBlockedNumber(pair.getKey());
                        }
                    }
                    else {
                        if (!mywhitelist.contains(pair.getKey())) {
                            myblacklist.remove(pair.getKey());
                            mywhitelist.add(pair.getKey());
                            blackList.noDBdeleteBlockedNumber(pair.getKey());
                        }
                    }
                }
                List<String> deleteFromlist = new ArrayList<>();
                for (String number : myblacklist) {
                    if (!map.containsKey(number)) {
                        if (!subcribeBlacklistcount.containsKey(number)) {
                            blackList.noDBdeleteBlockedNumber(number);
                        }
                        deleteFromlist.add(number);
                    }
                }
                for (String number : deleteFromlist) {
                    myblacklist.remove(number);
                }
                deleteFromlist = new ArrayList<>();
                for (String number : mywhitelist) {
                    if (!map.containsKey(number)) {
                        if (subcribeBlacklistcount.containsKey(number)) {
                            blackList.noDBputBlockedNumber(number);
                        }
                        deleteFromlist.add(number);
                    }
                }
                for (String number : deleteFromlist) {
                    mywhitelist.remove(number);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        subcribeDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.w("BlackList", "Subcribe " + snapshot.getValue());
                Map<String, String> map = extractData(""+snapshot.getValue());
                for (Map.Entry<String, String> pair : map.entrySet()) {
                    if (pair.getValue().equals(SUBCRIBE)) {
                        String subcribeNumber = pair.getKey();
                        if (!subcribeBlacklist.containsKey(subcribeNumber)) {
                            subcribeBlacklist.put(subcribeNumber, new TreeSet<>());
                            listenerMap.put(subcribeNumber, new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Log.w("BlackList", "Number " + subcribeNumber + ": " + snapshot.getValue());
                                    Map<String, String> map = extractData(""+snapshot.getValue());
                                    Set<String> subBlacklist = new TreeSet<>();
                                    for (Map.Entry<String, String> pair : map.entrySet()) {
                                        if (pair.getValue().equals(IN_BLACKLIST))
                                            subBlacklist.add(pair.getKey());
                                    }
                                    List<String> deleteNumber = new ArrayList<>();
                                    for (String number : subcribeBlacklist.get(subcribeNumber)) {
                                        if (!subBlacklist.contains(number)) {
                                            deleteNumber.add(number);
                                        }
                                    }
                                    for (String number: deleteNumber) {
                                        subcribeBlacklistcount.put(number, subcribeBlacklistcount.get(number)-1);
                                        if (subcribeBlacklistcount.get(number) == 0) {
                                            subcribeBlacklistcount.remove(number);
                                            if (!myblacklist.contains(number))
                                                blackList.noDBdeleteBlockedNumber(number);
                                        }
                                    }
                                    for (String number : subBlacklist) {
                                        if (!subcribeBlacklist.get(subcribeNumber).contains(number)) {
                                            subcribeBlacklist.get(subcribeNumber).add(number);
                                            if (!subcribeBlacklistcount.containsKey(number)) {
                                                subcribeBlacklistcount.put(number, 1);
                                                if (!mywhitelist.contains(number))
                                                    blackList.noDBputBlockedNumber(number);
                                            }
                                            else {
                                                subcribeBlacklistcount.put(number, subcribeBlacklistcount.get(number)+1);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            db.getParent().child(subcribeNumber).child("numbers").addValueEventListener(listenerMap.get(subcribeNumber));
                        }
                    }
                }
                List<String> deleteSubcribe = new ArrayList<>();
                for (Map.Entry<String, Set<String>> pair : subcribeBlacklist.entrySet()) {
                    String subcribeNumber = pair.getKey();
                    if (!map.containsKey(subcribeNumber)) {
                        deleteSubcribe.add(subcribeNumber);
                    }
                }
                for (String subcribeNumber : deleteSubcribe) {
                    for (String number : subcribeBlacklist.get(subcribeNumber)) {
                        subcribeBlacklistcount.put(number, subcribeBlacklistcount.get(number) - 1);
                        if (subcribeBlacklistcount.get(number) <= 0) {
                            if (!myblacklist.contains(number)) {
                                blackList.noDBdeleteBlockedNumber(number);
                            }
                            subcribeBlacklistcount.remove(number);
                        }
                    }
                    subcribeBlacklist.remove(subcribeNumber);
                    db.getParent().child(subcribeNumber).child("numbers").removeEventListener(listenerMap.get(subcribeNumber));
                    listenerMap.remove(subcribeNumber);
                }
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
        subcribeDB.child(user_number).removeValue();
    }

    @NonNull
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

    public Set<String> getMyblacklist() {
        return new TreeSet<>(myblacklist);
    }

    public List<String> getMySubcribe() {
        List<String> mysubcribe = new ArrayList<>();
        Map<String, Set<String>> subcribe = new TreeMap<>(subcribeBlacklist);
        for (Map.Entry<String, Set<String>> pair : subcribe.entrySet()) {
            mysubcribe.add(pair.getKey());
        }
        return mysubcribe;
    }

    public Set<String> getMysubcribeBlacklist() {
        return new TreeSet<>(subcribeBlacklistcount.keySet());
    }
}
