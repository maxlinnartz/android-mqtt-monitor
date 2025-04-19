package com.example.mqttclient;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.widget.TextView;

public class RamUsageActivity {

    public enum RamType {
        TOTAL_RAM,
        USED_RAM
    }

    public static long getRamUsage(Context context, RamType ramType) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return -1;
        }

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        long totalRam;
        long usedRam
                ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            totalRam = memoryInfo.totalMem;
            usedRam = totalRam - memoryInfo.availMem; // totalRAM - verfügbaren = used
        } else {

            Debug.MemoryInfo debugMemoryInfo = new Debug.MemoryInfo();
            Debug.getMemoryInfo(debugMemoryInfo);
            usedRam = debugMemoryInfo.getTotalPss() * 1024L;
            totalRam = -1;
        }

        if (ramType == RamType.TOTAL_RAM) {
            return totalRam / (1024 * 1024); // ausgabe in MB
        } else if (ramType == RamType.USED_RAM) {
            return usedRam / (1024 * 1024); //  in MB
        }

        return -1;
    }

    public static void setRamUsage(final TextView ramTextView, final Context context) {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long ramUsage = getRamUsage(context, RamType.USED_RAM);
                long ramTotal = getRamUsage(context, RamType.TOTAL_RAM);
                ramTextView.setText(String.format("%d MB / %d MB", ramUsage, ramTotal));
            }
        });
    }
}
