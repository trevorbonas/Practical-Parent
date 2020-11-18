package com.raspberry.practicalparent.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import com.raspberry.practicalparent.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setupCreditButtons();
    }

    //Hyperlinks image credits
    private void setupCreditButtons() {
        Button btnCoin = (Button) findViewById(R.id.btn_credit_coin);
        Button btnCalm = (Button) findViewById(R.id.btn_credit_calm);
        Button btnIcon = (Button) findViewById(R.id.btn_credit_icon);
        Button btnMenu = (Button) findViewById(R.id.btn_credit_menu);

        btnCoin.setOnClickListener(OnClickListener);
        btnCalm.setOnClickListener(OnClickListener);
        btnIcon.setOnClickListener(OnClickListener);
        btnMenu.setOnClickListener(OnClickListener);
    }

    View.OnClickListener OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.btn_credit_coin:
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://en.wikipedia.org/wiki/Nickel_(Canadian_coin)"));
                    startActivity(intent);
                    break;
                case R.id.btn_credit_calm:
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/images/Images/Image-4.jpg"));
                    startActivity(intent);
                    break;
                case R.id.btn_credit_icon:
                    //TODO:
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.pinterest.ca/pin/367465650825732546/"));
                    startActivity(intent);
                    break;
                case R.id.btn_credit_menu:
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://pixabay.com/vectors/boy-child-dad-daughter-family-2025099/"));
                    startActivity(intent);
                    break;
            }
        }
    };

    //Make intent
    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, HelpActivity.class);
    }
}