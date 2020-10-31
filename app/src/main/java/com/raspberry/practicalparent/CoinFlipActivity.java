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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.Results;
import com.raspberry.practicalparent.model.ResultsManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

//xml animations adapted from https://www.youtube.com/watch?v=DnXWcGmLHHs

/*TODO:     1. Make KidManager pass to the next kid but show the history of the kid who just flipped
   in HistoryActivity (maybe by passing in the Kid's entire Results in an Intent)
        2. Make going back from HistoryActivity prevent further flips (As new child would have to
   pick H/T and we're not sure they'd want to do another flip right after the previous)
   Maybe it would be simpler to have the HistoryActivity exit to main menu, but for some reason
   that feels wrong
 */

public class CoinFlipActivity extends AppCompatActivity {
    //0 for heads, 1 for tails
    private int intCurrentFace = 0;

    // Choice passed in from ChooseActivity
    // Could have done it as an int but I like the
    // forwardness of reading "heads" or "tails"
    private String choice;

    private KidManager kids; // The singleton
    private String kidName; // Name of kid to be passed to History

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

        TextView nameTxt = findViewById(R.id.childFlipName);
        Button historyBtn = findViewById(R.id.historyBtn);
        historyBtn.setEnabled(false);

        if (kids.getNum() <= 0) {
            nameTxt.setText("No child's turn");
            this.choice = "Not set";
        }
        else {
            nameTxt.setText(kids.getKidAt(kids.getCurrentIndex()).getName()
                    + "'s turn\nChose " + choice);
            kidName = kids.getKidAt(kids.getCurrentIndex()).getName();
            historyBtn.setEnabled(true);
            Intent passedIntent = getIntent();
            this.choice = passedIntent.getStringExtra("Choice");
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
                startActivity(historyIntent);
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
            tv.setText("Rnd: Tails");
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
                // In case of getting heads
                if (intCurrentFace == 0) {
                    updateResultText("Heads");

                    // Kid chose heads and won
                    if (choice.equals("heads") && kids.getNum() > 0) {
                        ResultsManager stats = kids.getKidAt(kids.getCurrentIndex()).getResults();
                        Results results = new Results(true,
                                "heads", date,
                                kids.getKidAt(kids.getCurrentIndex()).getName());
                        stats.add(results);
                        saveKidManager();
                        btn.setEnabled(false);
                    }

                    // Kid chose tails and lost
                    else if (choice.equals("tails") && kids.getNum() > 0) {
                        ResultsManager stats = kids.getKidAt(kids.getCurrentIndex()).getResults();
                        Results results = new Results(false,
                                "tails", date,
                                kids.getKidAt(kids.getCurrentIndex()).getName());
                        stats.add(results);
                        saveKidManager();
                        btn.setEnabled(false);
                    }

                    // There isn't a kid choosing, user can flip as many times as they want
                    else {
                        btn.setEnabled(true);
                    }
                }

                // In case of getting tails
                else {
                    updateResultText("Tails");
                    // Kid chose tails and won
                    if (choice == "tails" && kids.getNum() > 0) {
                        ResultsManager stats = kids.getKidAt(kids.getCurrentIndex()).getResults();
                        Results results = new Results(true,
                                "tails", date,
                                kids.getKidAt(kids.getCurrentIndex()).getName());
                        stats.add(results);
                        saveKidManager();
                        btn.setEnabled(false);
                    }

                    // Kid chose heads and lost
                    else if (choice.equals("heads") && kids.getNum() > 0) {
                        ResultsManager stats = kids.getKidAt(kids.getCurrentIndex()).getResults();
                        Results results = new Results(false,
                                "heads", date,
                                kids.getKidAt(kids.getCurrentIndex()).getName());
                        stats.add(results);
                        saveKidManager();
                        btn.setEnabled(false);
                    }
                    // There isn't a kid choosing, user can flip as many times as they want
                    else {
                        btn.setEnabled(true);
                    }
                }
            }
        });
        frontAnimatorSet.start();
        backAnimatorSet.start();
    }

    private void saveKidManager() {
        // Save the KidManager interior values
        // Saving KidManager into SharedPreferences
        SharedPreferences prefs = getSharedPreferences("Kids", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(kids.getList()); // Saving list
        prefEditor.putString("List", json);
        json = gson.toJson(kids.getCurrentIndex()); // Saving list
        prefEditor.putString("Index", json); // Saving current index
        prefEditor.apply();
    }

    private boolean isSameFace(int currFaceIs, int newFaceIs) {
        return currFaceIs == newFaceIs;
    }
}