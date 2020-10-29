package com.raspberry.practicalparent;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class FlipCoinAnimation extends AppCompatActivity {

    private boolean isHeads = true;

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, FlipCoinAnimation.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin_animation);

        playAnimationXML();
    }
    private void updateResultText(String result) {
        TextView textView = findViewById(R.id.txtResult);
        textView.setText("You Flipped: " + result);
    }

//
//    private void playAnimation() {
//        final ImageView win = findViewById(R.id.winIv);
//        win.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(win, "scaleX", 1f, 0f);
//                final ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(win, "scaleX", 0f, 1f);
//                objectAnimator1.setDuration(100);
//                objectAnimator2.setDuration(100);
//                objectAnimator1.setInterpolator(new DecelerateInterpolator());
//                objectAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
//                objectAnimator1.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        win.setImageResource(R.drawable.tails);
//                        objectAnimator2.start();
//                    }
//                });
//                objectAnimator1.start();
//            }
//        });
//    }
//
//    private void playAnimation2() {
//        final ImageView v = findViewById(R.id.winIv);
//        int counter = 0;
//        v.setRotation(0);
//        v.animate().withLayer()
//                .rotationX(-90)
//                .setDuration(150)
//                .withEndAction(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                v.setImageResource(R.drawable.tails);
//                                v.setRotationX(90);
//                                v.animate().withLayer()
//                                        .rotationX(0)
//                                        .setDuration(150)
//                                        .withEndAction(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                //playAnimationTails();
//                                            }
//                                        })
//                                        .start();
//                            }
//                        }
//         ).start();
//    }
//
//    private void playAnimationTails() {
//        final ImageView v = findViewById(R.id.winIv);
//        int counter = 0;
//        v.setRotation(0);
//        v.animate().withLayer()
//                .rotationX(90)
//                .setDuration(150)
//                .withEndAction(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                v.setImageResource(R.drawable.heads);
//                                v.setRotationX(-90);
//                                v.animate().withLayer()
//                                        .rotationX(0)
//                                        .setDuration(150)
//                                        .withEndAction(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                playAnimation2();
//                                            }
//                                        })
//                                        .start();
//                            }
//                        }
//                ).start();
//    }
//
//    private void playAnimation3() {
//        final ImageView v = findViewById(R.id.winIv);
//        v.animate().withLayer()
//                .rotationX(90)
//                .setDuration(200)
//                .start();
//    }
//
//    private void playAnimation4() {
//        final ImageView win = findViewById(R.id.winIv);
//        final ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(win, "scaleY", 1f, 0f);
//        final ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(win, "scaleY", 0f, 1f);
//        objectAnimator1.setDuration(200);
//        objectAnimator2.setDuration(200);
//        objectAnimator1.setInterpolator(new DecelerateInterpolator());
//        objectAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
//        objectAnimator1.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                win.setImageResource(R.drawable.tails);
//                objectAnimator2.start();
//            }
//        });
//        objectAnimator2.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                win.setImageResource(R.drawable.heads);
//                playAnimation5();
//            }
//        });
//        objectAnimator1.start();
//    }
//    private void playAnimation5() {
//        final ImageView win = findViewById(R.id.winIv);
//        final ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(win, "scaleY", 1f, 0f);
//        final ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(win, "scaleY", 0f, 1f);
//        objectAnimator1.setDuration(200);
//        objectAnimator2.setDuration(200);
//        objectAnimator1.setInterpolator(new DecelerateInterpolator());
//        objectAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
//        objectAnimator1.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                win.setImageResource(R.drawable.heads);
//                objectAnimator2.start();
//            }
//        });
//        objectAnimator2.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                win.setImageResource(R.drawable.tails);
//                playAnimation4();
//            }
//        });
//        objectAnimator1.start();
//    }
    //
    private void playAnimationXML() {
        final ImageView heads = findViewById(R.id.ivHeads);
        final ImageView tails = findViewById(R.id.ivTails);


        Button btn = findViewById(R.id.btnFlipXml);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isHeads) {
                    AnimatorSet frontAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.heads_to_tail_animation);
                    AnimatorSet backAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.tails_to_head_animation);
                    frontAnimatorSet.setTarget(heads);
                    backAnimatorSet.setTarget(tails);
                    backAnimatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            updateResultText("Tails");
                        }
                    });
                    frontAnimatorSet.start();
                    backAnimatorSet.start();
                    isHeads = false;
                } else {
                    AnimatorSet frontAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.heads_to_tail_animation);
                    AnimatorSet backAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.tails_to_head_animation);
                    frontAnimatorSet.setTarget(tails);
                    backAnimatorSet.setTarget(heads);
                    backAnimatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            updateResultText("Heads");
                        }
                    });
                    frontAnimatorSet.start();
                    backAnimatorSet.start();
                    isHeads = true;
                }
            }
        });
    }
//    private void playAnimations() {
//        playAnimation2();
//        //playAnimation3();
//    }


//    }
//
//
//
//    private Runnable flip = new Runnable() {
//        @Override
//        public void run() {
//            playAnimation3();
//        }
//    };
//
//    private void setupButtons() {
//        Button btnFlipCoinAnimationHeads = findViewById(R.id.flipAnimationHeadsBtn);
//        btnFlipCoinAnimationHeads.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                playAnimation4();
//            }
//        });
//        Button btnFlipCoinAnimationTails = findViewById(R.id.flipAnimationTailsBtn);
//        btnFlipCoinAnimationTails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                playAnimation2();
//            }
//        });
//    }
}