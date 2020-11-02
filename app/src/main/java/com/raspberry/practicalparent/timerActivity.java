package com.raspberry.practicalparent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class timerActivity extends AppCompatActivity {


    private EditText mEditTextInput;
    private Button mButtonSet;

    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;

    private RadioGroup presetTimesRadioGroup;
    private int radioButtonIndex;

    private String CHANNEL_ID_TIMER_RUNNING = "my_channel5";
    private String CHANNEL_ID_TIMER_COMPLETE = "timer_complete_channel";
    private NotificationCompat.Builder builderTimerRunning;
    private NotificationCompat.Builder builderTimerComplete;


    public static Intent makeIntent(Context context) {
        return new Intent(context, timerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Intent intent = getIntent();
        //long startTimer = intent.getLongExtra(timerActivityMainMenu.EXTRA_INT, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setupPresetTimesRadioGroup();
        createNotificationChannelTimerRunning();
        createNotificationChannelTimerComplete();

        //setting time in minutes
        mEditTextInput = findViewById(R.id.edit_text_input);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        mButtonSet = findViewById(R.id.button_set);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);

        mButtonSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String input = mEditTextInput.getText().toString();
                if(input.length() == 0) {
                    Toast.makeText(timerActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisInput = Long.parseLong(input) * 60000;

                if(millisInput == 0){
                    Toast.makeText(timerActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }

                setTime(millisInput);
                mEditTextInput.setText("");
            }
        });

        //Remove checked radio button if user is inputting a custom time
        mEditTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                presetTimesRadioGroup.clearCheck();
                if (!(mEditTextInput.getText().toString().isEmpty())) {
                    String input = mEditTextInput.getText().toString();
                    long millisInput = Long.parseLong(input) * 60000;
                    autoSetTimeFromEditTextField(millisInput);
                }
            }
        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(mTimerRunning) {
                        pauseTimer();
                        deleteTimerRunningNotification();
                    } else {
                        startTimer();
                        int hours = (int) (mTimeLeftInMillis / 1000) / 3600; //turns hours to mins
                        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600)/ 60; //turns millis to mins
                        int seconds = (int) (mTimeLeftInMillis / 1000) % 60; //turns millis to secs
                        createTimerRunningNotification(hours, minutes, seconds);
                    }
            }
        });
        
        mButtonReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

    }

    //setting custom time
    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }

    private void autoSetTimeFromEditTextField(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
    }

    private void startTimer() {
        closeKeyboard();
        mEditTextInput.setText("");

        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                int hours = (int) (mTimeLeftInMillis / 1000) / 3600; //turns hours to mins
                int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600)/ 60; //turns millis to mins
                int seconds = (int) (mTimeLeftInMillis / 1000) % 60; //turns millis to secs
                updateCountDownText();
                updateTimerRunningNotification(hours, minutes, seconds);
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
//                mButtonStartPause.setText("Start");
//                mButtonStartPause.setVisibility(View.INVISIBLE);
//                mButtonReset.setVisibility(View.VISIBLE);
                updateWatchInterface();
                //TODO: add android notification
                deleteTimerRunningNotification();
                createTimerCompleteNotification();
            }
        }.start();

        mTimerRunning = true;
        updateWatchInterface();
    }

    private void createTimerRunningNotification(int hours, int minutes, int seconds) {
        String timeFormatted = formatTimer(hours, minutes, seconds);
        builderTimerRunning = new NotificationCompat.Builder(this, CHANNEL_ID_TIMER_RUNNING)
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentText("Time Remaining")
                .setContentTitle(timeFormatted)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(101, builderTimerRunning.build());
    }

    private void createTimerCompleteNotification() {
        builderTimerComplete = new NotificationCompat.Builder(this, CHANNEL_ID_TIMER_COMPLETE)
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentText("Done")
                .setContentTitle("Done")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(102, builderTimerComplete.build());
    }

    private void updateTimerRunningNotification(int hours, int minutes, int seconds) {
        String timeFormatted = formatTimer(hours, minutes, seconds);
        if (builderTimerRunning != null) {
            builderTimerRunning.setContentTitle(timeFormatted);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(101, builderTimerRunning.build());
        }
    }

    private void deleteTimerRunningNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(101);
    }

    private void createNotificationChannelTimerRunning() {
        //from https://developer.android.com/training/notify-user/build-notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name_timer_running);
            String description = getString(R.string.channel_description_timer_running);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_TIMER_RUNNING, name, importance);
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
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_TIMER_COMPLETE, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateWatchInterface();
    }
    
    private  void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
        updateWatchInterface();
    }

    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600; //turns hours to mins
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600)/ 60; //turns millis to mins
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60; //turns millis to secs
        String timeLeftFormatted = formatTimer(hours, minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private String formatTimer(int hours, int minutes, int seconds) {
        String timeLeftFormatted;
        if(hours > 0){
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours,  minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }
        return timeLeftFormatted;
    }

    private void updateWatchInterface() {
        if (mTimerRunning) {
            mEditTextInput.setVisibility(View.INVISIBLE);
            mButtonSet.setVisibility(View.INVISIBLE);  //set button and text input to invisible
            mButtonReset.setVisibility(View.INVISIBLE);
            presetTimesRadioGroup.setVisibility(View.INVISIBLE);
            presetTimesRadioGroup.clearCheck();
            mButtonStartPause.setText("Pause");
        } else {
            mEditTextInput.setVisibility(View.VISIBLE);
            mButtonSet.setVisibility(View.VISIBLE);  //set button and text input to visible
            presetTimesRadioGroup.setVisibility(View.VISIBLE);
            mButtonStartPause.setText("Start");

            if(mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }

            if(mTimeLeftInMillis < mStartTimeInMillis) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    // close keyboard after setting timer
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setupPresetTimesRadioGroup() {
        presetTimesRadioGroup = findViewById(R.id.rgPresetTimes);
        final int[] presetTimes = getResources().getIntArray(R.array.preset_times);

        for (int i = 0; i < presetTimes.length; i++) {
            final int minutes = presetTimes[i];
            RadioButton button = new RadioButton(this);
            button.setText(minutes + " minute(s)");
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    radioButtonIndex = finalI;
                    if (!(mEditTextInput.getText().toString().isEmpty())) {
                        mEditTextInput.getText().clear();
                        //Re-check the clicked radio button
                        ((RadioButton) presetTimesRadioGroup.getChildAt(radioButtonIndex)).setChecked(true);
                    }
                    int minutesToMilliseconds = minutes * 60000;
                    setTime(minutesToMilliseconds);
                }
            });
            presetTimesRadioGroup.addView(button);
        }
    }

    //for timer to run in background
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        //TODO: change strings to constants
        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if(mCountDownTimer != null){
            mCountDownTimer.cancel(); //cancel timer when time runs out
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateWatchInterface();

        if (mTimerRunning){
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            //check if overdue
            if(mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateWatchInterface();  //make buttons invisible
            } else {
                startTimer();
                int hours = (int) (mTimeLeftInMillis / 1000) / 3600; //turns hours to mins
                int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600)/ 60; //turns millis to mins
                int seconds = (int) (mTimeLeftInMillis / 1000) % 60; //turns millis to secs
                createTimerRunningNotification(hours, minutes, seconds);
            }
        }
    }
}