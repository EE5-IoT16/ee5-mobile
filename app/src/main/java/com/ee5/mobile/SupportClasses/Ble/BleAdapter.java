package com.ee5.mobile.SupportClasses.Ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
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

import com.ee5.mobile.R;

public class BleAdapter extends RecyclerView.Adapter<BleAdapter.RecyclerViewHolder> {
    private final static String TAG = "BleAdapter";
    private BleService mBleService;
    private Context context;
    private AlertDialog.Builder inputDialogueBuilder;
    private BluetoothDevice clickedDevice;

    public BleAdapter(Context context, AlertDialog.Builder builder) {
        this.context = context;
        this.mBleService = new BleService(context);
        this.inputDialogueBuilder = builder;
    }

    public void stopRecycler(){
        mBleService.stopScan();
    }

    public void restartRecycler() {
        mBleService.startScan();
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
        String connectedAddress = mBleService.getConnectedAddress();
        if(connectedAddress != null && connectedAddress == mBleService.getDeviceAddressAtPosition(position) && mBleService.isConnected()){
            holder.connState.setVisibility(View.VISIBLE);
        }

        String name = mBleService.getDeviceNameAtPosition(position);
        Log.d(TAG, "onBindViewHolder: " + name);
        textView.setText(name);

        BluetoothDevice device = mBleService.getDeviceAtPosition(position);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBleService.isConnected() && connectedAddress == mBleService.getDeviceAddressAtPosition(position)){
                    mBleService.disconnect();
                    holder.connState.setVisibility(View.INVISIBLE);
                }else{
                    mBleService.connect(device.getAddress());
                    holder.connState.setVisibility(View.VISIBLE);

                    inputDialogueBuilder.show();
                    //device.fetchUuidsWithSdp();
                    clickedDevice = device;
                }
                stopRecycler();
                Log.d(TAG, "onClick: row " + position);
            }
        });
    }

    public void onWifiInputAccept(byte[] ssid, byte[] pass){
        mBleService.wifiProvisionDevice(clickedDevice, ssid, pass);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mBleService.getDeviceListSize());
        return mBleService.getDeviceListSize();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

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
