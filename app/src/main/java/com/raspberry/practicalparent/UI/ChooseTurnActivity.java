package com.raspberry.practicalparent.UI;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;

import java.util.ArrayList;
import java.util.List;

public class ChooseTurnActivity extends AppCompatActivity {

    private KidManager kids = KidManager.getInstance();
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_turn);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); // Enable back button

        setupListView();
        //registerListClick();
    }

    public void setupListView() {
        // A list of the kids' names
        List<String> kidText = new ArrayList<String>();

        // The ListView to show the kids
        ListView listView = findViewById(R.id.namesListView);

        if (kids.getNum() > 0) {
            index = kids.getCurrentIndex();
        }

        // Adding all stored kids' names to the list
        for (int i = 0; i < kids.getNum(); i++) {
            Kid kid = kids.getKidAt(index);
            kidText.add(kid.getName());
            index = (index + 1) % (kids.getNum());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, kidText);
        listView.setAdapter(adapter);
    }

    /*private void registerListClick() {
        ListView listView = findViewById(R.id.childrenListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
    }*/
}