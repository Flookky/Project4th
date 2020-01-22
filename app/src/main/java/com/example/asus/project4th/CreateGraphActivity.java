package com.example.asus.project4th;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Answer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreateGraphActivity extends Activity {
    private Spinner spinner,subjectSpinner1,subjectSpinner2,subjectSpinner3,subjectSpinner4;
    private Button CreateGraph;
    private Button Cancel;
    private String Username,name;
    private ArrayList<String> listofSubject = new ArrayList<>();
    private ArrayList<String> listofSubjectID = new ArrayList<>();
    private ArrayList<String> listofSubjectMain = new ArrayList<>();
    private ArrayList<String> listofSubjectTime = new ArrayList<>();
    private ArrayList<String> listofScore_sub1 = new ArrayList<>();
    private ArrayList<String> listofScore_sub2 = new ArrayList<>();
    private ArrayList<String> listofScore_sub3 = new ArrayList<>();
    private ArrayList<String> listofScore_sub4 = new ArrayList<>();

    private TextView subjectID1,subjectID2,subjectID3,subjectID4;
    private String[] items = new String[]{"-","1", "2", "3", "4"};
    private String allSubject[];
    private String subject1,subject2,subject3,subject4;
    private FirebaseDatabase database;
    private DatabaseReference table_subject,table_stdscore;
    private int x;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_graph);
        init();
        Spinner();

        CreateGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.e("ItemPosition LALA",String.valueOf(x));
            Log.e("Spinner1 LALA",subject1);
            Log.e("Spinner2 LALA",subject2);
            Log.e("Spinner3 LALA",subject3);
            Log.e("Spinner4 LALA",subject4);
            CreateGraph();

            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void init(){
        Intent intent = getIntent();
        Username = intent.getStringExtra("Username");
        name = intent.getStringExtra("name");
        listofSubject = (ArrayList<String>)getIntent().getSerializableExtra("AllSubject");
        listofSubjectID = (ArrayList<String>)getIntent().getSerializableExtra("AllSubjectID");
        listofSubjectMain = (ArrayList<String>)getIntent().getSerializableExtra("AllSubjectMain");
        listofSubjectTime = (ArrayList<String>)getIntent().getSerializableExtra("AllSubjectTime");

        allSubject = listofSubject.toArray(new String[0]);
        Log.e("All Subject0",allSubject[0]);
        Log.e("All Subject1",allSubject[1]);

        spinner = findViewById(R.id.spinner1);
        subjectSpinner1 = findViewById(R.id.subjectSpinner1);
        subjectSpinner2 = findViewById(R.id.subjectSpinner2);
        subjectSpinner3 = findViewById(R.id.subjectSpinner3);
        subjectSpinner4 = findViewById(R.id.subjectSpinner4);
        subjectID1 = findViewById(R.id.subjecttxt1);
        subjectID2 = findViewById(R.id.subjecttxt2);
        subjectID3 = findViewById(R.id.subjecttxt3);
        subjectID4 = findViewById(R.id.subjecttxt4);
        CreateGraph = findViewById(R.id.CreateGraph);
        Cancel = findViewById(R.id.Cancel);

        database = FirebaseDatabase.getInstance();
        table_subject = database.getReference().child("Subjectstd");
        table_stdscore = database.getReference().child("Student_answer");

    }

    private void CreateGraph(){
        final DatabaseReference ref = table_stdscore.getRef();

        if(x == 1){
            if(subject1.equals("")){
                Toast.makeText(CreateGraphActivity.this, "Please select Subject1", Toast.LENGTH_SHORT).show();
            } else{
                listofScore_sub1.clear();

                DatabaseReference ref_assign = table_stdscore.getRef().child(Username).child(subject1);
                ref_assign.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sub_ID = "";
                        String sub_Time1 = "";

                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            Answer answer = new Answer();

                            for(int i = 0;i < listofSubjectMain.size();i++){
                                if(subject1.equals(listofSubjectMain.get(i))){
                                    sub_ID = listofSubjectID.get(i);
                                    sub_Time1 = listofSubjectTime.get(i);
                                    Log.e("subjectID1 is = ",sub_ID);
                                    Log.e("subjectTime1 is = ",sub_Time1);
                                    break;
                                }
                            }
                            //Show subjectID by Log test//
                            for(DataSnapshot answerSnapshot : postSnapshot.getChildren()){
                                answer = answerSnapshot.getValue(Answer.class);
                                if(sub_ID.equals(answer.getSubjectID()) && Username.equals(answer.getUsername())) {
                                    listofScore_sub1.add(String.valueOf(answer.getScore()));
                                }
                            }
                        }

                        if(listofScore_sub1.size()!=0){
                            Log.e("ListofScore",listofScore_sub1.toString());
                            int totalLines = 1;
                            Intent i = new Intent(CreateGraphActivity.this,ShowGraphActivity.class);
                            i.putExtra("Username",Username);
                            i.putExtra("name",name);
                            i.putExtra("totalLines",totalLines);
                            i.putExtra("subject1",subject1);
                            i.putExtra("subjectTime1",sub_Time1);
                            i.putExtra("listofScore1",listofScore_sub1);

                            startActivity(i);
                            finish();

                        } else{
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateGraphActivity.this);
                            mBuilder.setMessage("Your Subject 1 don't have score");
                            mBuilder.setCancelable(true);

                            mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog mDialog = mBuilder.create();
                            mDialog.show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        } else if(x == 2){
            if(subject1.equals("")){
                Toast.makeText(CreateGraphActivity.this, "Please select Subject1", Toast.LENGTH_SHORT).show();
            } else if(subject2.equals("")){
                Toast.makeText(CreateGraphActivity.this, "Please select Subject2", Toast.LENGTH_SHORT).show();
            } else{

                listofScore_sub1.clear();
                listofScore_sub2.clear();

                DatabaseReference ref_assign = table_stdscore.getRef().child(Username).child(subject1);
                ref_assign.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sub_ID = "";
                        String sub_Time1 = "";

                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            Answer answer = new Answer();

                            for(int i = 0;i < listofSubjectMain.size();i++){
                                if(subject1.equals(listofSubjectMain.get(i))){
                                    sub_ID = listofSubjectID.get(i);
                                    sub_Time1 = listofSubjectTime.get(i);
                                    Log.e("subjectID1 is = ",sub_ID);
                                    Log.e("subjectTime1 is = ",sub_Time1);
                                    break;
                                }
                            }
                            //Show subjectID by Log test//
                            for(DataSnapshot answerSnapshot : postSnapshot.getChildren()){
                                answer = answerSnapshot.getValue(Answer.class);
                                if(sub_ID.equals(answer.getSubjectID()) && Username.equals(answer.getUsername())) {
                                    listofScore_sub1.add(String.valueOf(answer.getScore()));
                                }
                            }
                        }

                        if(listofScore_sub1.size()!=0){
                            Log.e("ListofScore",listofScore_sub1.toString());

                            final String sub_ID_1 = sub_ID;
                            final String sub_Time_1 = sub_Time1;
                            DatabaseReference ref_assign2 = table_stdscore.getRef().child(Username).child(subject2);
                            ref_assign2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String sub_ID2 = "";
                                    String sub_Time2 = "";

                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                        Answer answer2 = new Answer();

                                        for(int i = 0;i < listofSubjectMain.size();i++){
                                            if(subject2.equals(listofSubjectMain.get(i))){
                                                sub_ID2 = listofSubjectID.get(i);
                                                sub_Time2 = listofSubjectTime.get(i);
                                                Log.e("subjectID1 is = ",sub_ID2);
                                                Log.e("subjectTime1 is = ",sub_Time2);
                                                break;
                                            }
                                        }
                                        //Show subjectID by Log test//
                                        for(DataSnapshot answerSnapshot : postSnapshot.getChildren()){
                                            answer2 = answerSnapshot.getValue(Answer.class);
                                            if(sub_ID2.equals(answer2.getSubjectID()) && Username.equals(answer2.getUsername())) {
                                                listofScore_sub2.add(String.valueOf(answer2.getScore()));
                                            }
                                        }
                                    }

                                    if(listofScore_sub2.size()!=0){
                                        Log.e("ListofScore2",listofScore_sub2.toString());
                                        int totalLines = 2;
                                        Intent i = new Intent(CreateGraphActivity.this,ShowGraphActivity.class);
                                        i.putExtra("Username",Username);
                                        i.putExtra("name",name);
                                        i.putExtra("totalLines",totalLines);
                                        i.putExtra("subject1",subject1);
                                        i.putExtra("subject2",subject2);
                                        i.putExtra("subjectTime1",sub_Time_1);
                                        i.putExtra("subjectTime2",sub_Time2);
                                        i.putExtra("listofScore1",listofScore_sub1);
                                        i.putExtra("listofScore2",listofScore_sub2);

                                        startActivity(i);
                                        finish();

                                    } else{
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateGraphActivity.this);
                                        mBuilder.setMessage("Your Subject 2 don't have score");
                                        mBuilder.setCancelable(true);

                                        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                dialogInterface.dismiss();
                                            }
                                        });

                                        AlertDialog mDialog = mBuilder.create();
                                        mDialog.show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else{
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateGraphActivity.this);
                            mBuilder.setMessage("Your Subject 1 don't have score");
                            mBuilder.setCancelable(true);

                            mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog mDialog = mBuilder.create();
                            mDialog.show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        } else if(x == 3){
            if(subject1.equals("")){
                Toast.makeText(CreateGraphActivity.this, "Please select Subject1", Toast.LENGTH_SHORT).show();
            } else if(subject2.equals("")){
                Toast.makeText(CreateGraphActivity.this, "Please select Subject2", Toast.LENGTH_SHORT).show();
            } else if(subject3.equals("")){
                Toast.makeText(CreateGraphActivity.this, "Please select Subject3", Toast.LENGTH_SHORT).show();
            } else{
                listofScore_sub1.clear();
                listofScore_sub2.clear();
                listofScore_sub3.clear();

                DatabaseReference ref_assign = table_stdscore.getRef().child(Username).child(subject1);
                ref_assign.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sub_ID = "";
                        String sub_Time1 = "";

                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            Answer answer = new Answer();

                            for(int i = 0;i < listofSubjectMain.size();i++){
                                if(subject1.equals(listofSubjectMain.get(i))){
                                    sub_ID = listofSubjectID.get(i);
                                    sub_Time1 = listofSubjectTime.get(i);
                                    Log.e("subjectID1 is = ",sub_ID);
                                    Log.e("subjectTime1 is = ",sub_Time1);
                                    break;
                                }
                            }
                            //Show subjectID by Log test//
                            for(DataSnapshot answerSnapshot : postSnapshot.getChildren()){
                                answer = answerSnapshot.getValue(Answer.class);
                                if(sub_ID.equals(answer.getSubjectID()) && Username.equals(answer.getUsername())) {
                                    listofScore_sub1.add(String.valueOf(answer.getScore()));
                                }
                            }
                        }

                        if(listofScore_sub1.size()!=0){
                            Log.e("ListofScore",listofScore_sub1.toString());

                            final String sub_ID_1 = sub_ID;
                            final String sub_Time_1 = sub_Time1;
                            DatabaseReference ref_assign2 = table_stdscore.getRef().child(Username).child(subject2);
                            ref_assign2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String sub_ID2 = "";
                                    String sub_Time2 = "";

                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                        Answer answer2 = new Answer();

                                        for(int i = 0;i < listofSubjectMain.size();i++){
                                            if(subject2.equals(listofSubjectMain.get(i))){
                                                sub_ID2 = listofSubjectID.get(i);
                                                sub_Time2 = listofSubjectTime.get(i);
                                                Log.e("subjectID1 is = ",sub_ID2);
                                                Log.e("subjectTime1 is = ",sub_Time2);
                                                break;
                                            }
                                        }
                                        //Show subjectID by Log test//
                                        for(DataSnapshot answerSnapshot : postSnapshot.getChildren()){
                                            answer2 = answerSnapshot.getValue(Answer.class);
                                            if(sub_ID2.equals(answer2.getSubjectID()) && Username.equals(answer2.getUsername())) {
                                                listofScore_sub2.add(String.valueOf(answer2.getScore()));
                                            }
                                        }
                                    }

                                    if(listofScore_sub2.size()!=0){
                                        Log.e("ListofScore2",listofScore_sub2.toString());
                                        final String sub_ID_2 = sub_ID2;
                                        final String sub_Time_2 = sub_Time2;
                                        DatabaseReference ref_assign3 = table_stdscore.getRef().child(Username).child(subject3);
                                        ref_assign3.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String sub_ID3 = "";
                                                String sub_Time3 = "";

                                                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                                    Answer answer3 = new Answer();

                                                    for(int i = 0;i < listofSubjectMain.size();i++){
                                                        if(subject3.equals(listofSubjectMain.get(i))){
                                                            sub_ID3 = listofSubjectID.get(i);
                                                            sub_Time3 = listofSubjectTime.get(i);
                                                            Log.e("subjectID1 is = ",sub_ID3);
                                                            Log.e("subjectTime1 is = ",sub_Time3);
                                                            break;
                                                        }
                                                    }
                                                    //Show subjectID by Log test//
                                                    for(DataSnapshot answerSnapshot : postSnapshot.getChildren()){
                                                        answer3 = answerSnapshot.getValue(Answer.class);
                                                        if(sub_ID3.equals(answer3.getSubjectID()) && Username.equals(answer3.getUsername())) {
                                                            listofScore_sub3.add(String.valueOf(answer3.getScore()));
                                                        }
                                                    }
                                                }

                                                if(listofScore_sub3.size()!=0){
                                                    Log.e("ListofScore3",listofScore_sub3.toString());
                                                    int totalLines = 3;
                                                    Intent i = new Intent(CreateGraphActivity.this,ShowGraphActivity.class);
                                                    i.putExtra("Username",Username);
                                                    i.putExtra("name",name);
                                                    i.putExtra("totalLines",totalLines);
                                                    i.putExtra("subject1",subject1);
                                                    i.putExtra("subject2",subject2);
                                                    i.putExtra("subject3",subject3);
                                                    i.putExtra("subjectTime1",sub_Time_1);
                                                    i.putExtra("subjectTime2",sub_Time_2);
                                                    i.putExtra("subjectTime3",sub_Time3);
                                                    i.putExtra("listofScore1",listofScore_sub1);
                                                    i.putExtra("listofScore2",listofScore_sub2);
                                                    i.putExtra("listofScore3",listofScore_sub3);
                                                    i.putExtra("AllSubjectTime",listofSubjectTime);

                                                    startActivity(i);
                                                    finish();

                                                } else{
                                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateGraphActivity.this);
                                                    mBuilder.setMessage("Your Subject 3 don't have score");
                                                    mBuilder.setCancelable(true);

                                                    mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int which) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    });

                                                    AlertDialog mDialog = mBuilder.create();
                                                    mDialog.show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    } else{
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateGraphActivity.this);
                                        mBuilder.setMessage("Your Subject 2 don't have score");
                                        mBuilder.setCancelable(true);

                                        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                dialogInterface.dismiss();
                                            }
                                        });

                                        AlertDialog mDialog = mBuilder.create();
                                        mDialog.show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else{
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateGraphActivity.this);
                            mBuilder.setMessage("Your Subject 1 don't have score");
                            mBuilder.setCancelable(true);

                            mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog mDialog = mBuilder.create();
                            mDialog.show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        } else if(x == 4){
            if(subject1.equals("")){
                Toast.makeText(CreateGraphActivity.this, "Please select Subject1", Toast.LENGTH_SHORT).show();
            } else if(subject2.equals("")){
                Toast.makeText(CreateGraphActivity.this, "Please select Subject2", Toast.LENGTH_SHORT).show();
            } else if(subject3.equals("")){
                Toast.makeText(CreateGraphActivity.this, "Please select Subject3", Toast.LENGTH_SHORT).show();
            } else if(subject4.equals("")){
                Toast.makeText(CreateGraphActivity.this, "Please select Subject4", Toast.LENGTH_SHORT).show();
            } else{
                listofScore_sub1.clear();
                listofScore_sub2.clear();
                listofScore_sub3.clear();
                listofScore_sub4.clear();

                DatabaseReference ref_assign = table_stdscore.getRef().child(Username).child(subject1);
                ref_assign.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sub_ID = "";
                        String sub_Time1 = "";

                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            Answer answer = new Answer();

                            for(int i = 0;i < listofSubjectMain.size();i++){
                                if(subject1.equals(listofSubjectMain.get(i))){
                                    sub_ID = listofSubjectID.get(i);
                                    sub_Time1 = listofSubjectTime.get(i);
                                    Log.e("subjectID1 is = ",sub_ID);
                                    Log.e("subjectTime1 is = ",sub_Time1);
                                    break;
                                }
                            }
                            //Show subjectID by Log test//
                            for(DataSnapshot answerSnapshot : postSnapshot.getChildren()){
                                answer = answerSnapshot.getValue(Answer.class);
                                if(sub_ID.equals(answer.getSubjectID()) && Username.equals(answer.getUsername())) {
                                    listofScore_sub1.add(String.valueOf(answer.getScore()));
                                }
                            }
                        }

                        if(listofScore_sub1.size()!=0){
                            Log.e("ListofScore",listofScore_sub1.toString());

                            final String sub_ID_1 = sub_ID;
                            final String sub_Time_1 = sub_Time1;
                            DatabaseReference ref_assign2 = table_stdscore.getRef().child(Username).child(subject2);
                            ref_assign2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String sub_ID2 = "";
                                    String sub_Time2 = "";

                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                        Answer answer2 = new Answer();

                                        for(int i = 0;i < listofSubjectMain.size();i++){
                                            if(subject2.equals(listofSubjectMain.get(i))){
                                                sub_ID2 = listofSubjectID.get(i);
                                                sub_Time2 = listofSubjectTime.get(i);
                                                Log.e("subjectID1 is = ",sub_ID2);
                                                Log.e("subjectTime1 is = ",sub_Time2);
                                                break;
                                            }
                                        }
                                        //Show subjectID by Log test//
                                        for(DataSnapshot answerSnapshot : postSnapshot.getChildren()){
                                            answer2 = answerSnapshot.getValue(Answer.class);
                                            if(sub_ID2.equals(answer2.getSubjectID()) && Username.equals(answer2.getUsername())) {
                                                listofScore_sub2.add(String.valueOf(answer2.getScore()));
                                            }
                                        }
                                    }

                                    if(listofScore_sub2.size()!=0){
                                        Log.e("ListofScore2",listofScore_sub2.toString());
                                        final String sub_ID_2 = sub_ID2;
                                        final String sub_Time_2 = sub_Time2;
                                        DatabaseReference ref_assign3 = table_stdscore.getRef().child(Username).child(subject3);
                                        ref_assign3.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String sub_ID3 = "";
                                                String sub_Time3 = "";

                                                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                                    Answer answer3 = new Answer();

                                                    for(int i = 0;i < listofSubjectMain.size();i++){
                                                        if(subject3.equals(listofSubjectMain.get(i))){
                                                            sub_ID3 = listofSubjectID.get(i);
                                                            sub_Time3 = listofSubjectTime.get(i);
                                                            Log.e("subjectID1 is = ",sub_ID3);
                                                            Log.e("subjectTime1 is = ",sub_Time3);
                                                            break;
                                                        }
                                                    }
                                                    //Show subjectID by Log test//
                                                    for(DataSnapshot answerSnapshot : postSnapshot.getChildren()){
                                                        answer3 = answerSnapshot.getValue(Answer.class);
                                                        if(sub_ID3.equals(answer3.getSubjectID()) && Username.equals(answer3.getUsername())) {
                                                            listofScore_sub3.add(String.valueOf(answer3.getScore()));
                                                        }
                                                    }
                                                }

                                                if(listofScore_sub3.size()!=0){
                                                    Log.e("ListofScore3",listofScore_sub3.toString());
                                                    final String sub_ID_3 = sub_ID3;
                                                    final String sub_Time_3 = sub_Time3;
                                                    DatabaseReference ref_assign4 = table_stdscore.getRef().child(Username).child(subject4);
                                                    ref_assign4.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            String sub_ID4 = "";
                                                            String sub_Time4 = "";

                                                            for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                                                Answer answer4 = new Answer();

                                                                for(int i = 0;i < listofSubjectMain.size();i++){
                                                                    if(subject4.equals(listofSubjectMain.get(i))){
                                                                        sub_ID4 = listofSubjectID.get(i);
                                                                        sub_Time4 = listofSubjectTime.get(i);
                                                                        Log.e("subjectID1 is = ",sub_ID4);
                                                                        Log.e("subjectTime1 is = ",sub_Time4);
                                                                        break;
                                                                    }
                                                                }
                                                                //Show subjectID by Log test//
                                                                for(DataSnapshot answerSnapshot : postSnapshot.getChildren()){
                                                                    answer4 = answerSnapshot.getValue(Answer.class);
                                                                    if(sub_ID4.equals(answer4.getSubjectID()) && Username.equals(answer4.getUsername())) {
                                                                        listofScore_sub4.add(String.valueOf(answer4.getScore()));
                                                                    }
                                                                }
                                                            }

                                                            if(listofScore_sub4.size()!=0){
                                                                Log.e("ListofScore4",listofScore_sub4.toString());
                                                                int totalLines = 4;
                                                                Intent i = new Intent(CreateGraphActivity.this,ShowGraphActivity.class);
                                                                i.putExtra("Username",Username);
                                                                i.putExtra("name",name);
                                                                i.putExtra("totalLines",totalLines);
                                                                i.putExtra("subject1",subject1);
                                                                i.putExtra("subject2",subject2);
                                                                i.putExtra("subject3",subject3);
                                                                i.putExtra("subject4",subject4);
                                                                i.putExtra("subjectTime1",sub_Time_1);
                                                                i.putExtra("subjectTime2",sub_Time_2);
                                                                i.putExtra("subjectTime3",sub_Time_3);
                                                                i.putExtra("subjectTime4",sub_Time4);
                                                                i.putExtra("listofScore1",listofScore_sub1);
                                                                i.putExtra("listofScore2",listofScore_sub2);
                                                                i.putExtra("listofScore3",listofScore_sub3);
                                                                i.putExtra("listofScore4",listofScore_sub4);
                                                                i.putExtra("AllSubjectTime",listofSubjectTime);

                                                                startActivity(i);
                                                                finish();

                                                            } else{
                                                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateGraphActivity.this);
                                                                mBuilder.setMessage("Your Subject 4 don't have score");
                                                                mBuilder.setCancelable(true);

                                                                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int which) {
                                                                        dialogInterface.dismiss();
                                                                    }
                                                                });

                                                                AlertDialog mDialog = mBuilder.create();
                                                                mDialog.show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });


                                                } else{
                                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateGraphActivity.this);
                                                    mBuilder.setMessage("Your Subject 3 don't have score");
                                                    mBuilder.setCancelable(true);

                                                    mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int which) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    });

                                                    AlertDialog mDialog = mBuilder.create();
                                                    mDialog.show();

                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    } else{
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateGraphActivity.this);
                                        mBuilder.setMessage("Your Subject 2 don't have score");
                                        mBuilder.setCancelable(true);

                                        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                dialogInterface.dismiss();
                                            }
                                        });

                                        AlertDialog mDialog = mBuilder.create();
                                        mDialog.show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else{
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateGraphActivity.this);
                            mBuilder.setMessage("Your Subject 1 don't have score");
                            mBuilder.setCancelable(true);

                            mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog mDialog = mBuilder.create();
                            mDialog.show();}

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    void Spinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        ArrayAdapter<String> SubjectAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, allSubject);
        subjectSpinner1.setAdapter(SubjectAdapter1);

        ArrayAdapter<String> SubjectAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, allSubject);
        subjectSpinner2.setAdapter(SubjectAdapter2);

        ArrayAdapter<String> SubjectAdapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, allSubject);
        subjectSpinner3.setAdapter(SubjectAdapter3);

        ArrayAdapter<String> SubjectAdapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, allSubject);
        subjectSpinner4.setAdapter(SubjectAdapter4);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        subjectID1.setVisibility(View.GONE);
                        subjectID2.setVisibility(View.GONE);
                        subjectID3.setVisibility(View.GONE);
                        subjectID4.setVisibility(View.GONE);
                        subjectSpinner1.setVisibility(View.GONE);
                        subjectSpinner2.setVisibility(View.GONE);
                        subjectSpinner3.setVisibility(View.GONE);
                        subjectSpinner4.setVisibility(View.GONE);
                        subjectSpinner1.setSelection(0);
                        subjectSpinner2.setSelection(0);
                        subjectSpinner3.setSelection(0);
                        subjectSpinner4.setSelection(0);
                        subject1 = "";
                        subject2 = "";
                        subject3 = "";
                        subject4 = "";
                        CreateGraph.setVisibility(View.INVISIBLE);
                        x = spinner.getSelectedItemPosition();
                        Log.e("ItemPosition0",String.valueOf(x));
                        break;

                    case 1:
                        subjectID1.setVisibility(View.VISIBLE);
                        subjectID2.setVisibility(View.GONE);
                        subjectID3.setVisibility(View.GONE);
                        subjectID4.setVisibility(View.GONE);
                        subjectSpinner1.setVisibility(View.VISIBLE);
                        subjectSpinner2.setVisibility(View.GONE);
                        subjectSpinner3.setVisibility(View.GONE);
                        subjectSpinner4.setVisibility(View.GONE);
                        subjectSpinner2.setSelection(0);
                        subjectSpinner3.setSelection(0);
                        subjectSpinner4.setSelection(0);
                        subject2 = "";
                        subject3 = "";
                        subject4 = "";
                        CreateGraph.setVisibility(View.VISIBLE);
                        x = spinner.getSelectedItemPosition();
                        Log.e("ItemPosition1",String.valueOf(x));
                        break;

                    case 2:
                        subjectID1.setVisibility(View.VISIBLE);
                        subjectID2.setVisibility(View.VISIBLE);
                        subjectID3.setVisibility(View.GONE);
                        subjectID4.setVisibility(View.GONE);
                        subjectSpinner1.setVisibility(View.VISIBLE);
                        subjectSpinner2.setVisibility(View.VISIBLE);
                        subjectSpinner3.setVisibility(View.GONE);
                        subjectSpinner4.setVisibility(View.GONE);
                        subjectSpinner3.setSelection(0);
                        subjectSpinner4.setSelection(0);
                        subject3 = "";
                        subject4 = "";
                        CreateGraph.setVisibility(View.VISIBLE);
                        x = spinner.getSelectedItemPosition();
                        Log.e("ItemPosition2",String.valueOf(x));
                        break;

                    case 3:
                        subjectID1.setVisibility(View.VISIBLE);
                        subjectID2.setVisibility(View.VISIBLE);
                        subjectID3.setVisibility(View.VISIBLE);
                        subjectID4.setVisibility(View.GONE);
                        subjectSpinner1.setVisibility(View.VISIBLE);
                        subjectSpinner2.setVisibility(View.VISIBLE);
                        subjectSpinner3.setVisibility(View.VISIBLE);
                        subjectSpinner4.setVisibility(View.GONE);
                        subjectSpinner4.setSelection(0);
                        CreateGraph.setVisibility(View.VISIBLE);
                        x = spinner.getSelectedItemPosition();
                        Log.e("ItemPosition3",String.valueOf(x));
                        break;

                    case 4:
                        subjectID1.setVisibility(View.VISIBLE);
                        subjectID2.setVisibility(View.VISIBLE);
                        subjectID3.setVisibility(View.VISIBLE);
                        subjectID4.setVisibility(View.VISIBLE);
                        subjectSpinner1.setVisibility(View.VISIBLE);
                        subjectSpinner2.setVisibility(View.VISIBLE);
                        subjectSpinner3.setVisibility(View.VISIBLE);
                        subjectSpinner4.setVisibility(View.VISIBLE);
                        CreateGraph.setVisibility(View.VISIBLE);
                        x = spinner.getSelectedItemPosition();
                        Log.e("ItemPosition4",String.valueOf(x));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subjectSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 ){
                    subject1 = "";
                } else{
                    subject1 = subjectSpinner1.getSelectedItem().toString();
                    if(subject1.equals(subject2)||subject1.equals(subject3)||subject1.equals(subject4)){
                        Toast.makeText(CreateGraphActivity.this, "Subject already select exist", Toast.LENGTH_SHORT).show();
                        subject1 = "";
                        subjectSpinner1.setSelection(0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subjectSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 ){
                    subject2 = "";
                } else{
                    subject2 = subjectSpinner2.getSelectedItem().toString();
                    if(subject2.equals(subject1)||subject2.equals(subject3)||subject2.equals(subject4)){
                        Toast.makeText(CreateGraphActivity.this, "Subject already select exist", Toast.LENGTH_SHORT).show();
                        subject2 = "";
                        subjectSpinner2.setSelection(0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subjectSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 ){
                    subject3 = "";
                } else{
                    subject3 = subjectSpinner3.getSelectedItem().toString();
                    if(subject3.equals(subject1)||subject3.equals(subject2)||subject3.equals(subject4)){
                        Toast.makeText(CreateGraphActivity.this, "Subject already select exist", Toast.LENGTH_SHORT).show();
                        subject3 = "";
                        subjectSpinner3.setSelection(0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subjectSpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 ){
                    subject4 = "";
                } else{
                    subject4 = subjectSpinner4.getSelectedItem().toString();
                    if(subject4.equals(subject1)||subject4.equals(subject2)||subject4.equals(subject3)){
                        Toast.makeText(CreateGraphActivity.this, "Subject already select exist", Toast.LENGTH_SHORT).show();
                        subject4 = "";
                        subjectSpinner4.setSelection(0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
