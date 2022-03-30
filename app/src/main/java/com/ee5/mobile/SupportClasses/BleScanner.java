package com.ee5.mobile.SupportClasses;

import android.bluetooth.BluetoothAdapter;

public class BleScanner {

    private static BluetoothAdapter mBluetoothAdapter;

    //TODO: implement scancallbackclass
    public static void startScan(){
        //TODO: startLeScan deprecated -> BleScanner class needs to be used now
        if (mBluetoothAdapter != null) mBluetoothAdapter.startLeScan(null);
    }

    public static void stopScan() {
        if (mBluetoothAdapter != null) mBluetoothAdapter.stopLeScan(null);
    }

}
