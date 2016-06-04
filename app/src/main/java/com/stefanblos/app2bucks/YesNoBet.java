package com.stefanblos.app2bucks;

import com.google.firebase.database.DataSnapshot;

import java.util.Map;

/**
 * Created by stefanblos on 03.06.16.
 */
public class YesNoBet implements IBet {

    // IBet properties
    private String mUid;
    private String mTitle;
    private String mChallengerUid;
    private String mOpponentUid;
    private String mDate;
    private String TYPE = Constants.BET_TYPE_YESNO;
    private String mWinnerUid;

    // specific properties
    private String mQuestion;
    private boolean mChallengerPick;
    private boolean mOpponentPick;

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

    public String getQuestion() {
        return mQuestion;
    }

    public boolean getChallengerPick() {
        return mChallengerPick;
    }

    public boolean getOpponentPick() {
        return mOpponentPick;
    }

    public void setWinner(String winnerUid) {
        mWinnerUid = winnerUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public YesNoBet(String title, String challengerUid, String opponentUid, String date,
                        String question, boolean challengerPick, boolean opponentPick) {
        mUid = "dummyUid";
        mTitle = title;
        mChallengerUid = challengerUid;
        mOpponentUid = opponentUid;
        mDate = date;
        mQuestion = question;
        mChallengerPick = challengerPick;
        mOpponentPick = opponentPick;
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

    public static YesNoBet getBetFromDataSnapshot(DataSnapshot snap) {
        String title = snap.child("title").getValue(String.class);
        String challengerUid = snap.child("challenger").getValue(String.class);
        String opponentUid = snap.child("opponent").getValue(String.class);
        String date = snap.child("date").getValue(String.class);
        String question = snap.child("question").getValue(String.class);
        boolean challengerPick = snap.child("challengerPick").getValue(Boolean.class);
        boolean opponentPick = snap.child("opponentPick").getValue(Boolean.class);

        YesNoBet betToReturn = new YesNoBet(title, challengerUid, opponentUid, date,
                question, challengerPick, opponentPick);

        String winnerUid = snap.child("winner").getValue(String.class);
        if(winnerUid != null) {
            betToReturn.setWinner(winnerUid);
        }
        String uid = snap.getKey();
        betToReturn.setUid(uid);

        return betToReturn;
    }
}
