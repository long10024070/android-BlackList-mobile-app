package com.example.blacklist.Receiver;

import android.Manifest;
import android.app.role.RoleManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.blacklist.MainActivity;
import com.example.blacklist.Service.BlackListService;

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Log.w("BlackList", "ACTION_BOOT_COMPLETED");
            Intent serviceIntent = new Intent(context, BlackListService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}