package com.example.asus.project4th;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class ViewAllQuestionActivity extends FragmentActivity {
    private MyPageAdapter adapter;
    private ViewPager pager;
    private Button btn_next,btn_prev;
    private Toolbar toolbar;
    private String subjectName;
    private String subjectID;
    private String assignname;
    private String totalquest;
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

        adapter = new MyPageAdapter(getSupportFragmentManager(),subjectID,subjectName,assignname,questsize,listofType,listofKey);
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
        listofQuestion = (ArrayList<String>)getIntent().getSerializableExtra("listofQuestion");
        listofType = (ArrayList<String>)getIntent().getSerializableExtra("listofType");
        listofKey = (ArrayList<String>)getIntent().getSerializableExtra("listofKey");

        Log.e("all Key",listofKey.toString());
        SharedPreferences sp = getSharedPreferences("SUBJECT", Context.MODE_PRIVATE);
        assignname = sp.getString("assignment",null);
        subjectID = sp.getString("subjectID",null);
        subjectName = sp.getString("subjectName",null);
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
