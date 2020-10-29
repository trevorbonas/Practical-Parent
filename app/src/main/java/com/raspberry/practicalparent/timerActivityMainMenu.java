package com.raspberry.practicalparent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class timerActivityMainMenu extends AppCompatActivity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_main_menu);
        button = (Button) findViewById(R.id.btnTimer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimer();
            }
        });
    }
    private void openTimer() {
        Intent intent = new Intent(this, timerActivity.class);
        startActivity(intent);
    }
}