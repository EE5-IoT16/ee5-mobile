package com.ee5.mobile.SupportClasses.Ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ee5.mobile.R;

import java.util.ArrayList;

public class BleAdapter extends RecyclerView.Adapter<BleAdapter.RecyclerViewHolder> {

    private BleService mBleService = new BleService();
    private Context context;

    public BleAdapter(){
        mBleService.init(context);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.ble_recycler_item, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BleAdapter.RecyclerViewHolder holder, int position) {
        TextView textView = holder.deviceName;
        BluetoothDevice device = mBleService.getDeviceAtPosition(position);
        String name = device.getName();
        textView.setText(name);
    }

    @Override
    public int getItemCount() {
        return mBleService.getDeviceListSize();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        public TextView deviceName;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = (TextView) itemView.findViewById(R.id.deviceName_tv);
        }
    }
}