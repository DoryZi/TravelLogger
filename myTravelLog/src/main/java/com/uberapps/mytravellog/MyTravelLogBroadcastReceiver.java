package com.uberapps.mytravellog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.countrypicker.CountryPicker;

public class MyTravelLogBroadcastReceiver extends BroadcastReceiver {

    public final static String INTENT_EXTRA_COUNTRY_FIELD = "CountryFieldExtra";

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
            case "android.intent.action.SIM_STATE_CHANGED":
                logCountry(context);
                break;
            default:
                break;
        }
    }

    static void logCountry(Context context) {
        Intent startServiceIntent = new Intent(context, LogMyLocationService.class);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String currentCountryISO = tm.getNetworkCountryIso();
        Log.i("TravelLogger", "Received Intent on broadcaster, country code: " + currentCountryISO);
        String currentCountry = CountryPicker.getCountryFromCode(currentCountryISO, context);
        Log.i("TravelLogger", " Intent Country code is " + currentCountry);
        startServiceIntent.putExtra(INTENT_EXTRA_COUNTRY_FIELD, currentCountry);
        context.startService(startServiceIntent);
    }
}

class ConnectionStateMonitor extends ConnectivityManager.NetworkCallback {

    final NetworkRequest networkRequest;
    final Context context;

    public ConnectionStateMonitor(Context context) {
        this.context = context;
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }

    public void enable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    @Override
    public void onAvailable(Network network) {
        MyTravelLogBroadcastReceiver.logCountry(context);
    }
}