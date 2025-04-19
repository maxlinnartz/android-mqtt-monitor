package com.example.mqttclient;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.widget.TextView;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class wlanActivity {

    public static String getSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    String ssid = wifiInfo.getSSID();
                    if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                        ssid = ssid.substring(1, ssid.length() - 1);
                    }
                    return ssid;
                }
            } else {
                return "Location Berechtigung fehlt";
            }
        }
        return "WLAN nicht verbunden";
    }
    public static int getWLANSpeed(Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo.getLinkSpeed();
            }
        }
        return -1;
    }

    public static void setWLAN(final TextView wlanTextView, final Context context) {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String wlanName = getSSID(context);
                wlanTextView.setText(String.format("%s", wlanName));
            }
        });
    }

    public static void setWLANspeed(final TextView wlanSpeedTextView, final Context context) {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long wlanSpeed = getWLANSpeed(context);
                wlanSpeedTextView.setText(String.format("%d Mbps", wlanSpeed));
            }
        });
    }
}
