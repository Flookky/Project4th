package com.example.asus.project4th;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class CheckLocationActivity extends AppCompatActivity implements LocationListener {
    private Toolbar toolbar;
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
        setContentView(R.layout.activity_checklocation);
        initInstance();
        backToolbar();
        getLocationPermission();
    }

    private void initInstance(){
        database = FirebaseDatabase.getInstance();
        table_assign = database.getReference("Assign");
        table_answer = database.getReference("Student_answer");

        SharedPreferences sp = getApplicationContext().getSharedPreferences("TEACHER_USERNAME", Context.MODE_PRIVATE);
        teacherusername = sp.getString("TeacherUsername",null);

        //-- Toolbar --***//
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("CheckLocation");

        Intent intent = getIntent();
        assignname = intent.getStringExtra("assignname");
        subjectID = intent.getStringExtra("subjectID");
        subjectName = intent.getStringExtra("subjectName");
        Username = intent.getStringExtra("Username");
        name = intent.getStringExtra("name");
        numberQuestion = intent.getStringExtra("numberQuestion");

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
                            Toast.makeText(CheckLocationActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
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

                            distance_str = String.valueOf(floatFormat.format(distance)) + " Metre";
                            Toast.makeText(CheckLocationActivity.this, distance_str, Toast.LENGTH_SHORT).show();
                        } else{

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(CheckLocationActivity.this);
                            builder1.setMessage("You cannot do this question because distance between and " +
                                    "teacher are more than 200 metres. You can check in Check Distance button");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent iassign = new Intent(CheckLocationActivity.this, StudentAssignNoQuestionActivity.class);
                                            iassign.putExtra("assignname", assignname);
                                            iassign.putExtra("subjectID", subjectID);
                                            iassign.putExtra("subjectName",subjectName);
                                            iassign.putExtra("Username", Username);
                                            iassign.putExtra("name", name);
                                            iassign.putExtra("numberQuestion", numberQuestion);
                                            startActivity(iassign);
                                            finish();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                            distance_str = String.valueOf(floatFormat.format(distance)) + " Metre";
                            Toast.makeText(CheckLocationActivity.this, distance_str, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(CheckLocationActivity.this);
                            builder1.setMessage("You cannot do this question because distance between and " +
                                    "teacher are more than 200 metres. You can check in Check Distance button");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent iassign = new Intent(CheckLocationActivity.this, StudentAssignNoQuestionActivity.class);
                                            iassign.putExtra("assignname", assignname);
                                            iassign.putExtra("subjectID", subjectID);
                                            iassign.putExtra("subjectName",subjectName);
                                            iassign.putExtra("Username", Username);
                                            iassign.putExtra("name", name);
                                            iassign.putExtra("numberQuestion", numberQuestion);
                                            startActivity(iassign);
                                            finish();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                        DecimalFormat floatFormat = new DecimalFormat("#.00");
                        distance = distance/1000;

                        distance_str = String.valueOf(floatFormat.format(distance)) + " Kilometre";
                        Toast.makeText(CheckLocationActivity.this, distance_str, Toast.LENGTH_SHORT).show();
                    }
                }
                //Toast.makeText(MapsActivity.this, strlat, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        final double longitude = location.getLongitude();
        final double latitude = location.getLatitude();
        String lon = String.valueOf(longitude);
        String lat = String.valueOf(latitude);
        //Log.d(TAG, lon);
        //Log.d(TAG, lat);

        //Toast.makeText(this, lon, Toast.LENGTH_SHORT).show();
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

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                //initMap();
                getDeviceLocation();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void backToolbar() {
        //toolbar.setTitle(getString(R.string.assignment));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iassign = new Intent(CheckLocationActivity.this, StudentAssignmentActivity.class);
                iassign.putExtra("assignname", assignname);
                iassign.putExtra("subjectID", subjectID);
                iassign.putExtra("subjectName",subjectName);
                iassign.putExtra("Username", Username);
                iassign.putExtra("name", name);
                iassign.putExtra("numberQuestion", numberQuestion);
                startActivity(iassign);

            }
        });
    }

}
