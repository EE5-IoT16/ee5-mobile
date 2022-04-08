package com.ee5.mobile.SupportClasses.Ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BleService extends Service {

    private final static String TAG = "BleService";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    private static final boolean AUTO_CONNECT = false;

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

    public boolean init(Context context){
        if(mBluetoothManager == null){

            mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null){
                Log.d(TAG, "BluetoothManager not initialised");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null){
            Log.e(TAG, "BluetoothAdapter not obtained");
            return false;
        }
        return true;
    }

    public boolean connect(String address){
        if(mBluetoothAdapter == null || BluetoothAdapter.checkBluetoothAddress(address)){
            Log.e(TAG, "BleAdapter is null or incorrect address");
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
            Log.e("BleService","BluetoothAdapter not initialized");
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

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        //TODO: maybe implementation of some functions
    };

}
