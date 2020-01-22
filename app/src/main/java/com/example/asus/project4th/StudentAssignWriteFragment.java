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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Answer;
import com.example.asus.project4th.Model.AnswerSolve;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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

import static android.support.constraint.Constraints.TAG;

public class StudentAssignWriteFragment extends Fragment implements View.OnClickListener, LocationListener {

    private View view;
    private TextView No,symbol,txtQuest;
    private TextView txtNo;
    private TextView ResultAnswerWrite;
    private EditText edittextA;
    private FirebaseDatabase database;
    private DatabaseReference table_ans,table_check,table_solve;
    private DatabaseReference table_quest;
    private ImageButton btnReset;
    private Button btnSubmit,checkDistance;
    private String numberQuestion;
    private String question;
    private String Username;
    private String assignname;
    private String subjectID,subjectName;
    private String answerWrite;
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_assign_write_stu, container, false);

        if (getArguments() != null) {

            numberQuestion = getArguments().getString("numberQuestion");
            question = getArguments().getString("question");
            Username = getArguments().getString("Username");
            name = getArguments().getString("name");
            assignname = getArguments().getString("assignname");
            subjectID = getArguments().getString("subjectID");
            subjectName = getArguments().getString("subjectName");
            answerWrite = getArguments().getString("answerWrite");

            //Toast.makeText(getContext(), Username, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Bundle == null", Toast.LENGTH_SHORT).show();
        }

        initInstance();
        //checkDoWrite();
        clickButton();
        getLocationPermission();
        return view;
    }

    private void initInstance() {
        //----------- Firebase ---------------//
        database = FirebaseDatabase.getInstance();
        table_ans = database.getReference("Student_answer");
        table_check = database.getReference("Student_answer");
        table_solve = database.getReference("Student_answer");
        table_quest = database.getReference("Assign");

        SharedPreferences sp = getActivity().getSharedPreferences("TEACHER_USERNAME", Context.MODE_PRIVATE);
        teacherusername = sp.getString("TeacherUsername",null);

        //----------- Question -----------//
        No = view.findViewById(R.id.No);
        symbol = view.findViewById(R.id.symbol);
        txtQuest = view.findViewById(R.id.txtQuest);
        txtNo = view.findViewById(R.id.txtNo);
        edittextA = view.findViewById(R.id.edittextA);
        ResultAnswerWrite = view.findViewById(R.id.ResultAnswerWrite);

        //---------- Button -------------//
        btnReset = view.findViewById(R.id.btnReset);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        checkDistance = view.findViewById(R.id.mapCheck);

        //----------- Set Up -----------//
        txtQuest.setText(question);
        txtNo.setText(numberQuestion);

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
                            checkDoWrite();

                            distance_str = String.valueOf(floatFormat.format(distance)) + " Metre";
                            Toast.makeText(getActivity(), distance_str, Toast.LENGTH_SHORT).show();
                        } else{
                            edittextA.setEnabled(false);
                            btnSubmit.setEnabled(false);
                            btnReset.setEnabled(false);
                            No.setVisibility(View.INVISIBLE);
                            symbol.setVisibility(View.INVISIBLE);
                            txtNo.setVisibility(View.INVISIBLE);
                            txtQuest.setVisibility(View.INVISIBLE);
                            edittextA.setVisibility(View.INVISIBLE);
                            btnSubmit.setVisibility(View.INVISIBLE);
                            btnReset.setVisibility(View.INVISIBLE);
                            checkDoWrite2();
                            String text = edittextA.getText().toString();
                            //Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                            if(text.equals("Correct") || text.equals("InCorrect")){

                            } else{
                                edittextA.setEnabled(false);

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
                            distance_str = String.valueOf(floatFormat.format(distance)) + " Metre";
                            Toast.makeText(getActivity(), distance_str, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        checkDoWrite2();
                        String text = edittextA.getText().toString();
                        //Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                        if(text.equals("Correct") || text.equals("InCorrect")){

                        } else{
                            edittextA.setEnabled(false);
                            btnSubmit.setEnabled(false);
                            btnReset.setEnabled(false);
                            //edittextA.setVisibility(View.INVISIBLE);
                            //btnSubmit.setVisibility(View.INVISIBLE);
                            //btnReset.setVisibility(View.INVISIBLE);

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


    public void checkDoWrite(){
        //table_solve.getRef().child(Username).child("No_question");
        table_check.getRef().child(Username).child("No_question");
        table_check.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            if(answerText.equals(answerWrite)){
                                edittextA.setText(answerText);
                                edittextA.setEnabled(false);
                                setAfterAnswer();
                                setTextTrue();
                            }
                            else{
                                edittextA.setText(answerText);
                                edittextA.setEnabled(false);
                                setAfterAnswer();
                                setTextFalse();
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

    public void checkDoWrite2(){
        //table_solve.getRef().child(Username).child("No_question");
        table_check.getRef().child(Username).child("No_question");
        table_check.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            edittextA.setVisibility(View.INVISIBLE);
                            btnReset.setVisibility(View.INVISIBLE);
                            btnSubmit.setVisibility(View.INVISIBLE);

                            btnReset.setEnabled(false);
                            btnSubmit.setEnabled(false);

                            if(answerText.equals(answerWrite)){
                                edittextA.setText(answerText);
                                edittextA.setEnabled(false);

                                setAfterAnswer();
                                setTextTrue();

                                No.setVisibility(View.VISIBLE);
                                symbol.setVisibility(View.VISIBLE);
                                txtNo.setVisibility(View.VISIBLE);
                                txtQuest.setVisibility(View.VISIBLE);
                                btnSubmit.setVisibility(View.VISIBLE);
                                checkDistance.setVisibility(View.VISIBLE);
                                btnReset.setEnabled(true);
                                btnSubmit.setEnabled(true);
                                checkDistance.setEnabled(true);
                            }
                            else if(!answerText.equals(answerWrite)){
                                edittextA.setText(answerText);
                                edittextA.setEnabled(false);

                                setAfterAnswer();
                                setTextFalse();

                                No.setVisibility(View.VISIBLE);
                                symbol.setVisibility(View.VISIBLE);
                                txtNo.setVisibility(View.VISIBLE);
                                txtQuest.setVisibility(View.VISIBLE);
                                btnSubmit.setVisibility(View.VISIBLE);
                                checkDistance.setVisibility(View.VISIBLE);
                                btnReset.setEnabled(true);
                                btnSubmit.setEnabled(true);
                                checkDistance.setEnabled(true);
                            }
                            else{}

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
        ResultAnswerWrite.setText("Correct");
        ResultAnswerWrite.setTextColor(Color.parseColor("#0eff25"));
        ResultAnswerWrite.setVisibility(View.VISIBLE);
    }

    public void setTextFalse(){
        ResultAnswerWrite.setText("Incorrect");
        ResultAnswerWrite.setTextColor(Color.parseColor("#FF1818"));
        ResultAnswerWrite.setVisibility(View.VISIBLE);
    }

    private void submitChoiceAns(){
        Query searchQuery = table_ans.orderByChild("subjectID").equalTo(subjectID);
        final String answertext = edittextA.getText().toString();
        //Log.e("tag",searchQuery.toString());
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.e("data", dataSnapshot.toString());
                String checkDataStuAnswer = "False";
                String dataKey = "";
                String checkAnswer = "False";
                String Answer=edittextA.getText().toString();
                //Setup Data Answer
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Log.e("data", postSnapshot.toString());
                    Answer answer = new Answer();
                    answer = postSnapshot.getValue(Answer.class);

                    //Log.e("Answer_assignname",answer.getAssignname());
                    //Log.e("Answer_Username",answer.getUsername());

                    if (answer.getAssignname().equals(assignname) && answer.getUsername().equals(Username)) {
                        //Check data in Student_answer
                        checkDataStuAnswer = "True";
                        //getKey for Setup
                        dataKey = postSnapshot.getKey();
                    }
                }
                //Check Answer
                Log.e("Answer",edittextA.getText().toString());
                if (edittextA.getText().toString().equals(answerWrite)){
                    checkAnswer = "True";
                }
                //Add Answer
                if (checkDataStuAnswer.equals("True")){
                    //Set Valuse No_Question
                    AnswerSolve answerSolve = new AnswerSolve(answertext,answertext,checkAnswer,question);
                    table_ans.child(dataKey).child("No_question").child(numberQuestion).setValue(answerSolve);
                    if(checkAnswer.equals("True")) {
                        Toast.makeText(getContext(), "Correct", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    //create answer Member
                    Answer answer = new Answer(Username,assignname,subjectID,0, name);
                    String newKey = table_ans.push().getKey();
                    table_ans.child(newKey).setValue(answer);
                    //Set Valuse No_Question
                    AnswerSolve answerSolve2 = new AnswerSolve(answertext,answertext,checkAnswer,question);
                    table_ans.child(newKey).child("No_question").child(numberQuestion).setValue(answerSolve2);
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
                    /*if (postSnapshot.child("check").getValue().toString().equals("True")){
                        countAnswer++;
                    }*/
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

    private void clickButton() {
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        checkDistance.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnReset) {
            Toast.makeText(getActivity(), "Reset this question", Toast.LENGTH_SHORT);
            edittextA.getText().clear();
        } else if (v == btnSubmit) {
            Toast.makeText(getActivity(), "Submit assignment", Toast.LENGTH_SHORT);
            submitChoiceAns();
        } else if (v == checkDistance) {
            MapDistance();
        }
    }

}

