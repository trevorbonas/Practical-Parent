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
    private String kidName;
    ResultsManager history = ResultsManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyText = findViewById(R.id.tv_history_name);
        toggleBtn = findViewById(R.id.btn_toggle_history);

        createRecyclerView();
        extractName();
        setUpPersonalHistory();
        registerClickCallback();
    }

    //Set up RecyclerView
    private void createRecyclerView() {

        //Setting up RecyclerView
        rv = findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        layoutManger = new LinearLayoutManager(this);
        adapter = new CardAdapter(cardList);

        rv.setLayoutManager(layoutManger);
        rv.setAdapter(adapter);
    }

    //Get child name from intent
    private void extractName() {
        Intent passedIntent = getIntent();
        this.kidName = passedIntent.getStringExtra("Kid name");
    }

    //Set up text for personal flip history calls populatePersonal
    private void setUpPersonalHistory() {

        //Set on screen text
        historyText.setText(kidName + "'s flips");
        toggleBtn.setText("All flips");
        updateText();

        populatePersonal(history, kidName);
    }

    //Set up text for all flips history calls populateAll
    private void setUpAllHistory() {

        historyText.setText("All flips");
        toggleBtn.setText(kidName + "'s flips");
        updateText();

        // Simply pass in the singleton
        populateAll(history);
    }

    //Populates personal flip history calls addToHistory
    private void populatePersonal(ResultsManager manager, String name) {

        clearRecyclerView();

        //Populate list
        for (Results flip: manager) {
            if (flip.getChildName().equals(name)) {
                addToHistory(flip);
            }
        }
    }

    //Populate all flips history calls addToHistory
    private void populateAll(ResultsManager manager) {

        clearRecyclerView();

        //Populate list
        for (Results flip: manager) {
            addToHistory(flip);
        }
    }

    //Add flip result to RecyclerView
    private void addToHistory(Results flip) {

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

    //Update text on history toggle
    private void updateText() {
        historyText.invalidate();
        toggleBtn.invalidate();
    }

    //Clear RecyclerView
    private void clearRecyclerView() {
        cardList.clear();
        adapter.notifyDataSetChanged();
    }

    //Register click for toggling history
    private void registerClickCallback() {
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!toggledAll) {
                    Toast.makeText(HistoryActivity.this, "Now showing all flips", Toast.LENGTH_SHORT).show();
                    toggledAll = true;
                    setUpAllHistory();
                }
                else {
                    Toast.makeText(HistoryActivity.this, "Now showing child's flips", Toast.LENGTH_SHORT).show();
                    toggledAll = false;
                    setUpPersonalHistory();
                }
            }
        });
    }

    //Make intent
    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, HistoryActivity.class);
    }

}