package com.example.asus.project4th;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ViewAllQuestionCheckActivity extends FragmentActivity {
    private MyPagerAdapterCheck adapter;
    private ViewPager pager;
    private Button btn_next,btn_prev;
    private Toolbar toolbar;
    private String subjectName;
    private String subjectID;
    private String assignname;
    private String totalquest;
    private String Username;
    private String StudentName;
    private int questsize;
    private ArrayList<String> listofQuestion = new ArrayList<>();
    private ArrayList<String> listofType = new ArrayList<>();
    private ArrayList<String> listofKey = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_question);
        initInstance();
        backToolbar();

        questsize = Integer.parseInt(totalquest);
        Log.e("Question Size = ",totalquest);

        adapter = new MyPagerAdapterCheck(getSupportFragmentManager(),subjectID,subjectName,assignname,questsize,listofType,listofKey,Username,StudentName);
        pager.setAdapter(adapter);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() + 1);
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() - 1);
            }
        });


    }

    private void initInstance(){
        pager = (ViewPager) findViewById(R.id.pager);
        btn_next = (Button)findViewById(R.id.btn_next);
        btn_prev = (Button)findViewById(R.id.btn_prev);

        Intent i = getIntent();
        totalquest = i.getStringExtra("totalQuest");
        subjectID = i.getStringExtra("subjectID");
        subjectName = i.getStringExtra("subjectName");
        assignname = i.getStringExtra("assignment");
        Username = i.getStringExtra("Username");
        StudentName = i.getStringExtra("StudentName");

        listofQuestion = (ArrayList<String>)getIntent().getSerializableExtra("listofQuestion");
        listofType = (ArrayList<String>)getIntent().getSerializableExtra("listofType");
        listofKey = (ArrayList<String>)getIntent().getSerializableExtra("listofKey");

        Log.e("TotalQuestView",totalquest);
        Log.e("SubjectID",subjectID);
        Log.e("SubjectName",subjectName);
        Log.e("Assignname",assignname);
        Log.e("Username",Username);
        Log.e("StudentName",StudentName);

        Log.e("listofQuestionView",listofQuestion.toString());
        Log.e("listofKeyView",listofKey.toString());
        Log.e("listofTypeView",listofType.toString());

    }

    private void backToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("List of Question");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
