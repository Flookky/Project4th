package com.example.asus.project4th;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.asus.project4th.Model.Student;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener {

    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private FirebaseDatabase database;
    private DatabaseReference table_subject;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap,mMapTeacher;
    private ArrayList<LatLng> listPoints = new ArrayList<>();
    private static Double Teacher_Lat;
    private static Double Teacher_Long;
    private static Double Student_Lat;
    private static Double Student_Long;
    private LatLng Teacher_latlng;
    private static LatLng Student_latlng;
    private Button Teacher,Student;
    private String teacherusername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        init();
        getLocationPermission();

        Teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCameraTeacher(new LatLng(Teacher_Lat, Teacher_Long),
                        DEFAULT_ZOOM);
            }
        });

        Student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCamera(new LatLng(Student_Lat, Student_Long),
                        DEFAULT_ZOOM,"Your Location");
            }
        });

    }

    private void init(){
        Teacher = findViewById(R.id.Teacher);
        Student = findViewById(R.id.Student);

        SharedPreferences sp = this.getSharedPreferences("TEACHER_USERNAME", Context.MODE_PRIVATE);
        teacherusername = sp.getString("TeacherUsername",null);

        database = FirebaseDatabase.getInstance();
        table_subject = database.getReference("Member").child("Student");
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void TeacherLocation(){
        final DatabaseReference teacherlocation = database.getReference("Member").child("Teacher").child(teacherusername);
        teacherlocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double lat = dataSnapshot.child("latitude").getValue(Double.class);
                Double lon = dataSnapshot.child("longitude").getValue(Double.class);

                Teacher_Lat = lat;
                Teacher_Long = lon;

                if(lat > 0 && lon > 0){
                    Student_latlng = new LatLng(Student_Lat, Student_Long);
                    Teacher_latlng = new LatLng(lat, lon);
                    listPoints.add(Teacher_latlng);

                    Location locstu = new Location(LocationManager.GPS_PROVIDER);
                    Location locteach = new Location(LocationManager.GPS_PROVIDER);

                    locstu.setLatitude(Student_latlng.latitude);
                    locstu.setLongitude(Student_latlng.longitude);

                    locteach.setLatitude(Teacher_latlng.latitude);
                    locteach.setLongitude(Teacher_latlng.longitude);

                    String distance_str;
                    float distance = locstu.distanceTo(locteach);
                    MarkerOptions markerTeacher = new MarkerOptions();

                    if(distance <= 1000.00){
                        DecimalFormat floatFormat = new DecimalFormat("#.00");
                        distance_str = String.valueOf(floatFormat.format(distance)) + " Metre";

                        if(listPoints.size() == 1){
                            listPoints.clear();
                            mMapTeacher.clear();
                        }
                        markerTeacher.position(Teacher_latlng)
                                .title("Teacher Location")
                                .snippet("Distance Between you and Teacher = " + distance_str)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mMapTeacher.addMarker(markerTeacher);
                    }
                    else{
                        DecimalFormat floatFormat = new DecimalFormat("#.00");
                        distance = distance/1000;
                        distance_str = String.valueOf(floatFormat.format(distance)) + " Kilometre";

                        if(listPoints.size() == 1){
                            listPoints.clear();
                            mMapTeacher.clear();
                        }
                        markerTeacher.position(Teacher_latlng)
                                .title("Teacher Location")
                                .snippet("Distance Between you and Teacher = " + distance_str)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mMapTeacher.addMarker(markerTeacher);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        mMapTeacher = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMapTeacher.setMyLocationEnabled(true);
            mMapTeacher.getUiSettings().setMyLocationButtonEnabled(false);


        }
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

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,"Your Location");
                            onLocationChanged(currentLocation);
                            Student_Lat = currentLocation.getLatitude();
                            Student_Long = currentLocation.getLongitude();
                            //Student_latlng = new LatLng(Student_Lat, Student_Long);
                            //String latlngstd = Student_Lat.toString();
                            //Toast.makeText(MapsActivity.this, latlngstd, Toast.LENGTH_SHORT).show();
                            TeacherLocation();

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    private void moveCameraTeacher(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMapTeacher.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    @Override
    public void onLocationChanged(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String lon = String.valueOf(longitude);
        String lat = String.valueOf(latitude);
        Student_Lat = latitude;
        Student_Long = longitude;
        Log.d(TAG, lon);
        Log.d(TAG, lat);
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

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
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
                    //initialize our map
                    initMap();
                }
            }
        }
    }

}