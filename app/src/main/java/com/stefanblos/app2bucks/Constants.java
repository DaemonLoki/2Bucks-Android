package com.stefanblos.app2bucks;

/**
 * Created by stefanblos on 03.06.16.
 */
public class Constants {

    // BETS AND BET-TYPES
    public static final String BET_TYPE_OVERUNDER = "Over/Under";
    public static final String BET_TYPE_YESNO = "Yes/No";
    public static final String[] BET_TYPES = {
            BET_TYPE_OVERUNDER,
            BET_TYPE_YESNO
    };

    // BROADCAST MESSAGES
    public static final String EVENT_BET_DATA_CHANGED = "BET_DATA_CHANGED";
}
