package com.raspberry.practicalparent.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.raspberry.practicalparent.R;

import net.mabboud.android_tone_player.ContinuousBuzzer;

import java.io.IOException;
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

        helpTxt.setText("Select number of desired breaths\nPress the button to start");

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        changeText("Begin");
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
                player.setLooping(true);
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
            player = MediaPlayer.create(BreatheActivity.this, R.raw.in_sound);
            player.start();
            helpTxt.setText("Press and hold the button and breathe in");
            startTime = System.nanoTime();
            timeHandler.postDelayed(runnable, 10000);
            changeColor(R.drawable.round_button_in);
            changeText("In");
            bigBtn.animate().scaleX(2.5f).scaleY(2.5f).setDuration(10000);

            Log.d("In", "In is now handling press");
        }

        @Override
        void handleOff() {
            long elapsedTime = System.nanoTime() - startTime;
            double seconds = (double)elapsedTime / 1_000_000_000.0;
            timeHandler.removeCallbacksAndMessages(null);
            player.stop();
            player.reset();
            player.release();
            player = null;
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
            player = MediaPlayer.create(BreatheActivity.this, R.raw.out_sound);
            player.start();
            outHandler.postDelayed(threeRunnable, 3000);
            outHandler.postDelayed(tenRunnable, 10000);
            helpTxt.setText("Breathe out");
        }

        @Override
        void handlePress() {
            long elapsedTime = System.nanoTime() - startTime;
            double seconds = (double)elapsedTime / 1_000_000_000.0;
            if (seconds >= 3) {
                // TODO Update of displayed number of breaths
                player.stop();
                player.reset();
                player.release();
                player = null;
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