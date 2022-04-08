package com.ee5.mobile.Activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.Ble.BleService;


public class SetupActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 0x01;

    private BluetoothAdapter bleAdapter;
    BleService mBleService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);

        ImageButton back_btn = findViewById(R.id.back_btn);
        RecyclerView bleDeviceRecycler = findViewById(R.id.Ble_rv);
        bleDeviceRecycler.setLayoutManager(new LinearLayoutManager(this));

        back_btn.setOnClickListener(v -> {
            Intent intent = new Intent(SetupActivity.this, OverviewActivity.class);
            startActivity(intent);
        });

        String espAddress = "C4:DD:57:9E:88:0E";


        mBleService = new BleService();

        if(!mBleService.init(this)){
            Log.d("SetupActivity", "mBleService not correctly initialised");
        }

        mBleService.connect(espAddress); //address needs to be dynamically set from UI

    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        //TODO: maybe implementation of some functions
    };

}
