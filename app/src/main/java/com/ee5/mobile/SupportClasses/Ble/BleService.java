package com.ee5.mobile.SupportClasses.Ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class BleService extends Service {

    private final static String TAG = "BleService";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBleScanner;

    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private Context context;

    private static final boolean AUTO_CONNECT = false;

    public BleService(Context context){
        this.context = context;
        init();
        getScanner();
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            deviceList.add(result.getDevice());
           // if (result == null)
            Log.d(TAG, result.toString());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            if (results == null){
                Log.e(TAG, "onBatchScanResults: results is null" );
            }

            for (ScanResult sr : results) {
                if(sr.getDevice().getName() != null && !deviceList.contains(sr.getDevice())){
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

    private boolean init(){
        if(mBluetoothManager == null){
            mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null){
                Log.d(TAG, "init: BluetoothManager not initialised");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null){
            Log.e(TAG, "init: BluetoothAdapter not obtained");
            return false;
        }
        return true;
    }

    private void getScanner() {
        if (mBleScanner == null) {
            BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
            if (bluetoothAdapter != null) {
                mBleScanner = bluetoothAdapter.getBluetoothLeScanner();
            }
            if (mBleScanner == null) {
                Log.e(TAG, "getScanner: Failed to make new Android L scanner");
            }
        }
    }

    public void startScan(){
        if(mBleScanner != null){
            ScanSettings settings = new ScanSettings.Builder()
                    .setReportDelay(1000)
                    .build();

            mBleScanner.startScan(null, settings, scanCallback); //TODO: add filter
        }
    }

    public void stopScan(){
        mBleScanner.stopScan(scanCallback);
    }

    public boolean connect(String address){
        if(mBluetoothAdapter == null || !BluetoothAdapter.checkBluetoothAddress(address)){
            Log.e(TAG, "connect: BleAdapter is null or incorrect address");
            return false;
        }
        if((mBluetoothGatt != null) && (address.equals(mBluetoothDeviceAddress))){
            Log.w(TAG, "trying to use existing Gatt for connection");
            return mBluetoothGatt.connect();
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if(device == null){
            Log.e(TAG, "Device not found");
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, AUTO_CONNECT, mGattCallback);
        mBluetoothDeviceAddress = address;
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG,"BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public BluetoothDevice getDeviceAtPosition(int position){
        return deviceList.get(position);
    }

    public String getDeviceNameAtPosition(int position){
        BluetoothDevice device = deviceList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return device.getAlias();
        }
        else return device.getName();
    }

    public int getDeviceListSize(){
        return deviceList.size();
    }


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        //TODO: maybe implementation of some functions
    };

}
