package com.example.asus.project4th;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Member;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    //<------------------------------------------------>
    final String TAG = "TTwTT";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationManager locationManager;
    private boolean GpsStatus;
    private Context context;
    private Intent intent1;
    private FirebaseDatabase database;
    private DatabaseReference table_teacher;
    private DrawerLayout drawerLayout;
    private TextView textUsername;
    private TextView textStatus;
    private TextView textName;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private View headerView;
    private static String name;
    private String username;

    //<------------------------------------------------>
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        initInstance();
        displayDrawerLayout();
        initFirebase();
        getLocationPermission();

        //Toast.makeText(this, "Hello GPS", Toast.LENGTH_SHORT).show();

        //------ Replace null Fragment -----***//
        if (savedInstanceState == null) {
            //Toast.makeText(this, Username, Toast.LENGTH_SHORT).show();
            Log.i("TAG","Null savedInstanceState");
            initCourses();
            navigationView.setCheckedItem(R.id.nav_coures);
        }
        else{
            //Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
        }

    }

    private void initInstance() {
        // TODO;
        //-- Toolbar & DrawerLayout --***//
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        //-----------------------------------------------//
        //------------ Receive Intent from SignIn ------------***//
        headerView = navigationView.getHeaderView(0);
        textUsername = headerView.findViewById(R.id.txtUsername);
        textStatus = headerView.findViewById(R.id.txtStatus);
        textName = headerView.findViewById(R.id.txtName);
        name = textName.getText().toString();

        database = FirebaseDatabase.getInstance();
        table_teacher = database.getReference("Member").child("Teacher");

        //------------------------------------------------------//
    }

    @Override
    public void onLocationChanged(Location location) {

        final double longitude = location.getLongitude();
        final double latitude = location.getLatitude();
        String lon = String.valueOf(longitude);
        String lat = String.valueOf(latitude);
        Log.d(TAG, lon);
        Log.d(TAG, lat);

        final DatabaseReference map = database.getReference("Member").child("Teacher");
        map.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                map.child(username).child("latitude").setValue(latitude);
                map.child(username).child("longitude").setValue(longitude);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(TeacherActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
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

    //<---------------------------------- Firebase & Intent -------------------------->//
    private void initFirebase() {
        //Init Firebase SignIn
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_member = database.getReference("Member").child("Teacher");
        table_member.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent = getIntent();
                //String userName = intent.getStringExtra("Username");
                username = intent.getStringExtra("Username");// make all method can use
                name = intent.getStringExtra("Name");
                String status = intent.getStringExtra("Status");

                //HeaderView in Drawer Layout
                textUsername.setText(username);
                textStatus.setText(status);
                textName.setText(name);
                Log.d(TAG, String.valueOf(textName));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void initChangePassword() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_chgmember = database.getReference("Member").child("Teacher");
        table_chgmember.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Member member = dataSnapshot.child(textUsername.getText().toString()).getValue(Member.class);

                Bundle changepw = new Bundle();
                changepw.putString("Username", member.getUsername());
                changepw.putString("Password", member.getPassword());
                ChangepwFragment myObj = new ChangepwFragment();
                myObj.setArguments(changepw);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        myObj).commit();

                Log.d(TAG, String.valueOf(textUsername));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void initCourses() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_crsmember = database.getReference("Member").child("Teacher");
        table_crsmember.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Member member = dataSnapshot.child(textUsername.getText().toString()).getValue(Member.class);

                //Send data to courseFragment
                Bundle courseFragment = new Bundle();
                courseFragment.putString("Username", username);
                courseFragment.putString("Name", name);
                TeacherCoursesFragment myObj = new TeacherCoursesFragment();
                myObj.setArguments(courseFragment);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            myObj).commitAllowingStateLoss();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //-- Toolbar & DrawerLayout --***//
    private void displayDrawerLayout() {
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_coures:
                initCourses();
                break;
            case R.id.nav_changepw:
                initChangePassword();
                break;
            case R.id.nav_logout:
                signOut();
                Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        Intent intent = new Intent(TeacherActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //--------- Back Press --------------------***//
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce){
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
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    //-----------------------------------------------//
    //<---------------------------------------------------------------------------------------------------->//

    @Override
    protected void onStop(){
        super.onStop();
    }

}
