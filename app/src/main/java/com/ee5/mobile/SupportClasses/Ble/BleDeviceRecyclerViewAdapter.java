package com.ee5.mobile.SupportClasses.Ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ee5.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class BleDeviceRecyclerViewAdapter extends RecyclerView.Adapter<BleDeviceRecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<BluetoothDevice> deviceList;
    private Context context;

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.ble_recycler_item, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BleDeviceRecyclerViewAdapter.RecyclerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
