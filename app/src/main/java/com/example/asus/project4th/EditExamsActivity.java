package com.example.asus.project4th;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice2;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EditExamsActivity extends AppCompatActivity {

    //<------------------------------------------------>
    final String TAG = "TTwTT";
    //-- Toolbar --***//
    private Toolbar toolbar;
    //-- DrawerLayout --***//
    private DrawerLayout drawerLayout;
    private TextView textUsername;
    private TextView textStatus;
    private TextView textName;
    private NavigationView navigationView;
    private View headerView;
    //<------------------------------------------------>

    private boolean doubleBackToExitPressedOnce;
    private String subjectID;
    private String subjectName;
    private String assignname;
    private String numberQuestion;
    private String status;
    private FirebaseDatabase database;
    private DatabaseReference table_assign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exams);
        initInstance();
        backToolbar();
        if (savedInstanceState == null) {
            //Toast.makeText(this, "TeacherActivity", Toast.LENGTH_SHORT).show();
            selectQuestion();
            /*getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_exam,
                    new EditWriteFragment()).commit();*/
        }
    }

    private void initInstance() {
        //-- Toolbar --***//
        toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        assignname = intent.getStringExtra("assignname");
        subjectID = intent.getStringExtra("subjectID");
        subjectName = intent.getStringExtra("subjectName");
        numberQuestion = intent.getStringExtra("numberQuestion");
        status = intent.getStringExtra("status");
        toolbar.setTitle(numberQuestion);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        //----- Firebase ------//
        database = FirebaseDatabase.getInstance();
        table_assign = database.getReference().child("Assign");
        //-----------------------------------------------//
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

    private void selectQuestion(){

        Query searchQuery = table_assign.child(subjectName).orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Log.e("check",dataSnapshot.toString());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Assign assign = new Assign();
                    assign = postSnapshot.getValue(Assign.class);
                    //numberQusetion
                    //String numberQusetion = "1";
                    String questionType = "";
                    String key = "";
                    if (assign.getAssignname().equals(assignname)){
                        for(DataSnapshot choiceSnapshot : postSnapshot.child("Quest").getChildren()) {
                            Choice2 choice2 = new Choice2();
                            choice2 = choiceSnapshot.getValue(Choice2.class);

                            if(choice2.getNumberQuestion().equals(numberQuestion)){
                                key = choiceSnapshot.getKey();
                                questionType = choice2.getType();
                                Log.e("Key",key);
                                Log.e("Type",questionType);

                                //Choose Question Type
                                if (questionType.equals("choice")){
                                    //Set up Question Data
                                    String question = choice2.getQuestion();
                                    String answer = choice2.getAnswer();
                                    String answeris = choice2.getAnsweris();
                                    String choiceA = choice2.getChoiceA();
                                    String choiceB = choice2.getChoiceB();
                                    String choiceC = choice2.getChoiceC();
                                    String choiceD = choice2.getChoiceD();

                                    //Send to Fragment
                                    Bundle questionFragment = new Bundle();
                                    questionFragment.putString("question", question);
                                    questionFragment.putString("answerChoice", answer);
                                    questionFragment.putString("answeris",answeris);
                                    questionFragment.putString("choiceA", choiceA);
                                    questionFragment.putString("choiceB", choiceB);
                                    questionFragment.putString("choiceC", choiceC);
                                    questionFragment.putString("choiceD", choiceD);
                                    questionFragment.putString("key", key);
                                    questionFragment.putString("numberQuestion", numberQuestion);
                                    questionFragment.putString("subjectID", subjectID);
                                    questionFragment.putString("subjectName",subjectName);
                                    questionFragment.putString("assignname", assignname);
                                    questionFragment.putString("status", status);

                                    EditChoiceFragment myObj = new EditChoiceFragment();
                                    myObj.setArguments(questionFragment);

                                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_exam,
                                            myObj).commit();

                                } else if(questionType.equals("write")){
                                    //Set up Question Data
                                    String question = choice2.getQuestion();
                                    String answer = choice2.getAnswer();

                                    //Send to Fragment
                                    Bundle questionFragment = new Bundle();
                                    questionFragment.putString("question", question);
                                    questionFragment.putString("answerWrite", answer);
                                    questionFragment.putString("key", key);
                                    questionFragment.putString("numberQuestion", numberQuestion);
                                    questionFragment.putString("subjectID", subjectID);
                                    questionFragment.putString("subjectName",subjectName);
                                    questionFragment.putString("assignname", assignname);
                                    questionFragment.putString("status", status);

                                    EditWriteFragment myObj = new EditWriteFragment();
                                    myObj.setArguments(questionFragment);

                                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_exam,
                                            myObj).commit();

                                } else if(questionType.equals("TrueFalse")){
                                    String question = choice2.getQuestion();
                                    String answer = choice2.getAnswer();
                                    String answeris = choice2.getAnsweris();
                                    String choiceA = choice2.getChoiceA();
                                    String choiceB = choice2.getChoiceB();

                                    //Send to Fragment
                                    Bundle questionFragment = new Bundle();
                                    questionFragment.putString("question", question);
                                    questionFragment.putString("answerChoice", answer);
                                    questionFragment.putString("answeris",answeris);
                                    questionFragment.putString("choiceA", choiceA);
                                    questionFragment.putString("choiceB", choiceB);
                                    questionFragment.putString("key", key);
                                    questionFragment.putString("numberQuestion", numberQuestion);
                                    questionFragment.putString("subjectID", subjectID);
                                    questionFragment.putString("subjectName",subjectName);
                                    questionFragment.putString("assignname", assignname);
                                    questionFragment.putString("status", status);

                                    EditTrueFalseFragment myObj = new EditTrueFalseFragment();
                                    myObj.setArguments(questionFragment);

                                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_exam,
                                            myObj).commit();

                                }

                            }
                        }

                        //questionType = postSnapshot.child("Quest").child(numberQuestion).child("type").getValue().toString();
                        //questionType = postSnapshot.child("Quest").child(key).child("type").getValue().toString();
                        //Log.e("Type",questionType);

                        /*
                        //Choose Question Type
                        if (questionType.equals("choice")){
                            //Set up Question Data
                            //String question = postSnapshot.child("Quest").child(numberQuestion).child("question").getValue().toString();
                            //String answer = postSnapshot.child("Quest").child(numberQuestion).child("answer").getValue().toString();
                            //String answeris = postSnapshot.child("Quest").child(numberQuestion).child("answeris").getValue().toString();
                            //String choiceA = postSnapshot.child("Quest").child(numberQuestion).child("choiceA").getValue().toString();
                            //String choiceB = postSnapshot.child("Quest").child(numberQuestion).child("choiceB").getValue().toString();
                            //String choiceC = postSnapshot.child("Quest").child(numberQuestion).child("choiceC").getValue().toString();
                            //String choiceD = postSnapshot.child("Quest").child(numberQuestion).child("choiceD").getValue().toString();

                            String question = postSnapshot.child("Quest").child(key).child("question").getValue().toString();
                            String answer = postSnapshot.child("Quest").child(key).child("answer").getValue().toString();
                            String answeris = postSnapshot.child("Quest").child(key).child("answeris").getValue().toString();
                            String choiceA = postSnapshot.child("Quest").child(key).child("choiceA").getValue().toString();
                            String choiceB = postSnapshot.child("Quest").child(key).child("choiceB").getValue().toString();
                            String choiceC = postSnapshot.child("Quest").child(key).child("choiceC").getValue().toString();
                            String choiceD = postSnapshot.child("Quest").child(key).child("choiceD").getValue().toString();

                            //Send to Fragment
                            Bundle questionFragment = new Bundle();
                            questionFragment.putString("question", question);
                            questionFragment.putString("answerChoice", answer);
                            questionFragment.putString("answeris",answeris);
                            questionFragment.putString("choiceA", choiceA);
                            questionFragment.putString("choiceB", choiceB);
                            questionFragment.putString("choiceC", choiceC);
                            questionFragment.putString("choiceD", choiceD);
                            questionFragment.putString("numberQuestion", numberQuestion);
                            questionFragment.putString("subjectID", subjectID);
                            questionFragment.putString("subjectName",subjectName);
                            questionFragment.putString("assignname", assignname);
                            questionFragment.putString("status", status);

                            EditChoiceFragment myObj = new EditChoiceFragment();
                            myObj.setArguments(questionFragment);

                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_exam,
                                    myObj).commit();

                        } else if(questionType.equals("write")){
                            //Set up Question Data
                            String question = postSnapshot.child("Quest").child(numberQuestion).child("question").getValue().toString();
                            String answer = postSnapshot.child("Quest").child(numberQuestion).child("answer").getValue().toString();

                            //Send to Fragment
                            Bundle questionFragment = new Bundle();
                            questionFragment.putString("question", question);
                            questionFragment.putString("answerWrite", answer);
                            questionFragment.putString("numberQuestion", numberQuestion);
                            questionFragment.putString("subjectID", subjectID);
                            questionFragment.putString("subjectName",subjectName);
                            questionFragment.putString("assignname", assignname);
                            questionFragment.putString("status", status);

                            EditWriteFragment myObj = new EditWriteFragment();
                            myObj.setArguments(questionFragment);

                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_exam,
                                    myObj).commit();

                        } else if(questionType.equals("TrueFalse")){
                            String question = postSnapshot.child("Quest").child(numberQuestion).child("question").getValue().toString();
                            String answer = postSnapshot.child("Quest").child(numberQuestion).child("answer").getValue().toString();
                            String answeris = postSnapshot.child("Quest").child(numberQuestion).child("answeris").getValue().toString();
                            String choiceA = postSnapshot.child("Quest").child(numberQuestion).child("choiceA").getValue().toString();
                            String choiceB = postSnapshot.child("Quest").child(numberQuestion).child("choiceB").getValue().toString();

                            //Send to Fragment
                            Bundle questionFragment = new Bundle();
                            questionFragment.putString("question", question);
                            questionFragment.putString("answerChoice", answer);
                            questionFragment.putString("answeris",answeris);
                            questionFragment.putString("choiceA", choiceA);
                            questionFragment.putString("choiceB", choiceB);
                            questionFragment.putString("numberQuestion", numberQuestion);
                            questionFragment.putString("subjectID", subjectID);
                            questionFragment.putString("subjectName",subjectName);
                            questionFragment.putString("assignname", assignname);
                            questionFragment.putString("status", status);

                            EditTrueFalseFragment myObj = new EditTrueFalseFragment();
                            myObj.setArguments(questionFragment);

                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_exam,
                                    myObj).commit();

                        }
                        */

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
}

