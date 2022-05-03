package com.ee5.mobile.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.ee5.mobile.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private Context myContext;
    private ArrayList<DataCard> dataCardList;
    private OnItemClickListener myListener;
    ArrayList barEntries;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        myListener = listener;
    }

    public RecyclerViewAdapter(Context context, ArrayList<DataCard> dataCardList) {
        myContext = context;
        this.dataCardList = dataCardList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.activity_recyclerview_item, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {    //here we populate the textviews in the specfic dataCard with the data from each dataCard object
        DataCard currentItem = dataCardList.get(position);

        String dataCardTitle = currentItem.getDataCardTitle();
        String dataCardRecord = currentItem.getDataCardRecord();
        BarData barData = currentItem.getBarData();

        holder.rv_dataCard_Title.setText(dataCardTitle);
        holder.rv_barChart.setData(barData);
        holder.rv_dataCard_record.setText(dataCardRecord);
    }


    @Override
    public int getItemCount() {
        return dataCardList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView rv_dataCard_Title;
        public TextView rv_dataCard_record;
        public BarChart rv_barChart;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_dataCard_Title = itemView.findViewById(R.id.rv_title_name);
            rv_barChart = itemView.findViewById(R.id.BarChart);
            rv_dataCard_record = itemView.findViewById(R.id.rv_activity_data);

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