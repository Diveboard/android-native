package com.diveboard.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetworkUtils {
    public static boolean isWifiAlive(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiMgr.isWifiEnabled() && wifiMgr.getConnectionInfo().getNetworkId() != -1;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connMgr.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }
}
