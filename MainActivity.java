package com.example.wi_fid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    private ListView DevicesList;
    private Button AttackButton;
    private ListView NetworkList;

    private static final int REQUEST_CODE = 123;
    private static boolean isAttack = true;
    private static List<ScanResult> scanResult;
    private static final ArrayList<WiFiInfo> networks = new ArrayList<>();
    private final WiFiRefresh wiFiRefresh = new WiFiRefresh(143);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.DevicesList    = findViewById(R.id.DevicesList);
        this.AttackButton   = findViewById(R.id.AttackButton);
        this.NetworkList    = findViewById(R.id.NetworkList);
        this.wifiManager    = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        registerReceiver(new WifiBroadcastReceiver(), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        MainActivity _this = this;
        this.AttackButton.setOnClickListener(view -> {
            WiFiInfo attackTo = null;

            for (WiFiInfo inf: networks) {
                if (inf.getStatus()) {
                    attackTo = inf;
                }
            }

            if (attackTo == null) {
                Toast.makeText(_this, "No network was selected", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isAttack) {
                AttackButton.setText(R.string.en_StopAttackButton);
            }
            else {
                AttackButton.setText(R.string.en_StartAttackButton);
            }

            isAttack = !isAttack;
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[] {
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_WIFI_STATE,
                        android.Manifest.permission.ACCESS_NETWORK_STATE
                }, REQUEST_CODE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!this.wiFiRefresh.isAlive())
            this.wiFiRefresh.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            this.wiFiRefresh.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)  {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                this.wifiManager.startScan();
            else
                Toast.makeText(this, "The application cannot work without permissions", Toast.LENGTH_LONG).show();
        }
    }

    public class WifiBroadcastReceiver extends BroadcastReceiver  {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent)   {
            if (intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false))  {
                scanResult = wifiManager.getScanResults();

                MainActivity.this.showNetworks();
            }
        }
    }

    public class WiFiRefresh extends Thread {
        long minPrime;
        WiFiRefresh(long minPrime) { this.minPrime = minPrime; }

        public void run() {
            boolean chosen = false;

            while (true) {
                for (WiFiInfo inf: networks) {
                    if (inf.getStatus()) {
                        chosen = true;
                        break;
                    }

                    chosen = false;
                }

                if (!chosen) {
                    while (!wifiManager.startScan());
                    continue;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void showNetworks() {
        networks.clear();

        for(final ScanResult result: scanResult)  {
            networks.add(new WiFiInfo(result.BSSID, result.SSID));
        }

        NetworkList.setAdapter(new WiFiInfoAdapter(this, R.layout.list_item, networks));
    }

    private void scanDevices(int channel) {

    }
}