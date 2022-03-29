package com.ee5.mobile.SupportClasses;

import android.bluetooth.BluetoothDevice;

public class BleDevice {
    private String name;
    private String macAddr;
    private BluetoothDevice bleDevice;

    public BleDevice (String name, String macAddr,BluetoothDevice bleDevice){
        this.name = name;
        this.macAddr = macAddr;
        this.bleDevice = bleDevice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public BluetoothDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BluetoothDevice bleDevice) {
        this.bleDevice = bleDevice;
    }
}
