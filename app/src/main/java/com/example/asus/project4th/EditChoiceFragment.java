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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice;
import com.example.asus.project4th.Model.Choice2;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EditChoiceFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private View view;
    private ImageButton btnAdd;
    private ImageButton btnReset;
    private Button btnSubmit,setenable;
    private RadioGroup radioGroupChoice;
    private EditText edittextQuest;
    private EditText edittextA;
    private EditText edittextB;
    private EditText edittextC;
    private EditText edittextD;
    private FirebaseDatabase database;
    private DatabaseReference table_assign;
    private TextView txtNo,checkquestion,checktxtA,checktxtB,checktxtC,checktxtD;
    private String sel = " ";
    private String numberQuestion;
    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private String answerChoice;
    private String answeris;
    private String subjectID;
    private String subjectName;
    private String assignname;
    private String status;
    private String key;
    private String specialChar = "[a-zA-Z0-9_ ]*";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_assign, container, false);

        if (getArguments() != null) {
            numberQuestion = getArguments().getString("numberQuestion");
            question = getArguments().getString("question");
            choiceA = getArguments().getString("choiceA");
            choiceB = getArguments().getString("choiceB");
            choiceC = getArguments().getString("choiceC");
            choiceD = getArguments().getString("choiceD");
            key = getArguments().getString("key");
            subjectID = getArguments().getString("subjectID");
            subjectName = getArguments().getString("subjectName");
            assignname = getArguments().getString("assignname");
            answerChoice = getArguments().getString("answerChoice");
            answeris = getArguments().getString("answeris");
            status = getArguments().getString("status");

            //Toast.makeText(getContext(), Username, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Bundle == null", Toast.LENGTH_SHORT).show();
        }
        initInstance();
        onClickRadioButton();
        clickButton();
        checkText();

        return view;
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
                if(s.length()==0){
                    checkquestion.setText("Require Question");
                    checkquestion.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>50){
                    checkquestion.setText("Maximum question length is 50");
                    checkquestion.setTextColor(Color.parseColor("#FA5858"));
                } else {
                    checkquestion.setText("");
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
                final String txtA = s.toString();
                final String txtB = edittextB.getText().toString();
                final String txtC = edittextC.getText().toString();
                final String txtD = edittextD.getText().toString();
                if (s.length()==0){
                    checktxtA.setText("Require Answer A");
                    checktxtA.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>30){
                    checktxtA.setText("Maximum Answer A length is 30");
                    checktxtA.setTextColor(Color.parseColor("#FA5858"));
                } else if (txtA.equals(txtB)||txtA.equals(txtC)||txtA.equals(txtD)){
                    checktxtA.setText("Your choice cannot same other choice");
                    checktxtA.setTextColor(Color.parseColor("#FA5858"));
                } else {
                    checktxtA.setText("");
                }
            }
        });

        edittextB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String txtA = edittextA.getText().toString();
                final String txtB = s.toString();
                final String txtC = edittextC.getText().toString();
                final String txtD = edittextD.getText().toString();
                if (s.length()==0){
                    checktxtB.setText("Require Answer B");
                    checktxtB.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>30){
                    checktxtB.setText("Maximum Answer B length is 30");
                    checktxtB.setTextColor(Color.parseColor("#FA5858"));
                } else if (txtB.equals(txtA)||txtB.equals(txtC)||txtB.equals(txtD)){
                    checktxtB.setText("Your choice cannot same other choice");
                    checktxtB.setTextColor(Color.parseColor("#FA5858"));
                } else {
                    checktxtB.setText("");
                }
            }
        });

        edittextC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String txtA = edittextA.getText().toString();
                final String txtB = edittextB.getText().toString();
                final String txtC = s.toString();
                final String txtD = edittextD.getText().toString();
                if (s.length()==0){
                    checktxtC.setText("Require Answer C");
                    checktxtC.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>30){
                    checktxtC.setText("Maximum Answer C length is 30");
                    checktxtC.setTextColor(Color.parseColor("#FA5858"));
                } else if (txtC.equals(txtA)||txtC.equals(txtB)||txtC.equals(txtD)){
                    checktxtC.setText("Your choice cannot same other choice");
                    checktxtC.setTextColor(Color.parseColor("#FA5858"));
                } else {
                    checktxtC.setText("");
                }
            }
        });

        edittextD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String txtA = edittextA.getText().toString();
                final String txtB = edittextB.getText().toString();
                final String txtC = edittextC.getText().toString();
                final String txtD = s.toString();
                if (s.length()==0){
                    checktxtD.setText("Require Answer D");
                    checktxtD.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>30){
                    checktxtD.setText("Maximum Answer D length is 30");
                    checktxtD.setTextColor(Color.parseColor("#FA5858"));
                }
                /*
                else if (!txtD.matches(specialChar)){
                    checktxtD.setText("Special Characters cannot use");
                    checktxtD.setTextColor(Color.parseColor("#FA5858"));
                }*/
                else if (txtD.equals(txtA)||txtD.equals(txtB)||txtD.equals(txtC)){
                    checktxtD.setText("Your choice cannot same other choice");
                    checktxtD.setTextColor(Color.parseColor("#FA5858"));
                } else {
                    checktxtD.setText("");
                }
            }
        });
    }

    private void initInstance() {
        //Init Firebase
        database = FirebaseDatabase.getInstance();
        table_assign = database.getReference("Assign").child(subjectName);

        //----------- Text View -----------//
        txtNo = view.findViewById(R.id.txtNo);

        //---------- Edit Text -----------//
        edittextQuest = view.findViewById(R.id.edittextQuest);
        edittextA = view.findViewById(R.id.edittextA);
        edittextB = view.findViewById(R.id.edittextB);
        edittextC = view.findViewById(R.id.edittextC);
        edittextD = view.findViewById(R.id.edittextD);

        checkquestion = view.findViewById(R.id.checkquestion);
        checktxtA = view.findViewById(R.id.checktextA);
        checktxtB = view.findViewById(R.id.checktextB);
        checktxtC = view.findViewById(R.id.checktextC);
        checktxtD = view.findViewById(R.id.checktextD);

        //---------- Radio Button ----------//
        radioGroupChoice = view.findViewById(R.id.radioGroupChoice);
        /*radioA = view.findViewById(R.id.radioA);
        radioB = view.findViewById(R.id.radioA);
        radioC = view.findViewById(R.id.radioA);
        radioD = view.findViewById(R.id.radioA);*/

        //---------- Button -------------//
        btnReset = view.findViewById(R.id.btnReset);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        setenable = view.findViewById(R.id.setQuestion);

        //----------- Set Up -----------//
        edittextQuest.setText(question);
        txtNo.setText(numberQuestion);
        edittextA.setText(choiceA);
        edittextB.setText(choiceB);
        edittextC.setText(choiceC);
        edittextD.setText(choiceD);

        if(status.equals("prepare")){
            setenable.setText("Enable Question");
        } else if(status.equals("ready")){
            setenable.setText("Disable Question");
        }

        //---------- Choice -------------//
        sel = answerChoice;
        switch (answerChoice){
            case "A":
                radioGroupChoice.check(R.id.radioA);
                break;
            case "B":
                radioGroupChoice.check(R.id.radioB);
                break;
            case "C":
                radioGroupChoice.check(R.id.radioC);
                break;
            case "D":
                radioGroupChoice.check(R.id.radioD);
                break;
        }

    }

    private void setEnableQuestion() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please waiting . . .");
        progressDialog.show();
        Query enableQuery = table_assign.orderByChild("subjectID").equalTo(subjectID);
        enableQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Assign assign = new Assign();
                    assign = postSnapshot.getValue(Assign.class);
                    String status_text ="";

                    if (assign.getAssignname().equals(assignname)){
                        if(status.equals("prepare")){
                            //table_assign.child(postSnapshot.getKey()).child("Quest").child(numberQuestion).child("status").setValue("ready");
                            table_assign.child(postSnapshot.getKey()).child("Quest").child(key).child("status").setValue("ready");
                            status="ready";
                            status_text="Enabled";
                            setenable.setText("Disable Question");
                        } else if(status.equals("ready")){
                            //table_assign.child(postSnapshot.getKey()).child("Quest").child(numberQuestion).child("status").setValue("prepare");
                            table_assign.child(postSnapshot.getKey()).child("Quest").child(key).child("status").setValue("prepare");
                            status="prepare";
                            status_text="Disabled";
                            setenable.setText("Enable Question");
                        }

                        Toast.makeText(getActivity(), status_text+" Question", Toast.LENGTH_SHORT).show();
                    }
                }
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
                String a = edittextA.getText().toString();
                String b = edittextB.getText().toString();
                String c = edittextC.getText().toString();
                String d = edittextD.getText().toString();
                //Check if editText is empty
                if (edittextQuest.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please enter question", Toast.LENGTH_SHORT).show();
                } else if (edittextA.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please enter answer A", Toast.LENGTH_SHORT).show();
                } else if (edittextB.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please enter answer B", Toast.LENGTH_SHORT).show();
                } else if (edittextC.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please enter answer C", Toast.LENGTH_SHORT).show();
                } else if (edittextD.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please enter answer D", Toast.LENGTH_SHORT).show();
                } else if (edittextQuest.length()>50){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Maximum Question length is 50", Toast.LENGTH_SHORT).show();
                } else if (edittextA.length()>30){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Maximum Choice A length is 30", Toast.LENGTH_SHORT).show();
                } else if (edittextB.length()>30){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Maximum Choice B length is 30", Toast.LENGTH_SHORT).show();
                } else if (edittextC.length()>30){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Maximum Choice C length is 30", Toast.LENGTH_SHORT).show();
                } else if (edittextD.length()>30){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Maximum Choice D length is 30", Toast.LENGTH_SHORT).show();
                } /*
                else if (!a.matches(specialChar)||!b.matches(specialChar)||!c.matches(specialChar)||!d.matches(specialChar)){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Special Characters cannot use", Toast.LENGTH_SHORT).show();
                }*/
                else if (a.equals(b)||a.equals(c)||a.equals(d)||b.equals(c)||b.equals(d)||c.equals(d)){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Any choice cannot same", Toast.LENGTH_SHORT).show();
                }

                else if (radioGroupChoice.getCheckedRadioButtonId() == -1) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please select an answer", Toast.LENGTH_SHORT).show();
                } else {
                    //Add Question
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Assign assign = new Assign();
                        assign = postSnapshot.getValue(Assign.class);

                        if (assign.getAssignname().equals(assignname)){
                            for(DataSnapshot choiceSnapshot : postSnapshot.child("Quest").getChildren()){
                                Choice2 checkNumquest = new Choice2();
                                checkNumquest = choiceSnapshot.getValue(Choice2.class);
                                if(numberQuestion.equals(checkNumquest.getNumberQuestion())){
                                    String key = choiceSnapshot.getKey();
                                    if(sel=="A"){
                                        answeris=edittextA.getText().toString();
                                        Choice2 choice2 = new Choice2(numberQuestion, edittextQuest.getText().toString(),
                                                edittextA.getText().toString(), edittextB.getText().toString(), edittextC.getText().toString(), edittextD.getText().toString(),
                                                "choice", sel, answeris,"prepare");
                                        //table_assign.child(postSnapshot.getKey()).child("Quest").child(numberQuestion).setValue(choice2);
                                        table_assign.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);

                                    }
                                    else if(sel=="B"){
                                        answeris=edittextB.getText().toString();
                                        Choice2 choice2 = new Choice2(numberQuestion, edittextQuest.getText().toString(),
                                                edittextA.getText().toString(), edittextB.getText().toString(), edittextC.getText().toString(), edittextD.getText().toString(),
                                                "choice", sel, answeris,"prepare");
                                        //table_assign.child(postSnapshot.getKey()).child("Quest").child(numberQuestion).setValue(choice2);
                                        table_assign.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);

                                    }
                                    else if(sel=="C"){
                                        answeris=edittextC.getText().toString();
                                        Choice2 choice2 = new Choice2(numberQuestion, edittextQuest.getText().toString(),
                                                edittextA.getText().toString(), edittextB.getText().toString(), edittextC.getText().toString(), edittextD.getText().toString(),
                                                "choice", sel, answeris,"prepare");
                                        //table_assign.child(postSnapshot.getKey()).child("Quest").child(numberQuestion).setValue(choice2);
                                        table_assign.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);

                                    }
                                    else{
                                        answeris=edittextD.getText().toString();
                                        Choice2 choice2 = new Choice2(numberQuestion, edittextQuest.getText().toString(),
                                                edittextA.getText().toString(), edittextB.getText().toString(), edittextC.getText().toString(), edittextD.getText().toString(),
                                                "choice", sel, answeris,"prepare");
                                        //table_assign.child(postSnapshot.getKey()).child("Quest").child(numberQuestion).setValue(choice2);
                                        table_assign.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);

                                    }

                                }
                            }                             }
                    }
                    Toast.makeText(getActivity(), "Add question", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //------------------------------------------------------------------------------//


    private void clickButton() {
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        setenable.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v == btnReset) {
            Toast.makeText(getActivity(), "Reset this question", Toast.LENGTH_SHORT);
            edittextQuest.getText().clear();
            edittextA.getText().clear();
            edittextB.getText().clear();
            edittextC.getText().clear();
            edittextD.getText().clear();
            radioGroupChoice.clearCheck();
        } else if (v == btnSubmit) {
            submitQuestionAns();
            Toast.makeText(getActivity(), "Submit assignment", Toast.LENGTH_SHORT);
        } else if (v == setenable){
            setEnableQuestion();
        }
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
            case R.id.radioC:
                sel = "C";
                break;
            case R.id.radioD:
                sel = "D";
                break;
        }
    }
    //--------------------------------------------------//
}
