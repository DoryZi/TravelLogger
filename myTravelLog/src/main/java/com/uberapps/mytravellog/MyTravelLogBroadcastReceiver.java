package com.uberapps.mytravellog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.countrypicker.CountryPicker;

public class MyTravelLogBroadcastReceiver extends BroadcastReceiver {

    public final static String INTENT_EXTRA_COUNTRY_FIELD = "CountryFieldExtra";
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent startServiceIntent = new Intent(context, LogMyLocationService.class);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String currentCountryISO = tm.getNetworkCountryIso();
        Log.i("TravelLogger", "Received Intent on broadcaster, country code: " + currentCountryISO);
        String currentCountry = CountryPicker.getCountryFromCode(currentCountryISO, context);
        Log.i("TravelLogger"," Intent Country code is " + currentCountry);
        startServiceIntent.putExtra(INTENT_EXTRA_COUNTRY_FIELD,currentCountry);
        context.startService(startServiceIntent);
    }
}