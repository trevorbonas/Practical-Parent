package com.raspberry.practicalparent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class KidOptionsActivity extends AppCompatActivity {
    private KidManager kids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid_options);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // All of this would be done in MainActivity
        // upon launch
        setupKidManager();

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

    private void setupKidManager() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("Kids", "");
        kids = gson.fromJson(json, KidManager.class);
        if (kids == null) {
            kids = KidManager.getInstance();
        }
    }

    public void setupListView() {
        KidManager kids = KidManager.getInstance();

        List<String> kidText = new ArrayList<String>();

        ListView listView = findViewById(R.id.childrenListView);

        for (int i = 0; i < kids.getNum(); i++) {
            Kid kid = kids.getKidAt(i);
            kidText.add(kid.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, kidText);
        listView.setAdapter(adapter);
    }

    private void registerListClick() {
        final KidManager kids = KidManager.getInstance();
        ListView listView = findViewById(R.id.childrenListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view;
                Bundle bundle = new Bundle();
                bundle.putString("Kid name", kids.getKidAt(position).getName());
                FragmentManager manager = getSupportFragmentManager();
                EditFragment dialog = new EditFragment();
                dialog.setArguments(bundle);
                dialog.show(manager, "Launched edit fragment");

            }
        });
    }
}