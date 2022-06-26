package com.example.blacklist.Service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.blacklist.database.appFirebase;

public class BlackListService extends Service {

    public class BlackListBinder extends Binder {
        public BlackListService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BlackListService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w("BlackList", "ON BIND");
        return null;
    }

    @Override
    public void onCreate() {
        if (!FullPermission())
            stopSelf();
        buildNotification();
        super.onCreate();
        Log.w("BlackList", "SERVICE CREATE");
        appFirebase.getInstance(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("BlackList", "SERVICE DETROY");
    }

    private void buildNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                this, "default")
                .setContentTitle("BlackList")
                .setContentText("BlackList Service is running")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "BlackList notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Execution is reaching this line.
        int NOTIFICATION_ID = 123456;
        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    public boolean FullPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            return false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }

    public boolean FullRole() {
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        if (!roleManager.isRoleHeld(RoleManager.ROLE_DIALER))
            return false;
        return true;
    }

    public boolean FullPermissionRole() {
        return FullPermission() && FullRole();
    }
}
