package com.raspberry.practicalparent.UI;

import android.content.Context;
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
    private String taskDescription;

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
        final EditText description = findViewById(R.id.inputTaskDescription);
        final Button okayBtn = findViewById(R.id.taskOkayBtn);

        MainActivity.disableBtn(okayBtn, this);

        // Save button
        // Upon clicking will add the input name into the list of tasks
        // and save to SharedPreferences the singleton variables
        /// and disable the save button until the text field is changed again
        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasks.addTask(taskName, taskDescription);

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
                taskDescription = description.getText().toString();
                setButtonEnabledIfStringsNonNull(taskName, taskDescription, okayBtn, AddTaskActivity.this);
            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                taskName = name.getText().toString();
                taskDescription = description.getText().toString();
                setButtonEnabledIfStringsNonNull(taskName, taskDescription, okayBtn, AddTaskActivity.this);
            }
        });
    }

    public static void setButtonEnabledIfStringsNonNull(String textName, String textDescription, Button button, Context context) {
        if (!textName.trim().isEmpty() && !textDescription.trim().isEmpty()) {
            MainActivity.enableBtn(button, context);
        } else {
            MainActivity.disableBtn(button, context);
        }
    }
}