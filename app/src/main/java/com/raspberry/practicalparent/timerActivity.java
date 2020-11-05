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
import android.util.Log;
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

        //setting time in minutes
        mEditTextInput = findViewById(R.id.edit_text_input);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);

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
                cancelNotification(444);
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
                    } else {
                        startTimer();
                    }
            }
        });
        
        mButtonReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetTimer();
                cancelNotification(444);
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
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateWatchInterface();
                Intent timerComplete = new Intent(timerActivity.this, TimerCompleteNotificationBroadcastReceiver.class);
                timerComplete.setAction("TimerFinishActivity");
                sendBroadcast(timerComplete);
            }
        }.start();

        mTimerRunning = true;
        updateWatchInterface();
    }

    private void startTimerNotificationService() {
        Intent intent = new Intent(this, TimerNotificationService.class);
        startService(intent);
        Toast.makeText(this, "Starting Service:", Toast.LENGTH_SHORT).show();
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
        int[] times = countdownTimerHoursMinutesSeconds(mTimeLeftInMillis);
        String timeLeftFormatted = formatTimer(times[0], times[1], times[2]);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    //Returns an array filled with times in hours, minutes, and seconds from time (in millis)
    public static int[] countdownTimerHoursMinutesSeconds(long time) {
        int hours = (int) (time / 1000) / 3600;
        int minutes = (int) ((time / 1000) % 3600)/ 60;
        int seconds = (int) (time / 1000) % 60;
        return new int[]{hours, minutes, seconds};
    }

    public static String formatTimer(int hours, int minutes, int seconds) {
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
            mButtonReset.setVisibility(View.INVISIBLE);
            presetTimesRadioGroup.setVisibility(View.INVISIBLE);
            presetTimesRadioGroup.clearCheck();
            mButtonStartPause.setText("Pause");
        } else {
            mEditTextInput.setVisibility(View.VISIBLE);
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
                    cancelNotification(444);
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

        //Quick 5s, TODO delete before submission
        RadioButton button = new RadioButton(this);
        button.setText(3 + "s");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioButtonIndex = 5;
                if (!(mEditTextInput.getText().toString().isEmpty())) {
                    mEditTextInput.getText().clear();
                    //Re-check the clicked radio button
                    ((RadioButton) presetTimesRadioGroup.getChildAt(radioButtonIndex)).setChecked(true);
                }
                int minutesToMilliseconds = 3000;
                setTime(minutesToMilliseconds);
            }
        });
        presetTimesRadioGroup.addView(button);
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
        if (mTimerRunning) {
            //startTimerNotificationService();
            if (Build.VERSION.SDK_INT >= 26) {
                Intent intent = new Intent(this, TimerNotificationService.class);
                startForegroundService(intent);
            } else {
                startTimerNotificationService();
            }
        }
        if(mCountDownTimer != null){
            mCountDownTimer.cancel(); //cancel timer when time runs out
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        stopService(new Intent(this, TimerNotificationService.class));
        cancelNotification(444);

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
            }
        }
    }

    private void cancelNotification(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }
}