package com.ee5.mobile.SupportClasses.Ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ee5.mobile.SupportClasses.IFrameBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleService extends Service {

    private final static String TAG = "BleService";
    private static final boolean AUTO_CONNECT = false;
    private final IBinder mBinder = new LocalBinder();
    private int dataSequence = 0;
    private BluetoothSocket mSocket;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        //TODO: maybe implementation of some functions
    };

    private Context context;
    private Handler handler = new Handler();
    private Boolean isConnected;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBleScanner;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private List<BluetoothDevice> deviceList = new ArrayList<>();

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

    public BleService(Context context) {
        this.context = context;
        this.isConnected = false;
        init();
        getScanner();
    }

    public Boolean isConnected() {
        return isConnected;
    }

    public String getConnectedAddress(){
        return mBluetoothDeviceAddress;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private boolean init() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.d(TAG, "init: BluetoothManager not initialised");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
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

    public boolean connect(String address) {
        if (mBluetoothAdapter == null || !BluetoothAdapter.checkBluetoothAddress(address)) {
            Log.e(TAG, "connect: BleAdapter is null or incorrect address");
            return false;
        }
        if ((mBluetoothGatt != null) && (address.equals(mBluetoothDeviceAddress))) {
            Log.w(TAG, "trying to use existing Gatt for connection");
            disconnect();
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "Device not found");
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, AUTO_CONNECT, mGattCallback);
        if (mBluetoothGatt == null) Log.d(TAG, "connect: mGatt is null");
        mBluetoothDeviceAddress = address;
        isConnected = true;
        stopScan();
        return true;
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
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public BluetoothDevice getDeviceAtPosition(int position) {
        return deviceList.get(position);
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

    public int getDeviceListSize() {
        return deviceList.size();
    }

    private void connectSocket(BluetoothDevice device) {
        if (device == null) Log.e(TAG, "wifiProvisionDevice: device is null");
        device.createBond();
        Log.d(TAG, "connectSocket: " + device.getBondState());

        mBluetoothAdapter.cancelDiscovery();

        UUID mUuid = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");


        try {
            mSocket = (BluetoothSocket) device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class)
                                                            .invoke(device,mUuid);
            assert mSocket != null;
            mSocket.connect();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | IOException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
        Log.d(TAG, "Socket state connected: " + mSocket.isConnected());
    }

    public Boolean wifiProvisionDevice(BluetoothDevice device, byte[] ssid, byte[] pass) {
        Log.d(TAG, "wifiProvisionDevice: provision to " + device.getAddress());

        dataSequence +=1;
        byte[] ssidFrame = IFrameBuilder.getSsidDataFrame(ssid, dataSequence);
        connectSocket(device);

        ConnectedThread thread = new ConnectedThread(mSocket);
        thread.write(ssidFrame);
        thread.cancel();
        return true;
    }


    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
        //add more if wanted
    }

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream
        //mmOutStream.write(bytes);
        private BluetoothGattCharacteristic mWriteChar = new BluetoothGattCharacteristic(UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"),BluetoothGattCharacteristic.FORMAT_FLOAT ,BluetoothGattCharacteristic.PERMISSION_WRITE);

        public ConnectedThread(BluetoothSocket socket) {
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            mWriteChar.setValue(bytes);
            mBluetoothGatt.writeCharacteristic(mWriteChar);
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

}
