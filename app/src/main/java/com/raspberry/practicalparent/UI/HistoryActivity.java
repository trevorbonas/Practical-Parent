package com.raspberry.practicalparent.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.CardViewMaker;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.Results;
import com.raspberry.practicalparent.model.ResultsManager;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManger;
    private ArrayList<CardViewMaker> cardList = new ArrayList<>();
    private TextView historyText;
    private Button toggleBtn;
    private boolean toggledAll = false;

    // Things for KidManager
    // Passed in index for the kid who just played
    private String kidName;
    KidManager kids = KidManager.getInstance();

    ResultsManager history = ResultsManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent passedIntent = getIntent();
        this.kidName = passedIntent.getStringExtra("Kid name");

        historyText = findViewById(R.id.tv_history_name);
        toggleBtn = findViewById(R.id.btn_toggle_history);

        createRecyclerView();
        populateKidHistory();
        registerClickCallback();
    }

    private void createRecyclerView() {
        //Setting up RecyclerView
        rv = findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        layoutManger = new LinearLayoutManager(this);
        adapter = new CardAdapter(cardList);

        rv.setLayoutManager(layoutManger);
        rv.setAdapter(adapter);
    }

    //Get child class from manager
    private Kid extractChild() {
        int kidPosition = kids.getCurrentIndex();
        return kids.getKidAt(kidPosition);
    }

    //TODO: populate kid's history (this is the default when viewing history)
    private void populateKidHistory() {

        //TODO: remove when populaterecyclerview works
        clearRecyclerView();

        //Set on screen text
        historyText.setText(kidName + "'S FLIPS");
        toggleBtn.setText("ALL FLIPS");
        updateText();

        populatePersonalRecyclerView(history, kidName);
    }

    private void populatePersonalRecyclerView(ResultsManager manager, String name) {
        clearRecyclerView();
        //Populate list
        for (Results flip: manager) {
            if (flip.getChildName().equals(name)) {

                //Image is lost flip by default
                int image = R.drawable.ic_x;
                Boolean won = flip.isWonFlip();
                String wonOrLost = "LOST";

                //Check if they won flip
                if (won) {
                    image = R.drawable.ic_check;
                    wonOrLost = "WON";
                }
                //Add flip to list
                cardList.add(0, new CardViewMaker(image, flip.getChildName(), wonOrLost, flip.getDateFlip(), flip.getSideChosen()));
                adapter.notifyItemInserted(0);
            }
        }
    }

    private void showAllHistory() {

        //TODO: remove when populaterecyclerview works
        clearRecyclerView();

        historyText.setText("ALL FLIPS");
        toggleBtn.setText(kidName + "'s FLIPS");
        updateText();

        // Simply pass in the singleton
        populateRecyclerView(history);
    }

    //Update text on toggle
    private void updateText() {
        historyText.invalidate();
        toggleBtn.invalidate();
    }

    //Register click for toggling history
    private void registerClickCallback() {
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!toggledAll) {
                    Toast.makeText(HistoryActivity.this, "Now showing all flips", Toast.LENGTH_SHORT).show();
                    toggledAll = true;
                    showAllHistory();
                }
                else {
                    Toast.makeText(HistoryActivity.this, "Now showing child's flips", Toast.LENGTH_SHORT).show();
                    toggledAll = false;
                    populateKidHistory();
                }
            }
        });
    }

    //Populate RecyclerView
    private void populateRecyclerView(ResultsManager manager) {

        clearRecyclerView();

        //Populate list
        for (Results flip: manager) {

            //Image is lost flip by default
            int image = R.drawable.ic_x;
            Boolean won = flip.isWonFlip();
            String wonOrLost = "LOST";

            //Check if they won flip
            if (won) {
                image = R.drawable.ic_check;
                wonOrLost = "WON";
            }
            //Add flip to list
            cardList.add(0, new CardViewMaker(image, flip.getChildName(), wonOrLost, flip.getDateFlip(), flip.getSideChosen()));
            adapter.notifyItemInserted(0);
        }
    }

    //Clear RecyclerView
    private void clearRecyclerView() {
        cardList.clear();
        adapter.notifyDataSetChanged();
    }

    //Make intent
    public static Intent makeLaunchIntent(Context context) {
        Intent intent = new Intent(context, HistoryActivity.class);
        return intent;
    }

}