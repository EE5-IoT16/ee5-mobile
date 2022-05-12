package com.ee5.mobile.SupportClasses;

import static com.ee5.mobile.Activities.OverviewActivity.update;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.ee5.mobile.Activities.OverviewActivity;
import com.ee5.mobile.Interfaces.GraphCallback;
import com.ee5.mobile.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;


public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private Context myContext;
    private ArrayList<DataCard> dataCardList;
    private OnItemClickListener myListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(myContext).inflate(R.layout.activity_recyclerview_item, parent, false);
            return new ViewHolderOne(v);
        } else {
            View v = LayoutInflater.from(myContext).inflate(R.layout.activity_recyclerview_item_hr, parent, false);
            return new ViewHolderTwo(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (dataCardList.get(position).getDataCardTitle() == "Heartrate") {
            //bind viewHolderTwo
            ViewHolderTwo viewHolderTwo = (ViewHolderTwo) holder;
            DataCard currentItem = dataCardList.get(position);

            viewHolderTwo.rv_dataCard_Title_hr.setText(currentItem.getDataCardTitle());
            viewHolderTwo.rv_dataCard_timeIndicator_hr.setText(currentItem.getDataCardTimeIndicator());
            viewHolderTwo.rv_dataCard_record_hr.setText(currentItem.getDataCardRecord());
            viewHolderTwo.rv_dataCard_recordText_hr.setText(currentItem.getDataCardRecordText());
            viewHolderTwo.rv_lineChart.setData(currentItem.getlineData());

        } else {
            //bind viewHolderOne
            ViewHolderOne viewHolderOne = (ViewHolderOne) holder;
            DataCard currentItem = dataCardList.get(position);


            viewHolderOne.rv_dataCard_Title.setText(currentItem.getDataCardTitle());
            viewHolderOne.rv_dataCard_timeIndicator.setText(currentItem.getDataCardTimeIndicator());
            viewHolderOne.rv_dataCard_record.setText(currentItem.getDataCardRecord());
            viewHolderOne.rv_dataCard_recordText.setText(currentItem.getDataCardRecordText());
            viewHolderOne.rv_barChart.setData(currentItem.getBarData());

            viewHolderOne.rv_barChart.notifyDataSetChanged();
            viewHolderOne.rv_barChart.invalidate();

        }
    }


    //return int which defines the type of layout you want at that position
    @Override
    public int getItemViewType(int position) {
        if (dataCardList.get(position).getDataCardTitle() == "Heartrate") {
            return 1;       //refers to viewHolderTwo
        }
        return 0;   //refers to viewHolderTwo
    }

    @Override
    public int getItemCount() {
        return dataCardList.size();
    }

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

    public class ViewHolderOne extends RecyclerView.ViewHolder {
        public TextView rv_dataCard_Title;
        public TextView rv_dataCard_timeIndicator;
        public TextView rv_dataCard_record;
        public TextView rv_dataCard_recordText;
        public BarChart rv_barChart;

        public ViewHolderOne(@NonNull View itemView) {
            super(itemView);
            rv_dataCard_Title = itemView.findViewById(R.id.rv_title_name);
            rv_dataCard_timeIndicator = itemView.findViewById(R.id.rv_time_indicator);
            rv_barChart = itemView.findViewById(R.id.BarChart);
            rv_dataCard_record = itemView.findViewById(R.id.rv_activity_data);
            rv_dataCard_recordText = itemView.findViewById(R.id.rv_activity_text);

            XAxis x = rv_barChart.getXAxis();
            YAxis yLeft = rv_barChart.getAxisLeft();
            YAxis yRight = rv_barChart.getAxisRight();
            x.setDrawAxisLine(false);
            x.setDrawGridLines(false);
            x.setDrawGridLinesBehindData(false);
            yLeft.setDrawAxisLine(false);
            yRight.setDrawAxisLine(false);
            yLeft.setEnabled(false);
            yRight.setEnabled(false);
            x.setPosition(XAxis.XAxisPosition.BOTTOM);
            x.setTextColor(Color.argb(180, 255, 255, 255));
            x.setDrawGridLines(false);
            String[] labels = {"M", "T", "W", "T", "F", "S", "S"};
            x.setValueFormatter(new IndexAxisValueFormatter(labels));
            Description description = rv_barChart.getDescription();
            description.setEnabled(false);
            yLeft.setAxisMinimum(0f); // start at zero
            yLeft.setAxisMaximum(10f); // the axis maximum is 100


            rv_barChart.notifyDataSetChanged();
            rv_barChart.invalidate();


            itemView.setOnClickListener(v -> {
                if (myListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        myListener.onItemClick(position);
                    }
                }
            });
        }

        public void updateGraph() {
            rv_barChart.notifyDataSetChanged();
            rv_barChart.invalidate();
        }
    }


    class ViewHolderTwo extends RecyclerView.ViewHolder {
        public TextView rv_dataCard_Title_hr;
        public TextView rv_dataCard_timeIndicator_hr;
        public TextView rv_dataCard_record_hr;
        public TextView rv_dataCard_recordText_hr;
        public LineChart rv_lineChart;

        public ViewHolderTwo(@NonNull View itemView) {
            super(itemView);
            rv_dataCard_Title_hr = itemView.findViewById(R.id.rv_title_name_hr);
            rv_dataCard_timeIndicator_hr = itemView.findViewById(R.id.rv_time_indicator_hr);
            rv_lineChart = itemView.findViewById(R.id.LineChart);
            rv_dataCard_record_hr = itemView.findViewById(R.id.rv_activity_data_hr);
            rv_dataCard_recordText_hr = itemView.findViewById(R.id.rv_activity_text_hr);

            XAxis x = rv_lineChart.getXAxis();
            YAxis yLeft = rv_lineChart.getAxisLeft();
            YAxis yRight = rv_lineChart.getAxisRight();
            x.setDrawAxisLine(false);
            x.setDrawGridLines(false);
            x.setDrawGridLinesBehindData(false);
            yLeft.setDrawAxisLine(false);
            yRight.setDrawAxisLine(false);
            yLeft.setEnabled(false);
            yRight.setEnabled(false);
            x.setPosition(XAxis.XAxisPosition.BOTTOM);
            x.setTextColor(Color.argb(180, 255, 255, 255));
            x.setDrawGridLines(false);
            String[] labels = {"10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
            x.setValueFormatter(new IndexAxisValueFormatter(labels));
            Description description = rv_lineChart.getDescription();
            description.setEnabled(false);

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
