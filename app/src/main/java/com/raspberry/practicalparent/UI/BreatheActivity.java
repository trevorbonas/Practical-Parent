package com.raspberry.practicalparent.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.raspberry.practicalparent.R;

import net.mabboud.android_tone_player.ContinuousBuzzer;

public class BreatheActivity extends AppCompatActivity {

    Button bigBtn;
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
        @Override
        void setup() {
            changeColor(R.drawable.round_button_in);
            changeText("In");
            handlePress();
        }

        @Override
        void handlePress() {
            if (time < 3) {
                //changeSize("in");
                changeColor(R.drawable.round_button_in);
                changeText("In");
                bigBtn.animate().scaleXBy(3.0f).scaleYBy(3.0f).setDuration(10000);
            } else {
                setState(out);
            }
        }

        @Override
        void handleOff() {
            if (time < 3) {
                //changeSize("Back to start");
                setState(start);
            } else {
                setState(out);
            }
        }
    }

    public class Out extends State {
        @Override
        void setup() {
            changeText("Out");
            changeColor(R.drawable.round_button_out);
            handlePress();
        }

        @Override
        void handlePress() {
            bigBtn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(10000);
        }

        @Override
        void handleOff() {
            super.handleOff();
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
            bigBtn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(1000);
        }
    }

    void changeText(String text) {
        assert text != null;
        bigBtn.setText(text);
    }

    void changeSize(String state) {
        assert state != null;
        int a;
        // Grow slowly
        if (state.equals("in")) {
            a = R.anim.grow;
        }
        // Shrink slowly
        else if (state.equals("out")) {
            a = R.anim.shrink;
        }
        // Shrink back quickly to starting position
        else {
            a = R.anim.shrink_fast;
        }
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), a);
        bigBtn.startAnimation(anim);
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