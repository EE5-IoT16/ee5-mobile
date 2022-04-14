package com.ee5.mobile.Activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.Ble.BleAdapter;
import com.ee5.mobile.SupportClasses.Ble.BleService;

import java.util.ArrayList;
import java.util.List;


public class SetupActivity extends AppCompatActivity {
    String TAG = "SetupActivity";
    private static final int REQUEST_PERMISSION = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);

        ImageButton back_btn = findViewById(R.id.back_btn);
        Button refresh_btn = findViewById(R.id.refresh_btn);
        RecyclerView bleDeviceRecycler = findViewById(R.id.Ble_rv);


        BleAdapter bleAdapter = new BleAdapter(this);
        bleDeviceRecycler.setAdapter(bleAdapter);

        bleDeviceRecycler.setLayoutManager(new LinearLayoutManager(this));

        back_btn.setOnClickListener(v -> {
            bleAdapter.stopRecycler();
            Intent intent = new Intent(SetupActivity.this, OverviewActivity.class);
            startActivity(intent);
        });

        refresh_btn.setOnClickListener(v -> {
            bleAdapter.restartRecycler();
            bleAdapter.notifyDataSetChanged();
        });

        String espAddress = "C4:DD:57:9E:88:0E";

        //mBleService.connect(espAddress); //address needs to be dynamically set from UI

    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        //TODO: maybe implementation of some functions
    };

}
