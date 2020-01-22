package com.example.asus.project4th;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Answer;
import com.example.asus.project4th.Model.AnswerSolve;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.xml.transform.Result;

import static android.support.constraint.Constraints.TAG;

public class StudentAssignChoiceFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, LocationListener {

    private View view;
    private TextView No,symbol,txtQuest;
    private TextView txtNo,ResultAnswer;
    private RadioGroup radioGroupChoice;
    private RadioButton radioA;
    private RadioButton radioB;
    private RadioButton radioC;
    private RadioButton radioD;
    private FirebaseDatabase database;
    private DatabaseReference table_ans,table_solve,table_solve2,table_solve3;
    private DatabaseReference table_quest;
    private String assignname;
    private String subjectID,subjectName;
    private String Username;
    private String sel = " ";
    private ImageButton btnReset;
    private Button btnSubmit,checkDistance;
    private String numberQuestion;
    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private String answerChoice;
    private String answerIs;
    private String keyAssign;
    private String getChoiceA;
    private String getChoiceB;
    private String getChoiceC;
    private String getChoiceD;
    private String name,teacherusername;
    private static Double Teacher_Lat;
    private static Double Teacher_Long;
    private static Double Student_Lat;
    private static Double Student_Long;
    private LatLng Teacher_latlng;
    private static LatLng Student_latlng;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ArrayList<LatLng> listPoints = new ArrayList<>();
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private static String answercheck;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_assign_stu, container, false);

        if (getArguments() != null) {
            numberQuestion = getArguments().getString("numberQuestion");
            question = getArguments().getString("question");
            choiceA = getArguments().getString("choiceA");
            choiceB = getArguments().getString("choiceB");
            choiceC = getArguments().getString("choiceC");
            choiceD = getArguments().getString("choiceD");
            answerIs = getArguments().getString("answerIs");
            answerChoice = getArguments().getString("answerChoice");
            Username = getArguments().getString("Username");
            name = getArguments().getString("name");
            assignname = getArguments().getString("assignname");
            subjectID = getArguments().getString("subjectID");
            subjectName = getArguments().getString("subjectName");
            keyAssign = getArguments().getString("keyAssign");

            Log.e("answerChoice",answerChoice);
            //Log.e("Stud_choice_Username",Username);
            //Toast.makeText(getContext(), Username, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Bundle == null", Toast.LENGTH_SHORT).show();
        }

        //Toast.makeText(getContext(), answerIs, Toast.LENGTH_SHORT).show();
        //checkradioanswer();

        initInstance();
        //checkDoquestion();
        clickButton();
        onClickRadioButton();
        getLocationPermission();
        return view;
    }

    private void initInstance() {

        //----------- Firebase ---------------//
        database = FirebaseDatabase.getInstance();
        table_ans = database.getReference("Student_answer");
        table_solve = database.getReference("Student_answer");
        table_solve2 = database.getReference("Student_answer");
        table_solve3 = database.getReference("Student_answer");
        table_quest = database.getReference("Assign");

        SharedPreferences sp = getActivity().getSharedPreferences("TEACHER_USERNAME", Context.MODE_PRIVATE);
        teacherusername = sp.getString("TeacherUsername",null);
        //Toast.makeText(getActivity(), teacherusername, Toast.LENGTH_SHORT).show();

        //----------- Question -----------//
        No = view.findViewById(R.id.No);
        symbol = view.findViewById(R.id.symbol);
        txtQuest = view.findViewById(R.id.txtQuest);
        txtNo = view.findViewById(R.id.txtNo);
        radioGroupChoice = view.findViewById(R.id.radioGroupChoice);
        radioA = view.findViewById(R.id.radioA);
        radioB = view.findViewById(R.id.radioB);
        radioC = view.findViewById(R.id.radioC);
        radioD = view.findViewById(R.id.radioD);

        //-----------ResultText-----------//
        ResultAnswer = view.findViewById(R.id.ResultAnswer);

        //---------- Button -------------//
        btnReset = view.findViewById(R.id.btnReset);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        checkDistance = view.findViewById(R.id.mapCheck);

        //----------- Set Up -----------//
        txtQuest.setText(question);
        txtNo.setText(numberQuestion);
        radioA.setText(choiceA);
        radioB.setText(choiceB);
        radioC.setText(choiceC);
        radioD.setText(choiceD);

        getChoiceA=radioA.getText().toString();
        getChoiceB=radioB.getText().toString();
        getChoiceC=radioC.getText().toString();
        getChoiceD=radioD.getText().toString();

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try{
            //Current Location for me
            if(mLocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            onLocationChanged(currentLocation);

                            Student_Lat = currentLocation.getLatitude();
                            Student_Long = currentLocation.getLongitude();
                            //Toast.makeText(MapsActivity.this, latlngstd, Toast.LENGTH_SHORT).show();
                            initMap();

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        final double longitude = location.getLongitude();
        final double latitude = location.getLatitude();
        String lon = String.valueOf(longitude);
        String lat = String.valueOf(latitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initMap();
                    getDeviceLocation();
                }
            }
        }
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                //initMap();
                getDeviceLocation();
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap(){
        final DatabaseReference teacherlocation = database.getReference("Member").child("Teacher").child(teacherusername);
        teacherlocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double lat = dataSnapshot.child("latitude").getValue(Double.class);
                Double lon = dataSnapshot.child("longitude").getValue(Double.class);

                String strlat = String.valueOf(lat);
                String strlon = String.valueOf(lon);

                Teacher_Lat = lat;
                Teacher_Long = lon;

                if(lat > 0 && lon > 0){
                    Student_latlng = new LatLng(Student_Lat, Student_Long);
                    Teacher_latlng = new LatLng(lat, lon);
                    //listPoints.add(Teacher_latlng);

                    Location locstu = new Location(LocationManager.GPS_PROVIDER);
                    Location locteach = new Location(LocationManager.GPS_PROVIDER);

                    locstu.setLatitude(Student_latlng.latitude);
                    locstu.setLongitude(Student_latlng.longitude);

                    locteach.setLatitude(Teacher_latlng.latitude);
                    locteach.setLongitude(Teacher_latlng.longitude);

                    String distance_str;
                    float distance = locstu.distanceTo(locteach);

                    if(distance <= 1000.00){
                        DecimalFormat floatFormat = new DecimalFormat("#.00");
                        if(distance <= 200.00){
                            radioA.setEnabled(true);
                            radioB.setEnabled(true);
                            radioC.setEnabled(true);
                            radioD.setEnabled(true);
                            checkDoquestion();
                            //String text = ResultAnswer.getText().toString();
                            //Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

                            distance_str = String.valueOf(floatFormat.format(distance)) + " Metre";
                            Toast.makeText(getActivity(), distance_str, Toast.LENGTH_SHORT).show();
                        } else{
                            checkDoquestion2();
                            String text = ResultAnswer.getText().toString();
                            //Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

                                radioA.setEnabled(false);
                                radioB.setEnabled(false);
                                radioC.setEnabled(false);
                                radioD.setEnabled(false);

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                builder1.setMessage("You cannot do this question because distance between and " +
                                        "teacher are more than 200 metres. You can check in Check Distance button");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                            distance_str = String.valueOf(floatFormat.format(distance)) + " Metre";
                            Toast.makeText(getActivity(), distance_str, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        checkDoquestion2();
                        String text = ResultAnswer.getText().toString();
                        //Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                        if(text.equals("Correct") || text.equals("InCorrect")){

                        } else{
                            radioA.setEnabled(false);
                            radioB.setEnabled(false);
                            radioC.setEnabled(false);
                            radioD.setEnabled(false);

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                            builder1.setMessage("You cannot do this question because distance between and " +
                                    "teacher are more than 200 metres. You can check in Check Distance button");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                        }

                        DecimalFormat floatFormat = new DecimalFormat("#.00");
                        distance = distance/1000;

                        distance_str = String.valueOf(floatFormat.format(distance)) + " Kilometre";
                        Toast.makeText(getActivity(), distance_str, Toast.LENGTH_SHORT).show();
                    }
                }
                //Toast.makeText(MapsActivity.this, strlat, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void MapDistance(){
        Intent map = new Intent(getActivity().getApplication(), MapsActivity.class);
        startActivity(map);
    }

    public void checkDoquestion(){
        table_solve2.getRef().child(Username).child("No_question");
        table_solve2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Answer answer = new Answer();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    answer = postSnapshot.getValue(Answer.class);
                    if(assignname.equals(answer.getAssignname())
                            && Username.equals(answer.getUsername())
                            && subjectID.equals(answer.getSubjectID()))
                    {
                        if(postSnapshot.child("No_question").child(numberQuestion).exists()){
                            AnswerSolve answerSolve=postSnapshot.child("No_question").child(numberQuestion).getValue(AnswerSolve.class);
                            String answerText=answerSolve.getAnswertext();
                            String answerCheck=answerSolve.getCheck();
                            //Toast.makeText(getContext(), answerText, Toast.LENGTH_SHORT).show();
                            if(answerText.equals(getChoiceA)){
                                radioA.setChecked(true);
                                radioB.setEnabled(false);
                                radioC.setEnabled(false);
                                radioD.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}
                            }
                            else if(answerText.equals(getChoiceB)){
                                radioB.setChecked(true);
                                radioA.setEnabled(false);
                                radioC.setEnabled(false);
                                radioD.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}
                            }
                            else if(answerText.equals(getChoiceC)){
                                radioC.setChecked(true);
                                radioA.setEnabled(false);
                                radioB.setEnabled(false);
                                radioD.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}
                            }
                            else if(answerText.equals(getChoiceD)){
                                radioD.setChecked(true);
                                radioA.setEnabled(false);
                                radioB.setEnabled(false);
                                radioC.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkDoquestion2(){
        table_solve2.getRef().child(Username).child("No_question");
        table_solve2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Answer answer = new Answer();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    answer = postSnapshot.getValue(Answer.class);
                    if(assignname.equals(answer.getAssignname())
                            && Username.equals(answer.getUsername())
                            && subjectID.equals(answer.getSubjectID()))
                    {
                        if(postSnapshot.child("No_question").child(numberQuestion).exists()){
                            AnswerSolve answerSolve=postSnapshot.child("No_question").child(numberQuestion).getValue(AnswerSolve.class);
                            String answerText=answerSolve.getAnswertext();
                            String answerCheck=answerSolve.getCheck();
                            //Toast.makeText(getContext(), answerText, Toast.LENGTH_SHORT).show();

                            No.setVisibility(View.INVISIBLE);
                            symbol.setVisibility(View.INVISIBLE);
                            txtNo.setVisibility(View.INVISIBLE);
                            txtQuest.setVisibility(View.INVISIBLE);
                            radioA.setVisibility(View.INVISIBLE);
                            radioB.setVisibility(View.INVISIBLE);
                            radioC.setVisibility(View.INVISIBLE);
                            radioD.setVisibility(View.INVISIBLE);
                            btnReset.setVisibility(View.INVISIBLE);
                            btnSubmit.setVisibility(View.INVISIBLE);
                            btnReset.setEnabled(false);
                            btnSubmit.setEnabled(false);
                            //checkDistance.setEnabled(false);

                            if(answerText.equals(getChoiceA)){
                                radioA.setChecked(true);
                                radioB.setEnabled(false);
                                radioC.setEnabled(false);
                                radioD.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}

                                No.setVisibility(View.VISIBLE);
                                symbol.setVisibility(View.VISIBLE);
                                txtNo.setVisibility(View.VISIBLE);
                                txtQuest.setVisibility(View.VISIBLE);
                                radioA.setVisibility(View.VISIBLE);
                                radioB.setVisibility(View.VISIBLE);
                                radioC.setVisibility(View.VISIBLE);
                                radioD.setVisibility(View.VISIBLE);
                                btnSubmit.setVisibility(View.VISIBLE);
                                checkDistance.setVisibility(View.VISIBLE);
                                btnSubmit.setEnabled(true);
                                checkDistance.setEnabled(true);
                            }
                            else if(answerText.equals(getChoiceB)){
                                radioB.setChecked(true);
                                radioA.setEnabled(false);
                                radioC.setEnabled(false);
                                radioD.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}

                                No.setVisibility(View.VISIBLE);
                                symbol.setVisibility(View.VISIBLE);
                                txtNo.setVisibility(View.VISIBLE);
                                txtQuest.setVisibility(View.VISIBLE);
                                radioA.setVisibility(View.VISIBLE);
                                radioB.setVisibility(View.VISIBLE);
                                radioC.setVisibility(View.VISIBLE);
                                radioD.setVisibility(View.VISIBLE);
                                btnSubmit.setVisibility(View.VISIBLE);
                                checkDistance.setVisibility(View.VISIBLE);
                                btnSubmit.setEnabled(true);
                                checkDistance.setEnabled(true);
                            }
                            else if(answerText.equals(getChoiceC)){
                                radioC.setChecked(true);
                                radioA.setEnabled(false);
                                radioB.setEnabled(false);
                                radioD.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}

                                No.setVisibility(View.VISIBLE);
                                symbol.setVisibility(View.VISIBLE);
                                txtNo.setVisibility(View.VISIBLE);
                                txtQuest.setVisibility(View.VISIBLE);
                                radioA.setVisibility(View.VISIBLE);
                                radioB.setVisibility(View.VISIBLE);
                                radioC.setVisibility(View.VISIBLE);
                                radioD.setVisibility(View.VISIBLE);
                                btnSubmit.setVisibility(View.VISIBLE);
                                checkDistance.setVisibility(View.VISIBLE);
                                btnSubmit.setEnabled(true);
                                checkDistance.setEnabled(true);
                            }
                            else if(answerText.equals(getChoiceD)){
                                radioD.setChecked(true);
                                radioA.setEnabled(false);
                                radioB.setEnabled(false);
                                radioC.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}

                                No.setVisibility(View.VISIBLE);
                                symbol.setVisibility(View.VISIBLE);
                                txtNo.setVisibility(View.VISIBLE);
                                txtQuest.setVisibility(View.VISIBLE);
                                radioA.setVisibility(View.VISIBLE);
                                radioB.setVisibility(View.VISIBLE);
                                radioC.setVisibility(View.VISIBLE);
                                radioD.setVisibility(View.VISIBLE);
                                btnSubmit.setVisibility(View.VISIBLE);
                                checkDistance.setVisibility(View.VISIBLE);
                                btnSubmit.setEnabled(true);
                                checkDistance.setEnabled(true);
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setAfterAnswer(){
        btnSubmit.setText("Back");
        btnSubmit.setBackgroundResource(R.drawable.search_btn);
        btnReset.setEnabled(false);
        btnReset.setVisibility(View.GONE);
    }

    public void setTextTrue(){
        ResultAnswer.setText("Correct");
        ResultAnswer.setTextColor(Color.parseColor("#0eff25"));
        ResultAnswer.setVisibility(View.VISIBLE);
    }

    public void setTextFalse(){
        ResultAnswer.setText("Incorrect");
        ResultAnswer.setTextColor(Color.parseColor("#FF1818"));
        ResultAnswer.setVisibility(View.VISIBLE);
    }

    private void submitChoiceAns(){
        final DatabaseReference table_ans2;
        //table_ans2=table_ans.getRef().child(Username);
        Query searchQuery = table_ans.orderByChild("subjectID").equalTo(subjectID);
        //Log.e("tag",searchQuery.toString());
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.e("data", dataSnapshot.toString());
                String checkDataStuAnswer = "False";
                String dataKey = "";
                String checkAnswer = "False";
                //Setup Data Answer
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Log.e("data", postSnapshot.toString());
                    Answer answer = new Answer();
                    answer = postSnapshot.getValue(Answer.class);

                    if (answer.getAssignname().equals(assignname) && answer.getUsername().equals(Username)) {
                        //Check data in Student_answer
                        checkDataStuAnswer = "True";
                        //getKey for Setup
                        dataKey = postSnapshot.getKey();
                    }
                }

                String choiceA = radioA.getText().toString();
                String choiceB = radioB.getText().toString();
                String choiceC = radioC.getText().toString();
                String choiceD = radioD.getText().toString();
                String choiceans="";

                if(sel.equals("A")){
                    choiceans=choiceA;
                }
                else if(sel.equals("B")){
                    choiceans=choiceB;
                }
                else if(sel.equals("C")){
                    choiceans=choiceC;
                }
                else{
                    choiceans=choiceD;
                }

                //Check Answer

                Log.e("answerChoice",answerChoice);
                Log.e("sel",sel);
                if (sel.equals(answerChoice)){
                    checkAnswer = "True";
                }
                //Add Answer
                if (checkDataStuAnswer.equals("True")){
                    //Set Valuse No_Question
                    //test question firebase
                    AnswerSolve answerSolve = new AnswerSolve(sel,choiceans,checkAnswer,question);
                    //table_solve3.child(Username).child("No_question").child(numberQuestion).setValue(answerSolve);
                    table_solve3.child(dataKey).child("No_question").child(numberQuestion).setValue(answerSolve);
                    if(checkAnswer.equals("True")) {
                        Toast.makeText(getContext(), "Correct", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    //create answer Member
                    String newKey = table_ans.push().getKey();
                    Answer answer = new Answer(Username,assignname,subjectID,0, name);
                    //Set Valuse No_Question
                    table_ans.child(newKey).setValue(answer);
                    AnswerSolve answerSolve = new AnswerSolve(sel,choiceans,checkAnswer,question);
                    table_ans.child(newKey).child("No_question").child(numberQuestion).setValue(answerSolve);
                    if(checkAnswer.equals("True")) {
                        Toast.makeText(getContext(), "Correct", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                    }

                }
                CheckScore();
                getActivity().finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void CheckScore(){
        Log.e("CheckScore","Runing");
        Query searchQuery = table_ans.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addChildEventListener(new ChildEventListener(){

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Answer answer = new Answer();
                answer = dataSnapshot.getValue(Answer.class);
                int countAnswer = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.child("No_question").getChildren()) {
                    //Log.e("Snapshot",postSnapshot.child("check").getValue().toString());
                    try {
                        if (postSnapshot.child("check").getValue().toString().equals("True")){
                            countAnswer++;
                        }
                    } catch (NullPointerException exception) {

                    }
                }
                table_ans.child(dataSnapshot.getKey()).child("score").setValue(countAnswer);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //------------- Radio Button: Choice -----------------//
    private void onClickRadioButton() {
        radioGroupChoice.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.radioA:
                sel = "A";
                break;
            case R.id.radioB:
                sel = "B";
                break;
            case R.id.radioC:
                sel = "C";
                break;
            case R.id.radioD:
                sel = "D";
                break;
        }
    }
    //--------------------------------------------------//


    private void clickButton() {
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        checkDistance.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnReset) {
            Toast.makeText(getActivity(), "Reset this question", Toast.LENGTH_SHORT);
            radioGroupChoice.clearCheck();
        } else if (v == btnSubmit) {
            Toast.makeText(getActivity(), "Submit assignment", Toast.LENGTH_SHORT);
            submitChoiceAns();
        } else if (v == checkDistance) {
            MapDistance();
        }
    }

}
