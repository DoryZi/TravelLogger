package com.uberapps.mytravellog;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

public class LogMyLocationService extends IntentService {

    protected Handler mHandler = null;
    protected String mCurrentCountry = null;

    protected TravelLogDBHelper m_DBHelper = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public LogMyLocationService() {
        super("LogMyLocationService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(@NonNull Intent intent) {
        mCurrentCountry = intent.getStringExtra(MyTravelLogBroadcastReceiver.INTENT_EXTRA_COUNTRY_FIELD);
        if (mCurrentCountry == null || mCurrentCountry.isEmpty()) {
            Log.e("TravelLogIntentService", "Empty country received! not updating!!");
            return;
        }
        updateEntriesForToday();
    }

    protected void updateEntryToFieldToToday(@NonNull TravelLogEntry logEntry) {
        logEntry.setTo(new Date());
        m_DBHelper.updateLogEntry(logEntry);
        sendEntryUpdateIntent();
    }

    protected void createCurrentCountryOneDayEntry() {
        createNewOneDayEntry(mCurrentCountry);
    }

    protected void createNewOneDayEntry(String countryToCreateFor) {
        TravelLogEntry newEntry = new TravelLogEntry(countryToCreateFor, new Date(), new Date());
        m_DBHelper.addLogEntry(newEntry);
        sendEntryUpdateIntent();
    }

    protected void updateEntriesForToday() {
        m_DBHelper = new TravelLogDBHelper(getApplicationContext());
        TravelLogEntry latestToFieldLogEntry = m_DBHelper.getLastEntryByToDate();
        if (latestToFieldLogEntry == null || !latestToFieldLogEntry.getCountry().equals(mCurrentCountry)) {
            createCurrentCountryOneDayEntry();
        } else {
            updateEntryToFieldToToday(latestToFieldLogEntry);
        }
    }

    protected void sendEntryUpdateIntent() {
        Intent updateCompleteIntent = new Intent();
        updateCompleteIntent.setAction(LogEntriesFragment.DB_UPDATED_NOTIFICAITON);
        sendBroadcast(updateCompleteIntent);
    }
}