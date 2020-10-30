package com.raspberry.practicalparent;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;

public class EditFragment extends AppCompatDialogFragment {

    private int index;

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.edit_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Create the view to show
        Bundle bundle = this.getArguments();
        final KidManager kids = KidManager.getInstance();
        final EditText name = v.findViewById(R.id.childName);
        final String kidName = bundle.getString("Kid name");
        this.index = bundle.getInt("Index");
        name.setText(kidName);
        name.setEnabled(false);
        name.setInputType(InputType.TYPE_NULL);

        Button cancelBtn = v.findViewById(R.id.cancelBtn);
        Button editBtn = v.findViewById(R.id.editBtn);
        Button deleteBtn = v.findViewById(R.id.deleteBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(true);
                name.setInputType(InputType.TYPE_CLASS_TEXT);
                name.setSelected(true);
                name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        switch(actionId){
                            case EditorInfo.IME_ACTION_DONE:
                            case EditorInfo.IME_ACTION_NEXT:
                            case EditorInfo.IME_ACTION_PREVIOUS:
                                String newName = name.getText().toString();
                                kids.getKidAt(index).setName(newName);
                                ((KidOptionsActivity)getActivity()).setupListView();
                                dismiss();
                                return true;
                        }
                        return true;
                    }
                });
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kids.deleteKid(index);
                ((KidOptionsActivity)getActivity()).setupListView();
                dismiss();
            }
        });

        // Build the alert dialog

        Dialog d = builder.setView(v).create();
        d.getWindow().setSoftInputMode(WindowManager
                .LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return d;

        /*return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();*/
    }
}