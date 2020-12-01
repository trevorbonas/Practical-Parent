package com.raspberry.practicalparent.TimerNotificationClasses;

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

import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.UI.TimerActivity;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Creates notification when user leaves TimerActivity
 * Allows user to start, stop, or reset timer from notification
 */

public class TimerNotificationService extends Service {

    private CountDownTimer countDownTimer;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private long mStartTimeInMillis;
    private boolean isTimerRunning = true;
    private int mSpeedFactor;
    private final int START_SERVICE = 0;
    private final int START_FROM_NOTIFICATION_STOP = 1;
    private NotificationCompat.Builder builderTimerRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "Created Timer Notification Service");
        createNotificationChannelTimerRunning();
        createTimerRunningNotification();
        startForeground(333, builderTimerRunning.build());

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(getString(R.string.intent_action_stop_timer));
        filter.addAction(getString(R.string.intent_action_start_timer));
        filter.addAction(getString(R.string.intent_action_reset_timer));
        this.registerReceiver(broadcastReceiver, filter);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mStartTimeInMillis = prefs.getLong(getString(R.string.shared_preferences_start_time_in_millis), 0);
        mSpeedFactor = prefs.getInt(getString(R.string.shared_preferences_speed_factor), 1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer(mSpeedFactor, START_SERVICE);
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
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id_timer_running), name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createTimerRunningNotification() {
        Intent touchActionIntent = new Intent(this, TimerActivity.class);
        touchActionIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //Inflate back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(touchActionIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //Add actions
        Intent stopTimerIntent = new Intent(this, TimerNotificationServiceBroadcastReceiver.class);
        stopTimerIntent.setAction(getString(R.string.intent_action_stop_timer));
        PendingIntent stopPendingTimerIntent = PendingIntent.getBroadcast(this, 0, stopTimerIntent, 0);

        Intent startTimerIntent = new Intent(this, TimerNotificationServiceBroadcastReceiver.class);
        startTimerIntent.setAction(getString(R.string.intent_action_start_timer));
        PendingIntent startPendingTimerIntent = PendingIntent.getBroadcast(this, 0, startTimerIntent, 0);

        Intent resetTimerIntent = new Intent(this, TimerNotificationServiceBroadcastReceiver.class);
        resetTimerIntent.setAction(getString(R.string.intent_action_reset_timer));
        PendingIntent resetPendingTimerIntent = PendingIntent.getBroadcast(this, 0, resetTimerIntent, 0);

        builderTimerRunning = new NotificationCompat.Builder(this, getString(R.string.channel_id_timer_running))
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentText(getString(R.string.time_remaining))
                .setContentTitle(getString(R.string.starting_timer))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_baseline_timer_24, getString(R.string.intent_action_stop_button), stopPendingTimerIntent)
                .addAction(R.drawable.ic_baseline_timer_24, getString(R.string.intent_action_start_button), startPendingTimerIntent)
                .addAction(R.drawable.ic_baseline_timer_24, getString(R.string.intent_action_reset_button), resetPendingTimerIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(333, builderTimerRunning.build());
    }

    private void startTimer(final int speedFactor, int type) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        long mStartTimeInMillis = prefs.getLong(getString(R.string.shared_preferences_start_time_in_millis), 600000);
        if (type == 0) {
            mTimeLeftInMillis = prefs.getLong(getString(R.string.shared_preferences_time_left_in_millis), mStartTimeInMillis);
        } else {
            mTimeLeftInMillis = prefs.getLong(getString(R.string.shared_preferences_time_left_in_millis), mStartTimeInMillis) / speedFactor;
        }
        if (isTimerRunning) {
            mEndTime = prefs.getLong(getString(R.string.shared_preferences_end_time), 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
        } else {
            mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        }
        isTimerRunning = true;
        Log.d("TAG", "startTimer: mTimeLeftInMillis: " + mTimeLeftInMillis);
        countDownTimer = new CountDownTimer(mTimeLeftInMillis , 1000 / speedFactor) {
            @Override
            public void onTick(long l) {
                int[] times = TimerActivity.countdownTimerHoursMinutesSeconds(l * speedFactor);
                //Log.d("TAG", "Timing is ticking: " + times[0] + ":" + times[1] + ":" + times[2]);
                //Log.d("TAG", "endTime: " + mEndTime);
                //Log.d("TAG", "mTimeLeftInMillis: " + (mEndTime - System.currentTimeMillis()) * speedFactor);
                editor.putLong(getString(R.string.shared_preferences_time_left_in_millis), (mEndTime - System.currentTimeMillis()) * speedFactor);
                editor.putLong(getString(R.string.shared_preferences_end_time), mEndTime);
                editor.apply();
                updateTimerRunningNotification(times[0], times[1], times[2]);
            }

            @Override
            public void onFinish() {
                Intent timerComplete = new Intent(TimerNotificationService.this, TimerCompleteNotificationBroadcastReceiver.class);
                timerComplete.setAction(getString(R.string.intent_action_timer_finished));
                sendBroadcast(timerComplete);

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

    private void updateTimerRunningNotification(int hours, int minutes, int seconds) {
        String timeLeftFormatted = TimerActivity.formatTimer(hours, minutes, seconds);
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
            if (intent.getAction().equals(getString(R.string.intent_action_stop_timer))) {
                Log.d("TAG", "Stopping timer from Notification");
                cancelTimer();
                editor.putBoolean(getString(R.string.shared_preferences_timer_running), false);
                //editor.putLong(getString(R.string.shared_preferences_time_left_in_millis), mEndTime - System.currentTimeMillis());
                //editor.putLong(getString(R.string.shared_preferences_end_time), mEndTime);
                editor.apply();
            } else if (intent.getAction().equals(getString(R.string.intent_action_start_timer))) {
                if (!isTimerRunning) {
                    Log.d("TAG", "Starting timer from notification");
                    startTimer(mSpeedFactor, START_FROM_NOTIFICATION_STOP);
                    editor.putBoolean(getString(R.string.shared_preferences_timer_running), true);
                    editor.apply();
                }
            } else if (intent.getAction().equals(getString(R.string.intent_action_reset_timer))) {
                Log.d("TAG", "Resetting timer from notification");
                cancelTimer();
                mTimeLeftInMillis = mStartTimeInMillis;
                mEndTime = System.currentTimeMillis() + mStartTimeInMillis;
                editor.putBoolean(getString(R.string.shared_preferences_timer_running), false);
                editor.putLong(getString(R.string.shared_preferences_time_left_in_millis), mTimeLeftInMillis);
                editor.putLong(getString(R.string.shared_preferences_end_time), mEndTime);
                editor.apply();
                int[] times = TimerActivity.countdownTimerHoursMinutesSeconds(mTimeLeftInMillis);
                updateTimerRunningNotification(times[0], times[1], times[2]);
            }
        }
    };
}
