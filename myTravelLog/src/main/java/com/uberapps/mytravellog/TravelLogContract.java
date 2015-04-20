package com.uberapps.mytravellog;

import android.provider.BaseColumns;

/**
 * represents the tarvel log db entry
 * @author DoryZ
 *
 */
public final class TravelLogContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TravelLogContract() {}
    
    public static final String TABLE_NAME = "log_entries";
    
    
    public static final String LOG_ENTRIES_TABLE_CREATE_SQL =
            "CREATE TABLE " + TABLE_NAME + " (" +
            LogEntry._ID + " INTEGER PRIMARY KEY," +
            LogEntry.COLUMN_NAME_COUNTRY + TravelLogDBHelper.TEXT_TYPE + TravelLogDBHelper.COMMA_SEP +
            LogEntry.COLUMN_NAME_FROM + TravelLogDBHelper.TEXT_TYPE  + TravelLogDBHelper.COMMA_SEP +
            LogEntry.COLUMN_NAME_TO + TravelLogDBHelper.TEXT_TYPE  +
            " );";
    
    public static final String LOG_ENTRIES_DROP_TABLE_SQL =
    	    "DROP TABLE IF EXISTS " + TABLE_NAME;
    
    
    

    /* Inner class that defines the table contents */
    public static abstract class LogEntry implements BaseColumns {
    	public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_FROM = "from_date";
        public static final String COLUMN_NAME_TO = "to_date";
        
    }
}
