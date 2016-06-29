package com.example.switchlistener;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.lang.reflect.Method;
import com.android.internal.telephony.ITelephony;

import net.bluetoothviewer.BluetoothNetFlix;

public class PhoneCallReceiver extends BroadcastReceiver {

    private ITelephony telephonyService;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(Utilities.DEBUG_TAG, "PhoneCallReceiver: onReceive()");

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        boolean dndModeOn = sharedPreferences.getBoolean(context.getString(R.string.pref_dnd_mode_on), false);

        if (!dndModeOn) {
            Log.d(Utilities.DEBUG_TAG, "Do not disturb mode off -> doing nothing");
            return;
        }

        Log.d(Utilities.DEBUG_TAG, "Do not disturb mode on -> blocking");

        try {
            Thread.sleep(Utilities.CALL_BLOCK_WAIT_TIME);
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class c = Class.forName(telephonyManager.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephonyManager);
            telephonyService.endCall();
            createNotification(context, android.R.drawable.stat_notify_missed_call, "Call blocked", "Do not disturb activated");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotification(Context context, int drawableResource, String title, String text) {

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(drawableResource)
                .setContentTitle(title)
                .setContentText(text);

        Intent resultIntent = new Intent(context, BluetoothNetFlix.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(BluetoothNetFlix.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(100, mBuilder.build());
    }
}
