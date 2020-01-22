package com.example.asus.project4th;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.project4th.Model.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signin1Activity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonSignin;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin1);
        init();
        backToolbar();

        //Init Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference member_Teacher = database.getReference("Member").child("Teacher");
        final DatabaseReference member_Student = database.getReference("Member").child("Student");

        buttonSignin.setOnClickListener(new View.OnClickListener() {
            String Username = "";

            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(Signin1Activity.this);
                progressDialog.setMessage("Please waiting . . .");
                progressDialog.show();
                member_Teacher.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (editTextUsername.getText().toString().isEmpty()) {
                            progressDialog.dismiss();
                            Toast.makeText(Signin1Activity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                        } else if (editTextPassword.getText().toString().isEmpty()) {
                            progressDialog.dismiss();
                            Toast.makeText(Signin1Activity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                        } else {
                            //Check if user not exist in database
                            String Username = "";
                            if (dataSnapshot.child(editTextUsername.getText().toString()).exists()) {
                                //Get user information
                                progressDialog.dismiss();
                                Member member = dataSnapshot.child(editTextUsername.getText().toString()).getValue(Member.class);
                                //Member member = dataSnapshot.child(editTextUsername.getText().toString()).getValue(Member.class);
                                if (member.getPassword().equals(editTextPassword.getText().toString())) {
                                    //Toast.makeText(Signin1Activity.this, "Sign In successfully! ", Toast.LENGTH_SHORT).show();
                                    //TODO:
                                    if (member.getStatus().equals("Teacher")) {
                                        Intent signIn = new Intent(Signin1Activity.this, TeacherActivity.class);
                                        signIn.putExtra("Username", member.getUsername());
                                        signIn.putExtra("Status", member.getStatus());
                                        signIn.putExtra("Name", member.getName());
                                        signIn.putExtra("Password", member.getPassword());
                                        startActivity(signIn);
                                        Username = member.getUsername();

                                        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("USERNAME", member.getUsername());
                                        editor.putString("NAME", member.getName());
                                        editor.commit();
                                        Toast.makeText(Signin1Activity.this, "Sign In successfully! Welcome " + editTextUsername.getText().toString(), Toast.LENGTH_SHORT).show();

                                    } else {
                                        Intent signIn = new Intent(Signin1Activity.this, MainActivity.class);
                                        signIn.putExtra("Username", member.getUsername());
                                        signIn.putExtra("Status", member.getStatus());
                                        signIn.putExtra("Name", member.getName());
                                        signIn.putExtra("Password", member.getPassword());
                                        startActivity(signIn);
                                        Toast.makeText(Signin1Activity.this, "Sign In successfully! Welcome " + editTextUsername.getText().toString(), Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(Signin1Activity.this, "Wrong username or password!!!", Toast.LENGTH_SHORT).show();
                                }
                            } else{
                                member_Student.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (editTextUsername.getText().toString().isEmpty()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(Signin1Activity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                                        } else if (editTextPassword.getText().toString().isEmpty()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(Signin1Activity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                                        }

                                        else if (dataSnapshot.child(editTextUsername.getText().toString()).exists()) {
                                            Member member = dataSnapshot.child(editTextUsername.getText().toString()).getValue(Member.class);
                                            if (member.getPassword().equals(editTextPassword.getText().toString())) {
                                                //TODO:
                                                if (member.getStatus().equals("Student")) {
                                                    Intent signIn = new Intent(Signin1Activity.this, StudentActivity.class);
                                                    signIn.putExtra("Username", member.getUsername());
                                                    signIn.putExtra("Status", member.getStatus());
                                                    signIn.putExtra("Name", member.getName());
                                                    signIn.putExtra("Password", member.getPassword());
                                                    startActivity(signIn);
                                                    //Username = member.getUsername();

                                                    SharedPreferences sp2 = getSharedPreferences("PREF_NAME2", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sp2.edit();
                                                    editor.putString("USERNAME", member.getUsername());
                                                    editor.putString("NAME", member.getName());
                                                    editor.commit();
                                                    Toast.makeText(Signin1Activity.this, "Sign In successfully! Welcome " + editTextUsername.getText().toString(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                } else {
                                                    Intent signIn = new Intent(Signin1Activity.this, MainActivity.class);
                                                    signIn.putExtra("Username", member.getUsername());
                                                    signIn.putExtra("Status", member.getStatus());
                                                    signIn.putExtra("Name", member.getName());
                                                    signIn.putExtra("Password", member.getPassword());
                                                    startActivity(signIn);
                                                    Toast.makeText(Signin1Activity.this, "Sign In successfully! Welcome " + editTextUsername.getText().toString(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }

                                            } else {
                                                Toast.makeText(Signin1Activity.this, "Wrong username or password!!!", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        } else{
                                            Toast.makeText(Signin1Activity.this, "There is no this username", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }



                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    public void init() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignin = findViewById(R.id.buttonSignin);
    }

    private void backToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}

