package com.ee5.mobile.SupportClasses.Ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BleScanner {
    private static final String TAG = "BleScanner";
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private BluetoothLeScanner mBleScanner;

    private void getScanner() {
        if (mBleScanner == null) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                mBleScanner = bluetoothAdapter.getBluetoothLeScanner();
            }
            if (mBleScanner == null) {
                Log.e(TAG, "getScanner: Failed to make new Android L scanner");
            }
        }
    }

    public void startScan() {
        if (mBleScanner != null) {
            ScanSettings settings = new ScanSettings.Builder()
                    .setReportDelay(1000)
                    .build();

            mBleScanner.startScan(null, settings, scanCallback); //TODO: add filter
        }
    }

    public void stopScan() {
        mBleScanner.stopScan(scanCallback);
    }

    public String getDeviceAddressAtPosition(int position){
        return deviceList.get(position).getAddress();
    }

    public String getDeviceNameAtPosition(int position) {
        BluetoothDevice device = deviceList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return device.getAlias();
        } else return device.getName();
    }

    public BluetoothDevice getDeviceAtPosition(int position) {
        return deviceList.get(position);
    }

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            deviceList.add(result.getDevice());
            Log.d(TAG, result.toString());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            if (results == null) {
                Log.e(TAG, "onBatchScanResults: results is null");
            }

            for (ScanResult sr : results) {
                if (sr.getDevice().getName() != null && !deviceList.contains(sr.getDevice())) {
                    deviceList.add(sr.getDevice());
                    Log.d(TAG, "onBatchScanResults: " + sr.getDevice().getName());
                }
                //deviceList.add(sr.getDevice());
            }

            Log.d(TAG, "deviceList: " + deviceList.toString());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, String.valueOf(errorCode));
        }
    };
}
