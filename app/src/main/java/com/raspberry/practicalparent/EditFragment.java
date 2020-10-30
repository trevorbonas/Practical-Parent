package com.raspberry.practicalparent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.gson.Gson;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;

public class EditFragment extends AppCompatDialogFragment {
    private int index;
    private String kidName;
    private EditText name;
    private View v;
    private KidManager kids = KidManager.getInstance();

    @Override
    @Nullable
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.edit_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        Bundle bundle = this.getArguments();
        this.index = bundle.getInt("Index");
        kidName = bundle.getString("Kid name");

        Button cancelBtn = v.findViewById(R.id.cancelBtn);
        Button deleteBtn = v.findViewById(R.id.deleteBtn);

        setupInputField();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


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

                ((KidOptionsActivity)getActivity()).setupListView();
                dismiss();
            }
        });

        // Build the alert dialog

        Dialog d = builder.setView(v).create();

        return d;

        /*return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();*/
    }

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
                    case EditorInfo.IME_ACTION_PREVIOUS:
                        String newName = name.getText().toString();

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
                        dismiss();
                        return true;
                }
                return true;
            }
        });
    }
}