package com.example.asus.project4th;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
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
import android.widget.RadioGroup;
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

public class EditTrueFalseFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{

    private View view;
    private ImageButton btnAdd;
    private ImageButton btnReset;
    private Button btnSubmit,setenable;
    private RadioGroup radioGroupChoice;
    private EditText edittextQuest;
    private FirebaseDatabase database;
    private DatabaseReference table_assign;
    private TextView txtquestion,txtNo,txtTrue,txtFalse;
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
    private final String truestr = "True";
    private final String falsestr = "False";
    private String specialChar = "[a-zA-Z0-9_ ]*";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_assign_truefalse, container, false);

        if (getArguments() != null) {
            numberQuestion = getArguments().getString("numberQuestion");
            question = getArguments().getString("question");
            choiceA = getArguments().getString("choiceA");
            choiceB = getArguments().getString("choiceB");
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

        Log.d("TAG check",numberQuestion);
        initInstance();
        onClickRadioButton();
        clickButton();
        checkText();

        return view;
    }

    private void initInstance() {
        //Init Firebase
        database = FirebaseDatabase.getInstance();
        table_assign = database.getReference("Assign").child(subjectName);

        //----------- Text View -----------//
        txtquestion = view.findViewById(R.id.txtquestion);
        txtNo = view.findViewById(R.id.txtNo);
        txtTrue = view.findViewById(R.id.Truetxt);
        txtFalse = view.findViewById(R.id.Falsetxt);

        //---------- Edit Text -----------//
        edittextQuest = view.findViewById(R.id.edittextQuest);

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
        }

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
                    txtquestion.setText("Require Question");
                    txtquestion.setTextColor(Color.parseColor("#FA5858"));
                } else if (s.length()>50){
                    txtquestion.setText("Maximum Question length is 50");
                    txtquestion.setTextColor(Color.parseColor("#FA5858"));
                } else {
                    txtquestion.setText("");
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
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Assign assign = new Assign();
                    assign = postSnapshot.getValue(Assign.class);
                    String status_text ="";

                    if (assign.getAssignname().equals(assignname)){
                        if(status.equals("prepare")){
                            table_assign.child(postSnapshot.getKey()).child("Quest").child(key).child("status").setValue("ready");
                            status="ready";
                            status_text ="Enabled";
                            setenable.setText("Disable Question");
                        } else if(status.equals("ready")){
                            table_assign.child(postSnapshot.getKey()).child("Quest").child(key).child("status").setValue("prepare");
                            status="prepare";
                            status_text ="Disabled";
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

    private void submitQuestionAns() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please waiting . . .");
        progressDialog.show();
        Query searchQuery = table_assign.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Check if editText is empty
                if (edittextQuest.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please enter question", Toast.LENGTH_SHORT).show();
                } else if (edittextQuest.getText().toString().length()>50){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Maximum question length is 50", Toast.LENGTH_SHORT).show();
                } else if (radioGroupChoice.getCheckedRadioButtonId() == -1) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please select an answer", Toast.LENGTH_SHORT).show();
                } else {
                    //Add Question
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Assign assign = new Assign();
                        assign = postSnapshot.getValue(Assign.class);

                        if (assign.getAssignname().equals(assignname)){
                            for(DataSnapshot choiceSnapshot : postSnapshot.child("Quest").getChildren()) {
                                Choice2 checkNumquest = new Choice2();
                                checkNumquest = choiceSnapshot.getValue(Choice2.class);
                                if(numberQuestion.equals(checkNumquest.getNumberQuestion())){
                                    String key = choiceSnapshot.getKey();
                                    if(sel=="A"){
                                        answeris=txtTrue.getText().toString();
                                        Choice2 choice2 = new Choice2(numberQuestion, edittextQuest.getText().toString(),
                                                truestr, falsestr, "TrueFalse", sel, answeris,"prepare");
                                        table_assign.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);
                                        //table_assign.child(postSnapshot.getKey()).child("Quest").child(numberQuestion).setValue(choice2);
                                        Log.d("Number",assign.getTotalQuest());
                                    }
                                    else if(sel=="B"){
                                        answeris=txtFalse.getText().toString();
                                        Choice2 choice2 = new Choice2(numberQuestion, edittextQuest.getText().toString(),
                                                truestr, falsestr,"TrueFalse", sel, answeris,"prepare");
                                        table_assign.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);
                                        //table_assign.child(postSnapshot.getKey()).child("Quest").child(numberQuestion).setValue(choice2);
                                        Log.d("Number",assign.getTotalQuest());
                                    }
                                }
                            }
                        }
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
        }
    }

}
