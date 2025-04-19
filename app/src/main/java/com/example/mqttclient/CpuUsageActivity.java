package com.example.mqttclient;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

public class CpuUsageActivity {

    private static float currentCpuUsage = 0f;

    public static float getCpuUsage(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        long availMem = memoryInfo.availMem;
        long totalMem = memoryInfo.totalMem;

        currentCpuUsage = (1 - (float) availMem / totalMem) * 100;
        return currentCpuUsage;
    }

    public static void setCpuUsage(final TextView cpuTextView, final Context context) {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float cpuUsage = getCpuUsage(context);
                cpuTextView.setText(String.format("%.2f%%", cpuUsage));
            }
        });
    }

}
