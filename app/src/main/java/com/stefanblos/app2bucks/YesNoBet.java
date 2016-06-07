package com.stefanblos.app2bucks;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
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
    public Map<String, Object> createRequestDictionaryForFirebase() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("title", mTitle);
        requestMap.put("date", mDate);
        requestMap.put("challenger", mChallengerUid);
        requestMap.put("opponent", mOpponentUid);
        requestMap.put("challengerPick", mChallengerPick);
        requestMap.put("opponentPick", mOpponentPick);
        requestMap.put("type", TYPE);
        requestMap.put("question", mQuestion);

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
        betMap.put("question", mQuestion);
        betMap.put("timestamp", ServerValue.TIMESTAMP);

        return betMap;
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

    public static YesNoBet getRequestFromDataSnapshot(DataSnapshot snap) {
        String title = snap.child("title").getValue(String.class);
        String challengerUid = snap.child("challenger").getValue(String.class);
        String opponentUid = snap.child("opponent").getValue(String.class);
        String date = snap.child("date").getValue(String.class);
        String question = snap.child("question").getValue(String.class);
        boolean challengerPick = snap.child("challengerPick").getValue(Boolean.class);
        boolean opponentPick = snap.child("opponentPick").getValue(Boolean.class);

        YesNoBet bet = new YesNoBet(title, challengerUid, opponentUid, date,
                question, challengerPick, opponentPick);
        String uid = snap.getKey();
        bet.setUid(uid);

        return bet;
    }

    public static YesNoBet getBetFromIntent(Intent intent) {
        String uid = intent.getStringExtra("UID");
        String title = intent.getStringExtra("TITLE");
        String challengerUid = intent.getStringExtra("CHALLENGER");
        String opponentUid = intent.getStringExtra("OPPONENT");
        String date = intent.getStringExtra("DATE");
        String question = intent.getStringExtra("QUESTION");
        boolean challengerPick = intent.getBooleanExtra("CHALLENGERPICK", true);
        boolean opponentPick = intent.getBooleanExtra("OPPONENTPICK", true);

        YesNoBet toReturn = new YesNoBet(title, challengerUid, opponentUid, date,
                question, challengerPick, opponentPick);
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
        intent.putExtra("QUESTION", mQuestion);
        intent.putExtra("CHALLENGERPICK", mChallengerPick);
        intent.putExtra("OPPONENTPICK", mOpponentPick);
        intent.putExtra("TYPE", TYPE);

        return intent;
    }
}
