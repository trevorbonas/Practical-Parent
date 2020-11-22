package com.raspberry.practicalparent.UI;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.app.Dialog;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.KidManager;

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
    private static Uri imageUri;
    private String path;
    private Button saveBtn;

    private ImageView imageView;

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

        imageView = v.findViewById(R.id.kidPicEditKid);

        path = kids.getKidAt(index).getPicPath();
        newName = kidName;

        MainActivity.displayPortrait(v.getContext(), path, imageView);

        Button cancelBtn = v.findViewById(R.id.cancelBtn);
        Button deleteBtn = v.findViewById(R.id.deleteBtn);
        Button captureBtn = v.findViewById(R.id.captureImageEditKid);
        Button chooseBtn = v.findViewById(R.id.chooseImageGalleryEditKid);

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = AddKidActivity.takePhotoUsingCamera(v.getContext());
                if (result == AddKidActivity.OPEN_CAMERA) {
                    openCameraFragment(v.getContext());
                }
                Log.d("Clicked Capture", "onClick: " + result);
            }
        });

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = AddKidActivity.selectPhotoFromDevice(v.getContext());
                if (result == AddKidActivity.OPEN_GALLERY) {
                    pickImageFromGalleryFragment();
                }
                Log.d("Clicked Choose", "onClick: " + result);
            }
        });

        saveBtn = v.findViewById(R.id.saveBtn);

        // Until name has been edited make it not enabled
        MainActivity.disableBtn(saveBtn, v.getContext());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Changing singleton
                kids.getKidAt(index).setName(newName);
                kids.getKidAt(index).setPicPath(path);
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
                dismiss(); // Closing fragment
            }
        });

        // Build the alert dialog
        Dialog d = builder.setView(v).setTitle("Select name to edit").create();

        return d;
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
        //now using TextWatcher

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                newName = name.getText().toString();
                if (!newName.trim().isEmpty()) {
                    MainActivity.enableBtn(saveBtn, v.getContext());
                } else {
                    MainActivity.disableBtn(saveBtn, v.getContext());
                }

            }
        });
    }

    private void openCameraFragment(Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, AddKidActivity.IMAGE_CAPTURE_CODE);
    }

    private void pickImageFromGalleryFragment() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, AddKidActivity.IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AddKidActivity.onRequestPermission(requestCode, grantResults, v.getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        path = AddKidActivity.onActivityResultGlobalFunction(requestCode, resultCode, data, imageView, v.getContext(), imageUri);
        if (!newName.trim().isEmpty()) {
            MainActivity.enableBtn(saveBtn, v.getContext());
        } else {
            MainActivity.disableBtn(saveBtn, v.getContext());
        }
    }
}