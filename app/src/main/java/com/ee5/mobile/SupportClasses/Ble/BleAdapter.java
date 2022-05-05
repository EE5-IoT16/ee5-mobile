package com.ee5.mobile.SupportClasses.Ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ee5.mobile.Activities.DeviceControlActivity;
import com.ee5.mobile.Activities.OverviewActivity;
import com.ee5.mobile.Activities.SetupActivity;
import com.ee5.mobile.R;

public class BleAdapter extends RecyclerView.Adapter<BleAdapter.RecyclerViewHolder> {
    private final static String TAG = "BleAdapter";
    private BleService mBleService;
    private final Context context;
    private BluetoothDevice clickedDevice;
    private BleScanner mBleScanner;

    public BleAdapter(Context context) {
        this.context = context;
        this.mBleScanner = new BleScanner();
        mBleScanner.getScanner();
        mBleScanner.startScan();
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
        ImageView state = holder.connState;
//        if(connectedAddress != null && connectedAddress == mBleScanner.getDeviceAddressAtPosition(position) && mBleService.isConnected()){
//            holder.connState.setVisibility(View.VISIBLE);
//        }

        String name = mBleScanner.getDeviceNameAtPosition(position);
        textView.setText(name);

        String address = mBleScanner.getDeviceAddressAtPosition(position);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBleScanner.stopScan();
                Intent intent = new Intent(context, DeviceControlActivity.class);
                intent.putExtra("address", address);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mBleScanner.getDeviceListSize());
        return mBleScanner.getDeviceListSize();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        public TextView deviceName;
        public ConstraintLayout layout;
        public ImageView connState;
        public ConstraintLayout input_layout;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = (TextView) itemView.findViewById(R.id.deviceName_tv);
            layout = (ConstraintLayout) itemView.findViewById(R.id.recycler_item_layout);
            connState = (ImageView) itemView.findViewById(R.id.state_iv);
            connState.setVisibility(View.INVISIBLE);
            input_layout = itemView.findViewById(R.id.input_layout);
        }
    }
}
