package com.raspberry.practicalparent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TimerNotificationService extends Service {

    private CountDownTimer countDownTimer;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private long mStartTimeInMillis;
    private boolean isTimerRunning = true;
    private NotificationCompat.Builder builderTimerRunning;
    private NotificationCompat.Builder builderTimerComplete;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "Created Timer Notification Service");
        createNotificationChannelTimerRunning();
        createNotificationChannelTimerComplete();
        createTimerRunningNotification();
        startForeground(333, builderTimerRunning.build());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("Stop Timer");
        filter.addAction("Start Timer");
        filter.addAction("Reset Timer");
        this.registerReceiver(broadcastReceiver, filter);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 0);
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
        try {
            this.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            //Already unregistered
        }
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
        Intent stopTimerIntent = new Intent(this, TimerNotificationServiceBroadcastReceiver.class);
        stopTimerIntent.setAction("Stop Timer");
        PendingIntent stopPendingTimerIntent = PendingIntent.getBroadcast(this, 0, stopTimerIntent, 0);

        Intent startTimerIntent = new Intent(this, TimerNotificationServiceBroadcastReceiver.class);
        startTimerIntent.setAction("Start Timer");
        PendingIntent startPendingTimerIntent = PendingIntent.getBroadcast(this, 0, startTimerIntent, 0);

        Intent resetTimerIntent = new Intent(this, TimerNotificationServiceBroadcastReceiver.class);
        resetTimerIntent.setAction("Reset Timer");
        PendingIntent resetPendingTimerIntent = PendingIntent.getBroadcast(this, 0, resetTimerIntent, 0);

        builderTimerRunning = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentText("Time Remaining")
                .setContentTitle("Staring Timer")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_baseline_timer_24, "Stop", stopPendingTimerIntent)
                .addAction(R.drawable.ic_baseline_timer_24, "Start", startPendingTimerIntent)
                .addAction(R.drawable.ic_baseline_timer_24, "Reset", resetPendingTimerIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(333, builderTimerRunning.build());
    }

    private void startTimer() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        long mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        if (isTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
        } else {
            mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        }
        isTimerRunning = true;
        countDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                int hours = (int) (l / 1000) / 3600; //turns hours to mins
                int minutes = (int) ((l / 1000) % 3600)/ 60; //turns millis to mins
                int seconds = (int) (l / 1000) % 60; //turns millis to secs
                Log.d("TAG", "Timing is ticking: " + hours + ":" + minutes + ":" + seconds);
                //Log.d("TAG", "Timer: " + (mEndTime - System.currentTimeMillis()));
                //Log.d("TAG", "Timer s: " + (mEndTime - System.currentTimeMillis())/1000%60);

                editor.putLong("millisLeft", mEndTime - System.currentTimeMillis());
                editor.putLong("endTime", mEndTime);
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

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
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

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            if (intent.getAction().equals("Stop Timer")) {
                Log.d("TAG", "Stopping timer from Notification");
                cancelTimer();
                editor.putBoolean("timerRunning", false);
                editor.apply();
            } else if (intent.getAction().equals("Start Timer")) {
                if (!isTimerRunning) {
                    Log.d("TAG", "Starting timer from notification");
                    startTimer();
                    editor.putBoolean("timerRunning", true);
                    editor.apply();
                }
            } else if (intent.getAction().equals("Reset Timer")) {
                Log.d("TAG", "Resetting timer from notification");
                cancelTimer();
                mTimeLeftInMillis = mStartTimeInMillis;
                mEndTime = System.currentTimeMillis() + mStartTimeInMillis;
                editor.putBoolean("timerRunning", false);
                editor.putLong("millisLeft", mTimeLeftInMillis);
                editor.putLong("endTime", mEndTime);
                editor.apply();
                int hours = (int) (mTimeLeftInMillis / 1000) / 3600; //turns hours to mins
                int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600)/ 60; //turns millis to mins
                int seconds = (int) (mTimeLeftInMillis / 1000) % 60; //turns millis to secs
                updateTimerRunningNotification(hours, minutes, seconds);
            }
        }
    };
}
