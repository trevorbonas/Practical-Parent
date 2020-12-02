package com.raspberry.practicalparent.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.raspberry.practicalparent.R;

import net.mabboud.android_tone_player.ContinuousBuzzer;

import java.util.Timer;

public class BreatheActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
@SuppressLint("ClickableViewAccessibility")
    // Used to synchronize pressed states, i.e., make sure when button state "up" it was
    // first in state "down"
    int pressedState = 0;
    Button bigBtn;
    TextView helpTxt;
    Button startBtn;
    int numBreaths = 1; // Default number of breaths is 3
    In in = new In();
    Out out = new Out();
    Start start = new Start();
    State currentState = start;
    int time; // Time button pressed, set to zero when button in starting state
    private Spinner breathDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_breathe);

        //Spinner/dropdown menu
        setUpDropdown();

        bigBtn = findViewById(R.id.bigBtn);
        helpTxt = findViewById(R.id.helpTxt);

        helpTxt.setText("Select number of desired breaths\nPress the button to start");

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

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
        breathDropdown.setSelection(2); //default
        breathDropdown.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        numBreaths = (int) adapterView.getItemAtPosition(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
    }

    public class In extends State {
        long startTime;
        long checkTime;
        Handler timeHandler = new Handler();
        Runnable disableRnb = new Runnable() {
            @Override
            public void run() {
                bigBtn.setEnabled(true);
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                helpTxt.setText("Release button and breathe out");
                timeHandler.removeCallbacksAndMessages(null);
            }
        };
        @Override
        void setup() {
            changeColor(R.drawable.round_button_in);
            changeText("In");
        }

        @Override
        void handlePress() {
            breathDropdown.setVisibility(View.GONE);

            helpTxt.setText("Press and hold the button and breathe in");
            startTime = System.nanoTime();
            timeHandler.postDelayed(runnable, 10000);
            changeColor(R.drawable.round_button_in);
            changeText("In");
            bigBtn.animate().scaleX(2.3f).scaleY(2.3f).setDuration(10000);
            Log.d("In", "In is now handling press");
        }

        @Override
        void handleOff() {
            long elapsedTime = System.nanoTime() - startTime;
            double seconds = (double)elapsedTime / 1_000_000_000.0;
            timeHandler.removeCallbacksAndMessages(null);
            Log.d("Time", "Elapsed seconds: " + seconds);
            if (seconds < 3.0) {
                // Shrink back down so user can try again
                bigBtn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(700);
                bigBtn.setEnabled(false);
                timeHandler.postDelayed(disableRnb, 700);
            } else {
                setState(out);
            }
        }
    }

    public class Out extends State {
        long startTime;
        Handler outHandler = new Handler();
        Runnable tenRunnable = new Runnable() {
            @Override
            public void run() {
                // TODO Update of displayed number of breaths
                setupBigBtn();
            }
        };
        Runnable threeRunnable = new Runnable() {
            @Override
            public void run() {
                numBreaths--;
                if (numBreaths > 0) {
                    changeText("In");
                    changeColor(R.drawable.round_button_in);
                }
                else {
                    changeText("Good job");
                    changeColor(R.drawable.round_button);
                    bigBtn.setEnabled(false);
                    helpTxt.setText("All breaths completed");
                }
            }
        };
        @Override
        void setup() {
            changeText("Out");
            changeColor(R.drawable.round_button_out);
            startTime = System.nanoTime();
            bigBtn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(10000);
            outHandler.postDelayed(threeRunnable, 3000);
            outHandler.postDelayed(tenRunnable, 10000);
        }

        @Override
        void handlePress() {
            long elapsedTime = System.nanoTime() - startTime;
            double seconds = (double)elapsedTime / 1_000_000_000.0;
            if (seconds >= 3) {
                // TODO Update of displayed number of breaths
                setState(in);
                currentState.handlePress();
                outHandler.removeCallbacksAndMessages(null);
            }
        }

        @Override
        void handleOff() {
            // Do nothing
        }
    }

    public class Start extends State {
        @Override
        void setup() {
            changeText("Begin");
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
            helpTxt.setText("Press and hold the button and breathe in");
        }
        else {
            changeColor(R.drawable.round_button);
            bigBtn.setText("Good job");
            helpTxt.setText("All breaths completed");
            bigBtn.setEnabled(false);
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
}