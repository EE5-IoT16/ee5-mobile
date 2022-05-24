package com.ee5.mobile.SupportClasses;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.ee5.mobile.Activities.FallActivity;
import com.ee5.mobile.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

public class FallsRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context myContext;
    ArrayList<Fall> falls;
    private OnItemClickListener myListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.falls_item, parent, false);
        return new ViewHolderFalls(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderFalls viewHolderFalls = (ViewHolderFalls) holder;
        Fall currentItem = falls.get(position);

        viewHolderFalls.rv_fall_date.setText(currentItem.getDate());
        viewHolderFalls.rv_fall_time.setText(currentItem.getTime());
    }

    @Override
    public int getItemCount() {
        return falls.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        myListener = listener;
    }

    public FallsRecyclerViewAdapter(Context context, ArrayList<Fall> falls) {
        myContext = context;
        this.falls = falls;
    }

    class ViewHolderFalls extends RecyclerView.ViewHolder {
        public TextView rv_fall_date;
        public TextView rv_fall_time;

        public ViewHolderFalls(@NonNull View itemView) {
            super(itemView);
            rv_fall_date = itemView.findViewById(R.id.rv_falls_date);
            rv_fall_time = itemView.findViewById(R.id.rv_falls_time);

            itemView.setOnClickListener(v -> {
                if (myListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        myListener.onItemClick(position);
                    }
                }
            });
        }
    }
}
