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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EMonitorActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private PieChart PieChart;
    private List<Exercise> Elist = new ArrayList<Exercise>();
    private TextView Averagetext,HeartBeattext;

    private BarChart BarChart;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        ExerciseList ExerciseAPI = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("FirstName", "Shawn") ;

        Call<List<Exercise>> loadSizeCall = ExerciseAPI.loadExercise(map);

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

                setChart();
                setBarChart();
            }

          @Override
          public void onFailure(Call<List<Exercise>> call, Throwable t) {
              Toast.makeText(EMonitorActivity.this, t.getMessage(),Toast.LENGTH_LONG);
          }


        });


    }
    private void setBarChart() {

        BarChart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        BarChart.setPinchZoom(false);

        BarChart.setDrawBarShadow(false);
        BarChart.setDrawGridBackground(false);

        XAxis xAxis = PieChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        BarChart.getAxisLeft().setDrawGridLines(false);

        BarChart.getLegend().setEnabled(false);


        BarChart.setFitBars(true);
    }
    private void setChart() {



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
