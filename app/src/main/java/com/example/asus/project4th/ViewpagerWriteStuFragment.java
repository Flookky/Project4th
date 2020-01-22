package com.example.asus.project4th;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Answer;
import com.example.asus.project4th.Model.AnswerSolve;
import com.example.asus.project4th.Model.Choice;
import com.example.asus.project4th.Model.Write;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewpagerWriteStuFragment extends Fragment implements View.OnClickListener {
    private int questionNumber;
    private String numberquest, assignname, subjectID, subjectName, Username, name, key;
    private TextView txtNo,txtQuestion,ResultAnswer;
    private Button btnSubmit,btnReset;
    private EditText edittextA;
    private String answerWrite;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference table_quest = database.getReference("Assign");
    private DatabaseReference table_solve2 = database.getReference("Student_answer");

    public static ViewpagerWriteStuFragment newInstance(int questionNo, String assignname, String subjectID, String subjectName, String Username, String name, String key) {
        ViewpagerWriteStuFragment fragment = new ViewpagerWriteStuFragment();
        Bundle args = new Bundle();
        args.putInt("questionNo", questionNo);
        args.putString("assignname", assignname);
        args.putString("subjectID", subjectID);
        args.putString("subjectName", subjectName);
        args.putString("Username", Username);
        args.putString("name", name);
        args.putString("key", key);
        Log.d("SUBJECT_ID",subjectID);
        fragment.setArguments(args);

        return fragment;
    }

    public ViewpagerWriteStuFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignname = getArguments().getString("assignname");
        subjectID = getArguments().getString("subjectID");
        subjectName = getArguments().getString("subjectName");
        questionNumber = getArguments().getInt("questionNo");
        Username = getArguments().getString("Username");
        name = getArguments().getString("name");
        key = getArguments().getString("key");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_write_stu, container, false);
        txtQuestion = view.findViewById(R.id.txtQuest);
        txtNo = view.findViewById(R.id.txtNo);
        edittextA = view.findViewById(R.id.edittextA);
        ResultAnswer = view.findViewById(R.id.ResultAnswer);
        btnReset = view.findViewById(R.id.btnReset);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        numberquest = String.valueOf(questionNumber);
        txtNo.setText(numberquest);

        initFirebase();
        clickButton();

        return view;
    }

    void initFirebase() {
        DatabaseReference ref = table_quest.getRef().child(subjectName).child(assignname).child("Quest").child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Write write = new Write();
                write = dataSnapshot.getValue(Write.class);
                txtQuestion.setText(write.getQuestion());
                String answerQuestion = write.getAnswer();
                checkDoWrite(answerQuestion);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkDoWrite(final String answerQuestion){
        DatabaseReference ref = database.getReference("Student_answer").child(Username).child(subjectName).child(assignname);
        ref.getRef().child(Username).child("No_question");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Answer answer = new Answer();
                String NumberQuestion = String.valueOf(questionNumber);
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    answer = postSnapshot.getValue(Answer.class);
                    if(assignname.equals(answer.getAssignname())
                            && Username.equals(answer.getUsername())
                            && subjectID.equals(answer.getSubjectID()))
                    {
                        if(postSnapshot.child("No_question").child(key).exists()){
                            AnswerSolve answerSolve=postSnapshot.child("No_question").child(key).getValue(AnswerSolve.class);
                            String answerText=answerSolve.getAnswertext();
                            //String answerCheck=answerSolve.getCheck();
                            //Toast.makeText(getContext(), answerText, Toast.LENGTH_SHORT).show();
                            if(answerText.equals(answerQuestion)){
                                edittextA.setText(answerText);
                                edittextA.setEnabled(false);
                                setAfterAnswer();
                                setTextTrue();
                            }
                            else{
                                edittextA.setText(answerText);
                                edittextA.setEnabled(false);
                                setAfterAnswer();
                                setTextFalse();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void submitAnswer(){
        DatabaseReference ref = table_quest.getRef().child(subjectName).child(assignname).child("Quest").child(key);
        if(edittextA.getText().toString().isEmpty()||edittextA.getText().toString().equals("")){
            Toast.makeText(getContext(), "Please press answer", Toast.LENGTH_SHORT).show();
        } else{
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Write write = new Write();
                    write = dataSnapshot.getValue(Write.class);
                    txtQuestion.setText(write.getQuestion());

                    String answerWrite = write.getAnswer();
                    String Question = write.getQuestion();
                    String NumberQuestion = write.getNumberQuestion();

                    enterAnswer(Question,answerWrite);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void enterAnswer(final String Question, final String answerWrite){

        final DatabaseReference ref = database.getReference("Student_answer").child(Username).child(subjectName).child(assignname);

        Query searchQuery = ref.orderByChild("subjectID").equalTo(subjectID);
        final String answertext = edittextA.getText().toString();

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.e("data", dataSnapshot.toString());
                String checkDataStuAnswer = "False";
                String dataKey = "";
                String checkAnswer = "False";
                String Answer = edittextA.getText().toString();
                String NumberQuestion = String.valueOf(questionNumber);
                //Setup Data Answer
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Answer answer = new Answer();
                    answer = postSnapshot.getValue(Answer.class);

                    if (answer.getAssignname().equals(assignname) && answer.getUsername().equals(Username)) {
                        //Check data in Student_answer
                        checkDataStuAnswer = "True";
                        //getKey for Setup
                        dataKey = postSnapshot.getKey();
                    }
                }
                //Check Answer
                Log.e("Answer",edittextA.getText().toString());
                if (edittextA.getText().toString().equals(answerWrite)){
                    checkAnswer = "True";
                }
                //Add Answer
                if (checkDataStuAnswer.equals("True")){
                    //Set Value No_Question
                    AnswerSolve answerSolve = new AnswerSolve(answertext,answertext,checkAnswer,Question,NumberQuestion);
                    String questionKey = ref.push().getKey();
                    ref.child(dataKey).child("No_question").child(key).setValue(answerSolve);
                    if(checkAnswer.equals("True")) {
                        Toast.makeText(getContext(), "Correct", Toast.LENGTH_SHORT).show();
                        edittextA.setText(answertext);
                        edittextA.setEnabled(false);
                        setAfterAnswer();
                        setTextTrue();
                    }
                    else{
                        Toast.makeText(getContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                        edittextA.setText(answertext);
                        edittextA.setEnabled(false);
                        setAfterAnswer();
                        setTextFalse();
                    }
                } else {

                    Answer answer = new Answer(Username,assignname,subjectID,0, name);
                    String newKey = table_solve2.push().getKey();
                    String questionKey = ref.push().getKey();
                    ref.child(newKey).setValue(answer);
                    //Set Valuse No_Question
                    AnswerSolve answerSolve2 = new AnswerSolve(answertext,answertext,checkAnswer,Question,NumberQuestion);
                    ref.child(newKey).child("No_question").child(key).setValue(answerSolve2);
                    if(checkAnswer.equals("True")) {
                        Toast.makeText(getContext(), "Correct", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(), "Correct", Toast.LENGTH_SHORT).show();
                        edittextA.setText(answertext);
                        edittextA.setEnabled(false);
                        setAfterAnswer();
                        setTextTrue();
                    }
                    else{
                        Toast.makeText(getContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                        edittextA.setText(answertext);
                        edittextA.setEnabled(false);
                        setAfterAnswer();
                        setTextFalse();
                    }
                }
                CheckScore();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void CheckScore(){
        Log.e("CheckScore","Runing");
        final DatabaseReference ref = database.getReference("Student_answer").child(Username).child(subjectName).child(assignname);
        Query searchQuery = ref.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addChildEventListener(new ChildEventListener(){

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Answer answer = new Answer();
                answer = dataSnapshot.getValue(Answer.class);
                int countAnswer = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.child("No_question").getChildren()) {
                    //Log.e("Snapshot",postSnapshot.child("check").getValue().toString());
                    try {
                        if (postSnapshot.child("check").getValue().toString().equals("True")){
                            countAnswer++;
                        }
                    } catch (NullPointerException exception) {

                    }

                }
                ref.child(dataSnapshot.getKey()).child("score").setValue(countAnswer);
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

    public void setAfterAnswer(){
        btnSubmit.setEnabled(false);
        btnSubmit.setVisibility(View.GONE);
        btnReset.setEnabled(false);
        btnReset.setVisibility(View.GONE);
    }

    public void setTextTrue(){
        ResultAnswer.setText("Correct");
        ResultAnswer.setTextColor(Color.parseColor("#0eff25"));
        ResultAnswer.setVisibility(View.VISIBLE);
    }

    public void setTextFalse(){
        ResultAnswer.setText("Incorrect");
        ResultAnswer.setTextColor(Color.parseColor("#FF1818"));
        ResultAnswer.setVisibility(View.VISIBLE);
    }

    private void clickButton() {
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnReset) {
            Toast.makeText(getActivity(), "Reset this question", Toast.LENGTH_SHORT);
            edittextA.getText().clear();
        } else if (v == btnSubmit) {
            Toast.makeText(getActivity(), "Submit assignment", Toast.LENGTH_SHORT);
            submitAnswer();
        }
    }
}
