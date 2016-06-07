package com.stefanblos.app2bucks;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by stefanblos on 06.06.16.
 */
public class User {

    private String mUid;
    private String mUserName;
    private String mEmail;

    public User(String uid, String userName) {
        mUid = uid;
        mUserName = userName;
    }

    public String getUid() {
        return mUid;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void updateUser(User user) {
        if (!user.getUid().equals(mUid)) {
            mUid = user.getUid();
        }

        if (!user.getUserName().equals(mUserName)) {
            mUserName = user.getUserName();
        }

        if (!user.getEmail().equals(mEmail)) {
            mEmail = user.getEmail();
        }
    }

    public static User getUserFromDataSnapshot(DataSnapshot snap) {
        String snapUid = snap.getKey();
        String snapUsername = snap.child("username").getValue(String.class);

        User newUser = new User(snapUid, snapUsername);

        String snapMail = snap.child("mail").getValue(String.class);
        if (snapMail != null) {
            newUser.setEmail(snapMail);
        }

        return newUser;
    }

}
