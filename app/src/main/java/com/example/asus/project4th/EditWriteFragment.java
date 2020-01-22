package com.example.asus.project4th;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice2;
import com.example.asus.project4th.Model.Write;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EditWriteFragment extends Fragment implements View.OnClickListener {

    private View view;
    private String subjectID;
    private String assignname;
    private String subjectName;
    private FirebaseDatabase database;
    private DatabaseReference table_assign;
    private TextView txtNo,txtquestion,txtanswer;
    private EditText edittextQuest;
    private EditText edittextA;
    private ImageButton btnReset;
    private Button btnSubmit,setenable;
    private String numberQuestion;
    private String question;
    private String answerWrite;
    private String status;
    private String key;
    private String specialChar = "[a-zA-Z0-9_ ]*";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_assign_write, container, false);
        if (getArguments() != null) {
            numberQuestion = getArguments().getString("numberQuestion");
            question = getArguments().getString("question");
            subjectID = getArguments().getString("subjectID");
            subjectName = getArguments().getString("subjectName");
            assignname = getArguments().getString("assignname");
            answerWrite = getArguments().getString("answerWrite");
            status = getArguments().getString("status");
            key = getArguments().getString("key");

            //Log.e("numberQusetion",numberQusetion);
            //Toast.makeText(getContext(), Username, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Bundle == null", Toast.LENGTH_SHORT).show();
        }

        //Toast.makeText(getActivity(), assignname, Toast.LENGTH_SHORT).show();

        initInstance();
        clickButton();
        checkText();
        return view;
    }
    private void initInstance() {
        //Init Firebase
        database = FirebaseDatabase.getInstance();
        table_assign = database.getReference("Assign");

        //----------- Text View -----------//
        txtNo = view.findViewById(R.id.txtNo);
        txtquestion = view.findViewById(R.id.checkquestion);
        txtanswer = view.findViewById(R.id.checkanswer);

        //---------- Edit Text -----------//
        edittextQuest = view.findViewById(R.id.edittextQuest);
        edittextA = view.findViewById(R.id.edittextA);

        //---------- Button -------------//

        btnReset = view.findViewById(R.id.btnReset);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        setenable = view.findViewById(R.id.setQuestion);

        if(status.equals("prepare")){
            setenable.setText("Enable Question");
        } else{
            setenable.setText("Disable Question");
        }

        //----------- Set Up -----------//
        edittextQuest.setText(question);
        txtNo.setText(numberQuestion);
        edittextA.setText(answerWrite);

    }

    private void checkText(){
        edittextQuest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String question = edittextQuest.getText().toString();
                if (s.length()==0){
                    txtquestion.setText("Require Question");
                    txtquestion.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>50){
                    txtquestion.setText("Maximum Question length is 50");
                    txtquestion.setTextColor(Color.parseColor("#FA5858"));
                }
                /*
                else if (!question.matches(specialChar)){

                    txtquestion.setText("Special Characters cannot use");
                    txtquestion.setTextColor(Color.parseColor("#FA5858"));
                }*/
                else {
                    txtquestion.setText("");
                }
            }
        });

        edittextA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String answer = edittextA.getText().toString();
                if (s.length()==0){
                    txtanswer.setText("Require Answer");
                    txtanswer.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>30){
                    txtanswer.setText("Maximum Answer length is 30");
                    txtanswer.setTextColor(Color.parseColor("#FA5858"));
                }
                /*
                else if (!answer.matches(specialChar)){
                    txtanswer.setText("Special Characters cannot use");
                    txtanswer.setTextColor(Color.parseColor("#FA5858"));
                }*/
                else {
                    txtanswer.setText("");
                }
            }
        });
    }

    private void setEnableQuestion() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please waiting . . .");
        progressDialog.show();
        Query enableQuery = table_assign.orderByChild("subjectID").equalTo(subjectID);
        enableQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status_text = "";
                if(status.equals("prepare")){
                    table_assign.child(subjectName).child(assignname).child("Quest").child(key).child("status").setValue("ready");
                    status="ready";
                    status_text = "Enabled";
                    setenable.setText("Disable Question");
                } else if(status.equals("ready")){
                    table_assign.child(subjectName).child(assignname).child("Quest").child(key).child("status").setValue("prepare");
                    status="prepare";
                    status_text = "Disabled";
                    setenable.setText("Enable Question");
                }

                Toast.makeText(getActivity(), status_text+" Question", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                    }
                }, 2000);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //--------------------------- Submit button -------------------------------------//
    private void submitQuestionAns() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please waiting . . .");
        progressDialog.show();

        Query searchQuery = table_assign.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Check if editText is empty
                String questionnow = edittextQuest.getText().toString();
                String answer = edittextA.getText().toString();
                if (edittextQuest.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please enter question", Toast.LENGTH_SHORT).show();
                } else if (edittextA.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please enter answer", Toast.LENGTH_SHORT).show();
                } else if (questionnow.length()>50){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Maximum Question length is 50", Toast.LENGTH_SHORT).show();
                } else if (answer.length()>30){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Maximum Answer length is 30", Toast.LENGTH_SHORT).show();
                } else {
                    //Add Question
                    Write write = new Write(numberQuestion, questionnow, "write", answer,"prepare");
                    table_assign.child(subjectName).child(assignname).child("Quest").child(key).setValue(write);
                    Log.e("Key now",key);
                    Toast.makeText(getActivity(), "Add question", Toast.LENGTH_SHORT).show();
                    getActivity().finish();

                    //Write write = new Write(numberQuestion, questionnow, "write", answer);
                    //table_assign.child(subjectName).child(assignname).child("Quest").child(numberQuestion).setValue(write);

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    //---------------------------------------------------------------------------------//
    private void clickButton() {
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        setenable.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v == btnReset) {
            edittextQuest.getText().clear();
            edittextA.getText().clear();
            Toast.makeText(getActivity(), "Reset this question", Toast.LENGTH_SHORT);
        } else if (v == btnSubmit) {
            submitQuestionAns();
            Toast.makeText(getActivity(), "Submit assignment", Toast.LENGTH_SHORT);
        } else if (v == setenable){
            setEnableQuestion();
        }
    }
}
