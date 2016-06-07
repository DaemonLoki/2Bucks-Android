package com.stefanblos.app2bucks;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stefanblos on 03.06.16.
 */
public class OverUnderBet implements IBet {

    // IBet properties
    private String mUid;
    private String mTitle;
    private String mChallengerUid;
    private String mOpponentUid;
    private String mDate;
    private String TYPE = Constants.BET_TYPE_OVERUNDER;
    private String mWinnerUid;

    // specific properties
    private Double mOverUnder;
    private String mChallengerPick;
    private String mOpponentPick;

    @Override
    public String getUid() {
        return mUid;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getChallengerUid() {
        return mChallengerUid;
    }

    @Override
    public String getOpponentUid() {
        return mOpponentUid;
    }

    @Override
    public String getDate() {
        return mDate;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getWinner() {
        return mWinnerUid;
    }

    public Double getOverUnder() {
        return mOverUnder;
    }

    public String getChallengerPick() {
        return mChallengerPick;
    }

    public String getOpponentPick() {
        return mOpponentPick;
    }

    public void setWinner(String winnerUid) {
        mWinnerUid = winnerUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public OverUnderBet(String title, String challengerUid, String opponentUid, String date,
                        Double overUnder, String challengerPick, String opponentPick) {
        mUid = "dummyUid";
        mTitle = title;
        mChallengerUid = challengerUid;
        mOpponentUid = opponentUid;
        mDate = date;
        mOverUnder = overUnder;
        mChallengerPick = challengerPick;
        mOpponentPick = opponentPick;
        mWinnerUid = null;
    }

    @Override
    public Map<String, Object> createRequestDictionaryForFirebase() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("title", mTitle);
        requestMap.put("date", mDate);
        requestMap.put("challenger", mChallengerUid);
        requestMap.put("opponent", mOpponentUid);
        requestMap.put("challengerPick", mChallengerPick);
        requestMap.put("opponentPick", mOpponentPick);
        requestMap.put("type", TYPE);
        requestMap.put("overUnder", mOverUnder);

        return requestMap;
    }

    @Override
    public Map<String, Object> createBetDictionaryForFirebase() {
        Map<String, Object> betMap = new HashMap<>();
        betMap.put("title", mTitle);
        betMap.put("date", mDate);
        betMap.put("challenger", mChallengerUid);
        betMap.put("opponent", mOpponentUid);
        betMap.put("challengerPick", mChallengerPick);
        betMap.put("opponentPick", mOpponentPick);
        betMap.put("type", TYPE);
        betMap.put("overUnder", mOverUnder);
        betMap.put("timestamp", ServerValue.TIMESTAMP);

        return betMap;
    }

    public static OverUnderBet getBetFromDataSnapshot(DataSnapshot snap) {
        String title = snap.child("title").getValue(String.class);
        String challengerUid = snap.child("challenger").getValue(String.class);
        String opponentUid = snap.child("opponent").getValue(String.class);
        String date = snap.child("date").getValue(String.class);
        Double overUnder = snap.child("overUnder").getValue(Double.class);
        String challengerPick = snap.child("challengerPick").getValue(String.class);
        String opponentPick = snap.child("opponentPick").getValue(String.class);

        OverUnderBet betToReturn = new OverUnderBet(title, challengerUid, opponentUid, date,
                overUnder, challengerPick, opponentPick);

        String winnerUid = snap.child("winner").getValue(String.class);
        if(winnerUid != null) {
            betToReturn.setWinner(winnerUid);
        }
        String uid = snap.getKey();
        betToReturn.setUid(uid);

        return betToReturn;
    }

    public static OverUnderBet getRequestFromDataSnapshot(DataSnapshot snap) {
        String title = snap.child("title").getValue(String.class);
        String challengerUid = snap.child("challenger").getValue(String.class);
        String opponentUid = snap.child("opponent").getValue(String.class);
        String date = snap.child("date").getValue(String.class);
        Double overUnder = snap.child("overUnder").getValue(Double.class);
        String challengerPick = snap.child("challengerPick").getValue(String.class);
        String opponentPick = snap.child("opponentPick").getValue(String.class);

        OverUnderBet bet = new OverUnderBet(title, challengerUid, opponentUid, date,
                overUnder, challengerPick, opponentPick);

        String uid = snap.getKey();
        bet.setUid(uid);

        return bet;
    }

    public static OverUnderBet getBetFromIntent(Intent intent) {
        String uid = intent.getStringExtra("UID");
        String title = intent.getStringExtra("TITLE");
        String challengerUid = intent.getStringExtra("CHALLENGER");
        String opponentUid = intent.getStringExtra("OPPONENT");
        String date = intent.getStringExtra("DATE");
        Double overUnder = intent.getDoubleExtra("OVERUNDER", 1.0);
        String challengerPick = intent.getStringExtra("CHALLENGERPICK");
        String opponentPick = intent.getStringExtra("OPPONENTPICK");

        OverUnderBet toReturn = new OverUnderBet(title, challengerUid, opponentUid, date,
                overUnder, challengerPick, opponentPick);
        toReturn.setUid(uid);
        return toReturn;
    }

    public Intent createIntentFromBet(Context context, Class activityToStart) {
        Intent intent = new Intent(context, activityToStart);

        intent.putExtra("UID", mUid);
        intent.putExtra("TITLE", mTitle);
        intent.putExtra("CHALLENGER", mChallengerUid);
        intent.putExtra("OPPONENT", mOpponentUid);
        intent.putExtra("DATE", mDate);
        intent.putExtra("OVERUNDER", mOverUnder);
        intent.putExtra("CHALLENGERPICK", mChallengerPick);
        intent.putExtra("OPPONENTPICK", mOpponentPick);
        intent.putExtra("TYPE", TYPE);

        return intent;
    }
}
