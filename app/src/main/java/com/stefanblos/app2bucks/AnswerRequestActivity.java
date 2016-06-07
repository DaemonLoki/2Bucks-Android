package com.stefanblos.app2bucks;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class AnswerRequestActivity extends AppCompatActivity {

    private TextView mOpponentTextView;
    private TextView mTitleTextView;
    private TextView mDetailTextView;
    private TextView mOpponentPickTextView;
    private TextView mYourPickTextView;

    private Object mBet;
    private String mBetUid;
    private String mBetType;

    private FirebaseDatabase mFirebaseDatabase;

    private OnSwipeTouchListener mSwipeTouchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_request);

        mOpponentTextView = (TextView) findViewById(R.id.textview_request_opponent);
        mTitleTextView = (TextView) findViewById(R.id.textview_request_title);
        mDetailTextView = (TextView) findViewById(R.id.textview_request_detail);
        mOpponentPickTextView = (TextView) findViewById(R.id.textview_request_opponentpick);
        mYourPickTextView = (TextView) findViewById(R.id.textview_request_yourpick);

        mSwipeTouchListener = createSwipeTouchListener(getApplicationContext());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        String type = intent.getStringExtra("TYPE");

        switch (type) {
            case Constants.BET_TYPE_OVERUNDER:
                populateUIWithBet(OverUnderBet.getBetFromIntent(intent));
                break;

            case Constants.BET_TYPE_YESNO:
                populateUIWithBet(YesNoBet.getBetFromIntent(intent));
                break;

            default:
                break;
        }

        this.getWindow().getDecorView().setOnTouchListener(mSwipeTouchListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void populateUIWithBet(Object obj) {
        if (obj.getClass() == OverUnderBet.class) {
            OverUnderBet bet = (OverUnderBet) obj;
            mBet = bet;
            mBetType = Constants.BET_TYPE_OVERUNDER;
            mBetUid = bet.getUid();
            mOpponentTextView.setText(OverviewActivity.getData()
                    .getUserNameByUid(bet.getChallengerUid()));
            mTitleTextView.setText(bet.getTitle());
            mDetailTextView.setText(bet.getOverUnder().toString());
            mOpponentPickTextView.setText(bet.getChallengerPick());
            mYourPickTextView.setText(bet.getOpponentPick());
        } else if (obj.getClass() == YesNoBet.class) {
            YesNoBet bet = (YesNoBet) obj;
            mBet = bet;
            mBetType = Constants.BET_TYPE_YESNO;
            mBetUid = bet.getUid();
            mOpponentTextView.setText(OverviewActivity.getData()
                    .getUserNameByUid(bet.getChallengerUid()));
            mTitleTextView.setText(bet.getTitle());
            mDetailTextView.setText(bet.getQuestion());
            mOpponentPickTextView.setText(bet.getChallengerPick() ? "Yes" : "No");
            mYourPickTextView.setText(bet.getOpponentPick() ? "Yes" : "No");
        }
    }

    private OnSwipeTouchListener createSwipeTouchListener(final Context context) {
        return new OnSwipeTouchListener(context) {

            @Override
            public void onSwipeLeft() {
                Toast.makeText(context, "DENIED", Toast.LENGTH_SHORT).show();

                // decline request
                deleteFromOpenRequests(mBetUid);
                finish();
            }

            @Override
            public void onSwipeRight() {
                Toast.makeText(context, "ACCEPTED", Toast.LENGTH_SHORT).show();

                // accept request
                acceptRequest();
                deleteFromOpenRequests(mBetUid);

                finish();
            }
        };
    }

    private void acceptRequest() {

        switch(mBetType) {

            case Constants.BET_TYPE_OVERUNDER:
                OverUnderBet overUnderBet = (OverUnderBet) mBet;
                Map<String, Object> overUnderBetMap = overUnderBet.createBetDictionaryForFirebase();
                mFirebaseDatabase.getReference("bets")
                        .child(overUnderBet.getUid())
                        .setValue(overUnderBetMap);
                mFirebaseDatabase.getReference("users")
                        .child(OverviewActivity.getUid())
                        .child("bets")
                        .child(overUnderBet.getUid())
                        .setValue(true);
                break;
            case Constants.BET_TYPE_YESNO:
                YesNoBet yesNoBet = (YesNoBet) mBet;
                Map<String, Object> yesNoBetMap = yesNoBet.createBetDictionaryForFirebase();
                mFirebaseDatabase.getReference("bets")
                        .child(yesNoBet.getUid())
                        .setValue(yesNoBetMap);
                mFirebaseDatabase.getReference("users")
                        .child(OverviewActivity.getUid())
                        .child("bets")
                        .child(yesNoBet.getUid())
                        .setValue(true);
                break;
            default:
                break;
        }

    }

    private void deleteFromOpenRequests(String uid) {
        mFirebaseDatabase.getReference("requests")
                .child(mBetUid)
                .removeValue();
        mFirebaseDatabase.getReference("users")
                .child(OverviewActivity.getUid())
                .child("requests")
                .child(mBetUid)
                .removeValue();
    }
}
