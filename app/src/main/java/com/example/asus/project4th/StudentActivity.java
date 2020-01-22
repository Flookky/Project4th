package com.example.asus.project4th;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import com.example.asus.project4th.Model.Subject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //<------------------------------------------------>
    final String TAG = "TTwTT";
    //-- DrawerLayout --***//
    private DrawerLayout drawerLayout;
    private TextView textUsername;
    private TextView textStatus;
    private TextView textName;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private View headerView;
    private String userName;
    private String name;
    private String[] items = new String[]{"-","1", "2", "3"};
    private boolean doubleBackToExitPressedOnce;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference table_subject;
    private ArrayList<String> listofSubject = new ArrayList<>();
    private ArrayList<String> listofSubjectMain = new ArrayList<>();
    private ArrayList<String> listofSubjectID = new ArrayList<>();
    private ArrayList<String> listofSubjectTime = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        initInstance();
        displayDrawerLayout();
        initFirebase();

        //------ Replace null Fragment -----***//
        if (savedInstanceState == null) {
            initCourses();
            navigationView.setCheckedItem(R.id.nav_coures);
        }
        //---------------------------------------------------------//

    }

    //<------------------------------- Pattern drawerLayout ---------------------------------------->//
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

        table_subject = database.getReference().child("Subjectstd");

        //------------------------------------------------------//
    }

    //<---------------------------------- Firebase & Intetn -------------------------->//
    private void initFirebase() {
        //Init Firebase SignIn
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_member = database.getReference("Member").child("Student");
        table_member.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent = getIntent();
                userName = intent.getStringExtra("Username");
                String status = intent.getStringExtra("Status");
                name = intent.getStringExtra("Name");
                String password = intent.getStringExtra("Password");
                //HeaderView in Drawer Layout
                textUsername.setText(userName);
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
        final DatabaseReference table_chgmember = database.getReference("Member").child("Student");
        table_chgmember.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Member member = dataSnapshot.child(textUsername.getText().toString()).getValue(Member.class);

                Bundle changepw = new Bundle();
                changepw.putString("Username", member.getUsername());
                changepw.putString("Password", member.getPassword());
                ChangepwFragment2 myObj = new ChangepwFragment2();
                myObj.setArguments(changepw);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        myObj).commit();


                /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        myObj).commit();
                */
                Log.d(TAG, String.valueOf(textUsername));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void initCourses() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_crsmember = database.getReference("Member").child("Student");
        table_crsmember.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Member member = dataSnapshot.child(textUsername.getText().toString()).getValue(Member.class);
                //Send data to courseFragment
                Bundle courseFragment = new Bundle();
                courseFragment.putString("Username", userName);
                courseFragment.putString("name", name);
                StudentCoursesFragment myObj = new StudentCoursesFragment();
                myObj.setArguments(courseFragment);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        myObj).commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void GraphofSubjects(){
        listofSubject.clear();
        listofSubjectMain.clear();
        listofSubjectID.clear();
        listofSubjectTime.clear();

        final DatabaseReference ref_subject = table_subject.getRef().child(userName);
        ref_subject.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listofSubject.add("None");
                for(DataSnapshot subjectSnapshot : dataSnapshot.getChildren()){
                    Subject subject = new Subject();
                    subject = subjectSnapshot.getValue(Subject.class);
                    String time = subject.getTime().substring(6,10);
                    listofSubject.add(subject.getSubjectname());
                    listofSubjectMain.add(subject.getSubjectname());
                    listofSubjectID.add(subject.getSubjectID());
                    listofSubjectTime.add(time);
                    Log.e("The year is",time);
                    Log.e("Year length",String.valueOf(time.length()));
                }
                Log.e("Subject",listofSubject.toString());
                Log.e("SubjectMain",listofSubjectMain.toString());
                Log.e("SubjectID",listofSubjectID.toString());

                if(listofSubjectMain.size() == 0){
                    Toast.makeText(StudentActivity.this, "You have no any subject yet", Toast.LENGTH_SHORT).show();
                } else{
                    Intent i = new Intent(StudentActivity.this,CreateGraphActivity.class);
                    i.putExtra("Username", userName);
                    i.putExtra("name", name);
                    i.putExtra("AllSubject",listofSubject);
                    i.putExtra("AllSubjectMain",listofSubjectMain);
                    i.putExtra("AllSubjectID",listofSubjectID);
                    i.putExtra("AllSubjectTime",listofSubjectTime);

                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            case R.id.nav_graph:
                GraphofSubjects();
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
        Intent intent = new Intent(StudentActivity.this, MainActivity.class);
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

}

