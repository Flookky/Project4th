package com.example.asus.project4th;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.asus.project4th.Model.Answer;
import com.example.asus.project4th.Model.AnswerSolve;
import com.example.asus.project4th.Model.Choice;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewpagerTrueFalseCheckFragment extends Fragment {
    private int questionNumber;
    private String assignname, subjectID, subjectName, Username, StudentName;
    private String numberquest;
    private String key;
    private TextView txtNo,txtQuestion,ResultAnswer;
    private RadioButton radioA;
    private RadioButton radioB;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference table_quest = database.getReference("Assign");

    public static ViewpagerTrueFalseCheckFragment newInstance(int questionNo, String assignname, String subjectID, String subjectName, String key
            , String Username, String StudentName) {
        ViewpagerTrueFalseCheckFragment fragment = new ViewpagerTrueFalseCheckFragment();
        Bundle args = new Bundle();
        args.putInt("questionNo", questionNo);
        args.putString("assignname", assignname);
        args.putString("subjectID", subjectID);
        args.putString("subjectName", subjectName);
        args.putString("key",key);
        args.putString("Username", Username);
        args.putString("StudentName",StudentName);
        Log.d("SUBJECT_ID",subjectID);
        fragment.setArguments(args);

        return fragment;
    }

    public ViewpagerTrueFalseCheckFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignname = getArguments().getString("assignname");
        subjectID = getArguments().getString("subjectID");
        subjectName = getArguments().getString("subjectName");
        questionNumber = getArguments().getInt("questionNo");
        key = getArguments().getString("key");
        Username = getArguments().getString("Username");
        StudentName = getArguments().getString("StudentName");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_truefalse_check, container, false);
        txtQuestion = view.findViewById(R.id.txtQuest);
        txtNo = view.findViewById(R.id.txtNo);
        radioA = view.findViewById(R.id.radioA);
        radioB = view.findViewById(R.id.radioB);
        ResultAnswer = view.findViewById(R.id.ResultAnswer);
        numberquest = String.valueOf(questionNumber);
        txtNo.setText(numberquest);
        initFirebase();

        return view;
    }

    void initFirebase() {
        DatabaseReference ref = table_quest.getRef().child(subjectName).child(assignname).child("Quest").child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Choice choice = new Choice();
                choice = dataSnapshot.getValue(Choice.class);
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
        DatabaseReference ref = database.getReference("Student_answer").child(StudentName).child(subjectName).child(assignname);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Answer answer = new Answer();
                //String NumberQuestion = String.valueOf(questionNumber);
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    answer = postSnapshot.getValue(Answer.class);
                    if(assignname.equals(answer.getAssignname())
                            && StudentName.equals(answer.getUsername())
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
                                if(answerCheck.equals("True")){setTextTrue(); }
                                else{setTextFalse();}
                            }
                            else if(answerText.equals(choiceB)){
                                radioB.setChecked(true);
                                radioA.setEnabled(false);
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

}
