package com.ee5.mobile.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.Ble.BleService;

public class DeviceControlActivity extends AppCompatActivity {
    private BleService bleService;
    private final static String TAG = "DeviceControlActivity";
    private static String address;
    private boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        address = getIntent().getStringExtra("address");
        Log.d(TAG, "onCreate: " + address);

        Intent gattServiceIntent = new Intent(this, BleService.class);
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleService = ((BleService.LocalBinder) service).getService();
            if (bleService != null) {
                if(!bleService.init()){
                    Log.e(TAG, "onServiceConnected: Unable to initialize Bluetooth");
                    exitIntent();
                }
                bleService.connect(address);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleService = null;
        }
    };

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
                connected = true;
            } else if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connected = false;
            } else if (BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            // Show all the supported services and characteristics on the user interface.
                bleService.getSupportedGattServices();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bleService != null) {
            final boolean result = bleService.connect(address);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver);
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }

    private void exitIntent(){
        final Intent intent = new Intent(DeviceControlActivity.this, SetupActivity.class);
        startActivity(intent);
    }

}
