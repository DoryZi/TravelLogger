package com.uberapps.mytravellog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.uberapps.mytravellog.TravelLogContract.LogEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.TimeZone;

public class TravelLogDBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME ="TravelLog.db";
	private static final int DATABASE_VERSION = 1;
    
    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
        
    public static SimpleDateFormat ISO8601_DATE_FORMATTER= null;
    
	public TravelLogDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		ISO8601_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
		ISO8601_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TravelLogContract.LOG_ENTRIES_TABLE_CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(TravelLogContract.LOG_ENTRIES_DROP_TABLE_SQL);
        onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
	
	
	// Adding new contact
	public void addLogEntry(TravelLogEntry newEntry) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LogEntry.COLUMN_NAME_COUNTRY, newEntry.getCountry());
		values.put(LogEntry.COLUMN_NAME_FROM ,TravelLogDBHelper.ISO8601_DATE_FORMATTER.format(newEntry.getFrom()));
		values.put(LogEntry.COLUMN_NAME_TO, TravelLogDBHelper.ISO8601_DATE_FORMATTER.format(newEntry.getTo()));
		db.insert(TravelLogContract.TABLE_NAME, null, values);
		db.close();
	}
	 
		 
	// Getting All Contacts
	public List<TravelLogEntry> getAllEntries() {
		List<TravelLogEntry> travelLogEntries = new ArrayList<TravelLogEntry>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TravelLogContract.TABLE_NAME;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TravelLogEntry logEntry = null;
                Boolean doNotAdd = false;
				try {
					logEntry = new TravelLogEntry(
							Integer.parseInt(cursor.getString(0)),
							cursor.getString(1),
							TravelLogDBHelper.ISO8601_DATE_FORMATTER.parse (cursor.getString(2)),
							TravelLogDBHelper.ISO8601_DATE_FORMATTER.parse(cursor.getString(3))
							);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					doNotAdd = true;
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					doNotAdd = true;
					e.printStackTrace();
				}
                               // Adding contact to list
        		if (!doNotAdd) travelLogEntries.add(logEntry);
            } while (cursor.moveToNext());
        }
 
        db.close();
        
        Collections.sort(travelLogEntries,TravelLogEntry.FROM_DATE_COMPARER);
        // return contact list
        return travelLogEntries;
	}
	
	public TravelLogEntry findEntry(int id) {
		String countQuery = "SELECT  * FROM " + TravelLogContract.TABLE_NAME + " WHERE " + LogEntry._ID + "=" + id;
        
		SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst())
			try {
				return new TravelLogEntry(
					Integer.parseInt(cursor.getString(0)),
					cursor.getString(1),
					TravelLogDBHelper.ISO8601_DATE_FORMATTER.parse (cursor.getString(2)),
					TravelLogDBHelper.ISO8601_DATE_FORMATTER.parse(cursor.getString(3))
					);
			} catch (Exception e) {
				e.printStackTrace();
			} 
        return null;
	}
	 
	// Getting contacts Count
	public int getLogEntriesCount() {
		String countQuery = "SELECT  * FROM " + TravelLogContract.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        int retVal=  cursor.getCount();
        db.close();
        return retVal;
	}

    public TravelLogEntry getLastEntryByToDate() {
        String lastEntryQuery = "SELECT * FROM " + TravelLogContract.TABLE_NAME  + " WHERE "
                + LogEntry.COLUMN_NAME_TO + " in (SELECT max ("
                + LogEntry.COLUMN_NAME_TO + ") FROM " + TravelLogContract.TABLE_NAME + ")";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(lastEntryQuery, null);

        if (cursor.moveToFirst())
            try {
                return new TravelLogEntry(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        TravelLogDBHelper.ISO8601_DATE_FORMATTER.parse (cursor.getString(2)),
                        TravelLogDBHelper.ISO8601_DATE_FORMATTER.parse(cursor.getString(3))
                );
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        return null;
    }

    public HashMap<String,HashMap<String,Integer>> getLocationsSummary() {

        HashMap<String,HashMap<String,Integer>> summaryByCountries = new HashMap<String,HashMap<String,Integer>>();
        summaryByCountries.put("First 6 month this year",new HashMap<String,Integer>());
        summaryByCountries.put("This Year",new HashMap<String,Integer>());

        List<TravelLogEntry> allEntries = getAllEntries();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(("UTC")));
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        Date beginningOfYear = calendar.getTime();

        calendar.set(Calendar.MONTH,Calendar.JULY);
        Date afterSixMonth = calendar.getTime();

        calendar.set(Calendar.MONTH,Calendar.DECEMBER);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        Date endOfYear = calendar.getTime();

        for (int i = 0; i < allEntries.size(); ++i) {
            TravelLogEntry curEntry = allEntries.get(i);
            checkAndAddEntryDaysIfMatchsRange(beginningOfYear,endOfYear,curEntry,summaryByCountries.get("This Year"));
        }

        return summaryByCountries;
    }

    private void checkAndAddEntryDaysIfMatchsRange(Date from, Date to, TravelLogEntry logEntry, HashMap<String,Integer> countrySummary) {
        if (logEntry.getTo().after(from) && logEntry.getTo().before(to)) {
            if (logEntry.getTo().before(to)) {
                if (countrySummary.containsKey(logEntry.getCountry())) {
                    countrySummary.put(logEntry.getCountry(), countrySummary.get(logEntry.getCountry()) + logEntry.getTotalDays());
                } else {
                    countrySummary.put(logEntry.getCountry(),logEntry.getTotalDays());
                }
            }
        }
    }
	// Updating single contact
	public int updateLogEntry(TravelLogEntry logEntryToUpdate) {
		SQLiteDatabase db = this.getWritableDatabase();
		
	    ContentValues values = new ContentValues();
	    values.put(LogEntry.COLUMN_NAME_COUNTRY, logEntryToUpdate.getCountry());
	    values.put(LogEntry.COLUMN_NAME_FROM,TravelLogDBHelper.ISO8601_DATE_FORMATTER.format(logEntryToUpdate.getFrom()));
	    values.put(LogEntry.COLUMN_NAME_TO, TravelLogDBHelper.ISO8601_DATE_FORMATTER.format(logEntryToUpdate.getTo()));
	 
	        // updating row
	    int returnVal =  db.update(TravelLogContract.TABLE_NAME, values, LogEntry._ID + " = ?",
	           new String[] { String.valueOf(logEntryToUpdate.getID()) });  
	    db.close();
	    return returnVal;
	}
	 
	// Deleting single contact
	public void deleteEntry(TravelLogEntry logEntryToDelete) {
		SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TravelLogContract.TABLE_NAME, LogEntry._ID + " = ?",
                new String[] { String.valueOf(logEntryToDelete.getID()) });
        db.close();
	}

}