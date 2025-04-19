package com.example.mqttclient;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private Mqtt3AsyncClient mqttClient;
    private Handler handler = new Handler();
    private float currentCpuUsage = 0f;
    private int currentBatteryLevel = 0;
    private static float batteryTemp = 0.0f;
    private static long ramUsage = 0;
    private static long ramtotal = 0;
    private String wlanName = "";
    private static long wlanSpeed = 0;
    private int publishInterval = 2000; //   Intervall für das Senden an den Broker in millisekunden
    private int cpuCheckInterval = 2000;
    private Runnable cpuCheckRunnable;
    private Runnable publishRunnable;
    private Runnable publishChargingStatusRunnable;
    private ChargingStatusActivity chargingStatusActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        // TextViews setzen
        TextView ipTextView = findViewById(R.id.ipTextView);
        TextView cpuUsageTextView = findViewById(R.id.cpu_usage_textview);
        TextView batteryTextView = findViewById(R.id.batteryPercentageTextView);
        TextView chargingStatusTextView = findViewById(R.id.chargingStatusTextView);
        TextView batteryTempTextView = findViewById(R.id.batteryTempTextView);
        TextView ramTextView = findViewById(R.id.ram_usage_textview);
        TextView wlanTextView = findViewById(R.id.wlan_nameTextView);
        TextView wlanSpeedTextView = findViewById(R.id.wlan_SpeedTextView);

        // TextViews füllen
        DeviceIP.setDeviceIpAddress(ipTextView);
        CpuUsageActivity.setCpuUsage(cpuUsageTextView,this);
        BatteryLevelActivity.setBatteryLevel(batteryTextView,this);
        BatteryLevelActivity.setBatteryTemp(batteryTempTextView,this);
        RamUsageActivity.setRamUsage(ramTextView,this);
        wlanActivity.setWLAN(wlanTextView,this);
        wlanActivity.setWLANspeed(wlanSpeedTextView,this);

        //chargingStatus Überwachung starten
        chargingStatusActivity = new ChargingStatusActivity(this, chargingStatusTextView);
        chargingStatusActivity.startChargingStatusMonitoring();

        // MQTT Client initialisieren
        mqttClient = MqttClient.builder()
                .useMqttVersion3()
                .serverHost("localhost")  // MQTT Broker IP
                .serverPort(1883)
                .buildAsync();

        // connection zum Broker herstellen
        mqttClient.connectWith()
                .simpleAuth()
                .username("admin")
                .password("admin".getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        Log.e("MQTT", "Connection failed", throwable);
                    } else {
                        Log.d("MQTT", "Connected successfully");
                        startCpuUsageChecks(cpuUsageTextView);
                        startPublishingCpuUsage();
                        startPublishingBatteryLevel();
                        startPublishingChargingStatus();
                        startPublishingBatteryTemp();
                        startPublishingRamUsage();
                        startPublishingRamTotal();
                        startPublishingWLANname();
                        startPublishingWLANSpeed();
                    }
                });
    }

    //  periodische Abfragen der CPU-Auslastung
    private void startCpuUsageChecks(TextView cpuUsageTextView) {
        cpuCheckRunnable = new Runnable() {
            @Override
            public void run() {
                currentCpuUsage = CpuUsageActivity.getCpuUsage(MainActivity.this);  // CPU-Auslastung holen
                cpuUsageTextView.setText(String.format("%.2f%%", currentCpuUsage));  // Anzeige im TextView
                handler.postDelayed(this, cpuCheckInterval);  // Wiederhole alle 2 Sekunden
            }
        };
        handler.post(cpuCheckRunnable);  // Starte das erste Mal
    }

    // starten des senden der cpu an den MQTT-Broker
    private void startPublishingCpuUsage() {
        publishRunnable = new Runnable() {
            @Override
            public void run() {
                publishPerformanceMetrics("cpuUsage", String.format("%.2f", currentCpuUsage));
                handler.postDelayed(this, publishInterval);
            }
        };
        handler.post(publishRunnable);
    }



    private void startPublishingBatteryLevel() {
        publishRunnable = new Runnable() {
            @Override
            public void run() {
                currentBatteryLevel = BatteryLevelActivity.getBatteryLevel(MainActivity.this);
                publishPerformanceMetrics("BatteryLevel", String.format("%d", currentBatteryLevel));
                handler.postDelayed(this, publishInterval);
            }
        };
        handler.post(publishRunnable);
    }

    private void startPublishingChargingStatus() {
        publishChargingStatusRunnable = new Runnable() {
            @Override
            public void run() {
                int chargingStatus = chargingStatusActivity.isDeviceCharging() ? 1 : 0;
                publishPerformanceMetrics("chargingStatus", String.valueOf(chargingStatus));
                handler.postDelayed(this, publishInterval);
            }
        };
        handler.post(publishChargingStatusRunnable);
    }
    private void startPublishingBatteryTemp() {
        publishRunnable = new Runnable() {
            @Override
            public void run() {
                batteryTemp = BatteryLevelActivity.getBatteryTemp(MainActivity.this);
                publishPerformanceMetrics("BatteryTemp", String.format("%.2f", batteryTemp));
                handler.postDelayed(this, publishInterval);
            }
        };
        handler.post(publishRunnable);
    }

    private void startPublishingRamUsage() {
        publishRunnable = new Runnable() {
            @Override
            public void run() {
                ramUsage = RamUsageActivity.getRamUsage(MainActivity.this, RamUsageActivity.RamType.USED_RAM);
                publishPerformanceMetrics("RamUsage", String.format("%d", ramUsage));
                handler.postDelayed(this, publishInterval);  //  alle 10 Sek
            }
        };
        handler.post(publishRunnable);
    }
    private void startPublishingRamTotal() {
        publishRunnable = new Runnable() {
            @Override
            public void run() {
                ramtotal = RamUsageActivity.getRamUsage(MainActivity.this, RamUsageActivity.RamType.TOTAL_RAM);
                publishPerformanceMetrics("RamTotal", String.format("%d", ramtotal));
                handler.postDelayed(this, publishInterval);
            }
        };
        handler.post(publishRunnable);
    }
    private void startPublishingWLANname() {
        publishRunnable = new Runnable() {
            @Override
            public void run() {
                wlanName = wlanActivity.getSSID(MainActivity.this);
                publishPerformanceMetrics("SSID", String.format("%s", wlanName));
                handler.postDelayed(this, publishInterval);  //  alle 10 Sek
            }
        };
        handler.post(publishRunnable);
    }

    private void startPublishingWLANSpeed() {
        publishRunnable = new Runnable() {
            @Override
            public void run() {
                wlanSpeed = wlanActivity.getWLANSpeed(MainActivity.this);
                publishPerformanceMetrics("wlanSpeed", String.format("%d", wlanSpeed));
                handler.postDelayed(this, publishInterval);
            }
        };
        handler.post(publishRunnable);
    }

    // methode zum publishen der parameter
    private void publishPerformanceMetrics(String indicator, String value) {
        String deviceIp = DeviceIP.getDeviceIpAddress();  // hole die IP des Geräts
        String topic = deviceIp + "/" + indicator;  // Erstelle dynamischen Topic basierend auf der IP

        mqttClient.publishWith()
                .topic(topic)
                .payload(value.getBytes(StandardCharsets.UTF_8))
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        Log.e("MQTT", "Publish failed: " + throwable.getMessage());
                    } else {
                        Log.d("MQTT", "Published " + indicator + " to " + topic);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(cpuCheckRunnable);
        handler.removeCallbacks(publishRunnable);
        handler.removeCallbacks(publishChargingStatusRunnable);
        chargingStatusActivity.stopChargingStatusMonitoring();
    }
}
