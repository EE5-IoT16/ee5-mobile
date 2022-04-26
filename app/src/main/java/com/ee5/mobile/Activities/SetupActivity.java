package com.ee5.mobile.Activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.Ble.BleAdapter;
import com.ee5.mobile.SupportClasses.Ble.BleService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SetupActivity extends AppCompatActivity {
    String TAG = "SetupActivity";
    private static final int REQUEST_PERMISSION = 0x01;
    private String ssid_text = "";
    private String pass_text = "";
    private BleAdapter bleAdapter;
    private final static String espAddress = "C4:DD:57:9E:88:0E";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);



        ImageButton back_btn = findViewById(R.id.back_btn);
        Button refresh_btn = findViewById(R.id.refresh_btn);
        RecyclerView bleDeviceRecycler = findViewById(R.id.Ble_rv);

        bleAdapter = new BleAdapter(this, getDialogueBuilder());
        bleDeviceRecycler.setAdapter(bleAdapter);

        bleDeviceRecycler.setLayoutManager(new LinearLayoutManager(this));

        back_btn.setOnClickListener(v -> {
            bleAdapter.stopRecycler();
            exitIntent();
        });

        refresh_btn.setOnClickListener(v -> {
            bleAdapter.restartRecycler();
            bleAdapter.notifyDataSetChanged();
        });

    }

    private void exitIntent(){
        final Intent intent = new Intent(SetupActivity.this, OverviewActivity.class);
        startActivity(intent);
    }

    @NotNull
    private AlertDialog.Builder getDialogueBuilder() {
        AlertDialog.Builder inputDialogueBuilder = new AlertDialog.Builder(this);
        inputDialogueBuilder.setTitle("Title");
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
                bleAdapter.onWifiInputAccept(ssid_text.getBytes(), pass_text.getBytes());
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
