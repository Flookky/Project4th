package com.example.asus.project4th;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.asus.project4th.Model.Assign;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class StudentAssignmentActivity extends AppCompatActivity implements View.OnClickListener {

    //<------------------------------------------------>
    final String TAG = "TTwTT";
    //-- Toolbar --***//
    private Toolbar toolbar;
    //<------------------------------------------------>

    private boolean doubleBackToExitPressedOnce;
    private String assignname;
    private String subjectID,subjectName;
    private String Username;
    private String numberQuestion;
    private FirebaseDatabase database;
    private DatabaseReference table_assign;
    private DatabaseReference table_answer;
    private Button btnChoice;
    private Button btnWrite;
    private String name,teacherusername;
    private static Double Teacher_Lat;
    private static Double Teacher_Long;
    private LatLng Teacher_latlng;
    private static LatLng Student_latlng;
    private static Double Student_Lat;
    private static Double Student_Long;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ArrayList<LatLng> listPoints = new ArrayList<>();
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignment);
        initInstance();
        backToolbar();
        //onClickButtonType();

        if (savedInstanceState == null) {
            //Toast.makeText(this, "Here", Toast.LENGTH_SHORT).show();
            selectQuestion();
            /*
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_exam,
                    new CreateSpaceFragment()).commit(); //*** Fragment: select Type ***/
        }
    }

    private void initInstance() {

        //----------- Firebase ---------------//
        database = FirebaseDatabase.getInstance();
        table_assign = database.getReference("Assign");
        table_answer = database.getReference("Student_answer");

        //-- Toolbar --***//
        toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        assignname = intent.getStringExtra("assignname");
        subjectID = intent.getStringExtra("subjectID");
        subjectName = intent.getStringExtra("subjectName");
        Username = intent.getStringExtra("Username");
        name = intent.getStringExtra("name");
        numberQuestion = intent.getStringExtra("numberQuestion");
        toolbar.setTitle(numberQuestion);
        btnChoice = findViewById(R.id.btnChoice);
        btnWrite = findViewById(R.id.btnWrite);

    }

    private void selectQuestion(){
        DatabaseReference ref = table_assign.getRef().child(subjectName);
        Query searchQuery = ref.orderByChild("subjectID").equalTo(subjectID);
        //Query searchQuery = table_assign.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Assign assign = new Assign();
                    assign = postSnapshot.getValue(Assign.class);
                    String questionType = "";
                    if (assign.getAssignname().equals(assignname)) {
                        questionType = postSnapshot.child("Quest").child(numberQuestion).child("type").getValue().toString();
                        Log.e("Type", questionType);
                        if (questionType.equals("choice")){
                            //Set up Question Data
                            String question = postSnapshot.child("Quest").child(numberQuestion).child("question").getValue().toString();
                            String answer = postSnapshot.child("Quest").child(numberQuestion).child("answer").getValue().toString();
                            String answeris = postSnapshot.child("Quest").child(numberQuestion).child("answeris").getValue().toString();
                            String choiceA = postSnapshot.child("Quest").child(numberQuestion).child("choiceA").getValue().toString();
                            String choiceB = postSnapshot.child("Quest").child(numberQuestion).child("choiceB").getValue().toString();
                            String choiceC = postSnapshot.child("Quest").child(numberQuestion).child("choiceC").getValue().toString();
                            String choiceD = postSnapshot.child("Quest").child(numberQuestion).child("choiceD").getValue().toString();
                            //Send to Fragment
                            Bundle questionFragment = new Bundle();
                            questionFragment.putString("question", question);
                            questionFragment.putString("answerChoice", answer);
                            questionFragment.putString("answerIs", answeris);
                            questionFragment.putString("choiceA", choiceA);
                            questionFragment.putString("choiceB", choiceB);
                            questionFragment.putString("choiceC", choiceC);
                            questionFragment.putString("choiceD", choiceD);
                            questionFragment.putString("numberQuestion", numberQuestion);
                            questionFragment.putString("subjectID", subjectID);
                            questionFragment.putString("subjectName", subjectName);
                            questionFragment.putString("assignname", assignname);
                            questionFragment.putString("Username", Username);
                            questionFragment.putString("name", name);
                            StudentAssignChoiceFragment myObj = new StudentAssignChoiceFragment();
                            myObj.setArguments(questionFragment);

                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_exam,
                                    myObj).commit();
                        }

                        else if(questionType.equals("TrueFalse")){
                            //Set up Question Data
                            String question = postSnapshot.child("Quest").child(numberQuestion).child("question").getValue().toString();
                            String answer = postSnapshot.child("Quest").child(numberQuestion).child("answer").getValue().toString();
                            String answeris = postSnapshot.child("Quest").child(numberQuestion).child("answeris").getValue().toString();
                            String choiceA = postSnapshot.child("Quest").child(numberQuestion).child("choiceA").getValue().toString();
                            String choiceB = postSnapshot.child("Quest").child(numberQuestion).child("choiceB").getValue().toString();

                            //Send to Fragment
                            Bundle questionFragment = new Bundle();
                            questionFragment.putString("question", question);
                            questionFragment.putString("answertruefalse", answer);
                            questionFragment.putString("answerIs", answeris);
                            questionFragment.putString("choiceA", choiceA);
                            questionFragment.putString("choiceB", choiceB);
                            questionFragment.putString("numberQuestion", numberQuestion);
                            questionFragment.putString("subjectID", subjectID);
                            questionFragment.putString("subjectName", subjectName);
                            questionFragment.putString("assignname", assignname);
                            questionFragment.putString("Username", Username);
                            questionFragment.putString("name", name);
                            StudentAssignTrueFalseFragment myObj = new StudentAssignTrueFalseFragment();
                            myObj.setArguments(questionFragment);

                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_exam,
                                    myObj).commit();
                        }

                        else{
                            //Set up Question Data
                            String question = postSnapshot.child("Quest").child(numberQuestion).child("question").getValue().toString();
                            String answer = postSnapshot.child("Quest").child(numberQuestion).child("answer").getValue().toString();


                            //Send to Fragment
                            Bundle questionFragment = new Bundle();
                            questionFragment.putString("question", question);
                            questionFragment.putString("answerWrite", answer);
                            questionFragment.putString("numberQuestion", numberQuestion);
                            questionFragment.putString("subjectID", subjectID);
                            questionFragment.putString("subjectName", subjectName);
                            questionFragment.putString("assignname", assignname);
                            questionFragment.putString("Username", Username);
                            questionFragment.putString("name", name);
                            StudentAssignWriteFragment myObj = new StudentAssignWriteFragment();
                            myObj.setArguments(questionFragment);

                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_exam,
                                    myObj).commit();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }


    private void selectQuestion2(){
        DatabaseReference ref = table_assign.getRef().child(subjectName);
        Query searchQuery = ref.orderByChild("subjectID").equalTo(subjectID);
        //Query searchQuery = table_assign.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Assign assign = new Assign();
                    assign = postSnapshot.getValue(Assign.class);
                    String questionType = "";
                    if (assign.getAssignname().equals(assignname)) {
                        questionType = postSnapshot.child("Quest").child(numberQuestion).child("type").getValue().toString();
                        Log.e("Type", questionType);
                        if (questionType.equals("choice")){
                            //Set up Question Data
                            String question = postSnapshot.child("Quest").child(numberQuestion).child("question").getValue().toString();
                            String answer = postSnapshot.child("Quest").child(numberQuestion).child("answer").getValue().toString();
                            String answeris = postSnapshot.child("Quest").child(numberQuestion).child("answeris").getValue().toString();
                            String choiceA = postSnapshot.child("Quest").child(numberQuestion).child("choiceA").getValue().toString();
                            String choiceB = postSnapshot.child("Quest").child(numberQuestion).child("choiceB").getValue().toString();
                            String choiceC = postSnapshot.child("Quest").child(numberQuestion).child("choiceC").getValue().toString();
                            String choiceD = postSnapshot.child("Quest").child(numberQuestion).child("choiceD").getValue().toString();
                            //Send to Fragment
                            Intent questionFrag = new Intent(StudentAssignmentActivity.this, CheckLocationActivity.class);
                            questionFrag.putExtra("question", question);
                            questionFrag.putExtra("answerChoice", answer);
                            questionFrag.putExtra("answerIs", answeris);
                            questionFrag.putExtra("choiceA", choiceA);
                            questionFrag.putExtra("choiceB", choiceB);
                            questionFrag.putExtra("choiceC", choiceC);
                            questionFrag.putExtra("choiceD", choiceD);
                            questionFrag.putExtra("numberQuestion", numberQuestion);
                            questionFrag.putExtra("subjectID", subjectID);
                            questionFrag.putExtra("subjectName", subjectName);
                            questionFrag.putExtra("assignname", assignname);
                            questionFrag.putExtra("Username", Username);
                            questionFrag.putExtra("name", name);
                            startActivity(questionFrag);

                        }

                        else if(questionType.equals("TrueFalse")){
                            //Set up Question Data
                            String question = postSnapshot.child("Quest").child(numberQuestion).child("question").getValue().toString();
                            String answer = postSnapshot.child("Quest").child(numberQuestion).child("answer").getValue().toString();
                            String answeris = postSnapshot.child("Quest").child(numberQuestion).child("answeris").getValue().toString();
                            String choiceA = postSnapshot.child("Quest").child(numberQuestion).child("choiceA").getValue().toString();
                            String choiceB = postSnapshot.child("Quest").child(numberQuestion).child("choiceB").getValue().toString();

                            Intent questionFrag = new Intent(StudentAssignmentActivity.this, CheckLocationActivity.class);
                            questionFrag.putExtra("question", question);
                            questionFrag.putExtra("answerChoice", answer);
                            questionFrag.putExtra("answerIs", answeris);
                            questionFrag.putExtra("choiceA", choiceA);
                            questionFrag.putExtra("choiceB", choiceB);
                            questionFrag.putExtra("numberQuestion", numberQuestion);
                            questionFrag.putExtra("subjectID", subjectID);
                            questionFrag.putExtra("subjectName", subjectName);
                            questionFrag.putExtra("assignname", assignname);
                            questionFrag.putExtra("Username", Username);
                            questionFrag.putExtra("name", name);
                            startActivity(questionFrag);

                        }

                        else{
                            //Set up Question Data
                            String question = postSnapshot.child("Quest").child(numberQuestion).child("question").getValue().toString();
                            String answer = postSnapshot.child("Quest").child(numberQuestion).child("answer").getValue().toString();

                            Intent questionFrag = new Intent(StudentAssignmentActivity.this, CheckLocationActivity.class);
                            questionFrag.putExtra("question", question);
                            questionFrag.putExtra("answerChoice", answer);
                            questionFrag.putExtra("numberQuestion", numberQuestion);
                            questionFrag.putExtra("subjectID", subjectID);
                            questionFrag.putExtra("subjectName", subjectName);
                            questionFrag.putExtra("assignname", assignname);
                            questionFrag.putExtra("Username", Username);
                            questionFrag.putExtra("name", name);
                            startActivity(questionFrag);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void onClickButtonType() {
        btnChoice.setOnClickListener(this);
        btnWrite.setOnClickListener(this);
    }

    private void backToolbar() {
        //toolbar.setTitle(getString(R.string.assignment));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //------------------------------- Back Press --------------------------------------//
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(1);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    @Override
    public void onClick(View v) {
        if (v == btnChoice) {
            //selectChoiceFragment();
        } else if (v == btnWrite) {
            //selectWriteFragment();
        }
    }
}

