package com.raspberry.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.raspberry.practicalparent.model.Kid;
import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.Results;
import com.raspberry.practicalparent.model.ResultsManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

//xml animations adapted from https://www.youtube.com/watch?v=DnXWcGmLHHs


public class CoinFlipActivity extends AppCompatActivity {
    //0 for heads, 1 for tails
    private int intCurrentFace = 0;

    // Choice passed in from ChooseActivity
    // Could have done it as an int but I like the
    // forwardness of reading "heads" or "tails"
    private String choice;

    private KidManager kids; // Singleton
    private ResultsManager history; // Singleton
    private String kidName; // Needs to be class variable since current name may change

    private DateFormat df = new SimpleDateFormat("EEE, MMM. d, yyyy"); // Format for date
    private String date = df.format(Calendar.getInstance().getTime()); // Current date

    private ImageView currFace;
    private ImageView otherFace;

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, CoinFlipActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); // Enable back button

        kids = KidManager.getInstance();
        history = ResultsManager.getInstance();

        TextView nameTxt = findViewById(R.id.childFlipName);
        Button historyBtn = findViewById(R.id.historyBtn);
        historyBtn.setEnabled(false);

        if (kids.getNum() <= 0) {
            this.choice = "Not set";
            nameTxt.setText("No child's turn");
        }
        else {
            this.kidName = kids.getKidAt(kids.getCurrentIndex()).getName();
            Intent passedIntent = getIntent();
            this.choice = passedIntent.getStringExtra("Choice");
            nameTxt.setText(kids.getKidAt(kids.getCurrentIndex()).getName()
                    + "'s turn\nChose " + choice);
            historyBtn.setEnabled(true);
        }

        Log.println(Log.DEBUG, "Number of kids in KidManager",
                "NUMBER OF KIDS IN KIDMANAGER: "+ kids.getNum());
        Log.println(Log.DEBUG, "Choice Value",
                "CHOICE VALUE: "+ choice);

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(CoinFlipActivity.this,
                        HistoryActivity.class);
                historyIntent.putExtra("Kid name", kidName);
                startActivity(historyIntent);
                finish();
            }
        });


        currFace = findViewById(R.id.ivHeads);
        otherFace = findViewById(R.id.ivTails);


        playAnimationXML();
    }

    private int flipCoinResult() {
        Random rand = new Random();
        int n = rand.nextInt(2);

        TextView tv = findViewById(R.id.txtRng);
        if (n == 0) {
            //Heads
            tv.setText("Rng: Heads");
        } else {
            tv.setText("Rng: Tails");
        }
        return n;
    }

    private void updateResultText(String result) {
        TextView textView = findViewById(R.id.txtResult);
        textView.setText("You Flipped: " + result);
    }

    private void playAnimationXML() {
        final ImageView heads = findViewById(R.id.ivHeads);
        final ImageView tails = findViewById(R.id.ivTails);

        final Button btn = findViewById(R.id.btnFlipXml);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intCurrentFace == 0) {
                    playAnimation(heads, tails, isSameFace(intCurrentFace, flipCoinResult()), btn, view);
                } else {
                    playAnimation(tails, heads, isSameFace(intCurrentFace, flipCoinResult()), btn, view);
                }
            }
        });
    }

    private void playAnimation(ImageView currentFace, ImageView otherF, boolean sameFace, final Button btn, View view) {
        btn.setEnabled(false);
        MediaPlayer mp;
        mp = MediaPlayer.create(CoinFlipActivity.this, R.raw.coin_flip_sound);
        mp.start();
        AnimatorSet frontAnimatorSet;
        AnimatorSet backAnimatorSet;
        if (!(sameFace)) {
            frontAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.flip_first_half);
            backAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.flip_second_half);
            frontAnimatorSet.setTarget(currentFace);
            backAnimatorSet.setTarget(otherF);
            //Swap current and other faces
            currFace = otherF;
            otherFace = currentFace;
            if (intCurrentFace == 1) {
                intCurrentFace = 0;
            } else {
                intCurrentFace = 1;
            }
        } else {
            frontAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.flip_first_half_same_face);
            backAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.flip_second_half_same_face);
            frontAnimatorSet.setTarget(currentFace);
            backAnimatorSet.setTarget(otherF);
        }
        backAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                // There isn't a kid choosing, user can flip as many times as they want
                if (kids.getNum() <= 0) {
                    btn.setEnabled(true);
                }

                else {
                    Kid kid = kids.getKidAt(kids.getCurrentIndex());
                    boolean wonFlip = false; //lose by default

                    // In case of getting heads
                    if (intCurrentFace == 0) {
                        updateResultText("heads");

                        // Kid chose heads and won
                        if (choice.equals("heads")) {
                            wonFlip = true;
                        }
                    }

                    // In case of getting tails
                    else {
                        updateResultText("tails");

                        // Kid chose tails and won
                        if (choice.equals("tails")) {
                            wonFlip = true;
                        }
                    }
                    //Saving results
                    Results results = new Results(wonFlip, choice, date, kid.getName());
                    history.add(results);
                    kids.nextKid();
                    saveKidManager();
                    saveResultsManager();
                    btn.setEnabled(false);
                }
            }
        });
        frontAnimatorSet.start();
        backAnimatorSet.start();
    }

    private void saveKidManager() {
        // Save the KidManager interior values
        SharedPreferences prefs = getSharedPreferences("Kids", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(kids.getList()); // Saving list
        prefEditor.putString("List", json);
        json = gson.toJson(kids.getCurrentIndex()); // Saving list
        prefEditor.putString("Index", json); // Saving current index
        prefEditor.apply();
    }

    private void saveResultsManager() {
        // Save the ResultsManager interior list of Results
        SharedPreferences prefs = getSharedPreferences("History", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(history.getList()); // Saving list
        prefEditor.putString("List", json);
        prefEditor.apply();
    }

    private boolean isSameFace(int currFaceIs, int newFaceIs) {
        return currFaceIs == newFaceIs;
    }
}