package com.ee5.mobile.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.DataCard;
import com.ee5.mobile.SupportClasses.JsonArrayRequest;
import com.ee5.mobile.SupportClasses.User;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

public class DataCardDetailActivity extends AppCompatActivity {
    private TextView title;
    private TextView title_hr;
    private BarChart barChart;
    private LineChart lineChart;
    private TextInputLayout steps_layout;
    private TextInputLayout steps_layout_hr;
    private TextInputEditText steps_text;
    private TextInputEditText steps_text_hr;
    private TextInputLayout distance_amount;
    private TextInputLayout distance_amount_hr;
    private TextInputEditText distance_text;
    private TextInputEditText distance_text_hr;
    private TextInputEditText completion;
    private TextInputEditText completion_hr;
    private TextView todayDistance;
    private TextView weekSteps;
    private TextView weekDistance;
    private TextView monthSteps;
    private TextView monthDistance;
    private Button day;
    private Button week;
    private Button month;
    private ImageButton returnButton;
    private ImageButton returnButton_hr;


    private User user;

    private JsonArrayRequest jsonArrayRequest;
    private String prefixURL = "https://ee5-huzza.herokuapp.com/";

    ArrayList graphEntries;
    ArrayList<String> apiData = new ArrayList<>();
    DataCard dataCard;
    String graphAxis[] = new String[]{"M", "T", "W", "T", "F", "S", "S"};

    String userId;
    int userHeight;
    int stepGoal;

    int position = 0;
    int dailySteps = 0;
    int weeklySteps = 0;
    int monthlySteps = 0;
    int dailyHp = 0;
    int weeklyHp = 0;
    int monthlyHp = 0;
    int avgHr = 0;
    int maxHr = 0;
    LocalDateTime today;
    int todayDayOfTheYear;

