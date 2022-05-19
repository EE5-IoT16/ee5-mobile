package com.ee5.mobile.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.DataCard;
import com.ee5.mobile.SupportClasses.JsonArrayRequest;
import com.ee5.mobile.SupportClasses.RecyclerViewAdapter;
import com.ee5.mobile.SupportClasses.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class OverviewActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    RecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView myRecyclerView;
    ArrayList<DataCard> dataCards = new ArrayList<>();
    ArrayList<String> apiData = new ArrayList<>();
    ArrayList barEntriesSteps;
    ArrayList barEntriesHeartPoints;
    ArrayList entriesHeartRate;
    Button profileButton;
    TextView currTemp;
    TextView currHr;
    TextView currSteps;
    TextView stepsLeft;
    TextView stepsLeftText;

    ArrayList<Integer> stepsData = new ArrayList<>();
    ArrayList<Integer> heartPointsData = new ArrayList<>();
    ArrayList<Integer> heartRateData = new ArrayList<>();

    private int steps = 0;
    private int stepsRecord = 0;
    private int heartPointsRecord = 0;
    private int hr = 0;
    private int hrRecord = 0;
    private Double temp = 0.0;

    public static int setGraphAxis = 0;

    private int stepsToday = 0;
    private int stepsLeftToday = 0;

    public static int update = 0;
    public static int maxSteps = 0;
    public static int maxHp = 0;
    public static int maxHr = 170;
    public static int minHr = 30;

    BarData barDataSteps;
    BarDataSet barDataSetSteps;
    BarData barDataHeartPoints;
    BarDataSet barDataSetHeartPoints;
    LineData lineData;
    LineDataSet lineDataSet;

    private JsonArrayRequest jsonArrayRequest;
    private final String prefixURL = "https://ee5-huzza.herokuapp.com/";
    private String userId;
    private int currentDailySteps;
    private int currentDailyStepsData;
    LocalDateTime today;
    int todayDayOfTheYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        try{
            User user = getIntent().getParcelableExtra("user");
            Log.i("userParcel", user.getProfileEmail());
            Log.i("userParcel", user.getUserEmail());
        }
        catch(Exception e){
            Log.e("userParcelException", e.toString());
        }

        jsonArrayRequest = new JsonArrayRequest(this);

        APIconnection.getInstance(this);

        ConstraintLayout constraintLayout = findViewById(R.id.overview_layout);

        TextView stepsLeft_num = findViewById(R.id.stepsLeft_num);
        TextView quote = findViewById(R.id.quote_tv);
        Button setup_btn = findViewById(R.id.setupBtn);
        Button viewProfile_btn = findViewById(R.id.viewProfile_Btn);

        viewProfile_btn.setOnClickListener(v -> {
            Intent intent = new Intent(OverviewActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        setup_btn.setOnClickListener(view -> {
            Intent intent = new Intent(OverviewActivity.this, SetupActivity.class);
            startActivity(intent);
        });

        currTemp = findViewById(R.id.temp_num);
        currHr = findViewById(R.id.hr_num);
        currSteps = findViewById(R.id.step_num);
        stepsLeft = findViewById(R.id.stepsLeft_num);
        stepsLeftText = findViewById(R.id.stepsleft_text);

        //TODO: get number from User object
        stepsLeft_num.setText("100");

        String quoteString = "Look in the mirror, that’s the only competition.";

        //quoteString = getColoredText();

        //quote = get from http request to some api
        quote.setText(getColoredText(quoteString, "#4FA4FF"));

        myRecyclerView = findViewById(R.id.recyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter = new RecyclerViewAdapter(this, dataCards);
        myRecyclerViewAdapter.setOnItemClickListener(this);
        myRecyclerView.setAdapter(myRecyclerViewAdapter);

        today = LocalDateTime.now();
        todayDayOfTheYear = today.getDayOfYear();
        currentDailyStepsData = 0;

        profileButton = (Button) findViewById(R.id.viewProfile_Btn);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OverviewActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        parseJson();
    }


    public Spanned getColoredText(String text, String color) {
        String[] words = text.split(" ");
        int wordCount = words.length;

        int backSpace = 4;
        words[backSpace] = words[backSpace];

        String[] beforeColor = Arrays.copyOfRange(words, 0, backSpace);
        String[] afterColor = Arrays.copyOfRange(words, backSpace + 1, wordCount);

        String output = String.join(" ", beforeColor) + " " + "<font color=" + color + ">" + words[backSpace] + "</font>" + "<br> " + String.join(" ", afterColor);
        return Html.fromHtml(output);
    }

    public void parseJson() {

        try {
            DataCard dataCard = getIntent().getExtras().getParcelable("dataCard2");
            if (dataCard != null) {
                getUserId();
                getHeartRate();
                getTemperature();
                getStepsToday();

                initGraphs();

                //graphs
                setGraphAxis = 1;
                createCharts();

                //daily goal
                getDailyGoal();

                //add datacards to recyclerview
                DataCard dataCard1 = new DataCard("Steps", "Last 7 days", String.valueOf(stepsRecord), "Record", barDataSteps, null, null, stepsData, null, null);
                DataCard dataCard2 = new DataCard("Heart points", "Last 7 days", String.valueOf(heartPointsRecord), "Record", null, barDataHeartPoints, null, null, heartPointsData, null);
                DataCard dataCard3 = new DataCard("Heartrate", "Last 10 minutes", String.valueOf(hrRecord), "Peak", null, null, lineData, null, null, heartRateData);

                dataCards.add(dataCard1);
                dataCards.add(dataCard2);
                dataCards.add(dataCard3);
            }
        } catch (NullPointerException e) {
            getUserId();


            initGraphs();

            //add datacards to recyclerview
            DataCard dataCard1 = new DataCard("Steps", "Last 7 days", String.valueOf(stepsRecord), "Record", barDataSteps, null, null, stepsData, null, null);
            DataCard dataCard2 = new DataCard("Heart points", "Last 7 days", String.valueOf(heartPointsRecord), "Record", null, barDataHeartPoints, null, null, heartPointsData, null);
            DataCard dataCard3 = new DataCard("Heartrate", "Last 10 minutes", String.valueOf(hrRecord), "Peak", null, null, lineData, null, null, heartRateData);

            dataCards.add(dataCard1);
            dataCards.add(dataCard2);
            dataCards.add(dataCard3);
        }
    }

    public void getUserId() {
        userId = Integer.toString(1);
    }

    public void getTemperature() {
        apiData.clear();
        apiData.add(userId);
        APIconnection.getInstance().GETRequest("temperature", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseTemp = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                    responseTemp = curObject.getString("temperature");
                    currTemp.setText(responseTemp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getHeartRate() {
        apiData.clear();
        apiData.add(userId);
        APIconnection.getInstance().GETRequest("heartRate", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                    responseString = curObject.getString("bpm");
                    currHr.setText(responseString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getStepsToday() {
        apiData.clear();
        apiData.add(userId);
        APIconnection.getInstance().GETRequest("steps", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                    responseString = curObject.getString("steps");
                    currSteps.setText(responseString);
                    stepsToday = Integer.valueOf(responseString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getDailyGoal() {
        apiData.clear();
        apiData.add(userId);
        APIconnection.getInstance().GETRequest("goals", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseDailySteps = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(0);
                    responseDailySteps = curObject.getString("dailySteps");
                    int stepGoal = Integer.valueOf(responseDailySteps);
                    checkGoalCompletion(stepGoal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void checkGoalCompletion(int stepGoal) {
        int steps = (stepGoal - stepsToday);
        if (steps > 0) {
            stepsLeft.setText(Integer.toString(steps));
        } else {
            stepsLeft.setText("");
            stepsLeftText.setText("Today's goal completed!");
        }
    }

    public void initGraphs() {
        stepsData.clear();
        heartPointsData.clear();
        heartRateData.clear();
        try {
            DataCard dataCard = getIntent().getExtras().getParcelable("dataCard2");
            if (dataCard != null) {
                for (int i = 0; i < 7; i++) {
                    stepsData.add(dataCard.getDataCardStepData().get(i));
                    heartPointsData.add(dataCard.getDataCardHpData().get(i));
                }
                for (int i = 0; i < 10; i++) {
                    heartRateData.add(dataCard.getDataCardHrData().get(i));
                }

            }
        } catch (NullPointerException e) {
            for (int i = 0; i < 7; i++) {
                stepsData.add(1);
                heartPointsData.add(1);
            }

            for (int i = 0; i < 10; i++) {
                heartRateData.add(1);
            }

            getStepsGraphData();
        }
    }

    public static boolean checkDate(String ts, int dayOfTheYear) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(ts, formatter);
        int day = dateTime.getDayOfYear();
        return day == dayOfTheYear;
    }

    public void getStepsGraphData() {
        apiData.clear();
        apiData.add(userId);

        APIconnection.getInstance().GETRequest("steps", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {

                    for (int i = 0; i < 7; i++) {
                        JSONObject curObject = responseArray.getJSONObject(responseArray.length() - (i + 1));
                        responseString = curObject.getString("steps");
                        stepsData.set(6 - i, Integer.valueOf(responseString));
                        if (Integer.valueOf(responseString) > maxSteps)
                            maxSteps = Integer.valueOf(responseString);
                    }
                    getHPGraphData();
                } catch (JSONException e) {
                    e.printStackTrace();

                    Log.d("ERROR", String.valueOf(e));
                }
            }
        });
    }

    public void getHPGraphData() {
        userId = String.valueOf(50);        //not enough records in user 1 table //todo: what if not enough data
        apiData.clear();
        apiData.add(userId);

        APIconnection.getInstance().GETRequest("steps", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {

                    for (int i = 0; i < 7; i++) {
                        JSONObject curObject = responseArray.getJSONObject(responseArray.length() - (i + 1));
                        responseString = curObject.getString("steps");
                        heartPointsData.set(6 - i, Integer.valueOf(responseString));
                        if (Integer.valueOf(responseString) > maxHp)
                            maxHp = Integer.valueOf(responseString);
                    }
                    getHrGraphData();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ERROR", String.valueOf(e));
                }
            }
        });
    }

    public void getHrGraphData() {
        userId = String.valueOf(50);        //not enough records in user 1 table //todo: what if not enough data
        apiData.clear();
        apiData.add(userId);

        APIconnection.getInstance().GETRequest("heartRate", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    for (int i = 0; i < 10; i++) {
                        JSONObject curObject = responseArray.getJSONObject(responseArray.length() - (i + 1));
                        responseString = curObject.getString("bpm");
                        heartRateData.set(9 - i, Integer.valueOf(responseString));
                        if (Integer.valueOf(responseString) > maxHr)
                            maxHr = Integer.valueOf(responseString);
                        if (Integer.valueOf(responseString) < minHr)
                            minHr = Integer.valueOf(responseString);
                    }
                    Intent detailIntent = new Intent(getApplicationContext(), OverviewActivity.class);
                    DataCard dataCard1 = new DataCard("Steps", "Last 7 days", String.valueOf(stepsRecord), "Record",
                                                        barDataSteps, barDataHeartPoints, null, stepsData, heartPointsData, heartRateData);
                    detailIntent.putExtra("dataCard2", dataCard1);
                    startActivity(detailIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ERROR", String.valueOf(e));
                }
            }
        });
    }

    public void getGraphEntries(){
        barEntriesSteps = new ArrayList<>();
        barEntriesHeartPoints = new ArrayList<>();
        entriesHeartRate = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            barEntriesSteps.add(new BarEntry(Float.parseFloat(i + "f"), stepsData.get(i)));
            barEntriesHeartPoints.add(new BarEntry(Float.parseFloat(i + "f"), heartPointsData.get(i)));
        }
        for (int i = 0; i < 10; i++) {
            entriesHeartRate.add(new BarEntry(Float.parseFloat(i + "f"), heartRateData.get(i)));
        }
    }

    public void createCharts() {
        getGraphEntries();

        barDataSetSteps = new BarDataSet(barEntriesSteps, "");
        barDataSteps = new BarData(barDataSetSteps);

        barDataSetHeartPoints = new BarDataSet(barEntriesHeartPoints, "");
        barDataHeartPoints = new BarData(barDataSetHeartPoints);

        lineDataSet = new LineDataSet(entriesHeartRate, "");
        lineData = new LineData(lineDataSet);

        barDataSetSteps.setColor(Color.rgb(79, 164, 255));
        barDataSetSteps.setValueTextSize(0);
        barDataSetSteps.setFormSize(0);

        barDataSetHeartPoints.setColor(Color.rgb(79, 164, 255));
        barDataSetHeartPoints.setValueTextSize(0);
        barDataSetHeartPoints.setFormSize(0);

        lineDataSet.setColor(Color.rgb(255, 123, 123));
        lineDataSet.setValueTextSize(0);
        lineDataSet.setFormSize(0);
        lineDataSet.setCircleColor(Color.rgb(255, 123, 123));
        lineDataSet.setCircleHoleColor(Color.rgb(255, 123, 123));
    }


    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, DataCardDetailActivity.class);
        DataCard clickedItem = dataCards.get(position);
        detailIntent.putExtra("dataCard", clickedItem);
        detailIntent.putExtra("dataCardPosition", position);
        startActivity(detailIntent);
    }
}
