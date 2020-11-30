package com.raspberry.practicalparent.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.TimerNotificationClasses.TimerCompleteNotificationBroadcastReceiver;
import com.raspberry.practicalparent.TimerNotificationClasses.TimerNotificationService;

import java.util.Locale;
/**
* Timer class that displays the clock and updates the ticker
* inspired by https://www.youtube.com/watch?v=7dQJAkjNEjM
 */
public class TimerActivity extends AppCompatActivity {

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
        return new Intent(context, TimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                        removeCalmImage();
                    } else {
                        startTimer();
                        showCalmImage();
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

    private void showCalmImage() {
        ConstraintLayout constraintLayout = findViewById(R.id.activity_timer_constraint_layout);
        constraintLayout.setBackgroundResource(R.drawable.calm_imagejpg);
    }

    private void removeCalmImage() {
        ConstraintLayout constraintLayout = findViewById(R.id.activity_timer_constraint_layout);
        constraintLayout.setBackgroundResource(0);
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
                Intent timerComplete = new Intent(TimerActivity.this, TimerCompleteNotificationBroadcastReceiver.class);
                timerComplete.setAction(getString(R.string.intent_action_timer_finished_from_activity));
                sendBroadcast(timerComplete);
                removeCalmImage();
            }
        }.start();

        mTimerRunning = true;
        updateWatchInterface();
    }

    private void startTimerNotificationService() {
        Intent intent = new Intent(this, TimerNotificationService.class);
        startService(intent);
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
            mButtonStartPause.setText(R.string.pause);
        } else {
            mEditTextInput.setVisibility(View.VISIBLE);
            presetTimesRadioGroup.setVisibility(View.VISIBLE);
            mButtonStartPause.setText(R.string.start);

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
            button.setText(getResources().getQuantityString(R.plurals.radio_buttons_text_in_minutes, minutes, minutes));
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
    }

    //for timer to run in background
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(getString(R.string.shared_preferences_start_time_in_millis), mStartTimeInMillis);
        editor.putLong(getString(R.string.shared_preferences_time_left_in_millis), mTimeLeftInMillis);
        editor.putBoolean(getString(R.string.shared_preferences_timer_running), mTimerRunning);
        editor.putLong(getString(R.string.shared_preferences_end_time), mEndTime);

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
        mStartTimeInMillis = prefs.getLong(getString(R.string.shared_preferences_start_time_in_millis), 600000);
        mTimeLeftInMillis = prefs.getLong(getString(R.string.shared_preferences_time_left_in_millis), mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean(getString(R.string.shared_preferences_timer_running), false);

        updateCountDownText();
        updateWatchInterface();

        if (mTimerRunning){
            mEndTime = prefs.getLong(getString(R.string.shared_preferences_end_time), 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            showCalmImage();

            //check if overdue
            if(mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateWatchInterface();  //make buttons invisible
                removeCalmImage();
            } else {
                startTimer();
            }
        } else {
            removeCalmImage();
        }
    }

    private void cancelNotification(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    //Make intent
    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, TimerActivity.class);
    }
}