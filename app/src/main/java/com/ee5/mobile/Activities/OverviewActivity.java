package com.ee5.mobile.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.DataCard;
import com.ee5.mobile.SupportClasses.JsonArrayRequest;
import com.ee5.mobile.SupportClasses.RecyclerViewAdapter;
import com.ee5.mobile.SupportClasses.User;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class OverviewActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    RecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView myRecyclerView;
    ArrayList<DataCard> dataCards = new ArrayList<>();
    ArrayList<String> apiData = new ArrayList<>();
    ArrayList<String> quoteData = new ArrayList<>();
    ArrayList barEntriesSteps;
    ArrayList barEntriesHeartPoints;
    ArrayList entriesHeartRate;
    Button profileButton;
    TextView currTemp;
    TextView currHr;
    TextView currSteps;
    TextView stepsLeft;
    TextView stepsLeftText;
    TextView quoteText;
    TextView goalCompletedText;

    ArrayList<Integer> stepsData = new ArrayList<>();
    ArrayList<Integer> heartPointsData = new ArrayList<>();
    ArrayList<Integer> heartRateData = new ArrayList<>();

    private int steps = 0;
    private String stepsRecord = "";
    private String hpRecord = "";
    private int hr = 0;
    private String hrRecord = "";
    private Double temp = 0.0;

    public static int setGraphAxisSteps = 0;
    public static int setGraphAxisHp = 0;


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
    private User user;
    int stepGoal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        try {
            apiData.clear();
            user = getIntent().getParcelableExtra("user");
            Log.i("userParcel", user.getProfileEmail());
            Log.i("userParcel", user.getUserEmail());
            userId = String.valueOf(user.getUserId());
            Log.i("userParcel", userId);
            apiData.add(userId);
        } catch (Exception e) {
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
            intent.putExtra("user", user);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);

        });

        setup_btn.setOnClickListener(view -> {
            Intent intent = new Intent(OverviewActivity.this, SetupActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_down, android.R.anim.fade_out);

        });

        currTemp = findViewById(R.id.temp_num);
        currHr = findViewById(R.id.hr_num);
        currSteps = findViewById(R.id.step_num);
        stepsLeft = findViewById(R.id.stepsLeft_num);
        stepsLeftText = findViewById(R.id.stepsleft_text);
        quoteText = findViewById(R.id.quote_tv);
        goalCompletedText = findViewById(R.id.goalCompletedText);

        myRecyclerView = findViewById(R.id.recyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter = new RecyclerViewAdapter(this, dataCards);
        myRecyclerViewAdapter.setOnItemClickListener(this);
        myRecyclerView.setAdapter(myRecyclerViewAdapter);


        today = LocalDateTime.now();
        todayDayOfTheYear = today.getDayOfYear();
        currentDailyStepsData = 0;

        setGraphAxisSteps = 0;

        profileButton = (Button) findViewById(R.id.viewProfile_Btn);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OverviewActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        parseJson();

        final Handler handler = new Handler();
        final int delay = 1000;        //every 1 sec

        handler.postDelayed(new Runnable() {
            public void run() {
                getTemperature();
                getHeartRate();
                getStepsToday();
                getDailyGoal();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    public void getQuote() {
        APIconnection.getInstance().GETRequest("quotes", quoteData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    int size = responseArray.length();
                    int value = (int) ((Math.random() * size));
                    JSONObject curObject = responseArray.getJSONObject(value);
                    responseString = curObject.getString("quote");
                    quoteText.setText(getColoredText(responseString, "#4FA4FF"));
                    Log.d("QUOTE", responseString);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("quote", e.toString());
                }
            }
        });
    }

    public Spanned getColoredText(String text, String color) {
        String[] words = text.split(" ");
        int wordCount = words.length;

        int backSpace = words.length / 2;
        words[backSpace] = words[backSpace];

        String[] beforeColor = Arrays.copyOfRange(words, 0, backSpace);
        String[] afterColor = Arrays.copyOfRange(words, backSpace + 1, wordCount);

        String output = String.join(" ", beforeColor) + " " + "<font color=" + color + ">" + words[backSpace] + "</font>" + "<br> " + String.join(" ", afterColor);
        return Html.fromHtml(output);
    }

    public void parseJson() {
        try {
            DataCard dataCard = getIntent().getExtras().getParcelable("dataCard2");
            //Toast.makeText(getApplicationContext(), "try1", Toast.LENGTH_SHORT).show();
            //Log.d("try", dataCard.toString());
            Log.d("try", "record: " + dataCard.getDataCardHrRecord());
            if (dataCard != null) {
                //Toast.makeText(getApplicationContext(), "try", Toast.LENGTH_SHORT).show();
                //getUserId();
                getStepsToday();
                getHeartRate();
                getTemperature();

                getQuote();

                initGraphs();

                //graphs
                setGraphAxisSteps = 1;
                setGraphAxisHp = 1;
                createCharts();

                //daily goal
                getDailyGoal();


                Log.d("try", stepsRecord);
                //add datacards to recyclerview
                DataCard dataCard1 = new DataCard("Steps", "Last 7 days", dataCard.getDataCardStepRecord(), null, null, "Record", barDataSteps, null, null, dataCard.getDataCardStepData(), null, null);
                DataCard dataCard2 = new DataCard("Heart points", "Last 7 days", null, dataCard.getDataCardHpRecord(), null, "Record", null, barDataHeartPoints, null, null, dataCard.getDataCardHpData(), null);
                DataCard dataCard3 = new DataCard("Heartrate", "Last 10 minutes", null, null, dataCard.getDataCardHrRecord(), "Peak", null, null, lineData, null, null, dataCard.getDataCardHrData());

                dataCards.add(dataCard1);
                dataCards.add(dataCard2);
                dataCards.add(dataCard3);
            }
        } catch (NullPointerException e) {
            initGraphs();
            //Toast.makeText(getApplicationContext(), "catch", Toast.LENGTH_SHORT).show();

            //add datacards to recyclerview
            DataCard dataCard1 = new DataCard("Steps", "Last 7 days", stepsRecord, null, null, "Record", barDataSteps, null, null, stepsData, null, null);
            DataCard dataCard2 = new DataCard("Heart points", "Last 7 days", null, hpRecord, hrRecord, "Record", null, barDataHeartPoints, null, null, heartPointsData, null);
            DataCard dataCard3 = new DataCard("Heartrate", "Last 10 minutes", stepsRecord, hpRecord, hrRecord, "Peak", null, null, lineData, null, null, heartRateData);

            dataCards.add(dataCard1);
            dataCards.add(dataCard2);
            dataCards.add(dataCard3);
            Log.e("catch", e.toString());
        }
    }

    public void getTemperature() {
        Log.d("ERROR0", "temp: enter");
        APIconnection.getInstance().GETRequest("temperature", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseTemp = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(0);
                    responseTemp = curObject.getString("temperature");
                    Log.d("ERROR0", "temp: " + responseTemp);
                    currTemp.setText(responseTemp);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ERROR0", "tempError: " + e.toString());
                }
            }
        });
    }

    public void getHeartRate() {
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

    public void getDailyGoal() {
        stepGoal = user.getDailyStepGoal();
        Log.d("goal", String.valueOf(stepGoal));
        checkGoalCompletion(stepGoal);
    }

    public void getStepsToday() {
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


    public void checkGoalCompletion(int stepGoal) {
        if (stepGoal != 0) {
            stepsLeft.setVisibility(View.VISIBLE);
            stepsLeftText.setVisibility(View.VISIBLE);
            int steps = (stepGoal - stepsToday);
            if (steps > 0) {
                stepsLeft.setText(Integer.toString(steps));
                goalCompletedText.setVisibility(View.GONE);
            } else if (steps <= 0 && stepGoal != 0) {
                stepsLeft.setText("");
                stepsLeftText.setText("");
                goalCompletedText.setVisibility(View.VISIBLE);
                goalCompletedText.setText("Today's goal completed!");
            }
        } else {
            goalCompletedText.setVisibility(View.VISIBLE);
            stepsLeft.setVisibility(View.INVISIBLE);
            stepsLeftText.setVisibility(View.INVISIBLE);
            goalCompletedText.setText("No step goal set yet");
        }
    }

    public void initGraphs() {
        stepsData.clear();
        heartPointsData.clear();
        heartRateData.clear();
        try {
            DataCard dataCard = getIntent().getExtras().getParcelable("dataCard2");
            //Log.d("try", dataCard.toString());
            Log.d("try", "record: " + dataCard.getDataCardHrRecord());
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
                stepsData.add(0);
                heartPointsData.add(0);
            }

            for (int i = 0; i < 10; i++) {
                heartRateData.add(0);
            }

            getStepsGraphData();
        }
    }

    public void getStepsGraphData() {
        maxSteps = 0;
        APIconnection.getInstance().GETRequest("steps", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    if (responseArray.length() < 7 || responseArray.getJSONObject(0) == null) {
                        for (int j = 0; j < responseArray.length(); j++) {
                            JSONObject curObject = responseArray.getJSONObject(responseArray.length() - (j + 1));
                            responseString = curObject.getString("steps");
                            int graphValue = Integer.valueOf(responseString);
                            stepsData.set(6 - j, graphValue);
                            if (graphValue > maxSteps) {
                                maxSteps = Integer.valueOf(responseString);
                                stepsRecord = responseString;
                            }
                        }
                    } else {
                        for (int i = 0; i < 7; i++) {
                            JSONObject curObject = responseArray.getJSONObject(responseArray.length() - (i + 1));
                            responseString = curObject.getString("steps");
                            int graphValue = Integer.valueOf(responseString);
                            stepsData.set(6 - i, graphValue);
                            if (graphValue > maxSteps) {
                                maxSteps = Integer.valueOf(responseString);
                                stepsRecord = responseString;
                            }
                        }

                    }
                    getHPGraphData();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ERROR0", String.valueOf(e));
                }
            }
        });
    }

    public void getHPGraphData() {
        maxHp = 0;
        APIconnection.getInstance().GETRequest("heartPoints", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    if (responseArray.length() < 7 || responseArray.getJSONObject(0) == null) {
                        for (int j = 0; j < responseArray.length(); j++) {
                            JSONObject curObject = responseArray.getJSONObject(responseArray.length() - (j + 1));
                            responseString = curObject.getString("heartPoint");
                            int graphValue = 0;
                            graphValue = Integer.valueOf(responseString);
                            heartPointsData.set(6 - j, graphValue);
                            if (graphValue > maxHp) {
                                maxHp = Integer.valueOf(responseString);
                                hpRecord = responseString;
                            }
                        }
                    } else {
                        for (int i = 0; i < 7; i++) {
                            JSONObject curObject = responseArray.getJSONObject(responseArray.length() - (i + 1));
                            responseString = curObject.getString("heartPoint");
                            Log.d("hp", responseString + i);
                            int graphValue = Integer.valueOf(responseString);
                            heartPointsData.set(6 - i, graphValue);
                            if (graphValue > maxHp) {
                                maxHp = Integer.valueOf(responseString);
                                hpRecord = responseString;
                            }
                        }
                    }
                    Log.d("hp", heartPointsData.toString());
                    getHrGraphData();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "testt", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR2", String.valueOf(e));
                }
            }
        });
    }

    public void getHrGraphData() {
        maxHr = 0;
        APIconnection.getInstance().GETRequest("heartRate", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    if (responseArray.length() < 10 || responseArray.getJSONObject(0) == null) {
                        for (int j = 0; j < responseArray.length(); j++) {
                            JSONObject curObject = responseArray.getJSONObject(responseArray.length() - (j + 1));
                            responseString = curObject.getString("bpm");
                            int graphValue = Integer.valueOf(responseString);
                            heartRateData.set(9 - j, graphValue);
                            if (graphValue > maxHr) {
                                maxHr = Integer.valueOf(responseString);
                                hrRecord = responseString;
                            }
                            if (graphValue < minHr)
                                minHr = Integer.valueOf(responseString);
                        }
                    } else {
                        for (int i = 0; i < 10; i++) {
                            JSONObject curObject = responseArray.getJSONObject(responseArray.length() - (i + 1));
                            responseString = curObject.getString("bpm");
                            int graphValue = Integer.valueOf(responseString);
                            heartRateData.set(9 - i, graphValue);
                            if (graphValue > maxHr) {
                                maxHr = Integer.valueOf(responseString);
                                hrRecord = responseString;
                            }
                            if (graphValue < minHr)
                                minHr = Integer.valueOf(responseString);
                        }
                    }
                    Intent detailIntent = new Intent(getApplicationContext(), OverviewActivity.class);
                    DataCard dataCard1 = new DataCard("Steps", "Last 7 days", stepsRecord, hpRecord, hrRecord, "Record",
                            null, null, null, stepsData, heartPointsData, heartRateData);
                    Log.d("hp2", heartPointsData.toString());
                    detailIntent.putExtra("dataCard2", dataCard1);
                    detailIntent.putExtra("user", user);
                    startActivity(detailIntent);
                    Log.d("ERROR", stepsRecord + " / " + hpRecord + " - " + hrRecord);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    //Log.d("ERROR", dataCard1.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ERROR3", String.valueOf(e));
                }
            }
        });
    }

    public void getGraphEntries() {
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
        detailIntent.putExtra("user", user);
        startActivity(detailIntent);
        overridePendingTransition(R.anim.slide_in_up, android.R.anim.fade_out);
    }
}
