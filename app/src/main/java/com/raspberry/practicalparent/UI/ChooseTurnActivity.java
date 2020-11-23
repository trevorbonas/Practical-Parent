package com.raspberry.practicalparent.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;

import java.util.ArrayList;
import java.util.List;

public class ChooseTurnActivity extends AppCompatActivity {

    private KidManager kids = KidManager.getInstance();
    private List<Kid> kidList = new ArrayList<>();
    private ArrayAdapter<Kid> kidArrayAdapter;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_turn);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                Intent nobodyIntent = new Intent(ChooseTurnActivity.this,
                        CoinFlipActivity.class);
                nobodyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(nobodyIntent);
                Intent intent = new Intent("finish");
                sendBroadcast(intent);
                finish();
            }
        });
    }



    private void addItemsToKidList() {
        kidList.clear();
        for (int i = 0; i < kids.getNum(); i++) {
            Kid kid = kids.getKidAt(index);
            kidList.add(kid);
            index = (index + 1) % (kids.getNum());
        }
        index = kids.getCurrentIndex(); // Reset index
    }

    public void setupListView() {
        // The ListView to show the kids
        ListView listView = findViewById(R.id.namesListView);

        if (kids.getNum() > 0) {
            index = kids.getCurrentIndex();
        }
        addItemsToKidList();
        kidArrayAdapter = new KidListAdapter(this, kidList);
        listView.setAdapter(kidArrayAdapter);
        kidArrayAdapter.notifyDataSetChanged();
    }

    private class KidListAdapter extends ArrayAdapter<Kid> {

        public KidListAdapter(Context context, List<Kid> kidListAdapter) {
            super(context, R.layout.kid_list_layout, kidListAdapter);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.kid_list_layout, parent, false);
            }

            Kid currKid = kidList.get(position);

            ImageView imageView = itemView.findViewById(R.id.imgChildPic);
            MainActivity.displayPortrait(ChooseTurnActivity.this,
                    currKid.getPicPath(), imageView);

            TextView kidName = itemView.findViewById(R.id.kidName);
            kidName.setText(currKid.getName());

            return itemView;
        }
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