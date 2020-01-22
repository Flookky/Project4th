package com.example.asus.project4th;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Answer;
import com.example.asus.project4th.Model.AnswerSolve;
import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice;
import com.example.asus.project4th.Model.Choice2;
import com.example.asus.project4th.Model.Score;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditNoQuestionActivity extends AppCompatActivity  {

    //<------------------------------------------------>
    final String TAG = "TTwTT";
    //-- Toolbar --***//
    private Toolbar toolbar;
    //<------------------------------------------------>

    private boolean doubleBackToExitPressedOnce;
    private static String assignname;
    private static String subjectID;
    //private String assignname;
    //private String subjectID;
    private String Username;
    private static String subjectName;
    private FirebaseDatabase database;
    private static DatabaseReference table_quest,Question_list;
    private static RecyclerView recyclerViewNoQuestion;
    //private RecyclerView recyclerViewNoQuestion;
    private static FirebaseRecyclerAdapter recyclerAdapter;
    //private FirebaseRecyclerAdapter recyclerAdapter;
    private static List<Choice> listNoQuestion;
    private static List<Choice> listQuestion;
    //private List<Choice> listNoQuestion;
    //private List<Choice> listQuestion;
    //private NoQuestionAdapter noQuestionAdapter;
    private static NoQuestionAdapter noQuestionAdapter;
    private Button mOrder;
    private TextView mItemSelected;
    private String[] listItems;
    private static ArrayList<String> listofKey = new ArrayList<>();
    private static ArrayList<String> listofQuestion = new ArrayList<>();
    private static ArrayList<String> listofQuestionShow = new ArrayList<>();
    private static ArrayList<String> listofStatus = new ArrayList<>();
    //private ArrayList<String> listofKey = new ArrayList<>();
    //private ArrayList<String> listofQuestion = new ArrayList<>();
    //private ArrayList<String> listofQuestionShow = new ArrayList<>();
    //private ArrayList<String> listofStatus = new ArrayList<>();
    private ArrayList<Integer> mUserItems = new ArrayList<>();
    private boolean[] checkedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_no_question);

        initInstances();
        backToolbar();
        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_Question();
            }
        });

    }

    private void initInstances() {
        //-- Toolbar --***//
        toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        assignname = intent.getStringExtra("assignname");
        subjectID = intent.getStringExtra("subjectID");
        subjectName = intent.getStringExtra("subjectName");
        toolbar.setTitle(assignname);

        mOrder = findViewById(R.id.preview);

        //----- Firebase ------//
        database = FirebaseDatabase.getInstance();
        table_quest = database.getReference("Assign");

        //--------------- RecyclerView --------------------//
        recyclerViewNoQuestion = findViewById(R.id.recyclerViewNoQuestion);
        recyclerViewNoQuestion.setHasFixedSize(true);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(this);
        recyclerViewNoQuestion.setLayoutManager(LM);
        recyclerViewNoQuestion.setItemAnimator(new DefaultItemAnimator());
        //recyclerViewNoQuestion.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //recyclerViewNoQuestion.setAdapter(noQuestionAdapter);

        //----------------- Question list -------------------------------//
        listNoQuestion = new ArrayList<>();
        listQuestion = new ArrayList<>();
        noQuestionAdapter = new NoQuestionAdapter(EditNoQuestionActivity.this, listNoQuestion, listQuestion, subjectID, assignname, new NoQuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Choice choice) {
                DatabaseReference ref = table_quest.getRef().child(subjectName).child(assignname);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("Quest")) {
                            for(DataSnapshot choiceSnapshot : dataSnapshot.child("Quest").getChildren()){
                                Choice2 choice2 = new Choice2();
                                choice2 = choiceSnapshot.getValue(Choice2.class);
                                if(choice2.getNumberQuestion().equals(choice.getNumberQuestion())){
                                    //String key = choiceSnapshot.getKey();
                                    String status = choice2.getStatus();
                                    //String status = choiceSnapshot.child(key).child("status").getValue(String.class);

                                    Intent iassign = new Intent(EditNoQuestionActivity.this, EditExamsActivity.class);
                                    iassign.putExtra("assignname", assignname);
                                    iassign.putExtra("subjectID", subjectID);
                                    iassign.putExtra("subjectName",subjectName);
                                    iassign.putExtra("status",status);
                                    iassign.putExtra("numberQuestion", choice.getNumberQuestion());
                                    Log.e("Send NumberQuestion", choice.getNumberQuestion());
                                    startActivity(iassign);
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //iassign.putExtra("status",choice.getStatus());
            }
        });
        GetNoQuestionFirebase();
        //Log.e("Final",noQuestionAdapter.listNoQuestion.get(0).getQuestion());

    }

    private void list_Question(){
        //firebase list data
        listofQuestionShow.clear();
        listofQuestion.clear();
        listofStatus.clear();
        listofKey.clear();
        Question_list = table_quest.getRef().child(subjectName).child(assignname);
        Question_list.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Quest")) {
                    for (DataSnapshot postSnapshot : dataSnapshot.child("Quest").getChildren()) {
                        Choice choice = new Choice();
                        choice = postSnapshot.getValue(Choice.class);
                        listofQuestionShow.add("Question: " + choice.getNumberQuestion());
                        listofQuestion.add(choice.getNumberQuestion());
                        listofStatus.add(choice.getStatus());
                        listofKey.add(postSnapshot.getKey().toString());
                    }
                    Log.e("QuestionList_1", listofQuestion.toString());
                }

                checkedItems = new boolean[listofQuestion.size()];

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditNoQuestionActivity.this);
                mBuilder.setTitle(R.string.dialog_title);
                mBuilder.setMultiChoiceItems(listofQuestionShow.toArray(new String[listofQuestion.size()])
                        , checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                                if(isChecked){
                                    mUserItems.add(position);
                                    checkedItems[position] = true;
                                    listofStatus.set(position,"ready");
                                    Toast.makeText(EditNoQuestionActivity.this, "Question: "+ (position+1) +" enabled", Toast.LENGTH_SHORT).show();
                                } else{
                                    mUserItems.remove((Integer.valueOf(position)));
                                    checkedItems[position] = false;
                                    listofStatus.set(position,"prepare");
                                    Toast.makeText(EditNoQuestionActivity.this, "Question: "+ (position+1) +" disabled", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for(int i = 0; i < checkedItems.length;i++){
                            table_quest.child(subjectName).child(assignname).child("Quest").child(listofKey.get(i)).child("status").setValue(listofStatus.get(i));
                        }
                        Toast.makeText(EditNoQuestionActivity.this, "Set Questions completed", Toast.LENGTH_SHORT).show();
                        GetNoQuestionFirebase();
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });

                for (int i = 0; i < checkedItems.length; i++) {
                    if(listofStatus.get(i).equals("ready")){
                        checkedItems[i] = true;
                    } else if(listofStatus.get(i).equals("prepare")){
                        checkedItems[i] = false;
                    }
                }

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //***************************************************** No. Question lists ********************************************************************************//
    private static void GetNoQuestionFirebase() {
        //Clear ListSubject
        listNoQuestion.clear();
        listQuestion.clear();
        //Log.e("delete", subjectID);
        Log.e("QuestionList_Clear", listNoQuestion.toString());
        Query searchQuery = table_quest.child(subjectName).orderByChild("subjectID").equalTo(subjectID);
        //Toast.makeText(this, subjectName, Toast.LENGTH_SHORT).show();

        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Log.e("CheckQuest",dataSnapshot.toString());
                Assign assign = new Assign();
                assign = dataSnapshot.getValue(Assign.class);
                Log.e("check_data", dataSnapshot.toString());
                if (assign.getAssignname().equals(assignname)) {
                    if (dataSnapshot.hasChild("Quest")) {
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Quest").getChildren()) {
                            Choice choice = new Choice();
                            choice = postSnapshot.getValue(Choice.class);
                            //Log.e("Quest",postSnapshot.toString());
                            listNoQuestion.add(choice);
                            listQuestion.add(choice);
                        }

                        Log.e("QuestionList_1", listNoQuestion.toString());

                    }
                } else {
                    //TODO NoQuest;
                }
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

        List<Choice> listAssignNoQuestion;
        List<Choice> listAssignQuestion;
        final OnItemClickListener listener;
        private Context context;
        private String TAG = "TT3TT";
        private String subjectID;
        private String assignname;


        public NoQuestionAdapter(Context context, List<Choice> listAssignNoQuestion, List<Choice> listAssignQuestion, String subjectID,String assignname,OnItemClickListener listener) {
            this.context = context;
            this.listAssignNoQuestion = listAssignNoQuestion;
            this.listAssignQuestion = listAssignQuestion;
            this.subjectID = subjectID;
            this.assignname = assignname;
            this.listener = listener;
        }

        public interface OnItemClickListener {
            void onItemClick(Choice choice);
        }

        @NonNull
        @Override
        public NoQuestionAdapter.NoQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_assign_no_quest, parent, false);
            return new NoQuestionViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull NoQuestionAdapter.NoQuestionViewHolder holder, final int position) {
            final Choice no_quest = listAssignQuestion.get(position);
            final Choice question = listAssignQuestion.get(position);

            holder.bind(no_quest, question, listener);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), "This subject already deleted!", Toast.LENGTH_SHORT).show();
                    //deleteSubject(subjectID.getSubjectID(), subjectname.getSubjectname(), date.getTime(), position);
                    final Dialog deleteDialog = new Dialog(context);
                    deleteDialog.setContentView(R.layout.dialog_delete_no_quest);
                    ImageButton btnConfirm = deleteDialog.findViewById(R.id.btnConfirm);
                    ImageButton btnCancel = deleteDialog.findViewById(R.id.btnCancel);

                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //deleteSubject(no_quest.getNumberQuestion(), question.getQuestion(), position);
                            deleteQuestion(no_quest.getNumberQuestion(), question.getQuestion(), position, context);
                            deleteDialog.dismiss();
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.cancel();
                        }
                    });
                    deleteDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listAssignQuestion.size();
        }

        public class NoQuestionViewHolder extends RecyclerView.ViewHolder {

            private final TextView txtNo;
            private final TextView txtQuest;
            private final ImageButton btnDelete;
            RelativeLayout list_item_no_quest;

            public NoQuestionViewHolder(View itemView) {
                super(itemView);
                list_item_no_quest = itemView.findViewById(R.id.list_item_no_quest);
                txtNo = itemView.findViewById(R.id.txtNo);
                txtQuest = itemView.findViewById(R.id.txtQuest);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                //btnDelete.setVisibility(View.INVISIBLE);
                //btnDelete.setEnabled(false);
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


        //--------------------- Delete subject button ------------------------------//
        private void deleteSubject(final String no_quest, String question, final int position) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Assign").child(subjectName);
            Query subjectQuery = ref.orderByChild("subjectID").equalTo(subjectID);
            subjectQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot questionSnapshot : dataSnapshot.getChildren()){
                        Assign assign = new Assign();
                        assign = questionSnapshot.getValue(Assign.class);
                        if(assign.getAssignname().equals(assignname)){
                            for(DataSnapshot deleteSnapshot : questionSnapshot.child("Quest").getChildren()){
                                Choice choice = new Choice();
                                choice = deleteSnapshot.getValue(Choice.class);
                                if(no_quest.equals(choice.getNumberQuestion())){
                                    Toast.makeText(context, "Delete Question " + choice.getNumberQuestion() +" Completed", Toast.LENGTH_SHORT).show();

                                    for(DataSnapshot changeSnapshot : questionSnapshot.child("Quest").getChildren()){
                                        Choice2 choiceChange = new Choice2();
                                        choiceChange = changeSnapshot.getValue(Choice2.class);
                                        //if(Integer.parseInt(choiceChange.getNumberQuestion()) == 1){ }

                                        if(Integer.parseInt(choiceChange.getNumberQuestion()) >= Integer.parseInt(no_quest)){
                                            String key = changeSnapshot.getKey();
                                            int numquestion_change = Integer.parseInt(choiceChange.getNumberQuestion()) -1;
                                            choiceChange.setNumberQuestion(String.valueOf(numquestion_change));
                                            ref.child(questionSnapshot.getKey()).child("Quest").child(key).setValue(choiceChange);

                                        }
                                    }

                                    deleteSnapshot.getRef().removeValue();
                                    listAssignNoQuestion.remove(position);
                                    listAssignQuestion.remove(position);
                                    notifyDataSetChanged();

                                    //notifyItemRemoved(position);
                                    //notifyItemRangeChanged(position, listAssignNoQuestion.size());
                                    //int newNumberQuestion = Integer.parseInt(choice.getNumberQuestion())-1;
                                    //choice.setNumberQuestion(String.valueOf(newNumberQuestion));

                                }
                                //int newNumberQuestion = Integer.parseInt(choice.getNumberQuestion())-1;
                                //choice.setNumberQuestion(String.valueOf(newNumberQuestion));

                            }
                        }


                    }


                    /*
                    int flag = 0;
                    for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                        //Log.e("data", questionSnapshot.toString());
                        Assign assign = new Assign();
                        assign = questionSnapshot.getValue(Assign.class);
                        if (assign.getAssignname().equals(assignname)) {
                            Log.e("data", assignname);
                            Log.e("data", assign.getAssignname());

                            for (DataSnapshot choiceSnapshot : questionSnapshot.child("Quest").getChildren()) {
                                //Log.e("data", no_quest);
                                if (flag > 0){
                                    Choice choice = new Choice();
                                    choice = choiceSnapshot.getValue(Choice.class);
                                    int newNumberQuestion = Integer.parseInt(choice.getNumberQuestion())-1;
                                    choice.setNumberQuestion(String.valueOf(newNumberQuestion));
                                    ref.child(questionSnapshot.getKey()).child("Quest").child(String.valueOf(newNumberQuestion)).setValue(choice);
                                    choiceSnapshot.getRef().removeValue();

                                }

                                if(choiceSnapshot.child("numberQuestion").getValue().equals(no_quest)) {
                                    //Log.e("data", choiceSnapshot.toString());
                                    choiceSnapshot.getRef().removeValue();
                                    int new_totalQuest = Integer.parseInt(assign.getTotalQuest())-1;
                                    ref.child(questionSnapshot.getKey()).child("totalQuest").setValue(String.valueOf(new_totalQuest));
                                    //Log.e("data", choiceSnapshot.getRef().toString());
                                    flag = 1;
                                }
                            }
                        }
                    }
                    */
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });
        }
    }



    //Tempolary method by all static value if it isn't work pls delete static each values and set deleteSubject instead
    public static void deleteQuestion(final String no_quest, String question, final int position, Context context) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Assign").child(subjectName);
        final DatabaseReference ref_delans = FirebaseDatabase.getInstance().getReference("Student_answer");
        Query subjectQuery = ref.orderByChild("subjectID").equalTo(subjectID);
        subjectQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot questionSnapshot : dataSnapshot.getChildren()){
                    Assign assign = new Assign();
                    assign = questionSnapshot.getValue(Assign.class);
                    if(assign.getAssignname().equals(assignname)){
                        for(final DataSnapshot deleteSnapshot : questionSnapshot.child("Quest").getChildren()){
                            Choice choice = new Choice();
                            choice = deleteSnapshot.getValue(Choice.class);

                            if(no_quest.equals(choice.getNumberQuestion())){
                                final String delquestKey = deleteSnapshot.getKey();
                                //Change count of questions
                                for(DataSnapshot changeSnapshot : questionSnapshot.child("Quest").getChildren()){
                                    Choice2 choiceChange = new Choice2();
                                    choiceChange = changeSnapshot.getValue(Choice2.class);
                                    if(Integer.parseInt(choiceChange.getNumberQuestion()) >= Integer.parseInt(no_quest)){
                                        String key = changeSnapshot.getKey();
                                        int numquestion_change = Integer.parseInt(choiceChange.getNumberQuestion()) -1;
                                        choiceChange.setNumberQuestion(String.valueOf(numquestion_change));
                                        ref.child(questionSnapshot.getKey()).child("Quest").child(key).setValue(choiceChange);
                                    }
                                }

                                //Fil del student answer
                                /*
                                ref_delans.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot answerSnapshot : dataSnapshot.getChildren()){
                                            if(answerSnapshot.hasChild(subjectName)){
                                                if(answerSnapshot.child(subjectName).hasChild(assignname)){
                                                    for(DataSnapshot scoreSnapshot : answerSnapshot.child(subjectName).child(assignname).getChildren()){
                                                        if(scoreSnapshot.hasChild("No_question")){
                                                            //String score = scoreSnapshot.getKey();
                                                            for(DataSnapshot delSnapshot : scoreSnapshot.child("No_question").getChildren()){
                                                                if(delSnapshot.getKey().equals(delquestKey)){
                                                                    AnswerSolve answerSolve = new AnswerSolve();
                                                                    answerSolve = delSnapshot.getValue(AnswerSolve.class);
                                                                    if(answerSolve.getCheck().equals("True")){
                                                                        delSnapshot.getRef().removeValue();
                                                                        Log.e("True delete ","yes");
                                                                    } else{
                                                                        delSnapshot.getRef().removeValue();
                                                                    }
                                                                    
                                                                } else{
                                                                    Log.e("Key is ","wrong");
                                                                }
                                                            }

                                                        }
                                                    }
                                                } else{
                                                    Log.e("Cannot Assignname ",assignname);
                                                }
                                            } else{
                                                Log.e("Cannot SubjectName ",subjectName);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                */


                                int totalquest_change = Integer.parseInt(assign.getTotalQuest()) -1;
                                ref.child(questionSnapshot.getKey()).child("totalQuest").setValue(String.valueOf(totalquest_change));
                                deleteSnapshot.getRef().removeValue();
                                GetNoQuestionFirebase();

                            }

                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }

    //--------------------- Back press Toolbar -----------------------//
    private void backToolbar() {
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

