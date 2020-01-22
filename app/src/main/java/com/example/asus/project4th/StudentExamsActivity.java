package com.example.asus.project4th;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice;
import com.example.asus.project4th.Model.Choice2;
import com.example.asus.project4th.Model.Subject;
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
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class StudentExamsActivity extends AppCompatActivity implements LocationListener {

    //<------------------------------------------------>
    final String TAG = "TTwTT";
    //-- Toolbar --***//
    private Toolbar toolbar;
    //<------------------------------------------------>

    private boolean doubleBackToExitPressedOnce;
    private FloatingActionButton fab;
    private Button check_distance;
    private RecyclerView recyclerViewAssign;
    private FirebaseDatabase database;
    private DatabaseReference table_assign;
    private List<Subject> listSubjectID;
    private List<Assign> listAssignName;
    private List<Assign> listAssignDate;
    private List<Assign> listAssignLocationStatus;
    private List<Assign> listAssign_Status;
    private AssignAdapter assignAdapter;
    private String subjectID;
    private String subjectName;
    private String Username;
    private String name;
    private String teacherusername;
    private ArrayList<String> listofType = new ArrayList<>();
    private ArrayList<String> listofQuestion = new ArrayList<>();
    private ArrayList<String> listofKey = new ArrayList<>();
    private LatLng Teacher_latlng;
    private static Double Student_Lat;
    private static Double Student_Long;
    private static LatLng Student_latlng;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private ArrayList<LatLng> listPoints = new ArrayList<>();
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_exams);
        initInstance();
        backToolbar();
        getLocationPermission();

        check_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(StudentExamsActivity.this,MapsActivity.class);
                startActivity(map);
            }
        });
    }

    private void initInstance() {
        Intent intent = getIntent();
        subjectID = intent.getStringExtra("subjectID");
        subjectName = intent.getStringExtra("subjectName");
        Username = intent.getStringExtra("Username");
        teacherusername = intent.getStringExtra("TeacherUsername");
        name = intent.getStringExtra("name");

        //-- Toolbar --***//
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(subjectName);
        check_distance = findViewById(R.id.checkdistance);

        SharedPreferences sp = getSharedPreferences("TEACHER_USERNAME",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("TeacherUsername",teacherusername);
        editor.commit();

        //----- Firebase ------//
        database = FirebaseDatabase.getInstance();
        table_assign = database.getReference().child("Assign");

        //--------------- RecyclerView --------------------//
        recyclerViewAssign = findViewById(R.id.recyclerViewAssign);
        recyclerViewAssign.setHasFixedSize(true);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(this);
        recyclerViewAssign.setLayoutManager(LM);
        recyclerViewAssign.setItemAnimator(new DefaultItemAnimator());
        //recyclerViewAssign.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        //recyclerViewAssign.setAdapter(recyclerAdapter);

        //----------------- Assignment list -------------------------------//
        listSubjectID = new ArrayList<>();
        listAssignName = new ArrayList<>();
        listAssignDate = new ArrayList<>();
        listAssign_Status = new ArrayList<>();
        listAssignLocationStatus = new ArrayList<>();
        assignAdapter = new AssignAdapter(listAssignName,listAssignLocationStatus,listAssign_Status,listAssignDate, new AssignAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Assign assign) {
                //Check Location First
                if(assign.getAssign_status().equals("closed")){
                    Toast.makeText(StudentExamsActivity.this, "This assignment hasn't been opened yet", Toast.LENGTH_SHORT).show();
                } else{
                    if(assign.getLocation_status().equals("ready")){
                        getDeviceLocation(assign.getAssignname(),assign.getTotalQuest());
                        //Toast.makeText(StudentExamsActivity.this, "Location Check", Toast.LENGTH_SHORT).show();
                    } else{
                        GotoExam(assign.getAssignname(),assign.getTotalQuest());
                    }
                }
            }
        });

        GetAssignFirebase();

    }

    private void GotoExam(final String assignname,final String totalQuest){
        listofQuestion.clear();
        listofType.clear();
        listofKey.clear();

        final Query list_query = table_assign.child(subjectName).orderByChild("subjectID").equalTo(subjectID);
        list_query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Assign assign = new Assign();
                Choice2 choice2 = new Choice2();
                assign = dataSnapshot.getValue(Assign.class);
                Log.e("check_data", dataSnapshot.toString());
                if (assign.getAssignname().equals(assignname)) {
                    if (dataSnapshot.hasChild("Quest")) {
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Quest").getChildren()) {
                            choice2 = postSnapshot.getValue(Choice2.class);
                            if(choice2.getStatus().equals("ready")){
                                listofQuestion.add(choice2.getNumberQuestion());
                                listofKey.add(postSnapshot.getKey());
                                listofType.add(choice2.getType());
                            } else if(choice2.getStatus().equals("prepare")){}
                        }
                    }
                    if(listofQuestion.size() == 0 || listofType.size() == 0){
                        Toast.makeText(getApplicationContext(), "You haven't any question yet", Toast.LENGTH_SHORT).show();
                    } else{
                        Intent allQuestion = new Intent(getApplicationContext(),ViewAllQuestionStuActivity.class);
                        allQuestion.putExtra("totalQuest",assign.getTotalQuest());
                        allQuestion.putExtra("listofQuestion",listofQuestion);
                        allQuestion.putExtra("listofType",listofType);
                        allQuestion.putExtra("listofKey",listofKey);
                        allQuestion.putExtra("assignname", assignname);
                        allQuestion.putExtra("subjectID", subjectID);
                        allQuestion.putExtra("subjectName",subjectName);
                        allQuestion.putExtra("Username", Username);
                        allQuestion.putExtra("name", name);
                        startActivity(allQuestion);

                    }

                } else {
                    //TODO NoQuest;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CheckDistanceBeforeExam(final String assignname,final String totalQuest){
        final DatabaseReference teacherlocation = database.getReference("Member").child("Teacher").child(teacherusername);
        teacherlocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double lat = dataSnapshot.child("latitude").getValue(Double.class);
                Double lon = dataSnapshot.child("longitude").getValue(Double.class);

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
                        if(distance <= 100.00){
                            distance_str = String.valueOf(floatFormat.format(distance)) + " Metre";
                            Toast.makeText(StudentExamsActivity.this, "Distance between teacher and you is "+distance_str, Toast.LENGTH_SHORT).show();

                            listofQuestion.clear();
                            listofType.clear();
                            listofKey.clear();

                            final Query list_query = table_assign.child(subjectName).orderByChild("subjectID").equalTo(subjectID);
                            list_query.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    Assign assign = new Assign();
                                    Choice2 choice2 = new Choice2();
                                    assign = dataSnapshot.getValue(Assign.class);
                                    Log.e("check_data", dataSnapshot.toString());
                                    if (assign.getAssignname().equals(assignname)) {
                                        if (dataSnapshot.hasChild("Quest")) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.child("Quest").getChildren()) {
                                                choice2 = postSnapshot.getValue(Choice2.class);
                                                if(choice2.getStatus().equals("ready")){
                                                    listofQuestion.add(choice2.getNumberQuestion());
                                                    listofKey.add(postSnapshot.getKey());
                                                    listofType.add(choice2.getType());
                                                } else if(choice2.getStatus().equals("prepare")){}
                                            }
                                        }
                                        if(listofQuestion.size() == 0 || listofType.size() == 0){
                                            Toast.makeText(getApplicationContext(), "You haven't any question yet", Toast.LENGTH_SHORT).show();
                                        } else{
                                            Intent allQuestion = new Intent(getApplicationContext(),ViewAllQuestionStuActivity.class);
                                            allQuestion.putExtra("totalQuest",assign.getTotalQuest());
                                            allQuestion.putExtra("listofQuestion",listofQuestion);
                                            allQuestion.putExtra("listofType",listofType);
                                            allQuestion.putExtra("listofKey",listofKey);
                                            allQuestion.putExtra("assignname", assignname);
                                            allQuestion.putExtra("subjectID", subjectID);
                                            allQuestion.putExtra("subjectName",subjectName);
                                            allQuestion.putExtra("Username", Username);
                                            allQuestion.putExtra("name", name);
                                            startActivity(allQuestion);

                                        }

                                    } else {
                                        //TODO NoQuest;
                                    }
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else{
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(StudentExamsActivity.this);
                            builder1.setMessage("You cannot do this question because distance between and " +
                                    "teacher are more than 100 metres. You can check in Check Distance button");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            builder1.setNeutralButton(
                                    "Check Distance",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent map = new Intent(getApplicationContext(), MapsActivity.class);
                                            startActivity(map);
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                            distance_str = String.valueOf(floatFormat.format(distance)) + " Metre";
                            Toast.makeText(StudentExamsActivity.this, "Distance between teacher and you is "+distance_str, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(StudentExamsActivity.this);
                        builder1.setMessage("You cannot do this question because distance between and " +
                                "teacher are more than 100 metres. You can check in Check Distance button");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        builder1.setNeutralButton(
                                "Check Distance",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent map = new Intent(getApplicationContext(), MapsActivity.class);
                                        startActivity(map);
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                        DecimalFormat floatFormat = new DecimalFormat("#.00");
                        distance = distance/1000;

                        distance_str = String.valueOf(floatFormat.format(distance)) + " Kilometre";
                        Toast.makeText(StudentExamsActivity.this, "Distance between teacher and you is "+distance_str, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //***************************************************** Assignment lists ********************************************************************************//
    //<------------------------ Firebase search field and display list ------------------------------------>//
    private void GetAssignFirebase() {
        //Clear ListSubject
        listAssignName.clear();
        listAssignDate.clear();
        listAssign_Status.clear();
        listofQuestion.clear();
        listofType.clear();
        listAssignLocationStatus.clear();
        DatabaseReference ref = table_assign.getRef().child(subjectName);
        //Query searchQuery = table_assign.orderByChild("subjectID").equalTo(subjectID);
        Query searchQuery = ref.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Assign assign = new Assign();
                assign = dataSnapshot.getValue(Assign.class);
                //Add to ArrayList
                listAssignName.add(assign);
                listAssignDate.add(assign);
                listAssign_Status.add(assign);
                listAssignLocationStatus.add(assign);
                //Add List into Adapter/RecyclerView
                recyclerViewAssign.setAdapter(assignAdapter);
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

    //---------------- Subject List -------------------------------------------------//
    public static class AssignAdapter extends RecyclerView.Adapter<StudentExamsActivity.AssignAdapter.AssignHolder> {

        List<Assign> listAssignName;
        List<Assign> listAssign_Status;
        List<Assign> listAssignLocationStatus;
        List<Assign> listAssignDate;
        final OnItemClickListener listener;
        private Context context;

        public interface OnItemClickListener {
            void onItemClick(Assign assign);
        }

        public AssignAdapter(List<Assign> listAssignName, List<Assign> listAssignLocationStatus, List<Assign> listAssign_Status, List<Assign> listAssignDate, OnItemClickListener listener) {
            this.listAssignName = listAssignName;
            this.listAssignLocationStatus = listAssignLocationStatus;
            this.listAssign_Status = listAssign_Status;
            this.listAssignDate = listAssignDate;
            this.listener = listener;
        }

        @NonNull
        @Override
        public AssignAdapter.AssignHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_assign_names, parent, false);
            return new StudentExamsActivity.AssignAdapter.AssignHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AssignAdapter.AssignHolder holder, int position) {
            Assign assignname = listAssignName.get(position);
            Assign assigndate = listAssignDate.get(position);
            Assign assignstatus = listAssign_Status.get(position);
            Assign assignlocation = listAssignLocationStatus.get(position);
            holder.bind(assignname, assignlocation, assignstatus, assigndate, listener);
        }

        @Override
        public int getItemCount() {
            return listAssignName.size();
        }

        public class AssignHolder extends RecyclerView.ViewHolder {
            TextView textAssignName;
            TextView textDate;
            RelativeLayout list_assign_names;

            public AssignHolder(View itemView) {
                super(itemView);
                list_assign_names = itemView.findViewById(R.id.list_item_assign_name);
                textAssignName = itemView.findViewById(R.id.textAssignName);
                textDate = itemView.findViewById(R.id.textDate);
            }

            public void bind(final Assign assignname, Assign assignlocation, Assign assignstatus , Assign assigndate, final OnItemClickListener listener) {
                textAssignName.setText(assignname.getAssignname());
                textDate.setText(assigndate.getTime());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(assignname);
                    }
                });
            }
        }
    }

    //--------------------- Back press Toolbar -----------------------//
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

    private void getDeviceLocation1(){
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
                            //CheckDistanceBeforeExam();

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(StudentExamsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void getDeviceLocation(final String assignname,final String totalQuest){
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
                            CheckDistanceBeforeExam(assignname,totalQuest);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(StudentExamsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
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
                    //getDeviceLocation();
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
                //getDeviceLocation();
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

}