    ArrayList<Integer> dataCardStepDataList = new ArrayList<>();
    ArrayList<Integer> dataCardHpDataList = new ArrayList<>();
    ArrayList<Integer> dataCardHrDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            apiData.clear();
            user = getIntent().getParcelableExtra("user");
            userId = String.valueOf(user.getUserId());
            Log.i("userParcel1", String.valueOf(user.getUserId()));
            apiData.add(userId);
        } catch (Exception e) {
            Log.e("userParcelException", e.toString());
        }

        position = getIntent().getExtras().getInt("dataCardPosition");
        if (position == 2) {    //heart rate
            setContentView(R.layout.activity_recyclerview_detail_hr);
            title_hr = findViewById(R.id.rv_detail_cardTitle_hr);
            lineChart = findViewById(R.id.LineChart_datacard_detail);
            steps_layout_hr = findViewById(R.id.stepsDetail_layout_hr);
            steps_text_hr = findViewById(R.id.stepsDetail_edit_hr);
            completion_hr = findViewById(R.id.completion_edit_hr);
            returnButton_hr = findViewById(R.id.detail_back_btn_hr);
        } else if (position == 0 || position == 1)  {      //steps + heart points
            setContentView(R.layout.activity_recyclerview_detail);
            title = findViewById(R.id.rv_detail_cardTitle);
            barChart = findViewById(R.id.BarChart_datacard_detail);
            day = findViewById(R.id.day);
            week = findViewById(R.id.week);
            month = findViewById(R.id.month);
            steps_layout = findViewById(R.id.stepsDetail_layout);
            steps_text = findViewById(R.id.stepsDetail_edit);
            distance_amount = findViewById(R.id.distance_amount);
            distance_text = findViewById(R.id.distance_edit);
            completion = findViewById(R.id.completion_edit);
            returnButton = findViewById(R.id.detail_back_btn);
        }

        jsonArrayRequest = new JsonArrayRequest(this);

        today = LocalDateTime.now();
        todayDayOfTheYear = today.getDayOfYear();
        getUserHeight();
        getUserDailyGoal();

        dataCard = getIntent().getExtras().getParcelable("dataCard");
        dataCardStepDataList = dataCard.getDataCardStepData();
        dataCardHpDataList = dataCard.getDataCardHpData();
        dataCardHrDataList = dataCard.getDataCardHrData();


        if (position == 2) {
            title_hr.setText(dataCard.getDataCardTitle());
            createLineChart();
            returnButton_hr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailIntent = new Intent(DataCardDetailActivity.this, OverviewActivity.class);
                    detailIntent.putExtra("user", user);
                    startActivity(detailIntent);
                }
            });
        } else if (position == 1) {
            getHp(0);
            createHpBarChart();
            steps_layout.setHint("Heart points");
            distance_amount.setVisibility(View.GONE);
            returnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailIntent = new Intent(DataCardDetailActivity.this, OverviewActivity.class);
                    detailIntent.putExtra("user", user);
                    startActivity(detailIntent);
                }
            });
        } else {
            getSteps(0);
            createStepsBarChart();
            returnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailIntent = new Intent(DataCardDetailActivity.this, OverviewActivity.class);
                    detailIntent.putExtra("user", user);
                    startActivity(detailIntent);
                }
            });
        }

        if(position == 0 || position == 1) {
            title.setText(dataCard.getDataCardTitle());
            day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == 0) getSteps(0);
                    else if (position == 1) getHp(0);
                }
            });

            week.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == 0) getSteps(1);
                    else if (position == 1) getHp(1);
                }
            });

            month.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == 0) getSteps(2);
                    else if (position == 1) getHp(2);
                }
            });
        }
    }

    public static Double calculateDistance(int height, int steps) {
        Double distance = 0.0;
        Double strideLength = height * 0.43;        //https://www.inchcalculator.com/steps-to-distance-calculator/
        distance = (strideLength * steps) / 100000;
        return distance;
    }

    public void getSteps(int value) {
        if (value == 0) {
            APIconnection.getInstance().GETRequest("steps", apiData, new ServerCallback() {
                @Override
                public void onSuccess() {
                    String responseString = "";
                    JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                    try {
                        JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                        responseString = curObject.getString("steps");
                        if (responseString == "null") responseString = "0";
                        steps_text.setText(responseString);
                        dailySteps = Integer.valueOf(responseString);
                        Double distance = calculateDistance(userHeight, dailySteps);
                        distance_text.setText(distance.toString());
                        completion.setText(checkGoalCompletion(0, dailySteps));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (value == 1) {
            APIconnection.getInstance().GETRequest("weekly/steps", apiData, new ServerCallback() {
                @Override
                public void onSuccess() {
                    String responseString = "";
                    JSONArray responseArray = (JSONArray) APIconnection.getInstance().getAPIResponse();
                    try {
                        JSONObject curObject = responseArray.getJSONObject(0);
                        responseString = curObject.getString("totalSteps");
                        if (responseString == "null") responseString = "0";
                        steps_text.setText(responseString);
                        weeklySteps = Integer.valueOf(responseString);
                        Double distance = calculateDistance(userHeight, weeklySteps);
                        distance_text.setText(distance.toString());
                        completion.setText(checkGoalCompletion(1, weeklySteps));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ER", e.toString());
                    }
                }
            });
        } else if (value == 2) {
            APIconnection.getInstance().GETRequest("monthly/steps", apiData, new ServerCallback() {
                @Override
                public void onSuccess() {
                    String responseString = "";
                    JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                    try {
                        JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                        responseString = curObject.getString("totalSteps");
                        if (responseString == "null") responseString = "0";
                        steps_text.setText(responseString);
                        monthlySteps = Integer.valueOf(responseString);
                        Double distance = calculateDistance(userHeight, monthlySteps);
                        distance_text.setText(distance.toString());
                        completion.setText(checkGoalCompletion(2, monthlySteps));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void getHp(int value) {
        if (value == 0) {
            APIconnection.getInstance().GETRequest("heartPoints", apiData, new ServerCallback() {
                @Override
                public void onSuccess() {
                    String responseString = "";
                    JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                    try {
                        JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                        responseString = curObject.getString("heartPoint");
                        if (responseString == "null") responseString = "0";
                        steps_text.setText(responseString);
                        dailyHp = Integer.valueOf(responseString);
                        completion.setText(checkGoalCompletion(0, dailyHp));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (value == 1) {
            APIconnection.getInstance().GETRequest("weekly/heartPoints", apiData, new ServerCallback() {
                @Override
                public void onSuccess() {
                    String responseString = "";
                    JSONArray responseArray = (JSONArray) APIconnection.getInstance().getAPIResponse();
                    try {
                        JSONObject curObject = responseArray.getJSONObject(0);
                        responseString = curObject.getString("totalHeartPoints");
                        if (responseString == "null") responseString = "0";
                        Log.i("HP", responseString);
                        steps_text.setText(responseString);
                        weeklyHp = Integer.valueOf(responseString);
                        completion.setText(checkGoalCompletion(1, weeklyHp));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ER", e.toString());
                    }
                }
            });
        } else if (value == 2) {
            APIconnection.getInstance().GETRequest("monthly/heartPoints", apiData, new ServerCallback() {
                @Override
                public void onSuccess() {
                    String responseString = "";
                    JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                    try {
                        JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                        responseString = curObject.getString("totalHeartPoints");
                        if (responseString == "null") responseString = "0";
                        steps_text.setText(responseString);
                        monthlyHp = Integer.valueOf(responseString);
                        completion.setText(checkGoalCompletion(2, monthlyHp));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void getUserHeight() {
        APIconnection.getInstance().GETRequest("physicaldata", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseTemp = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(0);
                    String height = curObject.getString("height");
                    userHeight = Integer.parseInt(height);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String checkGoalCompletion(int value, int steps) {
        Double percentage = 0.0;

        if (value == 0) {
            percentage = (double) steps / stepGoal;
        } else if (value == 1) {
            percentage = (double) steps / (stepGoal * 7);
        } else if (value == 2) {
            Calendar c = Calendar.getInstance();
            int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            percentage = (double) steps / (stepGoal * monthMaxDays);
        }
        if (percentage > 1) percentage = 1.0;
        percentage *= 100;
        DecimalFormat df = new DecimalFormat("0.00");
        return (df.format(percentage));
    }

    public void getUserDailyGoal() {
        APIconnection.getInstance().GETRequest("goals", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseDailySteps = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(0);
                    responseDailySteps = curObject.getString("dailySteps");
                    stepGoal = Integer.valueOf(responseDailySteps);
                    ;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void createStepsBarChart() {
        graphEntries = new ArrayList<>();
        graphEntries.add(new BarEntry(0f, dataCardStepDataList.get(0)));
        graphEntries.add(new BarEntry(1f, dataCardStepDataList.get(1)));
        graphEntries.add(new BarEntry(2f, dataCardStepDataList.get(2)));
        graphEntries.add(new BarEntry(3f, dataCardStepDataList.get(3)));
        graphEntries.add(new BarEntry(4f, dataCardStepDataList.get(4)));
        graphEntries.add(new BarEntry(5f, dataCardStepDataList.get(5)));
        graphEntries.add(new BarEntry(6f, dataCardStepDataList.get(6)));

        BarDataSet barDataSet = new BarDataSet(graphEntries, "");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barDataSet.setColor(Color.rgb(79, 164, 255));
        barDataSet.setValueTextSize(0);
        barDataSet.setFormSize(0);
        barDataSet.setBarBorderWidth(1);
        barDataSet.setBarBorderColor(Color.rgb(231, 226, 211));

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
        String[] labels = setStepsGraphAxis();
        x.setValueFormatter(new IndexAxisValueFormatter(labels));
        Description description = barChart.getDescription();
        description.setEnabled(false);
    }

    public void createHpBarChart() {
        graphEntries = new ArrayList<>();
        graphEntries.add(new BarEntry(0f, dataCardHpDataList.get(0)));
        graphEntries.add(new BarEntry(1f, dataCardHpDataList.get(1)));
        graphEntries.add(new BarEntry(2f, dataCardHpDataList.get(2)));
        graphEntries.add(new BarEntry(3f, dataCardHpDataList.get(3)));
        graphEntries.add(new BarEntry(4f, dataCardHpDataList.get(4)));
        graphEntries.add(new BarEntry(5f, dataCardHpDataList.get(5)));
        graphEntries.add(new BarEntry(6f, dataCardHpDataList.get(6)));

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
        String[] labels = setStepsGraphAxis();
        x.setValueFormatter(new IndexAxisValueFormatter(labels));
        Description description = barChart.getDescription();
        description.setEnabled(false);
    }

    public String[] setStepsGraphAxis() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = DayOfWeek.from(today);
        int value = dayOfWeek.getValue();
        rotateLeft(graphAxis, value, 7);
        return graphAxis;
    }

    public void rotateLeft(String array[], int steps, int size)        //rotate array of x size by x steps
    {
        for (int i = 0; i < steps; i++) {
            int j;
            String temp;
            temp = array[0];
            for (j = 0; j < size - 1; j++)
                array[j] = array[j + 1];
            array[6] = temp;
        }
    }

    public void createLineChart() {
        int currentValue = 0;
        int avgHr = 0;
        for(int i = 0; i < 10; i++){
            currentValue = dataCard.getDataCardHrData().get(i);
            dataCardHrDataList.add(currentValue);
            avgHr += currentValue;
            if (currentValue > maxHr) {
                maxHr = currentValue;
            }
        }
        avgHr = currentValue/10;
        completion_hr.setText(String.valueOf(maxHr));
        steps_text_hr.setText(String.valueOf(avgHr));


        graphEntries = new ArrayList<>();
        graphEntries.add(new BarEntry(0f, dataCardHrDataList.get(0)));
        graphEntries.add(new BarEntry(1f, dataCardHrDataList.get(1)));
        graphEntries.add(new BarEntry(2f, dataCardHrDataList.get(2)));
        graphEntries.add(new BarEntry(3f, dataCardHrDataList.get(3)));
        graphEntries.add(new BarEntry(4f, dataCardHrDataList.get(4)));
        graphEntries.add(new BarEntry(5f, dataCardHrDataList.get(5)));
        graphEntries.add(new BarEntry(6f, dataCardHrDataList.get(6)));
        graphEntries.add(new BarEntry(7f, dataCardHrDataList.get(7)));
        graphEntries.add(new BarEntry(8f, dataCardHrDataList.get(8)));
        graphEntries.add(new BarEntry(9f, dataCardHrDataList.get(9)));

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
