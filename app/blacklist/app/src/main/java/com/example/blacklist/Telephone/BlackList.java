package com.example.blacklist.Telephone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BlockedNumberContract;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BlackList {
    private static final Queue<Pair<Integer, String>> actionBlockedNumbers = new LinkedList<>();

    private static class Action{
        public static Integer BLOCK = 0;
        public static Integer UNBLOCK = 1;
    }

    public static List<String> getBlockedNumbers(Context context) {
        Log.w("BlackList: Get blocked", "");

        Cursor c = context.getContentResolver().query(BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                new String[]{BlockedNumberContract.BlockedNumbers.COLUMN_ID,
                        BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                        BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER}, null, null, null);
        List<String> blockednumbers = new ArrayList<String>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            blockednumbers.add(c.getString(1));
            c.moveToNext();
        }
        return blockednumbers;
    }

    public static void putBlockedNumber(Context context, String number) {
        Log.w("BlackList: Block", number);
        ContentValues values = new ContentValues();
        values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number);
        Uri uri = context.getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values);

        // remember action
        actionBlockedNumbers.add(new Pair<>(Action.BLOCK, number));
    }

    public static void deleteBlockedNumber(Context context, String number) {
        Log.w("BlackList: Unblock", number);
        ContentValues values = new ContentValues();
        values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number);
        Uri uri = context.getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values);
        context.getContentResolver().delete(uri, null, null);

        // remember action
        actionBlockedNumbers.add(new Pair<>(Action.UNBLOCK, number));
    }
}
