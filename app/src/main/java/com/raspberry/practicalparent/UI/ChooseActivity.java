package com.raspberry.practicalparent.UI;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); // Enable back button

        KidManager kids = KidManager.getInstance();

        TextView greeting = findViewById(R.id.greetingTxt);

        if (kids.getNum() <= 0) {
            greeting.setText(R.string.choose_coin_heads_tails_no_children);
        }
        else {
            greeting.setText(getString(R.string.choose_coin_heads_tails_children, kids.getKidAt(kids.getCurrentIndex()).getName()));
        }

        Button heads = findViewById(R.id.headBtn);
        Button tails = findViewById(R.id.tailsBtn);

        heads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent headIntent = new Intent(ChooseActivity.this,
                        CoinFlipActivity.class);
                String value = "Heads";
                headIntent.putExtra("Choice", value);
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
                tailsIntent.putExtra("Choice", value);
                startActivity(tailsIntent);
                finish(); // A finish so we can't go back to this activity in CoinFlip
            }
        });


    }
}