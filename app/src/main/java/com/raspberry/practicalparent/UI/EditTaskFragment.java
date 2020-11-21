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

        Button cancelBtn = v.findViewById(R.id.taskCancelBtn);
        Button deleteBtn = v.findViewById(R.id.taskDeleteBtn);
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
        //TODO change title to take from strings.xml
        Dialog d = builder.setView(v).setTitle("Task").create();

        return d;
    }

    private void updateKidName() {
        TextView kidText = v.findViewById(R.id.kidTurnName);
        TaskManager tasks = TaskManager.getInstance();
        kidText.setText(tasks.getTaskAt(index).getKidName());
    }

    // Sets up the EditText to receive input
    // If the user edits the name and presses the checkmark on their
    // keyboard it will change the task's name and save this to SharedPreferences
    private void setupInputField() {
        taskNameText = v.findViewById(R.id.taskName);
        taskNameText.setText(taskName);
        taskNameText.setEnabled(true);
        taskNameText.setInputType(InputType.TYPE_CLASS_TEXT);

        taskDescriptionText = v.findViewById(R.id.taskDescription);
        taskDescriptionText.setText(taskDescription);
        taskDescriptionText.setEnabled(true);
        //taskDescriptionText.setInputType(InputType.TYPE_CLASS_TEXT);

        // Changes made to EditText
        // in edit task
        //Change to TextWatcher

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


//        taskNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                switch(actionId){
//                    case EditorInfo.IME_ACTION_DONE:
//                    case EditorInfo.IME_ACTION_NEXT:
//                    case EditorInfo.IME_ACTION_PREVIOUS: // Meaning checkmark has been pressed
//                        newTaskName = taskNameText.getText().toString();
//                        // If the input name is empty or just a space
//                        // Don't enable saving
//                        if (newTaskName.length() == 0 || newTaskName.charAt(0) == ' ') {
//                            MainActivity.disableBtn(saveBtn, v.getContext());
//                        }
//                        else {
//                            MainActivity.enableBtn(saveBtn, v.getContext());
//                        }
//                        return true;
//                }
//                return true;
//            }
//        });
    }
}