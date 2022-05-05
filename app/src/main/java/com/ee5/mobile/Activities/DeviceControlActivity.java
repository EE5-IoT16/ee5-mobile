package com.ee5.mobile.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.Ble.BleService;

import org.jetbrains.annotations.NotNull;

public class DeviceControlActivity extends AppCompatActivity {
    private BleService bleService;
    private final static String TAG = "DeviceControlActivity";
    private static String address;
    private boolean connected = false;
    private String ssid_text = "";
    private String pass_text = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        address = getIntent().getStringExtra("address");
        Log.d(TAG, "onCreate: " + address);

        Intent gattServiceIntent = new Intent(this, BleService.class);
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        AlertDialog.Builder dialog = getDialogueBuilder();
        dialog.show();
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
                Log.d(TAG, "onReceive: ACTION GATT SERVICES DISCOVERED");
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

    @NotNull
    private AlertDialog.Builder getDialogueBuilder() {
        AlertDialog.Builder inputDialogueBuilder = new AlertDialog.Builder(this);
        inputDialogueBuilder.setTitle("Wifi credentials");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.wifi_input_dialogue, (ViewGroup) findViewById(R.id.input_layout), false);
        // Set up the input
        final EditText ssid = (EditText) viewInflated.findViewById(R.id.ssid);
        final EditText password = (EditText) viewInflated.findViewById(R.id.password);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputDialogueBuilder.setView(viewInflated);

        // Set up the buttons
        inputDialogueBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ssid_text = ssid.getText().toString();
                pass_text = password.getText().toString();
                Log.d(TAG, "Dialogue onClick: " + ssid_text);
                Log.d(TAG, "Dialogue onClick: " + pass_text);
                bleService.wifiProvisionDevice(ssid_text, pass_text);
            }
        });
        inputDialogueBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return inputDialogueBuilder;
    }

}
