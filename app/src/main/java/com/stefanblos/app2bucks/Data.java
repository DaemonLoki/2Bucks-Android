package com.stefanblos.app2bucks;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by stefanblos on 06.06.16.
 */
public class Data {

    private static String TAG = "2Bucks - Data: ";

    private static Data mInstance = null;

    private ArrayList<User> mUserList;
    private ArrayList<Object> mBetList;
    private FirebaseDatabase mFirebaseDatabase;
    private Context mContext;

    public static Data getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Data(context);
        }

        return mInstance;
    }

    private Data(Context context) {

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mContext = context;

        mUserList = new ArrayList<>();
        mBetList = new ArrayList<>();
        populateUserList();
        populateBetList();

    }

    private void populateUserList() {

        mFirebaseDatabase.getReference("users")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        // get values for user
                        User newUser = User.getUserFromDataSnapshot(dataSnapshot);
                        if (newUser.getUid() != OverviewActivity.getUid()) {
                            mUserList.add(newUser);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        // get id of changed user
                        String uid = dataSnapshot.getKey();

                        User changedUser = getUserByUid(uid);
                        if (changedUser != null) {
                            changedUser.updateUser(User.getUserFromDataSnapshot(dataSnapshot));
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.v(TAG, "Removed user with uid: " + dataSnapshot.getKey());
                        User removedUser = getUserByUid(dataSnapshot.getKey());

                        mUserList.remove(removedUser);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void populateBetList() {
        mFirebaseDatabase.getReference("bets")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onBetChildAdded: " + dataSnapshot.getKey());
                        String type = dataSnapshot.child("type").getValue(String.class);

                        switch (type) {
                            case Constants.BET_TYPE_OVERUNDER:
                                OverUnderBet newOverUnderBet = (OverUnderBet)
                                        OverUnderBet.getBetFromDataSnapshot(dataSnapshot);
                                mBetList.add(newOverUnderBet);

                                // notify that data changed
                                notifyBetDataChanged();
                                break;
                            case Constants.BET_TYPE_YESNO:
                                YesNoBet newYesNoBet = (YesNoBet)
                                        YesNoBet.getBetFromDataSnapshot(dataSnapshot);
                                mBetList.add(newYesNoBet);

                                // notify that data changed
                                notifyBetDataChanged();
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private User getUserByUid(String uid) {
        for (User user: mUserList) {
            if (user.getUid().equals(uid)) return user;
        }

        return null;
    }

    public String getUserNameByUid(String uid) {
        for (User user : mUserList) {
            if (user.getUid().equals(uid)) return user.getUserName();
        }

        return null;
    }

    public String getUserUidByName(String userName) {
        for (User user : mUserList) {
            if (user.getUserName().equals(userName)) return user.getUid();
        }

        return null;
    }

    public ArrayList<Object> getBetList() {
        return mBetList;
    }

    public ArrayList<User> getUserList() { return  mUserList; }

    private void notifyBetDataChanged() {
        Collections.reverse(mBetList);
        Intent intent = new Intent(Constants.EVENT_BET_DATA_CHANGED);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
