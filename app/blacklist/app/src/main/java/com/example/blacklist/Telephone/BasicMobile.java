package com.example.blacklist.Telephone;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class BasicMobile {
    public static final int CALL_PHONE_REQUEST_CODE = 1;

    public static void MakeCall(Context context, String number) {
        Log.w("BasicMobile: MakeCall", number);
        // Check Permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},CALL_PHONE_REQUEST_CODE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                return;
        }
        // MakeCall
        Uri uri = Uri.fromParts("tel", number, null);
        Bundle extras = new Bundle();
        extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true);
        final TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        telecomManager.placeCall(uri, extras);
    }
}
