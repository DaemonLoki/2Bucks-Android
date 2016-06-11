package com.stefanblos.app2bucks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by stefanblos on 03.06.16.
 */
public class Constants {

    // MEMORY DATA
    public static final String PREFS_NAME = "USER_CREDS";
    public static final String MEMORY_USER_ID = "MEMORY_USER_ID";
    public static final String MEMORY_USER_MAIL = "MEMORY_USER_MAIL";
    public static final String MEMORY_USER_PW = "MEMORY_USER_PW";
    public static final String ERROR_GETTING_PREFS = "error_getting_prefs";

    // BETS AND BET-TYPES
    public static final String BET_TYPE_OVERUNDER = "Over/Under";
    public static final String BET_TYPE_YESNO = "Yes/No";
    public static final String[] BET_TYPES = {
            BET_TYPE_OVERUNDER,
            BET_TYPE_YESNO
    };

    // BROADCAST MESSAGES
    public static final String EVENT_BET_DATA_CHANGED = "BET_DATA_CHANGED";
    public static final String EVENT_USER_DATA_LOADED = "USER_DATA_LOADED";

    // Get current date
    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        return dateString;
    }
}
