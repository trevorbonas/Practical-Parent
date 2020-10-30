package com.raspberry.practicalparent;

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

import java.util.Random;

//xml animations adapted from https://www.youtube.com/watch?v=DnXWcGmLHHs

public class FlipCoinAnimation extends AppCompatActivity {
    //0 for heads, 1 for tails
    private int intCurrentFace = 0;

    private ImageView currFace;
    private ImageView otherFace;

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, FlipCoinAnimation.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin_animation);

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
                } else {
                    updateResultText("Tails");
                }
                btn.setEnabled(true);
            }
        });
        frontAnimatorSet.start();
        backAnimatorSet.start();
    }

    private boolean isSameFace(int currFaceIs, int newFaceIs) {
        return currFaceIs == newFaceIs;
    }
}