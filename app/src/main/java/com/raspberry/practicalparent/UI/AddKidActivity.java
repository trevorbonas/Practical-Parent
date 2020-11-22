package com.raspberry.practicalparent.UI;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.Task;
import com.raspberry.practicalparent.model.TaskManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// The activity to add a kid to the application
// launched from KidOptions
public class AddKidActivity extends AppCompatActivity {
    private String kidsName; // The input kid's name
    private TaskManager taskManager = TaskManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kid);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); // Enable back button

        // Instance of the singleton KidManager holding all the kids
        final KidManager kids = KidManager.getInstance();

        final EditText name = findViewById(R.id.inputKidName);
        final Button okayBtn = findViewById(R.id.okayBtn);
        //okayBtn.setEnabled(false);
        MainActivity.disableBtn(okayBtn, this);

        // Save button
        // Upon clicking will add the input name into the list of kids
        // and save to SharedPreferences the singleton variables
        /// and disable the save button until the text field is changed again
        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kids.addKid(kidsName);
                for (Task task : taskManager.getList()) {
                    task.updateKidName();
                }

                AddTaskActivity.saveTaskList(taskManager, AddKidActivity.this);

                MainActivity.saveKidManager(AddKidActivity.this);

                finish();
            }
        });

        // Watches the EditText in order to give the user the option of saving
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
                if (!kidsName.trim().isEmpty()) {
                    MainActivity.enableBtn(okayBtn, AddKidActivity.this);
                } else {
                    MainActivity.disableBtn(okayBtn, AddKidActivity.this);
                }
            }
        });
    }
}