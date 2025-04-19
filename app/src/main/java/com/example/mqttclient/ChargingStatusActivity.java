package com.example.mqttclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

public class ChargingStatusActivity {

    private final Context context;
    private final TextView chargingStatusTextView;
    private boolean isCharging; // Variable für Ladezustand
    private final BroadcastReceiver chargingReceiver;

    public ChargingStatusActivity(Context context, TextView chargingStatusTextView) {
        this.context = context;
        this.chargingStatusTextView = chargingStatusTextView;

        chargingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

                if (isCharging) {
                    chargingStatusTextView.setText("Gerät lädt...");
                    chargingStatusTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                } else {
                    chargingStatusTextView.setText("Gerät lädt nicht");
                    chargingStatusTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_light));
                }
            }
        };
    }

    public void startChargingStatusMonitoring() {
        context.registerReceiver(chargingReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void stopChargingStatusMonitoring() {
        context.unregisterReceiver(chargingReceiver);
    }

    public boolean isDeviceCharging() {
        return isCharging;
    }
}
