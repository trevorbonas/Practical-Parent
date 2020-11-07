package com.raspberry.practicalparent.TimerNotificationClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimerNotificationServiceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent(intent.getAction()));
    }
}