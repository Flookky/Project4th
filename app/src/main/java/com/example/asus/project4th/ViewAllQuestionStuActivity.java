package com.example.asus.project4th;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;

import com.example.asus.project4th.Model.Score;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAllQuestionStuActivity extends FragmentActivity {
    private MyPageAdapterStu adapter;
    private ViewPager pager;
    private Button btn_next,btn_prev;
    private Toolbar toolbar;
    private String subjectName;
    private String subjectID;
    private String assignname;
    private String totalquest;
    private String Username,name;
    private int questsize;
    private ArrayList<String> listofQuestion = new ArrayList<>();
    private ArrayList<String> listofType = new ArrayList<>();
    private ArrayList<String> listofKey = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_question);
        initInstance();
        backToolbar();

        questsize = Integer.parseInt(totalquest);
        Log.e("Question Size LALA= ",totalquest);
        Log.e("assigname LALA = ",assignname);
        Log.e("List Question LALA = ",listofQuestion.toString());
        Log.e("TypeLALA = ",listofType.toString());
        Log.e("SubjectID LALA = ",subjectID);
        Log.e("SubjectName LALA = ",subjectName);

        adapter = new MyPageAdapterStu(getSupportFragmentManager(),subjectID,subjectName,assignname,questsize,listofType,listofQuestion,Username,name,listofKey);
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
        listofQuestion = (ArrayList<String>)getIntent().getSerializableExtra("listofQuestion");
        listofType = (ArrayList<String>)getIntent().getSerializableExtra("listofType");
        listofKey = (ArrayList<String>)getIntent().getSerializableExtra("listofKey");
        assignname = i.getStringExtra("assignname");
        subjectID = i.getStringExtra("subjectID");
        subjectName = i.getStringExtra("subjectName");
        Username = i.getStringExtra("Username");
        name = i.getStringExtra("name");
        totalquest = String.valueOf(listofQuestion.size());
        Log.e("Total Questionlolo",totalquest);
    }

    private void backToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("List of Question");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref_allScore = database.getReference("Student_answer");
                ref_allScore.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(Username)){
                            if(dataSnapshot.child(Username).hasChild(subjectName)){
                                if(dataSnapshot.child(Username).child(subjectName).hasChild(assignname)){
                                    for(DataSnapshot scoreSnapshot : dataSnapshot.child(Username).child(subjectName).child(assignname).getChildren()){
                                        Score score = new Score();
                                        score = scoreSnapshot.getValue(Score.class);
                                        if(Username.equals(score.getUsername()) && subjectID.equals(score.getSubjectID())&& assignname.equals(score.getAssignname())){
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewAllQuestionStuActivity.this);
                                            builder1.setMessage("Your score is " + score.getScore() + " press OK to do exam continue");
                                            builder1.setCancelable(true);

                                            builder1.setPositiveButton(
                                                    "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                            builder1.setNeutralButton(
                                                    "Back to Assignment",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                            finish();
                                                        }
                                                    });

                                            AlertDialog alert11 = builder1.create();
                                            alert11.show();
                                        }
                                    }
                                } else{
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewAllQuestionStuActivity.this);
                                    builder1.setMessage("You do not take any Question yet press OK to do exam continue");
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });

                                    builder1.setNeutralButton(
                                            "Back to Assignment",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    finish();
                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }
                            } else{
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewAllQuestionStuActivity.this);
                                builder1.setMessage("You do not take any Question yet press OK to do exam continue");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                builder1.setNeutralButton(
                                        "Back to Assignment",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                finish();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        } else{
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewAllQuestionStuActivity.this);
                            builder1.setMessage("You do not take any Question yet press OK to do exam continue");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            builder1.setNeutralButton(
                                    "Back to Assignment",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            finish();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
