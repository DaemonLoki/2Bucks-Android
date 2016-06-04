package com.stefanblos.app2bucks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        public ViewHolder(View v)
        {
            super(v);
            mTitleTextView = (TextView) v.findViewById(R.id.recycler_bet_title);
            mDescTextView = (TextView) v.findViewById(R.id.recycler_bet_description);
            mChallengerTextView = (TextView) v.findViewById(R.id.recycler_bet_challenger);
            mOpponentTextView = (TextView) v.findViewById(R.id.recycler_bet_opponent);
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
        // TODO set the holder with bet text
        if (mDataSet.get(position).getClass() == OverUnderBet.class) {
            OverUnderBet bet = (OverUnderBet) mDataSet.get(position);
            holder.mTitleTextView.setText(bet.getTitle());
            holder.mChallengerTextView.setText(bet.getChallengerUid());
            holder.mOpponentTextView.setText(bet.getOpponentUid());
        } else if (mDataSet.get(position).getClass() == YesNoBet.class) {
            YesNoBet bet = (YesNoBet) mDataSet.get(position);
            holder.mTitleTextView.setText(bet.getTitle());
            holder.mDescTextView.setText(bet.getQuestion());
            holder.mChallengerTextView.setText(bet.getChallengerUid());
            holder.mOpponentTextView.setText(bet.getOpponentUid());
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
