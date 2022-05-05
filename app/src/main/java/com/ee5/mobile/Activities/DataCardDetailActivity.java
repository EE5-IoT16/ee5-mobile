package com.ee5.mobile.Activities;

import static com.ee5.mobile.Activities.OverviewActivity.checkDate;
import static com.ee5.mobile.Activities.ProfileActivity.heightUser;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.DataCard;
import com.ee5.mobile.SupportClasses.JsonArrayRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DataCardDetailActivity extends AppCompatActivity {
    private TextView title;
    private BarChart barChart;
    private LineChart lineChart;
    private TextView todaySteps;
    private TextView todayDistance;
    private TextView weekSteps;
    private TextView weekDistance;
    private TextView monthSteps;
    private TextView monthDistance;

    private JsonArrayRequest jsonArrayRequest;
    private String prefixURL = "https://ee5-huzza.herokuapp.com/";

    ArrayList graphEntries;
    DataCard dataCard;

    String userId;

    int position = 0;
    int dailySteps = 0;
    int steps = 0;
    LocalDateTime today;
    int todayDayOfTheYear;

    ArrayList<Integer> dataCardDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getIntent().getExtras().getInt("dataCardPosition");
        if (position == 2) {
            setContentView(R.layout.activity_recyclerview_detail_hr);
        } else {
            setContentView(R.layout.activity_recyclerview_detail);
        }
        jsonArrayRequest = new JsonArrayRequest(this);

        today = LocalDateTime.now();
        todayDayOfTheYear = today.getDayOfYear();

        getUserId();
        getDailySteps(todayDayOfTheYear, false);
        getPeriodSteps(todayDayOfTheYear - 7, todayDayOfTheYear, true);
        getPeriodSteps(todayDayOfTheYear - 30, todayDayOfTheYear, false);     //taken 1 month = 30 days


        title = findViewById(R.id.rv_detail_cardTitle);
        barChart = findViewById(R.id.BarChart_datacard_detail);
        lineChart = findViewById(R.id.LineChart_datacard_detail);
        todaySteps = findViewById(R.id.rv_detail_today_dataSteps);
        todayDistance = findViewById(R.id.rv_detail_today_dataKm);
        weekSteps = findViewById(R.id.rv_detail_week_dataSteps);
        weekDistance = findViewById(R.id.rv_detail_week_dataKm);
        monthSteps = findViewById(R.id.rv_detail_month_dataSteps);
        monthDistance = findViewById(R.id.rv_detail_month_dataKm);

        dataCard = getIntent().getExtras().getParcelable("dataCard");
        dataCardDataList = dataCard.getDataCardData();

        title.setText(dataCard.getDataCardTitle());

        if (position == 2) {
            createLineChart();
        } else {
            createBarChart();
        }
    }

    public static Double calculateDistance(int height, int steps) {
        Double distance = 0.0;
        Double strideLength = height * 0.43;        //https://www.inchcalculator.com/steps-to-distance-calculator/
        distance = strideLength * steps;
        return distance;
    }

    //todo: parse data through intent from overviewActivity
    public void getDailySteps(int dayOfTheYear, boolean graphData) {
        dailySteps = 0;
        userId = Integer.toString(50);
        jsonArrayRequest.getJSONArray(response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject user = response.getJSONObject(i);
                    String ts = user.getString("ts");
                    if (checkDate(ts, dayOfTheYear) && graphData == false) {
                        dailySteps++;

                    }
                }
                todaySteps.setText(Integer.toString(dailySteps));
                todayDistance.setText(Double.toString(calculateDistance(heightUser, dailySteps)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, prefixURL + "steps/" + userId);
    }

    public void getPeriodSteps(int startDayOfTheYear, int endDayOfTheYear, boolean week) {
        steps = 0;
        userId = Integer.toString(50);
        jsonArrayRequest.getJSONArray(response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject user = response.getJSONObject(i);
                    String ts = user.getString("ts");
                    if (checkDatePeriod(ts, startDayOfTheYear, endDayOfTheYear)) {
                        steps++;
                        //Toast.makeText(this, Integer.toString(steps), Toast.LENGTH_LONG).show();

                    }
                }
                if (week) {
                    weekSteps.setText(Integer.toString(steps));
                    weekDistance.setText(Double.toString(calculateDistance(heightUser, dailySteps)));
                } else
                    monthSteps.setText(Integer.toString(steps));
                monthDistance.setText(Double.toString(calculateDistance(heightUser, dailySteps)));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, prefixURL + "steps/" + userId);
    }

    public static boolean checkDatePeriod(String ts, int startDayOfTheYear, int endDayOfTheYear) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(ts, formatter);
        int day = dateTime.getDayOfYear();
        if (day >= startDayOfTheYear && day <= endDayOfTheYear) {
            return true;
        }
        return false;
    }

    public void getUserId() {
        userId = Integer.toString(1);
    }

    public void createBarChart() {
        graphEntries = new ArrayList<>();
        graphEntries.add(new BarEntry(0f, dataCardDataList.get(0)));
        graphEntries.add(new BarEntry(1f, dataCardDataList.get(1)));
        graphEntries.add(new BarEntry(2f, dataCardDataList.get(2)));
        graphEntries.add(new BarEntry(3f, dataCardDataList.get(3)));
        graphEntries.add(new BarEntry(4f, dataCardDataList.get(4)));
        graphEntries.add(new BarEntry(5f, dataCardDataList.get(5)));
        graphEntries.add(new BarEntry(6f, dataCardDataList.get(6)));

        BarDataSet barDataSet = new BarDataSet(graphEntries, "");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barDataSet.setColor(Color.rgb(79, 164, 255));
        barDataSet.setValueTextSize(0);
        barDataSet.setFormSize(0);

        XAxis x = barChart.getXAxis();
        YAxis yLeft = barChart.getAxisLeft();
        YAxis yRight = barChart.getAxisRight();
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
        Description description = barChart.getDescription();
        description.setEnabled(false);
    }

    public void createLineChart() {
        graphEntries = new ArrayList<>();
        graphEntries.add(new BarEntry(0f, dataCardDataList.get(0)));
        graphEntries.add(new BarEntry(1f, dataCardDataList.get(1)));
        graphEntries.add(new BarEntry(2f, dataCardDataList.get(2)));
        graphEntries.add(new BarEntry(3f, dataCardDataList.get(3)));
        graphEntries.add(new BarEntry(4f, dataCardDataList.get(4)));
        graphEntries.add(new BarEntry(5f, dataCardDataList.get(5)));
        graphEntries.add(new BarEntry(6f, dataCardDataList.get(6)));
        graphEntries.add(new BarEntry(7f, dataCardDataList.get(7)));
        graphEntries.add(new BarEntry(8f, dataCardDataList.get(8)));
        graphEntries.add(new BarEntry(9f, dataCardDataList.get(9)));

        LineDataSet lineDataSet = new LineDataSet(graphEntries, "");
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        lineDataSet.setColor(Color.rgb(255, 123, 123));
        lineDataSet.setValueTextSize(0);
        lineDataSet.setFormSize(0);
        lineDataSet.setCircleColor(Color.rgb(255, 123, 123));
        lineDataSet.setCircleHoleColor(Color.rgb(255, 123, 123));

        XAxis x = lineChart.getXAxis();
        YAxis yLeft = lineChart.getAxisLeft();
        YAxis yRight = lineChart.getAxisRight();
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
        Description description = lineChart.getDescription();
        description.setEnabled(false);
    }
}
