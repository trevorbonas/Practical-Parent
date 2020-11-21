package com.raspberry.practicalparent.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.Results;
import com.raspberry.practicalparent.model.ResultsManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Main menu and launching activity
 *
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnTimeoutTimer = (Button) findViewById(R.id.timeoutBtn);
        btnTimeoutTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityTimer();
            }
        });

        // Load the two singletons with info if there is info to load
        setupKidManager();
        setupResultsManager();

        ImageView test = findViewById(R.id.testImage);

        KidManager kids = KidManager.getInstance();
        Kid current = null;
        if (kids.getNum() > 0) {
            current = kids.getKidAt(kids.getCurrentIndex());
        }
        if (current != null && current.getUri() != null) {
            Glide.with(this)
                    .load(new File(current.getUri()) )
                    .into(test);
        }
        else {
            test.setBackground(ContextCompat.getDrawable(MainActivity.this,
                    R.drawable.calm_imagejpg));
        }

        // Button to go to coin flipping activity
        Button coinBtn = findViewById(R.id.flipBtn);
        coinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent coinFlipIntent;
                KidManager kids = KidManager.getInstance();
                // If there are no kids pressing the coin flip button
                // will take the user straight to the coin flipping activity
                // without heads or tails chosen
                if (kids.getNum() == 0) {
                    coinFlipIntent = new Intent(MainActivity.this,
                            CoinFlipActivity.class);
                }
                // If there are kids then pressing the coin flip button will take
                // the user to a screen for choosing heads or tails, then on to
                // the coin flipping activity
                else {
                    coinFlipIntent = new Intent(MainActivity.this,
                            ChooseActivity.class);
                }
                startActivity(coinFlipIntent);
            }
        });

        Button kidBtn = findViewById(R.id.kidsBtn);
        kidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent optionsIntent = new Intent(MainActivity.this,
                        KidOptionsActivity.class);
                startActivity(optionsIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    // Loads KidManager singleton from saved SharedPreferences
    private void setupKidManager() {
        KidManager kids = KidManager.getInstance(); // Just used to edit the singleton

        SharedPreferences prefs = getSharedPreferences("Kids", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("List", "");
        if (json.length() > 0) {
            Type listType = new TypeToken<ArrayList<Kid>>(){}.getType();
            List<Kid> list = gson.fromJson(json, listType);
            kids.setList(list);
            json = prefs.getString("Index", "");
            int index = gson.fromJson(json, Integer.class);
            kids.changeKid(index);
        }
    }

    private void openActivityTimer() {
        Intent intent = TimerActivity.makeIntent(this);
        startActivity(intent);
    }

    // Loads ResultsManager data which is used to display user history
    private void setupResultsManager() {
        ResultsManager history = ResultsManager.getInstance(); // Just used to edit the singleton

        SharedPreferences prefs = getSharedPreferences("History", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("List", "");
        if (json.length() > 0) {
            Type listType = new TypeToken<ArrayList<Results>>(){}.getType();
            List<Results> list = gson.fromJson(json, listType);
            history.setList(list);
        }
    }

    // Static function that can be called by any activity to make a button of its
    // is disabled both in appearance and functionality
    static public void disableBtn(Button btn, Context context) {
        btn.setEnabled(false);
        btn.setBackground(ContextCompat.getDrawable(context,
                R.drawable.disabled_button));
        btn.setTextColor(context.getResources().getColor(R.color.buttonDisabled,
                context.getTheme()));
    }

    // Static function that can be called by any activity to make a button of its
    // is enabled
    static public void enableBtn(Button btn, Context context) {
        btn.setEnabled(true);
        btn.setBackground(ContextCompat.getDrawable(context,
                R.drawable.outlined_button));
        btn.setTextColor(context.getResources().getColor(R.color.buttonTxt,
                context.getTheme()));
    }
}
