package com.raspberry.practicalparent;

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

import com.raspberry.practicalparent.model.CardViewMaker;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.Results;
import com.raspberry.practicalparent.model.ResultsManager;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManger;
    private ArrayList<CardViewMaker> cardList = new ArrayList<>();
    private TextView historyText;
    private Button toggleBtn;
    private boolean toggledAll = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //TODO: Examples to be removed
        cardList.add(0, new CardViewMaker(R.drawable.ic_check, "NAME", "WIN", "DATE", "SIDE"));
        cardList.add(0, new CardViewMaker(R.drawable.ic_x, "NAME", "LOST", "DATE", "SIDE"));

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
        KidManager manager = KidManager.getInstance();
        int kidPosition = manager.getCurrentIndex();
        return manager.getKidAt(kidPosition);
    }

    //TODO: populate kid's history (this is the default when viewing history)
    private void populateKidHistory() {

        //TODO: remove when populaterecyclerview works
        clearRecyclerView();

        //Get child information

        //Kid k = extractChild();
        //String name = k.getName();

        //Set on screen text
        //historyText.setText(name + "'S FLIPS");
        historyText.setText("KID'S FLIPS");
        toggleBtn.setText("ALL FLIPS");
        updateText();

        //Create child flips history
        //ResultsManager childHistory = k.getResults();
        //populateRecyclerView(childHistory);

        //TODO: example to be removed
        for (int i = 0; i < 4; i++) {
            cardList.add(0, new CardViewMaker(R.drawable.ic_x, "NAME", "LOST", "DATE", "SIDE"));
            adapter.notifyItemInserted(0);
        }
    }


    private void showAllHistory() {

        //TODO: remove when populaterecyclerview works
        clearRecyclerView();

        //String kidName = extractChild().getName();
        historyText.setText("ALL FLIPS");
        toggleBtn.setText("KID'S FLIPS");
        //toggleBtn.setText(kidName + "'s FLIPS");
        updateText();

        //Create all flips history
        //ResultsManager allHistory =
        //populateRecyclerView(allHistory);

        //TODO: Example to be removed
        for (int i = 0; i < 2; i++) {
            cardList.add(0, new CardViewMaker(R.drawable.ic_check, "NAME", "WIN", "DATE", "SIDE"));
            adapter.notifyItemInserted(0);
        }
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
                    Toast.makeText(History.this, "Now showing all flips", Toast.LENGTH_SHORT).show();
                    toggledAll = true;
                    showAllHistory();
                }
                else {
                    Toast.makeText(History.this, "Now showing child's flips", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(context, History.class);
        return intent;
    }

}