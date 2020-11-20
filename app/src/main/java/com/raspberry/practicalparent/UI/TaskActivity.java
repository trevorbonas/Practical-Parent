package com.raspberry.practicalparent.UI;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.Task;
import com.raspberry.practicalparent.model.TaskManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// Activity to allow users to edit, add, or delete kids
public class TaskActivity extends AppCompatActivity {
    private TaskManager tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); // Enable back button

        // The singleton holding all tasks
        tasks = TaskManager.getInstance();

        // Floating action button used to add a task
        FloatingActionButton fab = findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTaskIntent = new Intent(TaskActivity.this,
                        AddTaskActivity.class);
                startActivity(addTaskIntent);
                setupListView();
            }
        });

        setupListView();
        registerListClick();
    }

    // When returning from another activity or fragment
    // this will refresh the list of tasks
    @Override
    public void onResume(){
        super.onResume();
        setupListView();
    }

    public void setupListView() {
        // A list of the task names
        List<String> taskText = new ArrayList<String>();

        // The ListView to show the tasks
        ListView listView = findViewById(R.id.taskListView);

        // Adding all stored task names to the list
        for (int i = 0; i < tasks.getNum(); i++) {
            taskText.add(tasks.getTaskAt(i).getName() +
                    " " + tasks.getTaskAt(i).getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, taskText);
        listView.setAdapter(adapter);
    }

    // Clicking on a task will bring up an AlertDialog that allows
    // the user to edit or delete the task
    private void registerListClick() {
        ListView listView = findViewById(R.id.taskListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view;
                Bundle bundle = new Bundle();
                bundle.putString("Task name", tasks.getTaskAt(position).getName());
                bundle.putInt("Task index", position);
                FragmentManager manager = getSupportFragmentManager();
                EditTaskFragment dialog = new EditTaskFragment();
                dialog.setArguments(bundle);
                dialog.show(manager, "Launched edit task fragment");

            }
        });
    }
}