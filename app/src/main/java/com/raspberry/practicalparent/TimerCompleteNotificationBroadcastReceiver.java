package com.raspberry.practicalparent;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TimerCompleteNotificationBroadcastReceiver extends BroadcastReceiver {
    private NotificationCompat.Builder builderTimerComplete;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannelTimerComplete(context);
        if (intent.getAction().equals(context.getString(R.string.intent_action_timer_finished))) {
            createTimerCompleteNotification(context);
        } else if (intent.getAction().equals(context.getString(R.string.intent_action_timer_finished_from_activity))) {
            createTimerCompleteNotificationActivityStillOnTop(context);
        }
    }

    private void createNotificationChannelTimerComplete(Context context) {
        //from https://developer.android.com/training/notify-user/build-notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name_timer_complete);
            String description = context.getString(R.string.channel_description_timer_complete);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.channel_id_timer_complete), name, importance);
            channel.setDescription(description);

            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (notificationSound == null) {
                notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                if (notificationSound == null) {
                    notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                }
            }
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            channel.setSound(notificationSound, audioAttributes);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[] {1000, 1000});

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createTimerCompleteNotification(Context context) {
        Intent touchActionIntent = new Intent(context, timerActivity.class);
        touchActionIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //Inflate back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(touchActionIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (notificationSound == null) {
            notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (notificationSound == null) {
                notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        builderTimerComplete = new NotificationCompat.Builder(context, context.getString(R.string.channel_id_timer_complete))
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentText("Done")
                .setContentTitle("Done")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[] {1000, 1000})
                .setSound(notificationSound);
        Notification notification = builderTimerComplete.build();
        notification.flags = Notification.FLAG_INSISTENT;
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(444, notification);
    }

    //Intent.FLAG_ACTIVITY_SINGLE_TOP is not working for some reason, so create a
    //separate notification without any tap actions
    private void createTimerCompleteNotificationActivityStillOnTop(Context context) {
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (notificationSound == null) {
            notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (notificationSound == null) {
                notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        builderTimerComplete = new NotificationCompat.Builder(context, context.getString(R.string.channel_id_timer_complete))
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentText("Done")
                .setContentTitle("Done")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[] {1000, 1000})
                .setSound(notificationSound);
        Notification notification = builderTimerComplete.build();
        notification.flags = Notification.FLAG_INSISTENT;
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(444, notification);
    }
}
