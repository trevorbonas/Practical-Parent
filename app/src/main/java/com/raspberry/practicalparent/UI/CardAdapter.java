package com.raspberry.practicalparent.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raspberry.practicalparent.R;
import com.raspberry.practicalparent.model.CardViewMaker;

import java.util.ArrayList;

/**
 * RecyclerView for history of coin flips
 */

//adapted from https://www.youtube.com/watch?v=Nw9JF55LDzE
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private ArrayList<CardViewMaker> cardViewList;

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView childName;
        public TextView wonFlip;
        public TextView date;
        public TextView sideChosen;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageview);
            childName = itemView.findViewById(R.id.tv_child_name);
            wonFlip = itemView.findViewById(R.id.tv_win_loss);
            date = itemView.findViewById(R.id.tv_date);
            sideChosen = itemView.findViewById(R.id.tv_side_chosen);
        }
    }

    public CardAdapter(ArrayList<CardViewMaker> cardList) {
        cardViewList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardViewMaker currentItem = cardViewList.get(position);

        holder.image.setImageResource(currentItem.getImage());
        holder.childName.setText(currentItem.getChildName());
        holder.wonFlip.setText(currentItem.getWonFlip());
        holder.date.setText(currentItem.getDate());
        holder.sideChosen.setText(currentItem.getSideChosen());
    }

    @Override
    public int getItemCount() {
        return cardViewList.size();
    }
}
