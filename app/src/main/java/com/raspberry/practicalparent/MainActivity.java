package com.raspberry.practicalparent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.Results;
import com.raspberry.practicalparent.model.ResultsManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

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

        // Load the two singletons with info if there is info to load
        setupKidManager();
        setupResultsManager();

        Button coinBtn = findViewById(R.id.flipBtn);
        coinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent coinFlipIntent;
                KidManager kids = KidManager.getInstance();
                if (kids.getNum() == 0) {
                    coinFlipIntent = new Intent(MainActivity.this,
                            CoinFlipActivity.class);
                }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}