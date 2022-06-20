package com.example.blacklist.Telephone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BlockedNumberContract;
import android.util.Log;

import com.example.blacklist.database.appFirebase;

import java.util.ArrayList;
import java.util.List;

public class BlackList {
    private static BlackList instance = null;
    private static Context ctx;

    private BlackList(Context context) {
        ctx = context;
    }

    public static BlackList getInstance(Context context) {
        if (instance == null) {
            instance = new BlackList(context);
        }
        return instance;
    }

    public List<String> getBlackListNumbers() {
        Log.w("BlackList: Get blocked", "");
        Cursor c = ctx.getContentResolver().query(BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                new String[]{BlockedNumberContract.BlockedNumbers.COLUMN_ID,
                        BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                        BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER}, null, null, null);
        List<String> blockednumbers = new ArrayList<String>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            blockednumbers.add(c.getString(1));
            c.moveToNext();
        }
        c.close();
        return blockednumbers;
    }

    public List<String> getMyBlackListNumbers() {
        return new ArrayList<String>();
    }

    public boolean inBlackList(String number) {
        return false;
    }

    public void putBlockedNumber(String number) {
        Log.w("BlackList: Block", number);
        ContentValues values = new ContentValues();
        values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number);
        Uri uri = ctx.getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values);

        // To Database
        appFirebase db = appFirebase.getInstance(ctx);
        db.addBlackListNumber(number);
    }

    public void deleteBlockedNumber(String number) {
        Log.w("BlackList: Unblock", number);
        ContentValues values = new ContentValues();
        values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number);
        Uri uri = ctx.getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values);
        ctx.getContentResolver().delete(uri, null, null);

        // To Database
        appFirebase db = appFirebase.getInstance(ctx);
        db.addWhiteListNumber(number);
    }

    public void subcribeUser(String number) {
        // To Database
        appFirebase db = appFirebase.getInstance(ctx);
        db.subcribeUser(number);
    }

    public void unsubcribeUser(String number) {
        // To Database
        appFirebase db = appFirebase.getInstance(ctx);
        db.unsubcribeUser(number);
    }
}
