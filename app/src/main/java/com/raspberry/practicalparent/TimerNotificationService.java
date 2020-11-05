package com.raspberry.practicalparent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TimerNotificationService extends Service {

    private static CountDownTimer countDownTimer;
    private long mTimeLeftInMillis;
    private long mEndTime;
    NotificationCompat.Builder builderTimerRunning;
    NotificationCompat.Builder builderTimerComplete;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "Created Timer Notification Service");
        createNotificationChannelTimerRunning();
        createNotificationChannelTimerComplete();
        createTimerRunningNotification();
        startForeground(333, builderTimerRunning.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("TAG", "Destroying Service");
        cancelTimer();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(333);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Notification
    private void createNotificationChannelTimerRunning() {
        //from https://developer.android.com/training/notify-user/build-notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name_timer_running);
            String description = getString(R.string.channel_description_timer_running);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotificationChannelTimerComplete() {
        //from https://developer.android.com/training/notify-user/build-notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name_timer_complete);
            String description = getString(R.string.channel_description_timer_complete);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID_2", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createTimerRunningNotification() {
        Intent touchActionIntent = new Intent(this, timerActivity.class);
        touchActionIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //Inflate back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(touchActionIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //Add actions
        //Use broadcastreceiver??
        Intent stopTimerIntent = new Intent(this, TimerNotificationServiceBroadcastReceiver.class);
        stopTimerIntent.setAction("Stop Timer");
        PendingIntent stopPendingTimerIntent = PendingIntent.getBroadcast(this, 0, stopTimerIntent, 0);

        Intent startTimerIntent = new Intent(this, TimerNotificationServiceBroadcastReceiver.class);
        startTimerIntent.setAction("Start Timer");
        PendingIntent startPendingTimerIntent = PendingIntent.getBroadcast(this, 0, startTimerIntent, 0);

        builderTimerRunning = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentText("Time Remaining")
                .setContentTitle("Staring Timer")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_baseline_timer_24, "Stop", stopPendingTimerIntent)
                .addAction(R.drawable.ic_baseline_timer_24, "Start", startPendingTimerIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(333, builderTimerRunning.build());
    }

    private void startTimer() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        long mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mEndTime = prefs.getLong("endTime", 0);
        mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

        countDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                int hours = (int) (l / 1000) / 3600; //turns hours to mins
                int minutes = (int) ((l / 1000) % 3600)/ 60; //turns millis to mins
                int seconds = (int) (l / 1000) % 60; //turns millis to secs
                Log.d("TAG", "Timing is ticking: " + hours + ":" + minutes + ":" + seconds);
                editor.putLong("millisLeft", mEndTime - System.currentTimeMillis());
                editor.apply();
                updateTimerRunningNotification(hours, minutes, seconds);
            }

            @Override
            public void onFinish() {
                createTimerCompleteNotification();
                onDestroy();
            }
        }.start();
    }

    public static void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public static void startTimerFromBroadcast() {

    }

    private void createTimerCompleteNotification() {
        Intent touchActionIntent = new Intent(this, timerActivity.class);
        touchActionIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //Inflate back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(touchActionIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builderTimerComplete = new NotificationCompat.Builder(this, "CHANNEL_ID_2")
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentText("Done")
                .setContentTitle("Done")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(444, builderTimerComplete.build());
    }

    private void updateTimerRunningNotification(int hours, int minutes, int seconds) {
        String timeLeftFormatted = timerActivity.formatTimer(hours, minutes, seconds);
        if (builderTimerRunning != null) {
            builderTimerRunning.setContentTitle(timeLeftFormatted);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(333, builderTimerRunning.build());
        }
    }
}
