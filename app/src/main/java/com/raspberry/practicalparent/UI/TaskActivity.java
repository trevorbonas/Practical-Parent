package com.raspberry.practicalparent.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.gson.Gson;
import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.Task;
import com.raspberry.practicalparent.model.TaskManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// Activity to allow users to edit, add, or delete kids
public class TaskActivity extends AppCompatActivity {
    private TaskManager tasks;
    private KidManager kidManager = KidManager.getInstance();

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
        // The ListView to show the tasks
        ListView listView = findViewById(R.id.taskListView);


        ArrayAdapter<Task> taskArrayAdapter = new TaskListAdapter();
        listView.setAdapter(taskArrayAdapter);
        taskArrayAdapter.notifyDataSetChanged();
    }

    private class TaskListAdapter extends ArrayAdapter<Task> {

        public TaskListAdapter() {
            super(TaskActivity.this, R.layout.task_listview, tasks.getList());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.task_listview, parent, false);
            }
            Task currTask = tasks.getTaskAt(position);
            ImageView imageView = itemView.findViewById(R.id.imgChildLayout);

            if (kidManager.getNum() != 0) {
                MainActivity.displayPortrait(TaskActivity.this, kidManager.getKidAt(tasks.getTaskAt(position).getIndex()).getPicPath(), imageView);
            }
            TextView taskCurrentChildName = itemView.findViewById(R.id.taskCurrentChildLayout);
            TextView taskName = itemView.findViewById(R.id.taskNameLayout);
            taskCurrentChildName.setText(currTask.getKidName());
            taskName.setText(currTask.getName());

            return itemView;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveTaskManager(tasks, this);
    }

    // Clicking on a task will bring up an AlertDialog that allows
    // the user to edit or delete the task
    private void registerListClick() {
        ListView listView = findViewById(R.id.taskListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TextView textView = (TextView)view;
                Bundle bundle = new Bundle();
                bundle.putInt("Task index", position);
                FragmentManager manager = getSupportFragmentManager();
                EditTaskFragment dialog = new EditTaskFragment();
                dialog.setArguments(bundle);
                dialog.show(manager, "Launched edit task fragment");

            }
        });
    }

    public static void saveTaskManager(TaskManager taskManager, Context context) {
        // Saving TaskManager into SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("Tasks", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(taskManager.getList()); // Saving list
        prefEditor.putString("List", json);
        prefEditor.apply();
    }

    //Make intent
    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, TaskActivity.class);
    }
}