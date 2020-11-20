package com.raspberry.practicalparent.UI;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.KidManager;
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

// The activity to add a task to the application
// Initial assigned kid will be random
public class AddTaskActivity extends AppCompatActivity {
    private String taskName; // The input task name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); // Enable back button

        // Instance of the singleton TaskManager holding all the tasks
        final TaskManager tasks = TaskManager.getInstance();

        final EditText name = findViewById(R.id.inputTaskName);
        final Button okayBtn = findViewById(R.id.taskOkayBtn);
        MainActivity.disableBtn(okayBtn, this);

        // Save button
        // Upon clicking will add the input name into the list of tasks
        // and save to SharedPreferences the singleton variables
        /// and disable the save button until the text field is changed again
        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasks.addTask(taskName);

                // Saving KidManager into SharedPreferences
                SharedPreferences prefs = getSharedPreferences("Tasks", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(tasks.getList()); // Saving list
                prefEditor.putString("List", json);
                prefEditor.apply();

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
                taskName = name.getText().toString();
                if (!taskName.trim().isEmpty()) {
                    MainActivity.enableBtn(okayBtn, AddTaskActivity.this);
                } else {
                    MainActivity.disableBtn(okayBtn, AddTaskActivity.this);
                }
            }
        });
    }
}