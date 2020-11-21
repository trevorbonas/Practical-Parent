package com.raspberry.practicalparent.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.KidManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Allows user to choose heads or tails and sends choice to CoinFlipActivity
 */

public class ChooseActivity extends AppCompatActivity {
    private KidManager kids = KidManager.getInstance();
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); // Enable back button

        setGreeting();

        Button heads = findViewById(R.id.headBtn);
        Button tails = findViewById(R.id.tailsBtn);
        Button choose = findViewById(R.id.changeBtn);

        heads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent headIntent = new Intent(ChooseActivity.this,
                        CoinFlipActivity.class);
                String value = "Heads";
                if (!kids.isNobody()) {
                    headIntent.putExtra("Choice", value);
                }
                startActivity(headIntent);
                finish(); // A finish so we can't go back to this activity in CoinFlip
            }
        });

        tails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tailsIntent = new Intent(ChooseActivity.this,
                        CoinFlipActivity.class);
                String value = "Tails";
                if (!kids.isNobody()) {
                    tailsIntent.putExtra("Choice", value);
                }
                startActivity(tailsIntent);
                finish(); // A finish so we can't go back to this activity in CoinFlip
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tailsIntent = new Intent(ChooseActivity.this,
                        ChooseTurnActivity.class);
                startActivity(tailsIntent);
            }
        });


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void setGreeting() {
        TextView greeting = findViewById(R.id.greetingTxt);

        // Safety
        if (kids.getNum() <= 0) {
            greeting.setText(R.string.choose_coin_heads_tails_no_children);
        }
        else {
            greeting.setText(getString(R.string.choose_coin_heads_tails_children,
                    kids.getName()));
        }
    }

    // When returning from another activity or fragment
    // this will refresh the greeting
    @Override
    public void onResume(){
        super.onResume();
        setGreeting();
    }

}