package com.raspberry.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.raspberry.practicalparent.model.KidManager;
import com.raspberry.practicalparent.model.ResultsManager;

import org.w3c.dom.Text;

import java.util.Random;

//xml animations adapted from https://www.youtube.com/watch?v=DnXWcGmLHHs

public class CoinFlipActivity extends AppCompatActivity {
    //0 for heads, 1 for tails
    private int intCurrentFace = 0;

    // Choice passed in from ChooseActivity
    // Could have done it as an int but I like the
    // forwardness of reading "heads" or "tails"
    private String choice;

    private KidManager kids; // The singleton

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

        Intent passedIntent = getIntent();
        choice = passedIntent.getStringExtra("Choice");

        kids = KidManager.getInstance();

        TextView nameTxt = findViewById(R.id.childFlipName);

        if (kids.getNum() <= 0) {
            nameTxt.setText("No child's turn\nUser chose " + choice);
        }
        else {
            nameTxt.setText(kids.getKidAt(kids.getCurrentIndex()).getName()
                    + "'s turn\nChose " + choice);
        }

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
                if (intCurrentFace == 0) {
                    updateResultText("Heads");
                    if (choice == "heads" && kids.getNum() > 0) {
                        //ResultsManager stats = kids.getKidAt(kids.getCurrentIndex()).getResult();
                    }
                } else {
                    updateResultText("Tails");
                }
                //btn.setEnabled(true);
            }
        });
        frontAnimatorSet.start();
        backAnimatorSet.start();
    }

    private boolean isSameFace(int currFaceIs, int newFaceIs) {
        return currFaceIs == newFaceIs;
    }
}