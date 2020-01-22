package com.example.asus.project4th;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class ShowGraphActivity extends AppCompatActivity {
    private LineChart mpLineChart,chartSubject1,chartSubject2,chartSubject3,chartSubject4;
    private Toolbar toolbar;
    private Button line_btn1,line_btn2,line_btn3,line_btn4;
    private TextView AllLines,yeartxt1,yeartxt2,yeartxt3,yeartxt4;
    private FirebaseDatabase database;
    private DatabaseReference table_subject,table_stdscore;
    private String Username,name,subject1,subject2,subject3,subject4;
    private String timesubject1,timesubject2,timesubject3,timesubject4;
    private int totalLines;
    private ArrayList<String> listofScore1 = new ArrayList<>();
    private ArrayList<String> listofScore2 = new ArrayList<>();
    private ArrayList<String> listofScore3 = new ArrayList<>();
    private ArrayList<String> listofScore4 = new ArrayList<>();
    private LineDataSet lineDataSet,lineDataSet2,lineDataSet3,lineDataSet4;
    private ArrayList<ILineDataSet> dataSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_graph);
        initInstance();
        createGraph();
        backToolbar();

        line_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = line_btn1.getText().toString();
                if(x.equals("Hide Line1")){
                    lineDataSet.setVisible(false);
                    line_btn1.setText("Show Line1");
                    mpLineChart.invalidate();
                } else{
                    lineDataSet.setVisible(true);
                    line_btn1.setText("Hide Line1");
                    mpLineChart.invalidate();
                }

            }
        });

        line_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = line_btn2.getText().toString();
                if(x.equals("Hide Line2")){
                    lineDataSet2.setVisible(false);
                    line_btn2.setText("Show Line2");
                    mpLineChart.invalidate();
                } else{
                    lineDataSet2.setVisible(true);
                    line_btn2.setText("Hide Line2");
                    mpLineChart.invalidate();
                }

            }
        });

        line_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = line_btn3.getText().toString();
                if(x.equals("Hide Line3")){
                    lineDataSet3.setVisible(false);
                    line_btn3.setText("Show Line3");
                    mpLineChart.invalidate();
                } else{
                    lineDataSet3.setVisible(true);
                    line_btn3.setText("Hide Line3");
                    mpLineChart.invalidate();
                }

            }
        });

        line_btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = line_btn4.getText().toString();
                if(x.equals("Hide Line4")){
                    lineDataSet4.setVisible(false);
                    line_btn4.setText("Show Line4");
                    mpLineChart.invalidate();
                } else{
                    lineDataSet4.setVisible(true);
                    line_btn4.setText("Hide Line4");
                    mpLineChart.invalidate();
                }

            }
        });

    }

    private void initInstance(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Graph");
        database = FirebaseDatabase.getInstance();
        table_subject = database.getReference().child("Subjectstd");
        table_stdscore = database.getReference().child("Student_answer");
        line_btn1 = findViewById(R.id.graph_btn1);
        line_btn2 = findViewById(R.id.graph_btn2);
        line_btn3 = findViewById(R.id.graph_btn3);
        line_btn4 = findViewById(R.id.graph_btn4);

        AllLines = findViewById(R.id.AllLines);
        yeartxt1 = findViewById(R.id.yeartxt1);
        yeartxt2 = findViewById(R.id.yeartxt2);
        yeartxt3 = findViewById(R.id.yeartxt3);
        yeartxt4 = findViewById(R.id.yeartxt4);
        mpLineChart = findViewById(R.id.line_chart);
        chartSubject1 = findViewById(R.id.line_chart1);
        chartSubject2 = findViewById(R.id.line_chart2);
        chartSubject3 = findViewById(R.id.line_chart3);
        chartSubject4 = findViewById(R.id.line_chart4);

        Intent intent = getIntent();
        Username = intent.getStringExtra("Username");
        name = intent.getStringExtra("name");


        totalLines = intent.getIntExtra("totalLines",0);
        if(totalLines == 1){
            subject1 = intent.getStringExtra("subject1");
            timesubject1 = intent.getStringExtra("subjectTime1");
            Log.e("subject1",subject1);
            Log.e("Timesubject1",timesubject1);
            listofScore1 = (ArrayList<String>)getIntent().getSerializableExtra("listofScore1");
            Log.e("Score1",listofScore1.toString());

        } else if(totalLines == 2){
            subject1 = intent.getStringExtra("subject1");
            subject2 = intent.getStringExtra("subject2");
            timesubject1 = intent.getStringExtra("subjectTime1");
            timesubject2 = intent.getStringExtra("subjectTime2");
            Log.e("subject1",subject1);
            Log.e("subject2",subject2);
            Log.e("Timesubject1",timesubject1);
            Log.e("Timesubject2",timesubject2);
            listofScore1 = (ArrayList<String>)getIntent().getSerializableExtra("listofScore1");
            listofScore2 = (ArrayList<String>)getIntent().getSerializableExtra("listofScore2");
            Log.e("Score1",listofScore1.toString());
            Log.e("Score2",listofScore2.toString());

        } else if(totalLines == 3){
            subject1 = intent.getStringExtra("subject1");
            subject2 = intent.getStringExtra("subject2");
            subject3 = intent.getStringExtra("subject3");
            timesubject1 = intent.getStringExtra("subjectTime1");
            timesubject2 = intent.getStringExtra("subjectTime2");
            timesubject3 = intent.getStringExtra("subjectTime3");
            Log.e("subject1",subject1);
            Log.e("subject2",subject2);
            Log.e("subject3",subject3);
            Log.e("Timesubject1",timesubject1);
            Log.e("Timesubject2",timesubject2);
            Log.e("Timesubject3",timesubject3);
            listofScore1 = (ArrayList<String>)getIntent().getSerializableExtra("listofScore1");
            listofScore2 = (ArrayList<String>)getIntent().getSerializableExtra("listofScore2");
            listofScore3 = (ArrayList<String>)getIntent().getSerializableExtra("listofScore3");
            Log.e("Score1",listofScore1.toString());
            Log.e("Score2",listofScore2.toString());
            Log.e("Score3",listofScore3.toString());

        } else if(totalLines == 4){
            subject1 = intent.getStringExtra("subject1");
            subject2 = intent.getStringExtra("subject2");
            subject3 = intent.getStringExtra("subject3");
            subject4 = intent.getStringExtra("subject4");
            timesubject1 = intent.getStringExtra("subjectTime1");
            timesubject2 = intent.getStringExtra("subjectTime2");
            timesubject3 = intent.getStringExtra("subjectTime3");
            timesubject4 = intent.getStringExtra("subjectTime4");
            Log.e("subject1",subject1);
            Log.e("subject2",subject2);
            Log.e("subject3",subject3);
            Log.e("subject4",subject4);
            Log.e("Timesubject1",timesubject1);
            Log.e("Timesubject2",timesubject2);
            Log.e("Timesubject3",timesubject3);
            Log.e("Timesubject4",timesubject4);
            listofScore1 = (ArrayList<String>)getIntent().getSerializableExtra("listofScore1");
            listofScore2 = (ArrayList<String>)getIntent().getSerializableExtra("listofScore2");
            listofScore3 = (ArrayList<String>)getIntent().getSerializableExtra("listofScore3");
            listofScore4 = (ArrayList<String>)getIntent().getSerializableExtra("listofScore4");
            Log.e("Score1",listofScore1.toString());
            Log.e("Score2",listofScore2.toString());
            Log.e("Score3",listofScore3.toString());
            Log.e("Score4",listofScore4.toString());

        }
    }

    private void createGraph(){
        dataSets = new ArrayList<>();
        if(totalLines == 1){
            lineDataSet = new LineDataSet(customLine1(), subject1);
            lineDataSet.setColors(new int[] {R.color.red}, getApplicationContext());

            dataSets.add(lineDataSet);
            LineData data = new LineData(dataSets);
            mpLineChart.setData(data);
            mpLineChart.invalidate();
            mpLineChart.getData().setHighlightEnabled(false);
            AllLines.setText("Year " + timesubject1);
            line_btn2.setEnabled(false);
            line_btn2.setVisibility(View.GONE);
            line_btn3.setEnabled(false);
            line_btn3.setVisibility(View.GONE);
            line_btn4.setEnabled(false);
            line_btn4.setVisibility(View.GONE);
            mpLineChart.getDescription().setText("Assignment score");

        } else if(totalLines == 2){
            lineDataSet = new LineDataSet(customLine1(), subject1);
            lineDataSet2 = new LineDataSet(customLine2(), subject2);
            lineDataSet.setColors(new int[] {R.color.red}, getApplicationContext());
            lineDataSet2.setColors(new int[] {R.color.bluegreen}, getApplicationContext());

            dataSets.add(lineDataSet);
            dataSets.add(lineDataSet2);
            LineData data = new LineData(dataSets);
            mpLineChart.setData(data);
            mpLineChart.invalidate();
            mpLineChart.getData().setHighlightEnabled(false);
            AllLines.setText("All Lines");
            line_btn3.setEnabled(false);
            line_btn3.setVisibility(View.GONE);
            line_btn4.setEnabled(false);
            line_btn4.setVisibility(View.GONE);
            mpLineChart.getDescription().setText("Assignment score");

            if(timesubject1.equals(timesubject2)){
                AllLines.setText("All Lines Year " + timesubject1);
            } else{
                ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                dataSets1.add(lineDataSet);
                LineData chart1 = new LineData(dataSets1);
                chartSubject1.setData(chart1);
                chartSubject1.invalidate();
                chartSubject1.getData().setHighlightEnabled(false);
                chartSubject1.setVisibility(View.VISIBLE);
                yeartxt1.setVisibility(View.VISIBLE);
                yeartxt1.setText("Year " + timesubject1);
                chartSubject1.getDescription().setText("Assignment score");

                ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                dataSets2.add(lineDataSet2);
                LineData chart2 = new LineData(dataSets2);
                chartSubject2.setData(chart2);
                chartSubject2.invalidate();
                chartSubject2.getData().setHighlightEnabled(false);
                chartSubject2.setVisibility(View.VISIBLE);
                yeartxt2.setVisibility(View.VISIBLE);
                yeartxt2.setText("Year " + timesubject2);
                chartSubject2.getDescription().setText("Assignment score");

            }

        } else if(totalLines == 3){
            lineDataSet = new LineDataSet(customLine1(), subject1);
            lineDataSet2 = new LineDataSet(customLine2(), subject2);
            lineDataSet3 = new LineDataSet(customLine3(), subject3);
            lineDataSet.setColors(new int[] {R.color.red}, getApplicationContext());
            lineDataSet2.setColors(new int[] {R.color.bluegreen}, getApplicationContext());
            lineDataSet3.setColors(new int[] {R.color.black1}, getApplicationContext());

            dataSets.add(lineDataSet);
            dataSets.add(lineDataSet2);
            dataSets.add(lineDataSet3);
            LineData data = new LineData(dataSets);
            mpLineChart.setData(data);
            mpLineChart.invalidate();
            mpLineChart.getData().setHighlightEnabled(false);
            line_btn4.setEnabled(false);
            line_btn4.setVisibility(View.GONE);
            mpLineChart.getDescription().setText("Assignment score");

            if(timesubject1.equals(timesubject2) && timesubject1.equals(timesubject3) && timesubject2.equals(timesubject3)){
                AllLines.setText("All Lines Year " + timesubject1);
            } else{
                if(timesubject1.equals(timesubject2)){
                    ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                    dataSets1.add(lineDataSet);
                    dataSets1.add(lineDataSet2);
                    LineData chart1 = new LineData(dataSets1);
                    chartSubject1.setData(chart1);
                    chartSubject1.invalidate();
                    chartSubject1.getData().setHighlightEnabled(false);
                    chartSubject1.setVisibility(View.VISIBLE);
                    yeartxt1.setVisibility(View.VISIBLE);
                    yeartxt1.setText("Year " + timesubject1);
                    chartSubject1.getDescription().setText("Assignment score");

                    ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                    dataSets2.add(lineDataSet3);
                    LineData chart2 = new LineData(dataSets2);
                    chartSubject2.setData(chart2);
                    chartSubject2.invalidate();
                    chartSubject2.getData().setHighlightEnabled(false);
                    chartSubject2.setVisibility(View.VISIBLE);
                    yeartxt2.setVisibility(View.VISIBLE);
                    yeartxt2.setText("Year " + timesubject3);
                    chartSubject2.getDescription().setText("Assignment score");

                } else if(timesubject1.equals(timesubject3)){
                    ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                    dataSets1.add(lineDataSet);
                    dataSets1.add(lineDataSet3);
                    LineData chart1 = new LineData(dataSets1);
                    chartSubject1.setData(chart1);
                    chartSubject1.invalidate();
                    chartSubject1.getData().setHighlightEnabled(false);
                    chartSubject1.setVisibility(View.VISIBLE);
                    yeartxt1.setVisibility(View.VISIBLE);
                    yeartxt1.setText("Year " + timesubject1);
                    chartSubject1.getDescription().setText("Assignment score");

                    ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                    dataSets2.add(lineDataSet2);
                    LineData chart2 = new LineData(dataSets2);
                    chartSubject2.setData(chart2);
                    chartSubject2.invalidate();
                    chartSubject2.getData().setHighlightEnabled(false);
                    chartSubject2.setVisibility(View.VISIBLE);
                    yeartxt2.setVisibility(View.VISIBLE);
                    yeartxt2.setText("Year " + timesubject2);
                    chartSubject2.getDescription().setText("Assignment score");

                } else if(timesubject2.equals(timesubject3)){
                    ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                    dataSets1.add(lineDataSet2);
                    dataSets1.add(lineDataSet3);
                    LineData chart1 = new LineData(dataSets1);
                    chartSubject1.setData(chart1);
                    chartSubject1.invalidate();
                    chartSubject1.getData().setHighlightEnabled(false);
                    chartSubject1.setVisibility(View.VISIBLE);
                    yeartxt1.setVisibility(View.VISIBLE);
                    yeartxt1.setText("Year " + timesubject2);
                    chartSubject1.getDescription().setText("Assignment score");

                    ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                    dataSets2.add(lineDataSet);
                    LineData chart2 = new LineData(dataSets2);
                    chartSubject2.setData(chart2);
                    chartSubject2.invalidate();
                    chartSubject2.getData().setHighlightEnabled(false);
                    chartSubject2.setVisibility(View.VISIBLE);
                    yeartxt2.setVisibility(View.VISIBLE);
                    yeartxt2.setText("Year " + timesubject1);
                    chartSubject2.getDescription().setText("Assignment score");

                } else if(!timesubject1.equals(timesubject2) && !timesubject1.equals(timesubject3) && !timesubject2.equals(timesubject3)){
                    ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                    dataSets1.add(lineDataSet);
                    LineData chart1 = new LineData(dataSets1);
                    chartSubject1.setData(chart1);
                    chartSubject1.invalidate();
                    chartSubject1.getData().setHighlightEnabled(false);
                    chartSubject1.setVisibility(View.VISIBLE);
                    yeartxt1.setVisibility(View.VISIBLE);
                    yeartxt1.setText("Year " + timesubject1);
                    chartSubject1.getDescription().setText("Assignment score");

                    ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                    dataSets2.add(lineDataSet2);
                    LineData chart2 = new LineData(dataSets2);
                    chartSubject2.setData(chart2);
                    chartSubject2.invalidate();
                    chartSubject2.getData().setHighlightEnabled(false);
                    chartSubject2.setVisibility(View.VISIBLE);
                    yeartxt2.setVisibility(View.VISIBLE);
                    yeartxt2.setText("Year " + timesubject2);
                    chartSubject2.getDescription().setText("Assignment score");

                    ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
                    dataSets3.add(lineDataSet3);
                    LineData chart3 = new LineData(dataSets3);
                    chartSubject3.setData(chart3);
                    chartSubject3.invalidate();
                    chartSubject3.getData().setHighlightEnabled(false);
                    chartSubject3.setVisibility(View.VISIBLE);
                    yeartxt3.setVisibility(View.VISIBLE);
                    yeartxt3.setText("Year " + timesubject3);
                    chartSubject3.getDescription().setText("Assignment score");

                }
            }

        } else if(totalLines == 4){
            lineDataSet = new LineDataSet(customLine1(), subject1);
            lineDataSet2 = new LineDataSet(customLine2(), subject2);
            lineDataSet3 = new LineDataSet(customLine3(), subject3);
            lineDataSet4 = new LineDataSet(customLine4(), subject4);
            lineDataSet.setColors(new int[] {R.color.red}, getApplicationContext());
            lineDataSet2.setColors(new int[] {R.color.bluegreen}, getApplicationContext());
            lineDataSet3.setColors(new int[] {R.color.black1}, getApplicationContext());
            lineDataSet3.setColors(new int[] {R.color.green}, getApplicationContext());

            dataSets.add(lineDataSet);
            dataSets.add(lineDataSet2);
            dataSets.add(lineDataSet3);
            dataSets.add(lineDataSet4);
            LineData data = new LineData(dataSets);
            mpLineChart.setData(data);
            mpLineChart.invalidate();
            mpLineChart.getData().setHighlightEnabled(false);
            mpLineChart.getDescription().setText("Assignment score");

            if(timesubject1.equals(timesubject2) && timesubject1.equals(timesubject3) && timesubject1.equals(timesubject4)
                && timesubject2.equals(timesubject3) && timesubject2.equals(timesubject4) && timesubject3.equals(timesubject4) )
            {
                AllLines.setText("All Lines Year " + timesubject1);

            } else{
                if(timesubject1.equals(timesubject2)){
                    if(timesubject1.equals(timesubject3)){
                        if(timesubject1.equals(timesubject4)){
                            //1=2=3=4
                        }
                    } else if(timesubject1.equals(timesubject4)){
                        //1=2=4
                        ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                        dataSets1.add(lineDataSet);
                        dataSets1.add(lineDataSet2);
                        dataSets1.add(lineDataSet4);
                        LineData chart1 = new LineData(dataSets1);
                        chartSubject1.setData(chart1);
                        chartSubject1.invalidate();
                        chartSubject1.getData().setHighlightEnabled(false);
                        chartSubject1.setVisibility(View.VISIBLE);
                        yeartxt1.setVisibility(View.VISIBLE);
                        yeartxt1.setText("Year " + timesubject1);
                        chartSubject1.getDescription().setText("Assignment score");

                        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                        dataSets2.add(lineDataSet3);
                        LineData chart2 = new LineData(dataSets2);
                        chartSubject2.setData(chart2);
                        chartSubject2.invalidate();
                        chartSubject2.getData().setHighlightEnabled(false);
                        chartSubject2.setVisibility(View.VISIBLE);
                        yeartxt2.setVisibility(View.VISIBLE);
                        yeartxt2.setText("Year " + timesubject3);
                        chartSubject2.getDescription().setText("Assignment score");

                    } else{
                        //1=2
                        ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                        dataSets1.add(lineDataSet);
                        dataSets1.add(lineDataSet2);
                        LineData chart1 = new LineData(dataSets1);
                        chartSubject1.setData(chart1);
                        chartSubject1.invalidate();
                        chartSubject1.getData().setHighlightEnabled(false);
                        chartSubject1.setVisibility(View.VISIBLE);
                        yeartxt1.setVisibility(View.VISIBLE);
                        yeartxt1.setText("Year " + timesubject1);
                        chartSubject1.getDescription().setText("Assignment score");

                        if(timesubject3.equals(timesubject4)){
                            ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                            dataSets2.add(lineDataSet3);
                            dataSets2.add(lineDataSet4);
                            LineData chart2 = new LineData(dataSets2);
                            chartSubject2.setData(chart2);
                            chartSubject2.invalidate();
                            chartSubject2.getData().setHighlightEnabled(false);
                            chartSubject2.setVisibility(View.VISIBLE);
                            yeartxt2.setVisibility(View.VISIBLE);
                            yeartxt2.setText("Year " + timesubject3);
                            chartSubject2.getDescription().setText("Assignment score");

                        } else{
                            ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                            dataSets2.add(lineDataSet3);
                            LineData chart2 = new LineData(dataSets2);
                            chartSubject2.setData(chart2);
                            chartSubject2.invalidate();
                            chartSubject2.getData().setHighlightEnabled(false);
                            chartSubject2.setVisibility(View.VISIBLE);
                            yeartxt2.setVisibility(View.VISIBLE);
                            yeartxt2.setText("Year " + timesubject3);
                            chartSubject2.getDescription().setText("Assignment score");

                            ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
                            dataSets3.add(lineDataSet4);
                            LineData chart3 = new LineData(dataSets3);
                            chartSubject3.setData(chart3);
                            chartSubject3.invalidate();
                            chartSubject3.getData().setHighlightEnabled(false);
                            chartSubject3.setVisibility(View.VISIBLE);
                            yeartxt3.setVisibility(View.VISIBLE);
                            yeartxt3.setText("Year " + timesubject4);
                            chartSubject3.getDescription().setText("Assignment score");

                        }
                    }

                } else if(timesubject1.equals(timesubject3)){
                    if(timesubject1.equals(timesubject4)){
                        //1=3=4
                        ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                        dataSets1.add(lineDataSet);
                        dataSets1.add(lineDataSet3);
                        dataSets1.add(lineDataSet4);
                        LineData chart1 = new LineData(dataSets1);
                        chartSubject1.setData(chart1);
                        chartSubject1.invalidate();
                        chartSubject1.getData().setHighlightEnabled(false);
                        chartSubject1.setVisibility(View.VISIBLE);
                        yeartxt1.setVisibility(View.VISIBLE);
                        yeartxt1.setText("Year " + timesubject1);
                        chartSubject1.getDescription().setText("Assignment score");

                        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                        dataSets2.add(lineDataSet2);
                        LineData chart2 = new LineData(dataSets2);
                        chartSubject2.setData(chart2);
                        chartSubject2.invalidate();
                        chartSubject2.getData().setHighlightEnabled(false);
                        chartSubject2.setVisibility(View.VISIBLE);
                        yeartxt2.setVisibility(View.VISIBLE);
                        yeartxt2.setText("Year " + timesubject3);
                        chartSubject2.getDescription().setText("Assignment score");

                    } else{
                        //1=3
                        ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                        dataSets1.add(lineDataSet);
                        dataSets1.add(lineDataSet3);
                        LineData chart1 = new LineData(dataSets1);
                        chartSubject1.setData(chart1);
                        chartSubject1.invalidate();
                        chartSubject1.getData().setHighlightEnabled(false);
                        chartSubject1.setVisibility(View.VISIBLE);
                        yeartxt1.setVisibility(View.VISIBLE);
                        yeartxt1.setText("Year " + timesubject1);
                        chartSubject1.getDescription().setText("Assignment score");

                        if(timesubject2.equals(timesubject4)){
                            ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                            dataSets2.add(lineDataSet2);
                            dataSets2.add(lineDataSet4);
                            LineData chart2 = new LineData(dataSets2);
                            chartSubject2.setData(chart2);
                            chartSubject2.invalidate();
                            chartSubject2.getData().setHighlightEnabled(false);
                            chartSubject2.setVisibility(View.VISIBLE);
                            yeartxt2.setVisibility(View.VISIBLE);
                            yeartxt2.setText("Year " + timesubject2);
                            chartSubject2.getDescription().setText("Assignment score");

                        } else{
                            ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                            dataSets2.add(lineDataSet2);
                            LineData chart2 = new LineData(dataSets2);
                            chartSubject2.setData(chart2);
                            chartSubject2.invalidate();
                            chartSubject2.getData().setHighlightEnabled(false);
                            chartSubject2.setVisibility(View.VISIBLE);
                            yeartxt2.setVisibility(View.VISIBLE);
                            yeartxt2.setText("Year " + timesubject2);
                            chartSubject2.getDescription().setText("Assignment score");

                            ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
                            dataSets3.add(lineDataSet2);
                            LineData chart3 = new LineData(dataSets3);
                            chartSubject3.setData(chart3);
                            chartSubject3.invalidate();
                            chartSubject3.getData().setHighlightEnabled(false);
                            chartSubject3.setVisibility(View.VISIBLE);
                            yeartxt3.setVisibility(View.VISIBLE);
                            yeartxt3.setText("Year " + timesubject2);
                            chartSubject3.getDescription().setText("Assignment score");

                        }
                    }

                } else if(timesubject1.equals(timesubject4)){
                    //1=4
                    ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                    dataSets1.add(lineDataSet);
                    dataSets1.add(lineDataSet4);
                    LineData chart1 = new LineData(dataSets1);
                    chartSubject1.setData(chart1);
                    chartSubject1.invalidate();
                    chartSubject1.getData().setHighlightEnabled(false);
                    chartSubject1.setVisibility(View.VISIBLE);
                    yeartxt1.setVisibility(View.VISIBLE);
                    yeartxt1.setText("Year " + timesubject1);
                    chartSubject1.getDescription().setText("Assignment score");

                    if(timesubject2.equals(timesubject3)){
                        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                        dataSets2.add(lineDataSet2);
                        dataSets2.add(lineDataSet3);
                        LineData chart2 = new LineData(dataSets2);
                        chartSubject2.setData(chart2);
                        chartSubject2.invalidate();
                        chartSubject2.getData().setHighlightEnabled(false);
                        chartSubject2.setVisibility(View.VISIBLE);
                        yeartxt2.setVisibility(View.VISIBLE);
                        yeartxt2.setText("Year " + timesubject2);
                        chartSubject2.getDescription().setText("Assignment score");

                    } else{
                        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                        dataSets2.add(lineDataSet2);
                        LineData chart2 = new LineData(dataSets2);
                        chartSubject2.setData(chart2);
                        chartSubject2.invalidate();
                        chartSubject2.getData().setHighlightEnabled(false);
                        chartSubject2.setVisibility(View.VISIBLE);
                        yeartxt2.setVisibility(View.VISIBLE);
                        yeartxt2.setText("Year " + timesubject2);
                        chartSubject2.getDescription().setText("Assignment score");

                        ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
                        dataSets3.add(lineDataSet3);
                        LineData chart3 = new LineData(dataSets3);
                        chartSubject3.setData(chart3);
                        chartSubject3.invalidate();
                        chartSubject3.getData().setHighlightEnabled(false);
                        chartSubject3.setVisibility(View.VISIBLE);
                        yeartxt3.setVisibility(View.VISIBLE);
                        yeartxt3.setText("Year " + timesubject3);
                        chartSubject3.getDescription().setText("Assignment score");

                    }

                } else if(timesubject2.equals(timesubject3)){
                    if(timesubject2.equals(timesubject4)){
                        //2=3=4
                        ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                        dataSets1.add(lineDataSet2);
                        dataSets1.add(lineDataSet3);
                        dataSets1.add(lineDataSet4);
                        LineData chart1 = new LineData(dataSets1);
                        chartSubject1.setData(chart1);
                        chartSubject1.invalidate();
                        chartSubject1.getData().setHighlightEnabled(false);
                        chartSubject1.setVisibility(View.VISIBLE);
                        yeartxt1.setVisibility(View.VISIBLE);
                        yeartxt1.setText("Year " + timesubject2);
                        chartSubject1.getDescription().setText("Assignment score");

                        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                        dataSets2.add(lineDataSet);
                        LineData chart2 = new LineData(dataSets2);
                        chartSubject2.setData(chart2);
                        chartSubject2.invalidate();
                        chartSubject2.getData().setHighlightEnabled(false);
                        chartSubject2.setVisibility(View.VISIBLE);
                        yeartxt2.setVisibility(View.VISIBLE);
                        yeartxt2.setText("Year " + timesubject1);
                        chartSubject2.getDescription().setText("Assignment score");

                    } else{
                        //2=3
                        ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                        dataSets1.add(lineDataSet2);
                        dataSets1.add(lineDataSet3);
                        LineData chart1 = new LineData(dataSets1);
                        chartSubject1.setData(chart1);
                        chartSubject1.invalidate();
                        chartSubject1.getData().setHighlightEnabled(false);
                        chartSubject1.setVisibility(View.VISIBLE);
                        yeartxt1.setVisibility(View.VISIBLE);
                        yeartxt1.setText("Year " + timesubject2);
                        chartSubject1.getDescription().setText("Assignment score");

                        if(timesubject1.equals(timesubject4)){
                            ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                            dataSets2.add(lineDataSet);
                            dataSets2.add(lineDataSet4);
                            LineData chart2 = new LineData(dataSets2);
                            chartSubject2.setData(chart2);
                            chartSubject2.invalidate();
                            chartSubject2.getData().setHighlightEnabled(false);
                            chartSubject2.setVisibility(View.VISIBLE);
                            yeartxt2.setVisibility(View.VISIBLE);
                            yeartxt2.setText("Year " + timesubject1);
                            chartSubject2.getDescription().setText("Assignment score");

                        } else{
                            ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                            dataSets2.add(lineDataSet);
                            LineData chart2 = new LineData(dataSets2);
                            chartSubject2.setData(chart2);
                            chartSubject2.invalidate();
                            chartSubject2.getData().setHighlightEnabled(false);
                            chartSubject2.setVisibility(View.VISIBLE);
                            yeartxt2.setVisibility(View.VISIBLE);
                            yeartxt2.setText("Year " + timesubject1);
                            chartSubject2.getDescription().setText("Assignment score");

                            ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
                            dataSets3.add(lineDataSet4);
                            LineData chart3 = new LineData(dataSets3);
                            chartSubject3.setData(chart3);
                            chartSubject3.invalidate();
                            chartSubject3.getData().setHighlightEnabled(false);
                            chartSubject3.setVisibility(View.VISIBLE);
                            yeartxt3.setVisibility(View.VISIBLE);
                            yeartxt3.setText("Year " + timesubject4);
                            chartSubject3.getDescription().setText("Assignment score");

                        }
                    }

                } else if(timesubject2.equals(timesubject4)){
                    //2=4
                    ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                    dataSets1.add(lineDataSet2);
                    dataSets1.add(lineDataSet4);
                    LineData chart1 = new LineData(dataSets1);
                    chartSubject1.setData(chart1);
                    chartSubject1.invalidate();
                    chartSubject1.getData().setHighlightEnabled(false);
                    chartSubject1.setVisibility(View.VISIBLE);
                    yeartxt1.setVisibility(View.VISIBLE);
                    yeartxt1.setText("Year " + timesubject2);
                    chartSubject1.getDescription().setText("Assignment score");

                    if(timesubject1.equals(timesubject3)){
                        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                        dataSets2.add(lineDataSet);
                        dataSets2.add(lineDataSet3);
                        LineData chart2 = new LineData(dataSets2);
                        chartSubject2.setData(chart2);
                        chartSubject2.invalidate();
                        chartSubject2.getData().setHighlightEnabled(false);
                        chartSubject2.setVisibility(View.VISIBLE);
                        yeartxt2.setVisibility(View.VISIBLE);
                        yeartxt2.setText("Year " + timesubject1);
                        chartSubject2.getDescription().setText("Assignment score");

                    } else{
                        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                        dataSets2.add(lineDataSet);
                        LineData chart2 = new LineData(dataSets2);
                        chartSubject2.setData(chart2);
                        chartSubject2.invalidate();
                        chartSubject2.getData().setHighlightEnabled(false);
                        chartSubject2.setVisibility(View.VISIBLE);
                        yeartxt2.setVisibility(View.VISIBLE);
                        yeartxt2.setText("Year " + timesubject1);
                        chartSubject2.getDescription().setText("Assignment score");

                        ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
                        dataSets3.add(lineDataSet3);
                        LineData chart3 = new LineData(dataSets3);
                        chartSubject3.setData(chart3);
                        chartSubject3.invalidate();
                        chartSubject3.getData().setHighlightEnabled(false);
                        chartSubject3.setVisibility(View.VISIBLE);
                        yeartxt3.setVisibility(View.VISIBLE);
                        yeartxt3.setText("Year " + timesubject3);
                        chartSubject3.getDescription().setText("Assignment score");

                    }

                } else if(timesubject3.equals(timesubject4)){
                    //3=4
                    ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                    dataSets1.add(lineDataSet3);
                    dataSets1.add(lineDataSet4);
                    LineData chart1 = new LineData(dataSets1);
                    chartSubject1.setData(chart1);
                    chartSubject1.invalidate();
                    chartSubject1.getData().setHighlightEnabled(false);
                    chartSubject1.setVisibility(View.VISIBLE);
                    yeartxt1.setVisibility(View.VISIBLE);
                    yeartxt1.setText("Year " + timesubject3);
                    chartSubject1.getDescription().setText("Assignment score");

                    if(timesubject1.equals(timesubject2)){
                        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                        dataSets2.add(lineDataSet);
                        dataSets2.add(lineDataSet2);
                        LineData chart2 = new LineData(dataSets2);
                        chartSubject2.setData(chart2);
                        chartSubject2.invalidate();
                        chartSubject2.getData().setHighlightEnabled(false);
                        chartSubject2.setVisibility(View.VISIBLE);
                        yeartxt2.setVisibility(View.VISIBLE);
                        yeartxt2.setText("Year " + timesubject1);
                        chartSubject2.getDescription().setText("Assignment score");

                    } else{
                        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                        dataSets2.add(lineDataSet);
                        LineData chart2 = new LineData(dataSets2);
                        chartSubject2.setData(chart2);
                        chartSubject2.invalidate();
                        chartSubject2.getData().setHighlightEnabled(false);
                        chartSubject2.setVisibility(View.VISIBLE);
                        yeartxt2.setVisibility(View.VISIBLE);
                        yeartxt2.setText("Year " + timesubject1);
                        chartSubject2.getDescription().setText("Assignment score");

                        ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
                        dataSets3.add(lineDataSet2);
                        LineData chart3 = new LineData(dataSets3);
                        chartSubject3.setData(chart3);
                        chartSubject3.invalidate();
                        chartSubject3.getData().setHighlightEnabled(false);
                        chartSubject3.setVisibility(View.VISIBLE);
                        yeartxt3.setVisibility(View.VISIBLE);
                        yeartxt3.setText("Year " + timesubject2);
                        chartSubject3.getDescription().setText("Assignment score");

                    }
                } else{
                    //1!=2!=3!=4
                    ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                    dataSets1.add(lineDataSet);
                    LineData chart1 = new LineData(dataSets1);
                    chartSubject1.setData(chart1);
                    chartSubject1.invalidate();
                    chartSubject1.getData().setHighlightEnabled(false);
                    chartSubject1.setVisibility(View.VISIBLE);
                    yeartxt1.setVisibility(View.VISIBLE);
                    yeartxt1.setText("Year " + timesubject1);
                    chartSubject1.getDescription().setText("Assignment score");

                    ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                    dataSets2.add(lineDataSet2);
                    LineData chart2 = new LineData(dataSets2);
                    chartSubject2.setData(chart2);
                    chartSubject2.invalidate();
                    chartSubject2.getData().setHighlightEnabled(false);
                    chartSubject2.setVisibility(View.VISIBLE);
                    yeartxt2.setVisibility(View.VISIBLE);
                    yeartxt2.setText("Year " + timesubject2);
                    chartSubject2.getDescription().setText("Assignment score");

                    ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
                    dataSets3.add(lineDataSet3);
                    LineData chart3 = new LineData(dataSets3);
                    chartSubject3.setData(chart3);
                    chartSubject3.invalidate();
                    chartSubject3.getData().setHighlightEnabled(false);
                    chartSubject3.setVisibility(View.VISIBLE);
                    yeartxt3.setVisibility(View.VISIBLE);
                    yeartxt3.setText("Year " + timesubject3);
                    chartSubject3.getDescription().setText("Assignment score");

                    ArrayList<ILineDataSet> dataSets4 = new ArrayList<>();
                    dataSets4.add(lineDataSet4);
                    LineData chart4 = new LineData(dataSets4);
                    chartSubject4.setData(chart4);
                    chartSubject4.invalidate();
                    chartSubject4.getData().setHighlightEnabled(false);
                    chartSubject4.setVisibility(View.VISIBLE);
                    yeartxt4.setVisibility(View.VISIBLE);
                    yeartxt4.setText("Year " + timesubject4);
                    chartSubject4.getDescription().setText("Assignment score");

                }
            }
        }

    }

    public ArrayList<Entry> customLine1(){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        for(int j = 0;j < listofScore1.size();j++){
            dataVals.add(new Entry(j,Integer.parseInt(listofScore1.get(j))));
            Log.e("ListEntry",dataVals.toString());
        }
        return dataVals;
    }

    public ArrayList<Entry> customLine2(){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        for(int j = 0;j < listofScore2.size();j++){
            dataVals.add(new Entry(j,Integer.parseInt(listofScore2.get(j))));
            Log.e("ListEntry",dataVals.toString());
        }
        return dataVals;
    }

    public ArrayList<Entry> customLine3(){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        for(int j = 0;j < listofScore3.size();j++){
            dataVals.add(new Entry(j,Integer.parseInt(listofScore3.get(j))));
            Log.e("ListEntry",dataVals.toString());
        }
        return dataVals;
    }

    public ArrayList<Entry> customLine4(){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        for(int j = 0;j < listofScore4.size();j++){
            dataVals.add(new Entry(j,Integer.parseInt(listofScore4.get(j))));
            Log.e("ListEntry",dataVals.toString());
        }
        return dataVals;
    }

    private void backToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
