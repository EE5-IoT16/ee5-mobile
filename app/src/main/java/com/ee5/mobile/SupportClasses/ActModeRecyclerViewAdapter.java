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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import com.ee5.mobile.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

public class ActModeRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context myContext;
    private ArrayList<Activity> activitiesList;
    private OnItemClickListener myListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.activity_activitymode_item, parent, false);
        return new ViewHolderActivityMode(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderActivityMode viewHolderActivityMode = (ViewHolderActivityMode) holder;
        Activity currentItem = activitiesList.get(position);

        viewHolderActivityMode.rv_am_date.setText(formatDate(currentItem.getDateTime()));
        viewHolderActivityMode.rv_am_duration.setText((CharSequence) currentItem.getDuration());
        viewHolderActivityMode.rv_am_steps.setText(currentItem.getSteps());
        viewHolderActivityMode.rv_am_calories.setText(currentItem.getCalories());
        viewHolderActivityMode.rv_am_heartpoints.setText(currentItem.getHeartPoints());
    }

    public String formatDate(Date dateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        return dateFormat.format(dateTime);
    }

    @Override
    public int getItemCount() {
        return activitiesList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        myListener = listener;
    }

    public ActModeRecyclerViewAdapter(Context context, ArrayList<Activity> activitiesList) {
        myContext = context;
        this.activitiesList = activitiesList;
    }

    class ViewHolderActivityMode extends RecyclerView.ViewHolder {
        public TextView rv_am_date;
        public TextView rv_am_duration;
        public TextView rv_am_steps;
        public TextView rv_am_calories;
        public TextView rv_am_heartpoints;

        public ViewHolderActivityMode(@NonNull View itemView) {
            super(itemView);
            rv_am_date = itemView.findViewById(R.id.am_date_data);
            rv_am_duration = itemView.findViewById(R.id.am_duration_data);
            rv_am_steps = itemView.findViewById(R.id.am_steps_data);
            rv_am_calories = itemView.findViewById(R.id.am_calories_data);
            rv_am_heartpoints = itemView.findViewById(R.id.am_HeartPoints_data);

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
