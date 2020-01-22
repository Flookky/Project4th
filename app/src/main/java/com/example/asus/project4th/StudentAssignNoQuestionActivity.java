package com.example.asus.project4th;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class StudentAssignNoQuestionActivity extends AppCompatActivity {

    //<------------------------------------------------>
    final String TAG = "TTwTT";
    //-- Toolbar --***//
    private Toolbar toolbar;
    //<------------------------------------------------>

    //private EditText searchField;
    //private ImageButton searchBtn;

    private boolean doubleBackToExitPressedOnce;
    private String assignname;
    private String subjectID,subjectName;
    private String Username;
    private FirebaseDatabase database;
    private DatabaseReference table_quest,table_stdanswer;
    private RecyclerView recyclerViewNoQuestion;
    private String name;
    private List<Choice> listNoQuestion;
    private NoQuestionAdapter noQuestionAdapter;
    private List<Choice> listQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assign_no_question);

        initInstance();
        backToolbar();

    }

    private void initInstance() {
        //-- Toolbar --***//
        toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        assignname = intent.getStringExtra("assignname");
        subjectID = intent.getStringExtra("subjectID");
        subjectName = intent.getStringExtra("subjectName");
        Username = intent.getStringExtra("Username");
        name = intent.getStringExtra("name");
        toolbar.setTitle(assignname);

        //------------- Search No. -------------------//
        //searchField = findViewById(R.id.search_field);
        //searchBtn = findViewById(R.id.searchBtn);
        //searchBtn.setOnClickListener(this);

        //----- Firebase ------//
        database = FirebaseDatabase.getInstance();
        table_quest = database.getReference("Assign");
        table_stdanswer = database.getReference("Student_answer");

        //--------------- RecyclerView --------------------//
        recyclerViewNoQuestion = findViewById(R.id.recyclerViewNoQuestion);
        recyclerViewNoQuestion.setHasFixedSize(true);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(this);
        recyclerViewNoQuestion.setLayoutManager(LM);
        recyclerViewNoQuestion.setItemAnimator(new DefaultItemAnimator());
        //recyclerViewNoQuestion.addItemDecoration(new DividerItemDecoration(StudentAssignNoQuestionActivity.this, LinearLayoutManager.VERTICAL));

        //----------------- Question list -------------------------------//
        listNoQuestion = new ArrayList<>();
        listQuestion = new ArrayList<>();
        noQuestionAdapter = new NoQuestionAdapter(StudentAssignNoQuestionActivity.this, listNoQuestion, listQuestion, new NoQuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Choice choice) {

                Intent iassign = new Intent(StudentAssignNoQuestionActivity.this, StudentAssignmentActivity.class);
                iassign.putExtra("assignname", assignname);
                iassign.putExtra("subjectID", subjectID);
                iassign.putExtra("subjectName",subjectName);
                iassign.putExtra("Username", Username);
                iassign.putExtra("name", name);
                Log.e("Send NumberQuestion",choice.getNumberQuestion());
                iassign.putExtra("numberQuestion", choice.getNumberQuestion());
                startActivity(iassign);

            }
        });
        GetNoQuestionFirebase();
    }

    private void GetScoreStudent(){
        DatabaseReference score = table_quest.getRef().child(subjectName);
    }

    //***************************************************** No question lists ********************************************************************************//
    //<------------------------ Firebase search field and display list ------------------------------------>//
    private void GetNoQuestionFirebase() {
        //Clear ListSubject
        listNoQuestion.clear();
        listQuestion.clear();

        DatabaseReference ref = table_quest.getRef().child(subjectName);
        Query searchQuery = ref.orderByChild("subjectID").equalTo(subjectID);
        //Query searchQuery = table_quest.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Log.e("CheckQuest",dataSnapshot.toString());
                Assign assign = new Assign();
                assign = dataSnapshot.getValue(Assign.class);
                //String a;

                if (assign.getAssignname().equals(assignname)) {
                    if (dataSnapshot.hasChild("Quest")) {
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Quest").getChildren()) {
                            String status;
                            //for (int i = 1; i < Integer.parseInt(assign.getTotalQuest()); i++) {
                            Choice choice = new Choice();
                            //choice = postSnapshot.child(String.valueOf(i)).getValue(Choice.class);
                            choice = postSnapshot.getValue(Choice.class);
                            Log.e("Quest", postSnapshot.toString());

                            if(postSnapshot.hasChild("status")){
                                status = (String) postSnapshot.child("status").getValue();
                                if(status.equals("prepare")){
                                    //Toast.makeText(StudentAssignNoQuestionActivity.this, "Prepare", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    listNoQuestion.add(choice);
                                    listQuestion.add(choice);
                                }
                            }
                            else{
                                listNoQuestion.add(choice);
                                listQuestion.add(choice);
                            }
                        }
                    }
                    //a=assign.getTotalQuest();
                    //Toast.makeText(StudentAssignNoQuestionActivity.this, a, Toast.LENGTH_SHORT).show();

                } else {
                    //TODO NoQuest;
                }
                //Log.e("List", listNoQuestion.toString());
                recyclerViewNoQuestion.setAdapter(noQuestionAdapter);
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

    private void GetSearchNoQuestionFirebase(final String searchText) {
        //Clear ListSubject
        listNoQuestion.clear();
        listQuestion.clear();

        Query searchQuery = table_quest.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Log.e("CheckQuest",dataSnapshot.toString());
                Assign assign = new Assign();
                assign = dataSnapshot.getValue(Assign.class);
                if (assign.getAssignname().equals(assignname)) {
                    if (dataSnapshot.hasChild("Quest")) {
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Quest").getChildren()) {
                            //for (int i = 1; i < Integer.parseInt(assign.getTotalQuest()); i++) {
                            Choice choice = new Choice();
                            //choice = postSnapshot.child(String.valueOf(i)).getValue(Choice.class);
                            choice = postSnapshot.getValue(Choice.class);
                            Log.e("Quest", postSnapshot.toString());
                            if (choice.getNumberQuestion().contains(searchText)) {
                                listNoQuestion.add(choice);
                                listQuestion.add(choice);
                            } else if (choice.getQuestion().contains(searchText)) {
                                listNoQuestion.add(choice);
                                listQuestion.add(choice);
                            }
                            //}
                        }
                    }
                } else {
                    //TODO NoQuest;
                }
                //Log.e("List", listNoQuestion.toString());
                recyclerViewNoQuestion.setAdapter(noQuestionAdapter);
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

    //---------------- No. question List -------------------------------------------------//
    public static class NoQuestionAdapter extends RecyclerView.Adapter<NoQuestionAdapter.NoQuestionViewHolder> {

        List<Choice> listNoQuestion;
        List<Choice> listQuestion;
        final OnItemClickListener listener;
        private Context context;

        public NoQuestionAdapter(Context context, List<Choice> listNoQuestion, List<Choice> listQuestion, OnItemClickListener listener) {
            this.context = context;
            this.listNoQuestion = listNoQuestion;
            this.listQuestion = listQuestion;
            this.listener = listener;
        }

        public interface OnItemClickListener {
            void onItemClick(Choice choice);
        }


        @NonNull
        @Override
        public NoQuestionAdapter.NoQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_assign_no_quest_stu, parent, false);
            return new NoQuestionViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull NoQuestionAdapter.NoQuestionViewHolder holder, int position) {
            Choice no_quest = listNoQuestion.get(position);
            Choice question = listQuestion.get(position);
            holder.bind(no_quest, question, listener);
        }

        @Override
        public int getItemCount() {
            return listNoQuestion.size();
        }

        public class NoQuestionViewHolder extends RecyclerView.ViewHolder {
            private final TextView txtNo;
            private final TextView txtQuest;
            RelativeLayout list_item_no_quest;

            public NoQuestionViewHolder(View itemView) {
                super(itemView);
                list_item_no_quest = itemView.findViewById(R.id.list_item_no_quest);
                txtNo = itemView.findViewById(R.id.txtNo);
                txtQuest = itemView.findViewById(R.id.txtQuest);
            }

            public void bind(final Choice no_quest, Choice question, final OnItemClickListener listener) {
                txtNo.setText(no_quest.getNumberQuestion());
                txtQuest.setText(question.getQuestion());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(no_quest);
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

    /*
    @Override
    public void onClick(View v) {
        if (v == searchBtn){
            if (searchField.getText().toString().isEmpty()){
                GetNoQuestionFirebase();
            } else {
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                String searchText = searchField.getText().toString();
                GetSearchNoQuestionFirebase(searchText);
            }
        }
    }
    */
}

