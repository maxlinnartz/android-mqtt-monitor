package com.example.mqttclient;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.widget.TextView;

public class BatteryLevelActivity {

    private static int currentBatteryLevel = 0;
    private static float batteryTemp = 0.0f;

    public static int getBatteryLevel(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);

        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            currentBatteryLevel = (int) ((level / (float) scale) * 100);
        }

        return currentBatteryLevel;
    }

    public static void setBatteryLevel(final TextView batteryTextView, final Context context) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int batteryLevel = getBatteryLevel(context);
                batteryTextView.setText(String.format("%d%%", batteryLevel));
                handler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    public static float getBatteryTemp(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);
        if (batteryStatus != null) {
            batteryTemp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0f;
        }
        return batteryTemp;
    }

    public static void setBatteryTemp(final TextView batteryTempTextView, final Context context) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float batterytemp = getBatteryTemp(context);
                batteryTempTextView.setText(String.format("%.1f °C", batterytemp));
                handler.postDelayed(this, 2000);
            }
        }, 2000);
    }
}
