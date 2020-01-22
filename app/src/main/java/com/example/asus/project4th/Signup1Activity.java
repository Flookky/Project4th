package com.example.asus.project4th;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signup1Activity extends AppCompatActivity {

    final private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference table_member;
    private TextView checkUsername,checkPassword,checkRepassword,checkName;
    private EditText editTextUsername,editTextPassword,editTextRePassword,editTextName;
    private RadioGroup radioGroupStatus;
    private RadioButton radioStatus;
    private Button buttonRegister;
    private Toolbar toolbar;
    private String sel = " ";
    private String specialChar = "[a-zA-Z0-9_]*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);
        init();
        checkText();
        backToolbar();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSignup();
            }
        });
    }

    void init(){
        checkUsername = findViewById(R.id.checkUsername);
        checkPassword = findViewById(R.id.checkPassword);
        checkRepassword = findViewById(R.id.checkRepassword);
        checkName = findViewById(R.id.checkName);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);
        editTextName = findViewById(R.id.editTextName);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        buttonRegister = findViewById(R.id.buttonRegister);
        //Init Firebase
        table_member = database.getReference("Member");

    }

    private void checkText(){
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String txtUsername = s.toString();
                if (s.length()==0){
                    checkUsername.setText("Require Username");
                    checkUsername.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()<6){
                    checkUsername.setText("Minimum Username length is 6");
                    checkUsername.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>12){
                    checkUsername.setText("Maximum Username length is 12");
                    checkUsername.setTextColor(Color.parseColor("#FA5858"));
                } else if (!txtUsername.matches(specialChar)){
                    checkUsername.setText("Special Characters cannot use");
                    checkUsername.setTextColor(Color.parseColor("#FA5858"));
                } else {
                    table_member.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("Teacher").child(txtUsername).exists()){
                                checkUsername.setText("User already used");
                                checkUsername.setTextColor(Color.parseColor("#FA5858"));
                            } else if(dataSnapshot.child("Teacher").child(txtUsername.trim()).exists()){
                                checkUsername.setText("User already used");
                                checkUsername.setTextColor(Color.parseColor("#FA5858"));
                            } else if(dataSnapshot.child("Student").child(txtUsername).exists()){
                                checkUsername.setText("User already used");
                                checkUsername.setTextColor(Color.parseColor("#FA5858"));
                            } else if(dataSnapshot.child("Student").child(txtUsername.trim()).exists()){
                                checkUsername.setText("User already used");
                                checkUsername.setTextColor(Color.parseColor("#FA5858"));
                            } else{
                                checkUsername.setText("This Username can use");
                                checkUsername.setTextColor(Color.parseColor("#04B404"));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String txtPassword = s.toString();
                final String txtRepassword = editTextRePassword.getText().toString();

                if (s.length()==0){
                    checkPassword.setText("Require password");
                    checkPassword.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()<6){
                    checkPassword.setText("Minimum Password length is 6");
                    checkPassword.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>12){
                    checkPassword.setText("Maximum Password length is 12");
                    checkPassword.setTextColor(Color.parseColor("#FA5858"));
                } else if (!txtPassword.matches(specialChar)){
                    checkPassword.setText("Special Characters cannot use");
                    checkPassword.setTextColor(Color.parseColor("#FA5858"));
                } else if (!txtRepassword.equals(txtPassword)){
                    checkRepassword.setText("Password and Repassword must same");
                    checkRepassword.setTextColor(Color.parseColor("#FA5858"));
                } else{
                    checkPassword.setText("You can use this Password");
                    checkPassword.setTextColor(Color.parseColor("#04B404"));
                }
            }
        });

        editTextRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String password = editTextPassword.getText().toString();
                final String txtRepassword = s.toString();
                if (s.length()==0){
                    checkRepassword.setText("Require Repassword");
                    checkRepassword.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()<6){
                    checkRepassword.setText("Minimum Repassword length is 6");
                    checkRepassword.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>12){
                    checkRepassword.setText("Maximum Repassword length is 12");
                    checkRepassword.setTextColor(Color.parseColor("#FA5858"));
                } else if (!txtRepassword.matches(specialChar)){
                    checkRepassword.setText("Special Characters cannot use");
                    checkRepassword.setTextColor(Color.parseColor("#FA5858"));
                } else if (!txtRepassword.equals(password)){
                    checkRepassword.setText("Password and Repassword must same");
                    checkRepassword.setTextColor(Color.parseColor("#FA5858"));
                } else{
                    checkPassword.setText("You can use this Password");
                    checkPassword.setTextColor(Color.parseColor("#04B404"));
                    checkRepassword.setText("OK");
                    checkRepassword.setTextColor(Color.parseColor("#04B404"));
                }
            }
        });

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String txtName = editTextName.getText().toString();
                if (s.length()==0){
                    checkName.setText("Require Name");
                    checkName.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>12){
                    checkName.setText("Maximum Name length is 12");
                    checkName.setTextColor(Color.parseColor("#FA5858"));
                } else if (!txtName.matches(specialChar)){
                    checkName.setText("Special Characters cannot use");
                    checkName.setTextColor(Color.parseColor("#FA5858"));
                } else{
                    checkName.setText("This Name can use");
                    checkName.setTextColor(Color.parseColor("#04B404"));
                }
            }
        });
    }

    private void checkSignup(){
        final ProgressDialog progressDialog = new ProgressDialog(Signup1Activity.this);
        progressDialog.setMessage("Please waiting . . .");
        progressDialog.show();

        table_member.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String usernametext=editTextUsername.getText().toString();
                String passwordtext=editTextPassword.getText().toString();
                String repasswordtext=editTextRePassword.getText().toString();
                String nametext=editTextName.getText().toString();
                //Check if editText is empty
                if (usernametext.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                } else if (passwordtext.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                } else if (repasswordtext.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Please enter Repassword", Toast.LENGTH_SHORT).show();
                } else if (nametext.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                } else if (radioGroupStatus.getCheckedRadioButtonId() == -1) {
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Please select your status", Toast.LENGTH_SHORT).show();
                } else if (usernametext.length()<6) {
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Minimum Username length is 6", Toast.LENGTH_SHORT).show();
                } else if (usernametext.length()>12){
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Maximum Username length is 12", Toast.LENGTH_SHORT).show();
                } else if (!usernametext.matches(specialChar)){
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Special Characters cannot use", Toast.LENGTH_SHORT).show();
                } else if (passwordtext.length()<6) {
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Minimum Password length is 6", Toast.LENGTH_SHORT).show();
                } else if (passwordtext.length()>12){
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Maximum Password length is 12", Toast.LENGTH_SHORT).show();
                } else if(nametext.length()>12){
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Maximum Name length is 12", Toast.LENGTH_SHORT).show();
                } else if (!passwordtext.matches(specialChar)){
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Special Characters cannot use", Toast.LENGTH_SHORT).show();
                } else if (!passwordtext.equals(repasswordtext)){
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Password or Repassword not equal", Toast.LENGTH_SHORT).show();
                } else if (dataSnapshot.child(editTextUsername.getText().toString()).exists()) {
                    progressDialog.dismiss();
                    Toast.makeText(Signup1Activity.this, "Username already register", Toast.LENGTH_SHORT).show();
                } else{
                    if(sel=="Teacher"){
                        String name=editTextName.getText().toString();
                        String username=editTextUsername.getText().toString();
                        String password=editTextPassword.getText().toString();
                        Member member = new Member(name, password, sel, username);
                        //Teacher.child("No_question").child(numberQuestion).child("question").setValue(member);
                        table_member.child("Teacher").child(username).setValue(member);
                        Toast.makeText(Signup1Activity.this, "Sign up successfully!", Toast.LENGTH_SHORT).show();
                        Intent signUp = new Intent(Signup1Activity.this, MainActivity.class);
                        startActivity(signUp);
                    }
                    else{
                        String name=editTextName.getText().toString();
                        String username=editTextUsername.getText().toString();
                        String password=editTextPassword.getText().toString();
                        Member member = new Member(name, password, sel, username);
                        //Teacher.child("No_question").child(numberQuestion).child("question").setValue(member);
                        table_member.child("Student").child(username).setValue(member);
                        Toast.makeText(Signup1Activity.this, "Sign up successfully!", Toast.LENGTH_SHORT).show();
                        Intent signUp = new Intent(Signup1Activity.this, MainActivity.class);
                        startActivity(signUp);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioStudent:
                if (checked)
                    sel = "Student";
                break;
            case R.id.radioTeacher:
                if (checked)
                    sel = "Teacher";
                break;
        }
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

