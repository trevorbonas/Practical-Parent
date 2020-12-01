package com.raspberry.practicalparent.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.raspberry.practicalparent.R;

import net.mabboud.android_tone_player.ContinuousBuzzer;

import java.util.Timer;

public class BreatheActivity extends AppCompatActivity {
    Button bigBtn;
    TextView helpTxt;
    Button startBtn;
    int numBreaths = 3; // Default number of breaths is 3
    In in = new In();
    Out out = new Out();
    Start start = new Start();
    State currentState = start;
    ContinuousBuzzer tonePlayer = new ContinuousBuzzer();
    int time; // Time button pressed, set to zero when button in starting state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_breathe);
        bigBtn = findViewById(R.id.bigBtn);
        helpTxt = findViewById(R.id.helpTxt);
        startBtn = findViewById(R.id.startBtn);

        bigBtn.setEnabled(false);
        helpTxt.setText("Select number of desired breaths,\npress the start button to start");
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numBreaths > 0) {
                    bigBtn.setEnabled(true);
                    MainActivity.disableBtn(startBtn, BreatheActivity.this);
                    setupBigBtn();
                }
            }
        });

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        bigBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    currentState.handlePress();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    currentState.handleOff();

                }
                return false;
            }
        });
    }

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
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                helpTxt.setText("Release button and breathe out");
            }
        };
        @Override
        void setup() {
            changeColor(R.drawable.round_button_in);
            changeText("In");
        }

        @Override
        void handlePress() {
            startTime = System.nanoTime();
            timeHandler.postDelayed(runnable, 10000);
            changeColor(R.drawable.round_button_in);
            changeText("In");
            bigBtn.animate().scaleXBy(2.5f).scaleYBy(2.5f).setDuration(10000);
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
            } else {
                setState(out);
            }
        }
    }

    public class Out extends State {
        Handler outHandler = new Handler();
        Runnable outRunnable = new Runnable() {
            @Override
            public void run() {
                setupBigBtn();
            }
        };
        @Override
        void setup() {
            changeText("Out");
            changeColor(R.drawable.round_button_out);
            bigBtn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(3000);
            numBreaths--;
            // TODO Update of displayed number of breaths
            outHandler.postDelayed(outRunnable, 3000);
        }

        @Override
        void handlePress() {
            // Do nothing
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
            handleOff();
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

    void setHelpTxt(String text) {
        helpTxt.setText(text);
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