package com.raspberry.practicalparent.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Activity to allow users to edit, add, or delete kids
public class KidOptionsActivity extends AppCompatActivity {
    private KidManager kids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid_options);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); // Enable back button

        // The singleton holding all kids
        kids = KidManager.getInstance();

        // Floating action button used to add a kid
        FloatingActionButton fab = findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(KidOptionsActivity.this,
                        AddKidActivity.class);
                startActivity(addIntent);
                setupListView();
            }
        });

        setupListView();
        registerListClick();
    }

    // When returning from another activity or fragment
    // this will refresh the list of kids
    @Override
    public void onResume(){
        super.onResume();
        setupListView();
    }

    public void setupListView() {
        // The ListView to show the kids
        ListView listView = findViewById(R.id.childrenListView);

        ArrayAdapter<Kid> kidArrayAdapter = new KidListAdapter();
        listView.setAdapter(kidArrayAdapter);
    }

    private class KidListAdapter extends ArrayAdapter<Kid> {

        public KidListAdapter() {
            super(KidOptionsActivity.this, R.layout.kid_list_layout, kids.getList());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.kid_list_layout, parent, false);
            }
            Kid currKid = kids.getKidAt(position);

            ImageView imageView = itemView.findViewById(R.id.imgChildPic);
            String imgFileName = "/storage/emulated/0/saved_images/" + kids.getKidAt(position).getUri();
            File imgFile = new File(imgFileName);
            if (imgFile.exists()) {
                //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                //imageView.setImageBitmap(myBitmap);
                imageView.setImageDrawable(Drawable.createFromPath("/storage/emulated/0/saved_images/" + currKid.getUri()));
            }
            TextView taskCurrentChildName = itemView.findViewById(R.id.kidName);
            taskCurrentChildName.setText(currKid.getName());

            return itemView;
        }
    }


    // Clicking on a kid's name will bring up an AlertDialog that allows
    // the user to edit or delete the kid
    private void registerListClick() {
        ListView listView = findViewById(R.id.childrenListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TextView textView = (TextView)view;
                Bundle bundle = new Bundle();
                bundle.putString("Kid name", kids.getKidAt(position).getName());
                bundle.putInt("Kid index", position);
                FragmentManager manager = getSupportFragmentManager();
                EditFragment dialog = new EditFragment();
                dialog.setArguments(bundle);
                dialog.show(manager, "Launched edit fragment");

            }
        });
    }
}