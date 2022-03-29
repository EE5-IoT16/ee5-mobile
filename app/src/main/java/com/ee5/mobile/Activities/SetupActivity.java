package com.ee5.mobile.Activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;

import com.ee5.mobile.R;

import blufi.espressif.BlufiCallback;
import blufi.espressif.BlufiClient;

import java.util.Set;


public class SetupActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 0x01;

    BluetoothAdapter bleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);

        bleAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bleAdapter != null){
            System.out.println("bleAdapter not null");
        }

        Set<BluetoothDevice> BondedSet = bleAdapter.getBondedDevices();

        BondedSet.stream()
                .forEach(bluetoothDevice -> Log.d("Bluetooth",bluetoothDevice.getAddress()));

        String espAddress = "C4:DD:57:9E:88:40";

        //BluetoothDevice device = bleAdapter.getRemoteDevice();

        //BlufiClient client = new BlufiClient(this, device);
    }

}
