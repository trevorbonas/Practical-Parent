package com.raspberry.practicalparent.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.raspberry.practicalparent.R;

public class BreatheActivity extends AppCompatActivity {

    Button bigBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_breathe);
        bigBtn = findViewById(R.id.bigBtn);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

   /* private abstract class State {
        void start() {}
    }*/

    void changeText(String text) {
        //fab.text
    }

    //Make intent
    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, BreatheActivity.class);
    }
}