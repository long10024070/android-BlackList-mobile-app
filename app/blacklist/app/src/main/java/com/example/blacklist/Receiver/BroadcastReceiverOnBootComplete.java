package com.example.blacklist.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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