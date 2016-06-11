package com.stefanblos.app2bucks;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by stefanblos on 03.06.16.
 */
public class BetAdapter extends RecyclerView.Adapter<BetAdapter.ViewHolder> {

    private ArrayList<Object> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleTextView;
        public TextView mDescTextView;
        public TextView mChallengerTextView;
        public TextView mOpponentTextView;
        public ImageView mChallengerImageView;
        public ImageView mOpponentImageView;

        public ViewHolder(View v)
        {
            super(v);
            mTitleTextView = (TextView) v.findViewById(R.id.recycler_bet_title);
            mDescTextView = (TextView) v.findViewById(R.id.recycler_bet_description);
            mChallengerTextView = (TextView) v.findViewById(R.id.recycler_bet_challenger);
            mOpponentTextView = (TextView) v.findViewById(R.id.recycler_bet_opponent);
            mChallengerImageView = (ImageView) v.findViewById(R.id.recycler_bet_challenger_pick);
            mOpponentImageView = (ImageView) v.findViewById(R.id.recycler_bet_opponent_pick);
        }
    }

    // Constructor
    public BetAdapter(ArrayList<Object> dataSet) {
        mDataSet = dataSet;
    }

    @Override
    public BetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_bet, parent, false);

        // set view properties

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(BetAdapter.ViewHolder holder, int position) {

        // configure recycler item
        if (mDataSet.get(position).getClass() == OverUnderBet.class) {

            OverUnderBet bet = (OverUnderBet) mDataSet.get(position);

            // configure over/under
            holder.mTitleTextView.setText(bet.getTitle());

            holder.mDescTextView.setText(bet.getOverUnder().toString());

            holder.mChallengerTextView.setText(OverviewActivity.getData()
                    .getUserNameByUid(bet.getChallengerUid()));
            holder.mOpponentTextView.setText(OverviewActivity.getData()
                    .getUserNameByUid(bet.getOpponentUid()));

            if (bet.getChallengerPick() == "Over") {
                holder.mChallengerImageView.setImageResource(R.drawable.over_button);
                holder.mOpponentImageView.setImageResource(R.drawable.under_button);
            } else {
                holder.mChallengerImageView.setImageResource(R.drawable.under_button);
                holder.mOpponentImageView.setImageResource(R.drawable.over_button);
            }
        } else if (mDataSet.get(position).getClass() == YesNoBet.class) {
            YesNoBet bet = (YesNoBet) mDataSet.get(position);

            // configure yes/no
            holder.mTitleTextView.setText(bet.getQuestion());
            holder.mDescTextView.setText("");

            holder.mChallengerTextView.setText(OverviewActivity.getData()
                    .getUserNameByUid(bet.getChallengerUid()));
            holder.mOpponentTextView.setText(OverviewActivity.getData()
                    .getUserNameByUid(bet.getOpponentUid()));

            if (bet.getChallengerPick()) {
                holder.mChallengerImageView.setImageResource(R.drawable.yes_button);
                holder.mOpponentImageView.setImageResource(R.drawable.no_button);
            } else {
                holder.mChallengerImageView.setImageResource(R.drawable.no_button);
                holder.mOpponentImageView.setImageResource(R.drawable.yes_button);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
