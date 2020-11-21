package com.raspberry.practicalparent.UI;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
        final Intent nobodyIntent = new Intent(ChooseTurnActivity.this,
                CoinFlipActivity.class);
        nobodyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // We have come to this activity from ChooseActivity
        if (!kids.isNobody()) {
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true); // Enable back button
        }

        setupListView();
        registerListClick();

        Button nobody = findViewById(R.id.nobdyBtn);

        nobody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kids.setNobody(true);
                MainActivity.saveKidManager(ChooseTurnActivity.this);
                startActivity(nobodyIntent);
                finish();
            }
        });
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

        index = kids.getCurrentIndex(); // Reset index

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, kidText);
        listView.setAdapter(adapter);
    }

    private void registerListClick() {
        ListView listView = findViewById(R.id.namesListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (kids.isNobody()) {
                    kids.changeKid((index + position) % (kids.getNum()) );
                    Log.println(Log.DEBUG, "Check new current index",
                            "New KidManager index: " + kids.getCurrentIndex());
                    MainActivity.saveKidManager(ChooseTurnActivity.this);

                    Intent chooseIntent = new Intent(ChooseTurnActivity.this,
                            ChooseActivity.class);
                    chooseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(chooseIntent);
                    finish();
                }
                else {
                    kids.changeKid((index + position) % (kids.getNum()) );
                    Log.println(Log.DEBUG, "Check new current index",
                            "New KidManager index: " + kids.getCurrentIndex());
                    MainActivity.saveKidManager(ChooseTurnActivity.this);
                    finish();
                }
            }
        });
    }
}