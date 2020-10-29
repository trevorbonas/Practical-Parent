package com.raspberry.practicalparent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManger;
    private ArrayList<CardViewMaker> cardList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //Examples
        cardList.add(new CardViewMaker(R.drawable.ic_check, "NAME", "WIN", "DATE", "SIDE"));
        cardList.add(new CardViewMaker(R.drawable.ic_x, "NAME", "LOSS", "DATE", "SIDE"));

        //populateKidHistory();

        //Setting up RecyclerView
        rv = findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        layoutManger = new LinearLayoutManager(this);
        adapter = new CardAdapter(cardList);

        rv.setLayoutManager(layoutManger);
        rv.setAdapter(adapter);
    }

    //TODO: Extract kid class from shared prefs
/*    private Kid extractChild(Context context) {
        int kidPosition = extractDataFromIntent();
        //if sharedprefs for kidsmanager
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        KidManager manager = KidManger.getInstance();
        return manager.get(kidPosition);
    }*/

    //TODO: populate kid's history
   /* private void populateKidHistory() {
        //TODO: after Trevor finishes results class and kids class
        Kid k = extractChild();
        TextView historyText = findViewById(R.id.tv_history_name);
        Button toggleBtn = findViewById(R.id.btn_toggle_history);
        historyText.setText(k.getName() + "'S FLIPS");
        toggleBtn.setText("ALL FLIPS");
        Results res = k.getResults();
        for (int record: res) {
            Boolean won = record.getWin();
            int image = R.drawable.ic_x;
            if (won == true) {
                image = R.drawable.ic_check;
            }
            cardList.add(new CardViewMaker(image, record.getName(), won, record.getDate(), record.getSide()));
        }
    }*/

/*   //register click for toggling history
    private void registerClickCallback() {
        Button btn = (Button) findViewById(R.id.btn_toggle_history);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllHistory();
            }
        });
    }*/

/*    //TODO: show all history
    private void showAllHistory() {
        String kidName = extractChild().getName();
        TextView historyText = findViewById(R.id.tv_history_name);
        Button toggleBtn = findViewById(R.id.btn_toggle_history);
        historyText.setText("ALL FLIPS");
        toggleBtn.setText(kidName + "'s FLIPS");
    }*/

    //make intent
    public static Intent makeLaunchIntent(Context context) {
        Intent intent = new Intent(context, History.class);
        return intent;
    }

/*    //TODO: extract kid position for kidmanager
    private int extractDataFromIntent() {
        Intent intent = getIntent();
        return intent.getIntExtra(POSITION, -1);
    }*/
}