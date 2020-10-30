package com.raspberry.practicalparent;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.raspberry.practicalparent.model.KidManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddKidActivity extends AppCompatActivity {
    private String kidsName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kid);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        final KidManager kids = KidManager.getInstance();

        final EditText name = findViewById(R.id.inputKidName);
        final Button okayBtn = findViewById(R.id.okayBtn);
        okayBtn.setEnabled(false);

        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kids.addKid(kidsName);

                // Saving KidManager into SharedPreferences
                SharedPreferences prefs = getSharedPreferences("Kids", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(kids.getList()); // Saving list
                prefEditor.putString("List", json);
                json = gson.toJson(kids.getCurrentIndex()); // Saving list
                prefEditor.putString("Index", json); // Saving current index
                prefEditor.apply();

                Toast.makeText(AddKidActivity.this,
                        kidsName + " added", Toast.LENGTH_SHORT).show();
                okayBtn.setEnabled(false);
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                kidsName = name.getText().toString();
                okayBtn.setEnabled(true);
            }
        });
    }
}