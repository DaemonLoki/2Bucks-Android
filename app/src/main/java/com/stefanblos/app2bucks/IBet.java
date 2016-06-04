package com.stefanblos.app2bucks;

import com.google.firebase.database.DataSnapshot;

import java.util.Map;

/**
 * Created by stefanblos on 03.06.16.
 */
public interface IBet {

    // properties
    public String getUid();
    public String getTitle();
    public String getChallengerUid();
    public String getOpponentUid();
    public String getDate();
    public String getType();
    public String getWinner();

    // methods
    public Map<String, String> createRequestDictionaryForFirebase();
    public Map<String, String> createBetDictionaryForFirebase();
}
