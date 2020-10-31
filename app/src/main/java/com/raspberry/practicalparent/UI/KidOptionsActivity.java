package com.raspberry.practicalparent.UI;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;

import androidx.appcompat.app.ActionBar;
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

        kids = KidManager.getInstance();

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

    @Override
    public void onResume(){
        super.onResume();
        setupListView();
    }

    public void setupListView() {
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
        ListView listView = findViewById(R.id.childrenListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view;
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