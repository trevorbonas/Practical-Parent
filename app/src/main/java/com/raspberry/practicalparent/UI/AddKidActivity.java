package com.raspberry.practicalparent.UI;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.gson.Gson;
import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.KidManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Random;

// The activity to add a kid to the application
// launched from KidOptions
//contains code to intake picture from camera and take picture
public class AddKidActivity extends AppCompatActivity {

    private String kidsName; // The input kid's name
    ImageView mImageView;  //the image view
    Button mChooseBtn;  //button to choose the image
    Button mCaptureBtn; //button to capture the image
    Uri image_uri;      //uri of the taken image
    private String path;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;//code for choose gallery
    private static final int PERMISSION_CODE_TAKE_PICTURE = 1002;//code for taking picture
    private static final int IMAGE_CAPTURE_CODE = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kid);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); // Enable back button

        //Views
        mImageView = findViewById(R.id.image_view);
        mChooseBtn = findViewById(R.id.choose_image_btn);
        mCaptureBtn = findViewById(R.id.capture_image_btn);


        //handle take image button click
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if system os is >= marshmellow, request runtime permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED ){
                        //permission not enabled, request it
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup to request permission
                        requestPermissions(permission, PERMISSION_CODE_TAKE_PICTURE);
                    }
                    else {
                        //permission already granted
                        openCamera();
                    }
                }
                else {
                    openCamera();
                    //system os < marshmallow
                }
            }
        });

        // Handle gallery button click
        mChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check runtime permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                        //permission not granted, request it.
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else {
                    //system os is less than marshmallow
                    pickImageFromGallery();
                }
            }
        });
        // Instance of the singleton KidManager holding all the kids
        final KidManager kids = KidManager.getInstance();

        final EditText name = findViewById(R.id.inputKidName);
        final Button okayBtn = findViewById(R.id.okayBtn);
        MainActivity.disableBtn(okayBtn, this);

        // Save button
        // Upon clicking will add the input name into the list of kids
        // and save to SharedPreferences the singleton variables
        /// and disable the save button until the text field is changed again
        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kids.addKid(kidsName);
                if (path != null) {
                    kids.getKidAt(kids.getNum() - 1).setPicPath(path);
                    Log.println(Log.DEBUG, "path check",
                            "path is: " + path);
                } else if (path == null) {
                    Log.println(Log.DEBUG, "path check",
                            "path IS null");
                }
                // Saving KidManager into SharedPreferences
                SharedPreferences prefs = getSharedPreferences("Kids", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(kids.getList()); // Saving list
                prefEditor.putString("List", json);
                json = gson.toJson(kids.getCurrentIndex()); // Saving list
                prefEditor.putString("Index", json); // Saving current index
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
                kidsName = name.getText().toString();
                if (!kidsName.trim().isEmpty()) {
                    MainActivity.enableBtn(okayBtn, AddKidActivity.this);
                } else {
                    MainActivity.disableBtn(okayBtn, AddKidActivity.this);
                }
            }
        });
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //handle result of runtime permission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE_TAKE_PICTURE: {
                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera();
                    break;
                }
                else {
                    //permission was denied
                   Toast.makeText(this, "Permission denied for taking picture", Toast.LENGTH_SHORT).show();
                   break;
                }
            }
            case PERMISSION_CODE: {

                if( grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    pickImageFromGallery();
                    break;
                }
                else {
                    //permission was denied
                    Toast.makeText(this, "Permission denied for picking image", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    //handle result of picked image
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fileName = "Image-"+ n +".jpg";
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
            //set image to imageView
            mImageView.setImageURI(image_uri);
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
                saveImage(image, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to imageView
            mImageView.setImageURI(data.getData());
            Uri imageUri = data.getData();
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                saveImage(image, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*private void saveImage(Bitmap image) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        if (myDir.mkdirs()) {
            Log.println(Log.DEBUG, "Directory creation", "Directory created");
        } else {
            Log.println(Log.DEBUG, "Directory creation", "Directory NOT created");
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fileName = "Image-"+ n +".jpg";
        path = myDir.toString() + "/" + fileName; // Path that will be added to kid when saved
        File file = new File (myDir, fileName);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    } */

    private void saveImage(Bitmap bitmap, String fileName) throws IOException {
        OutputStream fos;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,
                    fileName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues);
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri) );
        } else {
            String dir = Environment.getExternalStoragePublicDirectory(Environment
                    .DIRECTORY_PICTURES).toString();
            File image = new File(dir, fileName);
            fos = new FileOutputStream(image);
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        path = Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES).toString() + "/" + fileName;
        fos.close();
    }
}