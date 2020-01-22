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

public class ViewpagerChoiceStuFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private int questionNumber;
    private String numberquest, assignname, subjectID, subjectName, Username, name;
    private TextView txtNo,txtQuestion,ResultAnswer;
    private Button btnSubmit,btnReset;
    private RadioButton radioA;
    private RadioButton radioB;
    private RadioButton radioC;
    private RadioButton radioD;
    private String getChoiceA;
    private String getChoiceB;
    private String getChoiceC;
    private String getChoiceD;
    private String sel = " ";
    private String key;
    private RadioGroup radioGroupChoice;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference table_quest = database.getReference("Assign");
    private DatabaseReference table_solve2 = database.getReference("Student_answer");

    public static ViewpagerChoiceStuFragment newInstance(int questionNo, String assignname, String subjectID, String subjectName, String Username, String name, String key) {
        ViewpagerChoiceStuFragment fragment = new ViewpagerChoiceStuFragment();
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

    public ViewpagerChoiceStuFragment() { }

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
        View view = inflater.inflate(R.layout.fragment_viewpager_choice_stu, container, false);

        txtQuestion = view.findViewById(R.id.txtQuest);
        txtNo = view.findViewById(R.id.txtNo);
        radioA = view.findViewById(R.id.radioA);
        radioB = view.findViewById(R.id.radioB);
        radioC = view.findViewById(R.id.radioC);
        radioD = view.findViewById(R.id.radioD);
        radioGroupChoice = view.findViewById(R.id.radioGroupChoice);
        ResultAnswer = view.findViewById(R.id.ResultAnswer);
        btnReset = view.findViewById(R.id.btnReset);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        numberquest = String.valueOf(questionNumber);
        txtNo.setText(numberquest);

        initFirebase();
        clickButton();
        onClickRadioButton();

        return view;
    }

    void initFirebase(){

        DatabaseReference ref = table_quest.getRef().child(subjectName).child(assignname).child("Quest").child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Choice2 choice = new Choice2();
                choice = dataSnapshot.getValue(Choice2.class);
                txtQuestion.setText(choice.getQuestion());
                radioA.setText(choice.getChoiceA());
                radioB.setText(choice.getChoiceB());
                radioC.setText(choice.getChoiceC());
                radioD.setText(choice.getChoiceD());

                getChoiceA = radioA.getText().toString();
                getChoiceB = radioB.getText().toString();
                getChoiceC = radioC.getText().toString();
                getChoiceD = radioD.getText().toString();

                checkDoquestion(getChoiceA,getChoiceB,getChoiceC,getChoiceD);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkDoquestion(final String getChoiceA,final String getChoiceB,final String getChoiceC,final String getChoiceD){
        DatabaseReference ref = database.getReference("Student_answer").child(Username).child(subjectName).child(assignname);
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
                            AnswerSolve answerSolve=postSnapshot.child("No_question").child(key).getValue(AnswerSolve.class);
                            String answerText=answerSolve.getAnswertext();
                            String answerCheck=answerSolve.getCheck();
                            //Toast.makeText(getContext(), answerText, Toast.LENGTH_SHORT).show();
                            if(answerText.equals(getChoiceA)){
                                radioA.setChecked(true);
                                radioB.setEnabled(false);
                                radioC.setEnabled(false);
                                radioD.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}
                            }
                            else if(answerText.equals(getChoiceB)){
                                radioB.setChecked(true);
                                radioA.setEnabled(false);
                                radioC.setEnabled(false);
                                radioD.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}
                            }
                            else if(answerText.equals(getChoiceC)){
                                radioC.setChecked(true);
                                radioA.setEnabled(false);
                                radioB.setEnabled(false);
                                radioD.setEnabled(false);
                                setAfterAnswer();
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}
                            }
                            else if(answerText.equals(getChoiceD)){
                                radioD.setChecked(true);
                                radioA.setEnabled(false);
                                radioB.setEnabled(false);
                                radioC.setEnabled(false);
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

                getChoiceA = choice.getChoiceA();
                getChoiceB = choice.getChoiceB();
                getChoiceC = choice.getChoiceC();
                getChoiceD = choice.getChoiceD();

                Log.e("ChoiceA LALA",getChoiceA);
                Log.e("ChoiceB LALA",getChoiceB);
                Log.e("ChoiceC LALA",getChoiceC);
                Log.e("ChoiceD LALA",getChoiceD);

                String answerChoice = choice.getAnswer();
                String Question = choice.getQuestion();
                String NumberQuestion = choice.getNumberQuestion();
                String choiceAns;

                if(sel.equals("A")){
                    choiceAns = getChoiceA;
                    Log.e("Select",choiceAns);
                    Log.e("Answer is",choice.getAnswer());
                    enterAnswer(Question,answerChoice,choiceAns,NumberQuestion);

                } else if(sel.equals("B")){
                    choiceAns = getChoiceB;
                    Log.e("Select",choiceAns);
                    Log.e("Answer is",choice.getAnswer());
                    enterAnswer(Question,answerChoice,choiceAns,NumberQuestion);

                } else if(sel.equals("C")){
                    choiceAns = getChoiceC;
                    Log.e("Select",choiceAns);
                    Log.e("Answer is",choice.getAnswer());
                    enterAnswer(Question,answerChoice,choiceAns,NumberQuestion);

                } else if(sel.equals("D")){
                    choiceAns = getChoiceD;
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
                if(sel.equals(answerChoice)){
                    checkAnswer = "True";
                }

                //Add Answer
                if (checkDataStuAnswer.equals("True")){
                    //Set Valuse No_Question
                    String questionKey = ref.push().getKey();
                    AnswerSolve answerSolve = new AnswerSolve(sel,txtChoice,checkAnswer,Question,NumberQuestion);
                    ref.child(dataKey).child("No_question").child(key).setValue(answerSolve);
                    if(checkAnswer.equals("True")) {
                        Toast.makeText(getContext(), "Correct", Toast.LENGTH_SHORT).show();
                        radioA.setEnabled(false);
                        radioB.setEnabled(false);
                        radioC.setEnabled(false);
                        radioD.setEnabled(false);
                        setAfterAnswer();
                        setTextTrue();
                    }
                    else{
                        Toast.makeText(getContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                        radioA.setEnabled(false);
                        radioB.setEnabled(false);
                        radioC.setEnabled(false);
                        radioD.setEnabled(false);
                        setAfterAnswer();
                        setTextFalse();
                    }

                }else {
                    //create answer Member
                    String newKey = ref.push().getKey();
                    String questionKey = ref.push().getKey();
                    Answer answer = new Answer(Username,assignname,subjectID,0, name);
                    //Set Valuse No_Question
                    ref.child(newKey).setValue(answer);
                    AnswerSolve answerSolve = new AnswerSolve(sel,txtChoice,checkAnswer,Question,NumberQuestion);
                    ref.child(newKey).child("No_question").child(key).setValue(answerSolve);
                    if(checkAnswer.equals("True")) {
                        Toast.makeText(getContext(), "Correct", Toast.LENGTH_SHORT).show();
                        radioA.setEnabled(false);
                        radioB.setEnabled(false);
                        radioC.setEnabled(false);
                        radioD.setEnabled(false);
                        setAfterAnswer();
                        setTextTrue();
                    }
                    else{
                        Toast.makeText(getContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                        radioA.setEnabled(false);
                        radioB.setEnabled(false);
                        radioC.setEnabled(false);
                        radioD.setEnabled(false);
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

    private void onClickRadioButton() {
        radioGroupChoice.setOnCheckedChangeListener(this);
    }

    private void clickButton() {
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
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
            case R.id.radioC:
                sel = "C";
                break;
            case R.id.radioD:
                sel = "D";
                break;
        }
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
