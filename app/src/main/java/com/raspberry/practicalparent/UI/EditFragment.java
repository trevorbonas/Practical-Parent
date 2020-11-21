package com.raspberry.practicalparent.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.app.Dialog;
import android.text.InputType;
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
import com.raspberry.practicalparent.model.Task;
import com.raspberry.practicalparent.model.TaskManager;

// Edit fragment pops up in KidOptionsActivity
// upon clicking on a kid from the list
// Allows editing of name, deletion, or simply exiting the fragment
public class EditFragment extends AppCompatDialogFragment {
    private int index; // Index of child in ListView passed in through a bundle
    private String kidName; // Current name of child in ListView passed in through a bundle
    private EditText name; // The EditText field of the child's name
    private String newName; // The changed name of the child
    private View v; // Current view
    private KidManager kids = KidManager.getInstance(); // An instance of the singleton
    Button saveBtn;
    private TaskManager taskManager = TaskManager.getInstance();

    @Override
    @Nullable
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.edit_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        // Getting all information passed in by KidOptionsActivity
        Bundle bundle = this.getArguments();
        this.index = bundle.getInt("Kid index");
        kidName = bundle.getString("Kid name");

        Button cancelBtn = v.findViewById(R.id.cancelBtn);
        Button deleteBtn = v.findViewById(R.id.deleteBtn);
        saveBtn = v.findViewById(R.id.saveBtn);

        // Until name has been edited make it not enabled
        MainActivity.disableBtn(saveBtn, v.getContext());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Changing singleton
                kids.getKidAt(index).setName(newName);
                name.clearFocus();

                // Saving KidManager into SharedPreferences
                SharedPreferences prefs = getActivity()
                        .getSharedPreferences("Kids", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(kids.getList()); // Saving list
                prefEditor.putString("List", json);
                json = gson.toJson(kids.getCurrentIndex()); // Saving list
                prefEditor.putString("Index", json); // Saving current index
                prefEditor.apply();

                // Refreshing activity list
                ((KidOptionsActivity)getActivity()).setupListView();
                adjustTaskChildName();
                dismiss();
            }
        });

        setupInputField();

        // Back button
        // Pressing this dismisses the fragment
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Delete button
        // Deletes the child
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kids.deleteKid(index);

                // Saving KidManager into SharedPreferences
                SharedPreferences prefs = getActivity()
                        .getSharedPreferences("Kids", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(kids.getList()); // Saving list
                prefEditor.putString("List", json);
                json = gson.toJson(kids.getCurrentIndex()); // Saving list
                prefEditor.putString("Index", json); // Saving current index
                prefEditor.apply();

                // Refreshing the ListView in KidOptionsActivity
                ((KidOptionsActivity)getActivity()).setupListView();

                adjustTaskManagerToAccountForDeletingChild();

                dismiss(); // Closing fragment
            }
        });

        // Build the alert dialog
        Dialog d = builder.setView(v).setTitle("Select name to edit").create();

        return d;
    }

    private void adjustTaskManagerToAccountForDeletingChild() {
        for (Task task : taskManager.getList()) {
            if (task.getIndex() >= index) {
                task.next();
            }
        }
    }

    private void adjustTaskChildName() {
        for (Task task : taskManager.getList()) {
            task.updateKidName();
        }
    }

    // Sets up the EditText to receive input
    // If the user edits the name and presses the checkmark on their
    // keyboard it will change the kid's name and save this to SharedPreferences
    private void setupInputField() {
        name = v.findViewById(R.id.childName);
        name.setText(kidName);
        name.setEnabled(true);
        name.setInputType(InputType.TYPE_CLASS_TEXT);

        // Changes made to EditText
        // in edit kid
        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch(actionId){
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                    case EditorInfo.IME_ACTION_PREVIOUS: // Meaning checkmark has been pressed
                        newName = name.getText().toString();
                        // If the input name is empty or just a space
                        // Don't enable saving
                        if (newName.length() == 0 || newName.charAt(0) == ' ') {
                            MainActivity.disableBtn(saveBtn, v.getContext());
                        }
                        else {
                           MainActivity.enableBtn(saveBtn, v.getContext());
                        }
                        return true;
                }
                return true;
            }
        });
    }
}