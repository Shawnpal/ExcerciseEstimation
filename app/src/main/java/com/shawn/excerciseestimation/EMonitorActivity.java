package com.shawn.excerciseestimation;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
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
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EMonitorActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private PieChart PieChart;
    private List<Exercise> Elist = new ArrayList<Exercise>();
    private List<Walks> Wlist = new ArrayList<Walks>();
    private TextView Averagetext,HeartBeattext;

    private BarChart BarChart;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        ExerciseList ExerciseAPI = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("FirstName", "Shawn") ;

        Call<List<Exercise>> loadSizeCall = ExerciseAPI.loadExercise(map);
        Call<List<Walks>> loadBarCall = ExerciseAPI.loadWalks(map);
        PieChart = findViewById(R.id.piechart);
        BarChart = findViewById(R.id.barchart);
        HeartBeattext = findViewById(R.id.textHeartBeat);
        Averagetext = findViewById(R.id.textAverageMins);



      loadSizeCall.enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                for(Exercise size: response.body()) {
                    Elist.add(size);

                }

                setPieChart();

            }

          @Override
          public void onFailure(Call<List<Exercise>> call, Throwable t) {
              Toast.makeText(EMonitorActivity.this, t.getMessage(),Toast.LENGTH_LONG);
          }


        });

        loadBarCall.enqueue(new Callback<List<Walks>>() {


            @Override
            public void onResponse(Call<List<Walks>> call, Response<List<Walks>> response) {

                for(Walks size: response.body()) {
                    Wlist.add(size);

                }
                setBarChart();
            }

            @Override
            public void onFailure(Call<List<Walks>> call, Throwable t) {

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



        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();
        ArrayList<BarEntry> values3 = new ArrayList<>();
        ArrayList<BarEntry> values4 = new ArrayList<>();

        values1.add(new BarEntry(1, (float) (Math.random() * 2)));
        values2.add(new BarEntry(1, (float) (Math.random() * 4)));
        values3.add(new BarEntry(1, (float) (Math.random() * 2)));
        values4.add(new BarEntry(1, (float) (Math.random() * 4)));

        BarDataSet set1, set2, set3, set4;

        if (BarChart.getData() != null && BarChart.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) BarChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) BarChart.getData().getDataSetByIndex(1);
            set3 = (BarDataSet) BarChart.getData().getDataSetByIndex(2);
            set4 = (BarDataSet) BarChart.getData().getDataSetByIndex(3);
            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            set4.setValues(values4);
            BarChart.getData().notifyDataChanged();
            BarChart.notifyDataSetChanged();

        } else {
            // create 4 DataSets
            set1 = new BarDataSet(values1, "Company A");
            set1.setColor(Color.rgb(104, 241, 175));
            set2 = new BarDataSet(values2, "Company B");
            set2.setColor(Color.rgb(164, 228, 251));
            set3 = new BarDataSet(values3, "Company C");
            set3.setColor(Color.rgb(242, 247, 158));
            set4 = new BarDataSet(values4, "Company D");
            set4.setColor(Color.rgb(255, 102, 0));

            BarData data = new BarData(set1, set2, set3, set4);
            data.setValueFormatter(new LargeValueFormatter());


            BarChart.setData(data);
        }

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
        for(Exercise E: list ) {

            int ExerTime = Integer.valueOf(E.getExerciseTime().replace(" ", ""));
            total = total + ExerTime;
            entries.add(new PieEntry(ExerTime));
        }

        int average = total/list.size();
        Averagetext.setText("Average time = " + average + " mins");
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
