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
import android.widget.Toast;

import com.ee5.mobile.Interfaces.GraphCallback;
import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.DataCard;
import com.ee5.mobile.SupportClasses.JsonArrayRequest;
import com.ee5.mobile.SupportClasses.RecyclerViewAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import blufi.espressif.params.BlufiParameter;
import java.util.List;
import java.util.Set;

public class OverviewActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    RecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView myRecyclerView;
    ArrayList<DataCard> dataCards = new ArrayList<>();
    ArrayList<String> tempData = new ArrayList<>();
    ArrayList barEntriesSteps;
    ArrayList barEntriesHeartPoints;
    ArrayList barEntriesHeartRate;
    Button profileButton;
    TextView currTemp;
    TextView currHr;
    TextView currSteps;

    ArrayList<Integer> stepsData = new ArrayList<>();
    ArrayList<Integer> heartPointsData = new ArrayList<>();
    ArrayList<Integer> heartRateData = new ArrayList<>();

    private int steps = 0;
    private int stepsRecord = 0;
    private int heartPointsRecord = 0;
    private int hr = 0;
    private int hrRecord = 0;
    private Double temp = 0.0;

    public int value = 0;

    public static int update = 0;

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

    public static int getUpdate() {
        return update;
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

        getUserId();
        getDailySteps(today.getDayOfYear(), false);      //add timer to fetch data every minute? But who would stay for 1 minute at this screen, better to just refresh?
        getSteps();
        getTEMP();
        getHeartRate();
        getHeartRatePoints();
        getLastHeartRate();

        //graphs
        createBarchart();


        //add datacards to recyclerview
        DataCard dataCard1 = new DataCard("Steps", "Last 7 days", String.valueOf(stepsRecord), "Record", barDataSteps, null, stepsData);
        DataCard dataCard2 = new DataCard("Heart points", "Last 7 days", String.valueOf(heartPointsRecord), "Record", barDataHeartPoints, null, heartPointsData);
        DataCard dataCard3 = new DataCard("Heartrate", "Last 10 minutes", String.valueOf(hrRecord), "Peak", null, lineData, heartRateData);

        dataCards.add(dataCard1);
        dataCards.add(dataCard2);
        dataCards.add(dataCard3);
    }

    public void getUserId() {
        userId = Integer.toString(1);
    }

    public void getTEMP() {
        tempData.add(userId);
        APIconnection.getInstance().GETRequest("temperature", tempData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseTemp = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                    responseTemp = curObject.getString("temperature");
                    currTemp.setText(responseTemp);
                    Log.i("API response", responseTemp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getHeartRate() {
        jsonArrayRequest.getJSONArray(response -> {
            try {
                JSONObject user = response.getJSONObject(0);
                String heartRate = user.getString("bpm");
                currHr.setText(heartRate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, prefixURL + "heartRate/" + userId);
    }

    public void getDailySteps(int dayOfTheYear, boolean graphData) {
        currentDailySteps = 0;
        userId = Integer.toString(50);
        jsonArrayRequest.getJSONArray(response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject user = response.getJSONObject(i);
                    String ts = user.getString("ts");
                    if (checkDate(ts, dayOfTheYear) && graphData == false) {
                        currentDailySteps++;
                        currSteps.setText(Integer.toString(currentDailySteps));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, prefixURL + "steps/" + userId);
    }

    public static boolean checkDate(String ts, int dayOfTheYear) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(ts, formatter);
        int day = dateTime.getDayOfYear();
        return day == dayOfTheYear;
    }

    public void getSteps() {
        //get step goal + current steps + record steps this week + add daily steps to arrayList:
        stepsData.clear();
        try {
            DataCard dataCard = getIntent().getExtras().getParcelable("dataCard");
            if (dataCard != null) {
                stepsData.add(dataCard.getDataCardData().get(0));
                stepsData.add(dataCard.getDataCardData().get(1));
                stepsData.add(dataCard.getDataCardData().get(2));
                stepsData.add(dataCard.getDataCardData().get(3));
                stepsData.add(dataCard.getDataCardData().get(4));
                stepsData.add(dataCard.getDataCardData().get(5));
                stepsData.add(dataCard.getDataCardData().get(6));
            }
        } catch (NullPointerException e) {
            stepsData.add(0);
            stepsData.add(0);
            stepsData.add(0);
            stepsData.add(0);
            stepsData.add(0);
            stepsData.add(0);
            stepsData.add(10);
            getDailyStepsData(todayDayOfTheYear, true);
        }
        //getDailyStepsData(todayDayOfTheYear, true);
        /*stepsData.add(0);
        stepsData.add(0);
        stepsData.add(0);
        stepsData.add(0);
        stepsData.add(0);
        stepsData.add(0);
        stepsData.add(0);*/

    }


    public void getDailyStepsData(int dayOfTheYear, boolean graphData) {
        currentDailyStepsData = 0;
        userId = Integer.toString(50);
        jsonArrayRequest.getJSONArray(response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject user = response.getJSONObject(i);
                    String ts = user.getString("ts");
                    /*if (checkDate(ts, dayOfTheYear) && graphData == true) {
                        currentDailyStepsData++;
                    }*/
                    currentDailyStepsData = 10;
                    stepsData.set(i, currentDailyStepsData);
                    Log.d("CallbackAPI", String.valueOf(stepsData.get(i)));
                    //intent();
                    //update = 1;
                    //myRecyclerViewAdapter.notifyItemChanged(i);
                }
                update = 1;
                myRecyclerViewAdapter.notifyDataSetChanged();
                try {
                    Object dataCard = getIntent().getExtras().getParcelable("dataCard");
                    Log.d("OBJECT", String.valueOf(dataCard));
                   /* if (dataCard != null) {
                        stepsData.add(1);
                        stepsData.add(1);
                        stepsData.add(5);
                        stepsData.add(1);
                        stepsData.add(5);
                        stepsData.add(5);
                        stepsData.add(5);
                    }*/
                } catch (NullPointerException e) {
                    Intent detailIntent = new Intent(this, OverviewActivity.class);
                    DataCard dataCard1 = new DataCard("Steps", "Last 7 days", String.valueOf(stepsRecord), "Record", barDataSteps, null, stepsData);
                    detailIntent.putExtra("dataCard", dataCard1);
                    startActivity(detailIntent);
                }
                //graphCallback.onUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, prefixURL + "steps/" + userId);
    }

    public void intent() {
        Intent detailIntent = new Intent(this, OverviewActivity.class);
        DataCard dataCard1 = new DataCard("Steps", "Last 7 days", String.valueOf(stepsRecord), "Record", barDataSteps, null, stepsData);

        detailIntent.putExtra("dataCard", dataCard1);
        startActivity(detailIntent);
    }

    public void getHeartRatePoints() {
        heartPointsData.add(12);
        heartPointsData.add(25);
        heartPointsData.add(19);
        heartPointsData.add(7);
        heartPointsData.add(26);
        heartPointsData.add(31);
        heartPointsData.add(4);
    }

    public void getLastHeartRate() {
        heartRateData.add(65);
        heartRateData.add(66);
        heartRateData.add(65);
        heartRateData.add(65);
        heartRateData.add(67);
        heartRateData.add(66);
        heartRateData.add(67);
        heartRateData.add(65);
        heartRateData.add(66);
        heartRateData.add(66);
        heartRateData.add(67);
    }

    public void getEntriesSteps() {
        barEntriesSteps = new ArrayList<>();
        barEntriesSteps.add(new BarEntry(0f, stepsData.get(0)));
        barEntriesSteps.add(new BarEntry(1f, stepsData.get(1)));
        barEntriesSteps.add(new BarEntry(2f, stepsData.get(2)));
        barEntriesSteps.add(new BarEntry(3f, stepsData.get(3)));
        barEntriesSteps.add(new BarEntry(4f, stepsData.get(4)));
        barEntriesSteps.add(new BarEntry(5f, stepsData.get(5)));
        barEntriesSteps.add(new BarEntry(6f, stepsData.get(6)));
    }

    public void getEntriesHeartPoints() {
        barEntriesHeartPoints = new ArrayList<>();
        barEntriesHeartPoints.add(new BarEntry(0f, heartPointsData.get(0)));
        barEntriesHeartPoints.add(new BarEntry(1f, heartPointsData.get(1)));
        barEntriesHeartPoints.add(new BarEntry(2f, heartPointsData.get(2)));
        barEntriesHeartPoints.add(new BarEntry(3f, heartPointsData.get(3)));
        barEntriesHeartPoints.add(new BarEntry(4f, heartPointsData.get(4)));
        barEntriesHeartPoints.add(new BarEntry(5f, heartPointsData.get(5)));
        barEntriesHeartPoints.add(new BarEntry(6f, heartPointsData.get(6)));
    }

    public void getEntriesHeartrate() {
        barEntriesHeartRate = new ArrayList<>();
        barEntriesHeartRate.add(new BarEntry(0f, heartRateData.get(0)));
        barEntriesHeartRate.add(new BarEntry(1f, heartRateData.get(1)));
        barEntriesHeartRate.add(new BarEntry(2f, heartRateData.get(2)));
        barEntriesHeartRate.add(new BarEntry(3f, heartRateData.get(3)));
        barEntriesHeartRate.add(new BarEntry(4f, heartRateData.get(4)));
        barEntriesHeartRate.add(new BarEntry(5f, heartRateData.get(5)));
        barEntriesHeartRate.add(new BarEntry(6f, heartRateData.get(6)));
        barEntriesHeartRate.add(new BarEntry(7f, heartRateData.get(7)));
        barEntriesHeartRate.add(new BarEntry(8f, heartRateData.get(8)));
        barEntriesHeartRate.add(new BarEntry(9f, heartRateData.get(9)));
    }

    public void createBarchart() {
        getEntriesSteps();
        getEntriesHeartPoints();
        getEntriesHeartrate();

        barDataSetSteps = new BarDataSet(barEntriesSteps, "");
        barDataSteps = new BarData(barDataSetSteps);
        barDataSteps.notifyDataChanged();

        barDataSetHeartPoints = new BarDataSet(barEntriesHeartPoints, "");
        barDataHeartPoints = new BarData(barDataSetHeartPoints);

        lineDataSet = new LineDataSet(barEntriesHeartRate, "");
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
