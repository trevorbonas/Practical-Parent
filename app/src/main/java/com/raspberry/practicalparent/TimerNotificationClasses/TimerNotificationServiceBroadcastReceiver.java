package com.raspberry.practicalparent.TimerNotificationClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Passes received message from TimerNotificationService back to TimerNotificationService
 * to run functions from TimerNotificationService
 */

public class TimerNotificationServiceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent(intent.getAction()));
    }
}