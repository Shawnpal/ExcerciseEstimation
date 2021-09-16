package com.shawn.excerciseestimation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.shawn.excerciseestimation.Retrofit.Exercise;
import com.shawn.excerciseestimation.Retrofit.RestClient;
import com.shawn.excerciseestimation.Retrofit.RetrofitInterface;
import com.shawn.excerciseestimation.Retrofit.Steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EMonitorActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private PieChart PieChart;
    private List<Exercise> Elist ;
    private List<Steps> Wlist ;
    private TextView Averagetext, HeartBeattext;
    String  Email;
    private BarChart BarChart;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        RetrofitInterface ExerciseAPI = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        Intent intent = getIntent();
        Email = intent.getStringExtra("Email");
        map.put("email", Email);

        Call<List<Exercise>> loadSizeCall = ExerciseAPI.loadExercise(map);
        Call<List<Steps>> loadBarCall = ExerciseAPI.loadSteps(map);
        PieChart = findViewById(R.id.piechart);
        BarChart = findViewById(R.id.barchart);
        HeartBeattext = findViewById(R.id.textHeartBeat);
        Averagetext = findViewById(R.id.textAverageMins);


        loadSizeCall.enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                Elist = new ArrayList<Exercise>();
                for (Exercise size : response.body()) {
                    Elist.add(size);
                }
                setPieChart();

            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Toast.makeText(EMonitorActivity.this, t.getMessage(), Toast.LENGTH_LONG);
            }


        });

        loadBarCall.enqueue(new Callback<List<Steps>>() {


            @Override
            public void onResponse(Call<List<Steps>> call, Response<List<Steps>> response) {
                Wlist = new ArrayList<Steps>();
                for (Steps size : response.body()) {
                    Wlist.add(size);

                }
                setBarChart();

            }

            @Override
            public void onFailure(Call<List<Steps>> call, Throwable t) {

            }


        });
    }

    private void setBarChart() {

        BarChart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        BarChart.setPinchZoom(false);

        BarChart.setDrawBarShadow(false);
        BarChart.setDrawGridBackground(false);


        BarChart.getAxisLeft().setDrawGridLines(false);

        BarChart.getLegend().setEnabled(false);


        BarChart.setFitBars(true);


        Legend l = BarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = BarChart.getXAxis();

        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);


        YAxis leftAxis = BarChart.getAxisLeft();

        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        BarChart.getAxisRight().setEnabled(false);


        //Setting data

        float groupSpace = 0.08f;
        float barSpace = 0.03f; // x4 DataSet
        float barWidth = 0.2f; // x4 DataSet
        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"
        ArrayList<ArrayList<BarEntry>> values = new ArrayList<>();
        int index = 0;
        for (Steps s : Wlist) {
            ArrayList<BarEntry> value = new ArrayList<>();
            value.add(new BarEntry(1,s.getLength() ));
            values.add(value);

            index++;

        }
        BarDataSet set0,set1, set2, set3, set4 ,set5,set6;
        set0 = new BarDataSet(values.get(0), Wlist.get(0).getDate());
        set0.setColor(Color.rgb(104, 241, 175));
        set1 = new BarDataSet(values.get(1), Wlist.get(1).getDate());
        set1.setColor(Color.rgb(104, 241, 175));
        set2 = new BarDataSet(values.get(2), Wlist.get(2).getDate());
        set2.setColor(Color.rgb(104, 241, 175));
        set3 = new BarDataSet(values.get(3), Wlist.get(3).getDate());
        set3.setColor(Color.rgb(104, 241, 175));
        set4 = new BarDataSet(values.get(4), Wlist.get(4).getDate());
        set4.setColor(Color.rgb(104, 241, 175));
        set5 = new BarDataSet(values.get(5), Wlist.get(5).getDate());
        set5.setColor(Color.rgb(104, 241, 175));
        set6 = new BarDataSet(values.get(6), Wlist.get(6).getDate());
        set6.setColor(Color.rgb(104, 241, 175));

        BarData data = new BarData(set0,set1, set2, set3, set4,set5,set6);
        data.setValueFormatter(new LargeValueFormatter());


        BarChart.setData(data);


        // specify the width each bar should have
        BarChart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        BarChart.getXAxis().setAxisMinimum(0);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        BarChart.getXAxis().setAxisMaximum(0 + BarChart.getBarData().getGroupWidth(groupSpace, barSpace) * 5);
        BarChart.groupBars(0, groupSpace, barSpace);
        BarChart.invalidate();
    }

    private void setPieChart() {


        PieChart.setUsePercentValues(true);
        PieChart.getDescription().setEnabled(false);
        PieChart.setExtraOffsets(5, 10, 5, 5);

        PieChart.setDragDecelerationFrictionCoef(0.95f);


        PieChart.setDrawHoleEnabled(true);
        PieChart.setHoleColor(Color.WHITE);

        PieChart.setTransparentCircleColor(Color.WHITE);
        PieChart.setTransparentCircleAlpha(110);

        PieChart.setHoleRadius(58f);
        PieChart.setTransparentCircleRadius(61f);
        PieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        PieChart.setRotationEnabled(true);
        PieChart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        PieChart.setOnChartValueSelectedListener(this);
        PieChart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = PieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        PieChart.setEntryLabelColor(Color.WHITE);
        PieChart.setEntryLabelTextSize(12f);

        setData(Elist);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    private void setData(List<Exercise> list) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        int total = 0;
        for (Exercise E : list) {

            int ExerTime = Integer.valueOf(E.getExerciseTime().replace(" ", ""));
            total = total + ExerTime;
            entries.add(new PieEntry(ExerTime));
        }
        int average = 0;
        if (list.size() > 0)
             average = total / list.size();
        Averagetext.setText("Average Exercise Time = " + average + " mins");
        HeartBeattext.setText("Heart Beat: 40 paces");


        PieDataSet dataSet = new PieDataSet(entries, "Exercise Percentage");

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        PieChart.setData(data);

        // undo all highlights
        PieChart.highlightValues(null);

        PieChart.invalidate();


    }


}
