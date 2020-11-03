package com.raspberry.practicalparent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
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

    CountDownTimer countDownTimer;
    private long mStartTimeInMillis;
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
        createTimerRunningNotification(1, 2, 1);
        startForeground(333, builderTimerRunning.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        //assume timer is running
        mEndTime = prefs.getLong("endTime", 0);
        mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

        countDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                int hours = (int) (l / 1000) / 3600; //turns hours to mins
                int minutes = (int) ((l / 1000) % 3600)/ 60; //turns millis to mins
                int seconds = (int) (l / 1000) % 60; //turns millis to secs
                Log.d("TAG", "Timing is ticking: " + hours + ":" + minutes + ":" + seconds);
                updateTimerRunningNotification(hours, minutes, seconds);
            }

            @Override
            public void onFinish() {
                createTimerCompleteNotification();
                onDestroy();
            }
        }.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("TAG", "Destroying Service");
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
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

    private void createTimerRunningNotification(int hours, int minutes, int seconds) {
        String contentTitle = "Time: " + hours + ":" + minutes + ":" + seconds;
        builderTimerRunning = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentText("Time Remaining")
                .setContentTitle(contentTitle)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(333, builderTimerRunning.build());
    }

    private void createTimerCompleteNotification() {
        builderTimerComplete = new NotificationCompat.Builder(this, "CHANNEL_ID_2")
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentText("Done")
                .setContentTitle("Done")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(444, builderTimerComplete.build());
    }


    private void updateTimerRunningNotification(int hours, int minutes, int seconds) {
        String contentTitle = "Time: " + hours + ":" + minutes + ":" + seconds;
        if (builderTimerRunning != null) {
            builderTimerRunning.setContentTitle(contentTitle);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(333, builderTimerRunning.build());
        }
    }


}
