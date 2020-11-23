package com.raspberry.practicalparent.UI;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.Task;
import com.raspberry.practicalparent.model.TaskManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
public class AddKidActivity extends AppCompatActivity {

    public static Uri image_uri;
    private String path;
    private String kidsName; // The input kid's name
    private TaskManager taskManager = TaskManager.getInstance();
    ImageView mImageView;  //the image view
    Button mChooseBtn;  //button to choose the image
    Button mCaptureBtn; //button to capture the image
    public static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;//code for choose gallery
    private static final int PERMISSION_CODE_TAKE_PICTURE = 1002;//code for taking picture
    public static final int IMAGE_CAPTURE_CODE = 1003;

    public static final int OPEN_CAMERA = 1;
    public static final int OPEN_GALLERY = 2;
    public static final int NO_PERMISSIONS = -1;

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
                int result = takePhotoUsingCamera(AddKidActivity.this);
                if (result == OPEN_CAMERA) {
                    openCamera(AddKidActivity.this);
                }
            }
        });

        // Handle gallery button click
        mChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = selectPhotoFromDevice(AddKidActivity.this);
                if (result == OPEN_GALLERY) {
                    pickImageFromGallery(AddKidActivity.this);
                }
            }
        });
        // Instance of the singleton KidManager holding all the kids
        final KidManager kids = KidManager.getInstance();

        final EditText name = findViewById(R.id.inputKidName);
        final Button okayBtn = findViewById(R.id.okayBtn);
        //okayBtn.setEnabled(false);
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

                for (Task task : taskManager.getList()) {
                    task.updateKidName();
                }

                AddTaskActivity.saveTaskList(taskManager, AddKidActivity.this);

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

    public static int takePhotoUsingCamera(Context context) {
        //if system os is >= marshmellow, request runtime permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(context.checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED ){
                //permission not enabled, request it
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup to request permission
                ((Activity) context).requestPermissions(permission, PERMISSION_CODE_TAKE_PICTURE);
            }
            else {
                //permission already granted
                return OPEN_CAMERA;
            }
        }
        else {
            return OPEN_CAMERA;
            //system os < marshmallow
        }
        return NO_PERMISSIONS;
    }

    public static int selectPhotoFromDevice(Context context) {
        //check runtime permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED ||
                    context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                //permission not granted, request it.
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup for runtime permission
                ((Activity) context).requestPermissions(permissions, PERMISSION_CODE);
            }
            else {
                //permission already granted
                return OPEN_GALLERY;
            }
        }
        else {
            //system os is less than marshmallow
            return OPEN_GALLERY;
        }
        return NO_PERMISSIONS;
    }

    private static void openCamera(Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        ((Activity) context).startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    private static void pickImageFromGallery(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        ((Activity) context).startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //handle result of runtime permission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int result = onRequestPermission(requestCode, grantResults, this);
        if (result == OPEN_CAMERA) {
            openCamera(this);
        } else if (result == OPEN_GALLERY) {
            pickImageFromGallery(this);
        }
    }

    public static int onRequestPermission(int requestCode, int[] grantResults, Context context) {
        switch (requestCode) {
            case PERMISSION_CODE_TAKE_PICTURE: {
                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    return OPEN_CAMERA;
                }
                else {
                    //permission was denied
                    Toast.makeText(context, "Permission denied for taking picture", Toast.LENGTH_SHORT).show();
                    return NO_PERMISSIONS;
                }
            }
            case PERMISSION_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    return OPEN_GALLERY;
                }
                else {
                    //permission was denied
                    Toast.makeText(context, "Permission denied for picking image", Toast.LENGTH_SHORT).show();
                    return NO_PERMISSIONS;
                }
            }
        }
        return NO_PERMISSIONS;
    }

    //handle result of picked image
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        path = onActivityResultGlobalFunction(requestCode, resultCode, data, mImageView, this, image_uri);
    }

    public static String onActivityResultGlobalFunction(int requestCode, int resultCode, Intent data, ImageView imageView, Context context, Uri imageUri) {
        Random generator = new Random();
        Log.d("image: ", "Image");
        String savedPath = null;
        int n = 10000;
        n = generator.nextInt(n);
        String fileName = "Image-"+ n +".jpg";
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
            //set image to imageView
            imageView.setImageURI(imageUri);
            Bitmap image = null;
            try {
                String realPath = getRealPathFromURI(context, imageUri);
                ExifInterface exif = new ExifInterface(realPath);
                String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                Log.d("Orientation", "Image orientation is: " + orientation);

                // If photo is taken from normal camera its orientation will be 6
                if (orientation.equals("6")) {
                    image = rotate(MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                            imageUri), 90);
                }
                // If photo is taken from selfie camera its orientation will be 8
                else if (orientation.equals("8")) {
                    image = rotate(MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                            imageUri), 270);
                }
                // If photo is taken from front or back camera 90 degree right its
                // orientation will be 3
                else if (orientation.equals("3")) {
                    image = rotate(MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                            imageUri), 180);
                }
                else {
                    image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                }
                savedPath = saveImage(image, fileName, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to imageView
            imageView.setImageURI(data.getData());
            imageUri = data.getData();
            Bitmap image = null;
            try {
                String realPath = getRealPathFromURI(context, imageUri);
                ExifInterface exif = new ExifInterface(realPath);
                String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                Log.d("Orientation", "Image orientation is: " + orientation);

                // If photo is taken from normal camera its orientation will be 6
                if (orientation.equals("6")) {
                    image = rotate(MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                            imageUri), 90);
                }
                // If photo is taken from selfie camera its orientation will be 8
                else if (orientation.equals("8")) {
                    image = rotate(MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                            imageUri), 270);
                }
                // If photo is taken from front or back camera 90 degree right its
                // orientation will be 3
                else if (orientation.equals("3")) {
                    image = rotate(MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                            imageUri), 180);
                }
                else {
                    image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                }
                savedPath = saveImage(image, fileName, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return savedPath;
    }

    private static Bitmap rotate(Bitmap image, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(),
                image.getHeight(), matrix, true);
    }

    // Taken from the sweet sweet user minimanimo
    // https://stackoverflow.com/questions/13511356/android-image-selected-from-gallery-orientation-is-always-0-exif-tag
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static String saveImage(Bitmap bitmap, String fileName, Context context) throws IOException {
        OutputStream fos;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
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
        String savedPath = Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES).toString() + "/" + fileName;
        fos.close();
        return savedPath;
    }
}