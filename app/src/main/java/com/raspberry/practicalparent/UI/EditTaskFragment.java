package com.raspberry.practicalparent.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.app.Dialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.TaskManager;

// Edit fragment pops up in TaskActivity
// upon clicking on a task from the list
// Allows editing of name, deletion, or simply exiting the fragment
public class EditTaskFragment extends AppCompatDialogFragment {
    private int index; // Index of child in ListView passed in through a bundle
    private String taskName; // Name of the task
    private String taskDescription;
    private EditText taskNameText; // The EditText field of the task name
    private EditText taskDescriptionText;
    private String newTaskDescription;
    private String newTaskName; // The changed name of the task
    private View v; // Current view
    private TaskManager tasks = TaskManager.getInstance(); // An instance of the singleton
    private KidManager kidManager = KidManager.getInstance();
    Button saveBtn;

    @Override
    @Nullable
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.edit_task_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        // Getting all information passed in by KidOptionsActivity
        Bundle bundle = this.getArguments();
        // Index in the listView
        this.index = bundle.getInt("Task index");
        taskName = tasks.getTaskAt(index).getName();
        taskDescription = tasks.getTaskAt(index).getDescription();

        ImageView imageView = v.findViewById(R.id.kidPic);
        Button cancelBtn = v.findViewById(R.id.taskCancelBtn);
        Button deleteBtn = v.findViewById(R.id.taskDeleteBtn);
        if (kidManager.getNum() != 0) {
            imageView.setImageResource(0);
            MainActivity.displayPortrait(v.getContext(), kidManager.getKidAt(tasks.getTaskAt(index).getIndex()).getPicPath(), imageView);
        }
        saveBtn = v.findViewById(R.id.taskSaveBtn);
        Button nextBtn = v.findViewById(R.id.completedBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasks.getTaskAt(index).next(); // Change to the next kid's turn
                updateKidName();

                ((TaskActivity)getActivity()).setupListView();
                TaskActivity.saveTaskManager(tasks, v.getContext());
                dismiss();
            }
        });

        // Until name has been edited make it not enabled
        MainActivity.disableBtn(saveBtn, v.getContext());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Changing singleton
                tasks.getTaskAt(index).setName(newTaskName);
                tasks.getTaskAt(index).setDescription(newTaskDescription);
                taskNameText.clearFocus();

                // Saving TaskManager into SharedPreferences
                TaskActivity.saveTaskManager(tasks, getActivity());

                // Refreshing activity list
                ((TaskActivity)getActivity()).setupListView();
                dismiss();
            }
        });

        setupInputField();

        updateKidName();

        // Back button
        // Pressing this dismisses the fragment
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Refreshing activity list
                ((TaskActivity)getActivity()).setupListView();
                dismiss();
            }
        });

        // Delete button
        // Deletes the child
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasks.deleteTask(index);

                // Saving KidManager into SharedPreferences
                SharedPreferences prefs = getActivity()
                        .getSharedPreferences("Tasks", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(tasks.getList()); // Saving list
                prefEditor.putString("List", json);
                prefEditor.apply();

                // Refreshing the ListView in KidOptionsActivity
                ((TaskActivity)getActivity()).setupListView();
                dismiss(); // Closing fragment
            }
        });

        // Build the alert dialog
        Dialog d = builder.setView(v).setTitle(getString(R.string.task)).create();

        return d;
    }

    private void updateKidName() {
        TextView kidText = v.findViewById(R.id.kidTurnName);
        TaskManager tasks = TaskManager.getInstance();
        kidText.setText(tasks.getTaskAt(index).getKidName());
    }

    // Sets up the EditText to receive input
    private void setupInputField() {
        taskNameText = v.findViewById(R.id.taskName);
        taskNameText.setText(taskName);
        taskNameText.setEnabled(true);
        taskNameText.setInputType(InputType.TYPE_CLASS_TEXT);

        taskDescriptionText = v.findViewById(R.id.taskDescription);
        taskDescriptionText.setText(taskDescription);
        taskDescriptionText.setEnabled(true);

        // Changes made to EditText
        // in edit task

        taskNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newTaskName = taskNameText.getText().toString();
                newTaskDescription = taskDescriptionText.getText().toString();
                AddTaskActivity.setButtonEnabledIfStringsNonNull(newTaskName, newTaskDescription, saveBtn, v.getContext());
            }
        });

        taskDescriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newTaskName = taskNameText.getText().toString();
                newTaskDescription = taskDescriptionText.getText().toString();
                AddTaskActivity.setButtonEnabledIfStringsNonNull(newTaskName, newTaskDescription, saveBtn, v.getContext());
            }
        });
    }
}