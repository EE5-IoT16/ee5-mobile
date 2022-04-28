package com.ee5.mobile.SupportClasses.Ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ee5.mobile.SupportClasses.IFrameBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleService extends Service {
    private final static String TAG = "BleService";
    private static final boolean AUTO_CONNECT = false;
    private final IBinder mBinder = new LocalBinder();
    private int dataSequence = 0;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;

    private int connectionState;
    private Handler handler = new Handler();
    private Boolean isConnected;

    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectionState = STATE_CONNECTED;
                broadcastUpdate(ACTION_GATT_CONNECTED);
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED;
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                List<BluetoothGattCharacteristic> ch_list = new ArrayList<>();
                gatt.getServices().forEach((BluetoothGattService gs) ->{
                    ch_list.addAll(gs.getCharacteristics());
                });
                ch_list.forEach((BluetoothGattCharacteristic ch) -> {
                    ch.setValue(IFrameBuilder.getCustomDataFrame("a", dataSequence));
                    gatt.writeCharacteristic(ch);
                });
                Log.d(TAG, "onServicesDiscovered: " + ch_list.toString());
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }
    };

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    public boolean init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "init: BluetoothAdapter not obtained");
            return false;
        }
        return true;
    }

    public boolean connect(final String address) {
        Log.d(TAG, "connect: " + address);

        if (mBluetoothAdapter == null || !BluetoothAdapter.checkBluetoothAddress(address)) {
            Log.e(TAG, "connect: BleAdapter not initialized or incorrect address");
            return false;
        }

        try {
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            mBluetoothGatt = device.connectGatt(this, AUTO_CONNECT, mGattCallback);
        } catch (IllegalArgumentException e){
            Log.w(TAG, "connect: Device not found with provided address");
        }
        mBluetoothDeviceAddress = address;
        return true;
    }

    public Boolean isConnected() {
        return isConnected;
    }

    public String getConnectedAddress(){
        return mBluetoothDeviceAddress;
    }

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothDeviceAddress = null;
        isConnected = false;
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public Boolean wifiProvisionDevice(BluetoothDevice device, byte[] ssid, byte[] pass) {
        Log.d(TAG, "wifiProvisionDevice: provision to " + device.getAddress());

        dataSequence +=1;
        byte[] ssidFrame = IFrameBuilder.getSsidDataFrame(ssid, dataSequence);

        return true;
    }
}
