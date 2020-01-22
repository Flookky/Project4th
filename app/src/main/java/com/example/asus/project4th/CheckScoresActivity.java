package com.example.asus.project4th;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Answer;
import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice;
import com.example.asus.project4th.Model.Choice2;
import com.example.asus.project4th.Model.Score;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckScoresActivity extends AppCompatActivity {

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

    private String userName;
    private boolean doubleBackToExitPressedOnce;
    private String subjectID;
    private String subjectname = "";
    private String assignname;
    private RecyclerView recyclerViewScore;
    //private FirebaseRecyclerAdapter recyclerAdapter;
    private List<Answer> listStuUserName;
    private List<Answer> listName;
    private List<Answer> listScore;
    private ScoreAdapter scoreAdapter;
    private FirebaseDatabase database;
    private DatabaseReference table_answer;
    private DatabaseReference table_member;
    private ArrayList<String> listofType = new ArrayList<>();
    private ArrayList<String> listofQuestion = new ArrayList<>();
    private ArrayList<String> listofKey = new ArrayList<>();
    private ArrayList<String> listofTypeStu = new ArrayList<>();
    private ArrayList<String> listofQuestionStu = new ArrayList<>();
    private ArrayList<String> listofKeyStu = new ArrayList<>();
    private String TotalQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_scores);

        initInstance();
        backToolbar();
    }

    private void initInstance() {

        //--------------------- Firebase ----------------------------//
        database = FirebaseDatabase.getInstance();
        table_answer = database.getReference("Student_answer");
        table_member = database.getReference("Member");

        //-- Toolbar --***//
        toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        assignname = intent.getStringExtra("assignname");
        subjectID = intent.getStringExtra("subjectID");
        subjectname = intent.getStringExtra("subjectName");
        userName = intent.getStringExtra("Username");
        toolbar.setTitle(assignname);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);

        //--------------- RecyclerView --------------------//
        recyclerViewScore = findViewById(R.id.recyclerViewSubject);
        recyclerViewScore.setHasFixedSize(true);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(this);
        recyclerViewScore.setLayoutManager(LM);
        recyclerViewScore.setItemAnimator(new DefaultItemAnimator());

        //----------------- Score list -------------------------------//
        listStuUserName = new ArrayList<>();
        listName = new ArrayList<>();
        listScore = new ArrayList<>();
        scoreAdapter = new ScoreAdapter(this, listStuUserName,listName ,listScore ,new ScoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Answer answer) {
                Log.e("Question",listofQuestion.toString());
                Log.e("Key",listofKey.toString());
                Log.e("Type",listofType.toString());
                DatabaseReference ref = database.getReference("Student_answer").child(answer.getUsername()).child(subjectname).child(assignname);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot keySnapshot : dataSnapshot.getChildren()){
                            Answer answer1 = new Answer();
                            answer1 = keySnapshot.getValue(Answer.class);
                            listofQuestionStu.clear();
                            listofKeyStu.clear();
                            listofTypeStu.clear();

                            if(!keySnapshot.getKey().isEmpty()){
                                //Toast.makeText(CheckScoresActivity.this, keySnapshot.getKey(), Toast.LENGTH_SHORT).show();
                                if(answer.getUsername().equals(answer1.getUsername()) && answer.getSubjectID().equals(answer1.getSubjectID())
                                       && answer.getAssignname().equals(answer1.getAssignname())){
                                    if(keySnapshot.hasChild("No_question")){
                                        for(DataSnapshot questionSnapshot : keySnapshot.child("No_question").getChildren()){
                                            for(int i = 0; i < listofKey.size();i++){
                                                if(questionSnapshot.getKey().equals(listofKey.get(i))){
                                                    listofQuestionStu.add(listofQuestion.get(i));
                                                    listofKeyStu.add(listofKey.get(i));
                                                    listofTypeStu.add(listofType.get(i));
                                                }
                                            }

                                            //Toast.makeText(CheckScoresActivity.this, questionSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(CheckScoresActivity.this, "No it hasn't No_question", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else{
                                //Toast.makeText(CheckScoresActivity.this, "No", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if(listofQuestionStu.size() == 0){
                            Toast.makeText(CheckScoresActivity.this, "You haven't any question yet", Toast.LENGTH_SHORT).show();
                        } else{
                            DatabaseReference allquest = database.getReference("Assign").child(subjectname).child(assignname);
                            allquest.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Assign assign = new Assign();
                                    assign = dataSnapshot.getValue(Assign.class);
                                    //Toast.makeText(CheckScoresActivity.this, assign.getTotalQuest(), Toast.LENGTH_SHORT).show();

                                    String totalquest = String.valueOf(listofQuestionStu.size());
                                    //Log.e("TotalQuestNOW",assign.getTotalQuest());
                                    Log.e("TotalQuestNOW",String.valueOf(totalquest));
                                    Log.e("QuestStuNOW",listofQuestionStu.toString());
                                    Log.e("KeyStuNOW",listofKeyStu.toString());
                                    Log.e("TypeStuNOW",listofTypeStu.toString());

                                    Intent allQuestion = new Intent(CheckScoresActivity.this,ViewAllQuestionCheckActivity.class);
                                    allQuestion.putExtra("totalQuest",totalquest);
                                    allQuestion.putExtra("listofQuestion",listofQuestionStu);
                                    allQuestion.putExtra("listofKey",listofKeyStu);
                                    allQuestion.putExtra("listofType",listofTypeStu);
                                    allQuestion.putExtra("assignment",assignname);
                                    allQuestion.putExtra("subjectName",subjectname);
                                    allQuestion.putExtra("subjectID",subjectID);
                                    allQuestion.putExtra("Username",userName);
                                    allQuestion.putExtra("StudentName",answer.getUsername());
                                    startActivity(allQuestion);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        GetScoreFirebase();
        GetYourQuestion();
    }

    //***************************************************** No question lists ********************************************************************************//
    //<------------------------ Firebase search field and display list ------------------------------------>//
    //TODO;
    private void GetYourQuestion(){
        listofQuestion.clear();
        listofKey.clear();
        listofType.clear();
        final DatabaseReference table_assign = database.getReference("Assign");
        final Query list_query = table_assign.child(subjectname).orderByChild("subjectID").equalTo(subjectID);
        list_query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Assign assign = new Assign();
                assign = dataSnapshot.getValue(Assign.class);
                Log.e("check_data", dataSnapshot.toString());
                if (assign.getAssignname().equals(assignname)) {
                    if (dataSnapshot.hasChild("Quest")) {
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Quest").getChildren()) {
                            Choice choice = new Choice();
                            choice = postSnapshot.getValue(Choice.class);
                            String key = postSnapshot.getKey();
                            listofQuestion.add(choice.getNumberQuestion());
                            listofType.add(choice.getType());
                            listofKey.add(key);
                        }
                    }
                    if(listofQuestion.size() == 0 || listofType.size() == 0){
                        Toast.makeText(CheckScoresActivity.this, "You haven't any question yet", Toast.LENGTH_SHORT).show();
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

    private void GetScoreFirebase(){
        //Clear ListSubject
        listStuUserName.clear();
        listName.clear();
        listScore.clear();
        Log.e("subjectID",subjectID.toString());
        //Display Score
        table_answer.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.hasChild(subjectname)){
                    if(dataSnapshot.child(subjectname).hasChild(assignname)){
                        for(DataSnapshot answerSnapshot : dataSnapshot.child(subjectname).child(assignname).getChildren()){
                            Answer answer = new Answer();
                            answer = answerSnapshot.getValue(Answer.class);
                            if(answer.getAssignname().equals(assignname)){
                                listStuUserName.add(answer);
                                listScore.add(answer);
                                listName.add(answer);
                            }
                            recyclerViewScore.setAdapter(scoreAdapter);
                        }
                    }

                }
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

    //--------------------------------*********************----------------------------------//

    //---------------- Score List -------------------------------------------------//
    public static class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>{

        List<Answer> listStuUserName;
        List<Answer> listName;
        List<Answer> listScore;
        final OnItemClickListener listener;
        private Context context;

        public ScoreAdapter(Context context, List<Answer> listStuUserName,List<Answer> listName, List<Answer> listScore, OnItemClickListener listener) {
            this.listStuUserName = listStuUserName;
            this.listScore = listScore;
            this.listName = listName;
            this.context = context;
            this.listener = listener;
        }

        public interface OnItemClickListener {
            void onItemClick(Answer answer);
        }

        @NonNull
        @Override
        public ScoreAdapter.ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_score_stu_username, parent, false);
            return new ScoreViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ScoreAdapter.ScoreViewHolder holder, int position) {
            final Answer userName = listStuUserName.get(position);
            final Answer name = listName.get(position);
            final Answer score = listScore.get(position);
            holder.bind(userName, name ,score ,listener);
        }

        @Override
        public int getItemCount() {
            return listStuUserName.size();
        }

        public class ScoreViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout list_item_score_stu;
            TextView textUserName;
            TextView textName;
            TextView textScore;

            public ScoreViewHolder(View itemView) {
                super(itemView);
                textUserName = itemView.findViewById(R.id.textUsername);
                textName = itemView.findViewById(R.id.textName);
                textScore = itemView.findViewById(R.id.textScore);
            }

            public void bind(final Answer userName, Answer name, Answer score, final OnItemClickListener listener) {
                textUserName.setText(userName.getUsername());
                textName.setText(name.getName());
                textScore.setText(String.valueOf(score.getScore()));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(userName);
                    }
                });
            }
        }
    }
    //-------------------------------------------------------------------------//



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

}

