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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Answer;
import com.example.asus.project4th.Model.AnswerSolve;
import com.example.asus.project4th.Model.Choice;
import com.example.asus.project4th.Model.Choice2;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewpagerTrueFalseStuFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private int questionNumber;
    private String numberquest, assignname, subjectID, subjectName, Username, name;
    private TextView txtNo,txtQuestion,ResultAnswer;
    private Button btnSubmit,btnReset;
    private RadioButton radioA;
    private RadioButton radioB;
    private RadioGroup radioGroupChoice;
    private final String getChoiceA = "True";
    private final String getChoiceB = "False";
    private String getChoice1,getChoice2;
    private String sel = " ";
    private String key;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference table_quest = database.getReference("Assign");
    private DatabaseReference table_solve2 = database.getReference("Student_answer");

    public static ViewpagerTrueFalseStuFragment newInstance(int questionNo, String assignname, String subjectID, String subjectName, String Username, String name, String key) {
        ViewpagerTrueFalseStuFragment fragment = new ViewpagerTrueFalseStuFragment();
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

    public ViewpagerTrueFalseStuFragment() { }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_truefalse_stu, container, false);
        txtQuestion = view.findViewById(R.id.txtQuest);
        txtNo = view.findViewById(R.id.txtNo);
        radioA = view.findViewById(R.id.radioA);
        radioB = view.findViewById(R.id.radioB);
        btnReset = view.findViewById(R.id.btnReset);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        ResultAnswer = view.findViewById(R.id.ResultAnswer);
        radioGroupChoice = view.findViewById(R.id.radioGroupChoice);
        numberquest = String.valueOf(questionNumber);
        txtNo.setText(numberquest);
        initFirebase();
        clickButton();
        onClickRadioButton();

        return view;
    }

    void initFirebase() {
        DatabaseReference ref = table_quest.getRef().child(subjectName).child(assignname).child("Quest").child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Choice2 choice = new Choice2();
                choice = dataSnapshot.getValue(Choice2.class);
                txtQuestion.setText(choice.getQuestion());
                Log.e("Choice A", choice.getChoiceA());
                Log.e("Choice B", choice.getChoiceB());
                checkDoquestion(choice.getChoiceA(),choice.getChoiceB());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkDoquestion(final String choiceA, final String choiceB){
        DatabaseReference ref = database.getReference("Student_answer").child(Username).child(subjectName).child(assignname);
        //table_solve2.getRef().child(Username).child("No_question");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Answer answer = new Answer();
                //String NumberQuestion = String.valueOf(questionNumber);
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    answer = postSnapshot.getValue(Answer.class);
                    if(assignname.equals(answer.getAssignname())
                            && Username.equals(answer.getUsername())
                            && subjectID.equals(answer.getSubjectID()))
                    {
                        if(postSnapshot.child("No_question").child(key).exists()){
                            AnswerSolve answerSolve = postSnapshot.child("No_question").child(key).getValue(AnswerSolve.class);
                            String answerText = answerSolve.getAnswertext();
                            String answerCheck = answerSolve.getCheck();
                            //Toast.makeText(getContext(), answerText, Toast.LENGTH_SHORT).show();
                            if(answerText.equals(choiceA)){
                                radioA.setChecked(true);
                                radioB.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}
                            }
                            else if(answerText.equals(choiceB)){
                                radioB.setChecked(true);
                                radioA.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}
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

    public void submitAnswer(){
        DatabaseReference ref = table_quest.getRef().child(subjectName).child(assignname).child("Quest").child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Choice choice = new Choice();
                choice = dataSnapshot.getValue(Choice.class);
                txtQuestion.setText(choice.getQuestion());

                getChoice1 = choice.getChoiceA();
                getChoice2 = choice.getChoiceB();

                Log.e("ChoiceA LALA",getChoice1);
                Log.e("ChoiceB LALA",getChoice2);

                String answerChoice = choice.getAnswer();
                String Question = choice.getQuestion();
                String NumberQuestion = choice.getNumberQuestion();
                String choiceAns;

                if(sel.equals("A")){
                    choiceAns = getChoice1;
                    Log.e("Select",choiceAns);
                    Log.e("Answer is",choice.getAnswer());
                    enterAnswer(Question,answerChoice,choiceAns,NumberQuestion);

                } else if(sel.equals("B")){
                    choiceAns = getChoice2;
                    Log.e("Select",choiceAns);
                    Log.e("Answer is",choice.getAnswer());
                    enterAnswer(Question,answerChoice,choiceAns,NumberQuestion);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void enterAnswer(final String Question, final String answerChoice, final String txtChoice, final String NumberQuestion){

        final DatabaseReference ref = database.getReference("Student_answer").child(Username).child(subjectName).child(assignname);

        Query searchQuery = ref.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.e("data", dataSnapshot.toString());
                String checkDataStuAnswer = "False";
                String dataKey = "";
                String checkAnswer = "False";

                //Setup Data Answer
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Log.e("data", postSnapshot.toString());
                    Answer answer = new Answer();
                    answer = postSnapshot.getValue(Answer.class);

                    if (answer.getAssignname().equals(assignname) && answer.getUsername().equals(Username)) {
                        //Check data in Student_answer
                        checkDataStuAnswer = "True";
                        //getKey for Setup
                        dataKey = postSnapshot.getKey();
                    }
                }

                if (sel.equals(answerChoice)){
                    checkAnswer = "True";
                }
                //Add Answer
                if (checkDataStuAnswer.equals("True")){
                    AnswerSolve answerSolve = new AnswerSolve(sel,txtChoice,checkAnswer,Question,NumberQuestion);
                    //table_solve3.child(Username).child("No_question").child(numberQuestion).setValue(answerSolve);
                    //String questionKey = ref.push().getKey();
                    ref.child(dataKey).child("No_question").child(key).setValue(answerSolve);
                    if(checkAnswer.equals("True")) {
                        Toast.makeText(getContext(), "Correct", Toast.LENGTH_SHORT).show();
                        radioA.setEnabled(false);
                        radioB.setEnabled(false);
                        setAfterAnswer();
                        setTextTrue();
                    }
                    else{
                        Toast.makeText(getContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                        radioA.setEnabled(false);
                        radioB.setEnabled(false);
                        setAfterAnswer();
                        setTextFalse();
                    }

                } else {
                    String newKey = ref.push().getKey();
                    //String questionKey = ref.push().getKey();
                    Answer answer = new Answer(Username,assignname,subjectID,0, name);
                    //Set Valuse No_Question
                    ref.child(newKey).setValue(answer);
                    AnswerSolve answerSolve = new AnswerSolve(sel,txtChoice,checkAnswer,Question,NumberQuestion);
                    //ref.child(assignname).child(newKey).child("No_question").child(NumberQuestion).setValue(answerSolve);
                    ref.child(newKey).child("No_question").child(key).setValue(answerSolve);
                    if(checkAnswer.equals("True")) {
                        Toast.makeText(getContext(), "Correct", Toast.LENGTH_SHORT).show();
                        radioA.setEnabled(false);
                        radioB.setEnabled(false);
                        setAfterAnswer();
                        setTextTrue();
                    }
                    else{
                        Toast.makeText(getContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                        radioA.setEnabled(false);
                        radioB.setEnabled(false);
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
        //Query searchQuery = table_solve2.orderByChild("subjectID").equalTo(subjectID);
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

    //------------- Radio Button: Choice -----------------//
    private void onClickRadioButton() {
        radioGroupChoice.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.radioA:
                sel = "A";
                break;
            case R.id.radioB:
                sel = "B";
                break;
        }
    }

    private void clickButton() {
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnReset) {
            Toast.makeText(getActivity(), "Reset this question", Toast.LENGTH_SHORT);
            radioGroupChoice.clearCheck();
        } else if (v == btnSubmit) {
            Toast.makeText(getActivity(), "Submit assignment", Toast.LENGTH_SHORT);
            submitAnswer();
        }
    }

}
