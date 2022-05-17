package com.example.blacklist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class InCallServiceImplementation extends InCallService {

    private static final String CHANNEL_ID = "10203040503";


    public void onCallAdded (Call call) {
        Log.w("TEST", "1 " + call.toString());

        if (call.getState() == Call.STATE_RINGING) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                    .setContentTitle("Incoming call")
                    .setContentText(call.getDetails().getHandle().toString())
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(call.getDetails().getHandle().toString()))
                    .setPriority(NotificationCompat.PRIORITY_MAX);
//            Log.w("TEST", "1.3 " + call.getDetails().getHandle().toString());
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            // notificationId is a unique int for each notification that you must define
            int notificationId = 110000000;
//            Log.w("TEST", "1.7 " + call.toString());
            notificationManager.notify(notificationId, builder.build());
        }

        Log.w("TEST", "2 " + call.toString());
    }

}
