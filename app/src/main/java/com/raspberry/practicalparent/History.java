package com.raspberry.practicalparent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ArrayList<CardViewMaker> cardList = new ArrayList<>();
        cardList.add(new CardViewMaker(R.drawable.ic_launcher_background, "name", "win/loss", "date", "side"));


        rv = findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        layoutManger = new LinearLayoutManager(this);
        adapter = new CardAdapter(cardList);

        rv.setLayoutManager(layoutManger);
        rv.setAdapter(adapter);
    }

    private void populateHistory() {

    }

    public static Intent makeLaunchIntent(Context context) {
        Intent intent = new Intent(context, History.class);
        return intent;
    }
}