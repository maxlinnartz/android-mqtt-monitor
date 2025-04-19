package com.example.mqttclient;

import android.util.Log;
import android.widget.TextView;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

public class DeviceIP {

    public static void setDeviceIpAddress(TextView ipTextView) {
        String ipAddress = getDeviceIpAddress();
        ipTextView.setText("IP Adresse: " + ipAddress);
    }

    public static String getDeviceIpAddress() {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses())) {
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("IP Address", "Error getting IP address", e);
        }
        return "IP not available";
    }
}
