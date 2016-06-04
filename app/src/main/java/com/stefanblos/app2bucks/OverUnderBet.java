package com.stefanblos.app2bucks;

import com.google.firebase.database.DataSnapshot;

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
        mOpponentPick = null;
        mWinnerUid = null;
    }

    @Override
    public Map<String, String> createRequestDictionaryForFirebase() {
        return null;
    }

    @Override
    public Map<String, String> createBetDictionaryForFirebase() {
        return null;
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
}
