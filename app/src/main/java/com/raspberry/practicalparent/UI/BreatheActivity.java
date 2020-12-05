package com.raspberry.practicalparent.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.raspberry.practicalparent.R;

public class BreatheActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
@SuppressLint("ClickableViewAccessibility")
    // Used to synchronize pressed states, i.e., make sure when button state "up" it was
    // first in state "down"
    int pressedState = 0;
    Button bigBtn;
    TextView helpTxt;
    Button startBtn;
    TextView breathTxt;
    int numBreaths = 1; // Default number of breaths is 3
    In in = new In();
    Out out = new Out();
    Start start = new Start();
    State currentState = in;
    int time; // Time button pressed, set to zero when button in starting state
    private Spinner breathDropdown;
    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_breathe);

        //Spinner/dropdown menu
        setUpDropdown();

        bigBtn = findViewById(R.id.bigBtn);
        helpTxt = findViewById(R.id.helpTxt);
        breathTxt = findViewById(R.id.breathTxt);

        helpTxt.setText(R.string.helptxt);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        changeText(getString(R.string.begin));
        changeColor(R.drawable.round_button);

        bigBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (pressedState == 0) {
                            pressedState = 1;
                            Log.d("Touch", "Action down");
                            currentState.handlePress();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (pressedState == 1) {
                            pressedState = 2;
                            Log.d("Touch", "Action move");
                            currentState.handlePress();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // Button is allowed to make currentState do handleOff
                        // if button has been pressed down first
                        if (pressedState == 2 || pressedState == 1) {
                            pressedState = 0;
                            Log.d("Touch", "Action up");
                            currentState.handleOff();
                        }
                        break;
                    default:
                        Log.d("Touch", "Default. Action id is " + event.getAction());
                        break;
                }
                return false;
            }
        });
    }

    /**
     * Menu for number of breaths
     */
    private void setUpDropdown() {
        breathDropdown = findViewById(R.id.spinner);
        Integer[] breaths = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, breaths);
        breathDropdown.setAdapter(adapter);
        numBreaths = getBreaths(this);
        breathDropdown.setSelection(numBreaths - 1); //position in array
        breathDropdown.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        numBreaths = (int) adapterView.getItemAtPosition(i);
        saveBreaths(numBreaths);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void saveBreaths(int breaths) {
        SharedPreferences prefs = this.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("breaths", breaths);
        editor.apply();
    }

    private int getBreaths(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt("breaths", 3);
    }

    private void updateBreathTxt() {
        breathTxt.setText("Breaths left: " + numBreaths);
        breathTxt.invalidate();
    }

    private void removeBreathTxt() {
        breathTxt.setText("");
        breathTxt.invalidate();
    }

    /**
     * States
     */
    private abstract class State {
        // Do everything needed to setup the button
        void setup() {}
        // Self explanatory
        void handlePress() {}
        // Handle the press stopping
        void handleOff() {}
        // Cancels whatever the object was doing
        void cancel() {}
    }

    public class In extends State {
        long startTime;
        long checkTime;
        Handler inHandler = new Handler();
        Runnable disableRnb = new Runnable() {
            @Override
            public void run() {
                bigBtn.setEnabled(true);
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                helpTxt.setText(R.string.release);
                inHandler.removeCallbacksAndMessages(null);
            }
        };
        @Override
        void setup() {
            changeColor(R.drawable.round_button_in);
            changeText(getString(R.string.in));
        }

        @Override
        void handlePress() {
            breathDropdown.setVisibility(View.GONE);
            updateBreathTxt();
            if (player == null) {
                player = MediaPlayer.create(BreatheActivity.this, R.raw.in_sound);
            }
            player.start();
            helpTxt.setText(R.string.press_and_hold);
            startTime = System.nanoTime();
            inHandler.postDelayed(runnable, 10000);
            changeColor(R.drawable.round_button_in);
            changeText(getString(R.string.in));
            bigBtn.animate().scaleX(2.5f).scaleY(2.5f).setDuration(10000);

            Log.d("In", "In is now handling press");
        }

        @Override
        void handleOff() {
            long elapsedTime = System.nanoTime() - startTime;
            double seconds = (double)elapsedTime / 1_000_000_000.0;
            inHandler.removeCallbacksAndMessages(null);
            if (player != null) {
                player.release();
                player = null;
            }
            Log.d("Time", "Elapsed seconds: " + seconds);
            if (seconds < 3.0) {
                // Shrink back down so user can try again
                bigBtn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(700);
                bigBtn.setEnabled(false);
                inHandler.postDelayed(disableRnb, 700);
            } else {
                setState(out);
            }
        }

        @Override
        void cancel() {
            bigBtn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(0);
            inHandler.removeCallbacksAndMessages(null);
        }
    }

    public class Out extends State {
        long startTime;
        Handler outHandler = new Handler();
        Runnable tenRunnable = new Runnable() {
            @Override
            public void run() {
                // TODO Update of displayed number of breaths
                if (player != null) {
                    player.release();
                    player = null;
                }
                setupBigBtn();
            }
        };
        Runnable threeRunnable = new Runnable() {
            @Override
            public void run() {
                numBreaths--;
                updateBreathTxt();
                if (numBreaths > 0) {
                    changeText(getString(R.string.in));
                    changeColor(R.drawable.round_button_in);
                }
                else {
                    changeText(getString(R.string.good_job));
                    changeColor(R.drawable.round_button);
                    bigBtn.setEnabled(false);
                    helpTxt.setText(R.string.breaths_completed);
                }
            }
        };
        @Override
        void setup() {
            changeText(getString(R.string.out));
            changeColor(R.drawable.round_button_out);
            startTime = System.nanoTime();
            bigBtn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(10000);
            if (player == null) {
                player = MediaPlayer.create(BreatheActivity.this, R.raw.out_sound);
            }
            player.start();
            outHandler.postDelayed(threeRunnable, 3000);
            outHandler.postDelayed(tenRunnable, 10000);
            helpTxt.setText(R.string.breathe_out);
        }

        @Override
        void handlePress() {
            long elapsedTime = System.nanoTime() - startTime;
            double seconds = (double)elapsedTime / 1_000_000_000.0;
            if (seconds >= 3) {
                // TODO Update of displayed number of breaths
                if (player != null) {
                    player.release();
                    player = null;
                }
                setState(in);
                currentState.handlePress();
                outHandler.removeCallbacksAndMessages(null);
            }
        }
        @Override
        void handleOff() {
            // Do nothing
        }
        @Override
        void cancel() {
            bigBtn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(0);
            outHandler.removeCallbacksAndMessages(null);
        }
    }

    public class Start extends State {
        @Override
        void setup() {
            changeText(getString(R.string.begin));
            changeColor(R.drawable.round_button);
        }
        @Override
        void handlePress() {
            if (numBreaths > 0) {
                setState(in);
            }
        }
        @Override
        void handleOff() {
            // Do nothing
        }
    }

    void changeText(String text) {
        assert text != null;
        bigBtn.setText(text);
    }

    void setupBigBtn() {
        if (numBreaths > 0) {
            setState(in);
            helpTxt.setText(R.string.press_and_hold);
        }
        else {
            changeColor(R.drawable.round_button);
            bigBtn.setText(R.string.good_job);
            helpTxt.setText(R.string.breaths_completed);
            bigBtn.setEnabled(false);
            removeBreathTxt();
        }
    }

    void changeColor(int color) {
        bigBtn.setBackground(ContextCompat.getDrawable(BreatheActivity.this,
                color));
    }

    void setState(State state) {
        assert currentState != null;
        currentState = state;
        currentState.setup();
    }

    //Make intent
    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, BreatheActivity.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // If the Up button is pressed this ends the sound
        switch (item.getItemId()) {
            case android.R.id.home:
                if (player != null) {
                    player.release();
                    player = null;
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Absolutely makes sure everything goes back to start if
    // user presses back button then re-starts activity
    @Override
    public void onBackPressed() {
        finish();
    }

    // This will reset things if the user goes to another
    // application
    @Override
    protected void onResume() {
        // TODO: Update display of breaths
        numBreaths = 3;
        setState(in);
        changeColor(R.drawable.round_button);
        helpTxt.setText(R.string.helptxt);
        changeText(getString(R.string.begin));
        bigBtn.setEnabled(true);
        breathDropdown.setVisibility(View.VISIBLE);
        removeBreathTxt();
        setUpDropdown();
        super.onResume();
    }

    // This will end the sound if app is navigated away from
    @Override
    protected void onPause() {
        if (player != null) {
            player.release();
            player = null;
        }
        currentState.cancel();
        super.onPause();
    }
}


