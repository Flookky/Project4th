package com.example.asus.project4th;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice;
import com.example.asus.project4th.Model.Choice2;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;


public class CreateChoiceFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private View view;
    private ImageButton btnAdd;
    private ImageButton btnReset;
    private Button btnSubmit;
    private RadioGroup radioGroupChoice;
    private RadioButton radioA;
    private RadioButton radioB;
    private RadioButton radioC;
    private RadioButton radioD;
    private EditText edittextQuest;
    private EditText edittextA;
    private EditText edittextB;
    private EditText edittextC;
    private EditText edittextD;
    private TextView checkquestion,checktxtA,checktxtB,checktxtC,checktxtD;
    private TextView txtNo;
    private String subjectID;
    private String assignname;
    private String subjectname;
    private FirebaseDatabase database;
    private DatabaseReference table_assign,table_quest;
    private Integer tmp;
    private String sel = " ";
    private String specialChar = "[a-zA-Z0-9_ ]*";
    private String answeris;
    private ArrayList<String> listofType = new ArrayList<>();
    private ArrayList<String> listofQuestion = new ArrayList<>();
    private ArrayList<String> listofKey = new ArrayList<>();
    private static final char[] chars ="0123456789".toCharArray();
    private static StringBuilder questionID;
    private int length = 8;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_assign, container, false);
        initInstance();
        onClickRadioButton();
        clickButton();
        checkText();
        //inputQuestionAns();

        if (getArguments() != null) {
            subjectID = getArguments().getString("subjectID");
            assignname = getArguments().getString("assignname");
            subjectname = getArguments().getString("subjectName");
        } else {
            Toast.makeText(getContext(), "Bundle == null", Toast.LENGTH_SHORT).show();
        }

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
                    checktxtA.setText("Maximum Choice A length is 30");
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
                    checktxtB.setText("Maximum Choice B length is 30");
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
                    checktxtC.setText("Maximum Choice C length is 30");
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
                    checktxtD.setText("Maximum Choice D length is 30");
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
        table_assign = database.getReference("Assign");
        table_quest = database.getReference("Assign");
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
        btnAdd = view.findViewById(R.id.btnAdd);
        btnReset = view.findViewById(R.id.btnReset);
        btnSubmit = view.findViewById(R.id.btnSubmit);
    }


    //-------------------------------- Add button ------------------------------------//
    private void addQuestionAns() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please waiting . . .");
        progressDialog.show();
        final DatabaseReference ref = table_assign.getRef().child(subjectname);;

        Query searchQuery = ref.orderByChild("subjectID").equalTo(subjectID);
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
                } else if (a.equals(b)||a.equals(c)||a.equals(d)||b.equals(c)||b.equals(d)||c.equals(d)){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Any choice cannot same", Toast.LENGTH_SHORT).show();
                } else if (radioGroupChoice.getCheckedRadioButtonId() == -1) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please select an answer", Toast.LENGTH_SHORT).show();
                } else {
                    //Add Question
                    //randomQuestionID();
                    //Toast.makeText(getContext(), questionID.toString(), Toast.LENGTH_SHORT).show();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Assign assign = new Assign();
                        assign = postSnapshot.getValue(Assign.class);

                        if (assign.getAssignname().equals(assignname)){
                            //Count Question
                            String totalQuest;
                            if (Integer.parseInt(assign.getTotalQuest()) >= 0){
                                totalQuest = assign.getTotalQuest();
                                //Log.e( "QuestCount",totalQuest);
                                totalQuest = String.valueOf(1 + Integer.parseInt(totalQuest));
                                assign.setTotalQuest(totalQuest);
                                ref.child(postSnapshot.getKey()).child("totalQuest").setValue(totalQuest);
                            }else {
                                assign.setTotalQuest("1");
                                ref.child(postSnapshot.getKey()).child("totalQuest").setValue("1");
                            }

                            if(sel=="A"){
                                String key = ref.push().getKey();
                                answeris=edittextA.getText().toString();
                                Choice2 choice2 = new Choice2(assign.getTotalQuest(), edittextQuest.getText().toString(),
                                        edittextA.getText().toString(), edittextB.getText().toString(), edittextC.getText().toString(), edittextD.getText().toString(),
                                        "choice", sel, answeris,"prepare");
                                //ref.child(postSnapshot.getKey()).child("Quest").child(assign.getTotalQuest()).setValue(choice2);
                                ref.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);

                            }
                            else if(sel=="B"){
                                String key = ref.push().getKey();
                                answeris=edittextB.getText().toString();
                                Choice2 choice2 = new Choice2(assign.getTotalQuest(), edittextQuest.getText().toString(),
                                        edittextA.getText().toString(), edittextB.getText().toString(), edittextC.getText().toString(), edittextD.getText().toString(),
                                        "choice", sel, answeris,"prepare");
                                //ref.child(postSnapshot.getKey()).child("Quest").child(assign.getTotalQuest()).setValue(choice2);
                                ref.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);

                            }
                            else if(sel=="C"){
                                String key = ref.push().getKey();
                                answeris=edittextC.getText().toString();
                                Choice2 choice2 = new Choice2(assign.getTotalQuest(), edittextQuest.getText().toString(),
                                        edittextA.getText().toString(), edittextB.getText().toString(), edittextC.getText().toString(), edittextD.getText().toString(),
                                        "choice", sel, answeris,"prepare");
                                //ref.child(postSnapshot.getKey()).child("Quest").child(assign.getTotalQuest()).setValue(choice2);
                                ref.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);

                            }
                            else{
                                String key = ref.push().getKey();
                                answeris=edittextD.getText().toString();
                                Choice2 choice2 = new Choice2(assign.getTotalQuest(), edittextQuest.getText().toString(),
                                        edittextA.getText().toString(), edittextB.getText().toString(), edittextC.getText().toString(), edittextD.getText().toString(),
                                        "choice", sel, answeris,"prepare");
                                //ref.child(postSnapshot.getKey()).child("Quest").child(assign.getTotalQuest()).setValue(choice2);
                                ref.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);

                            }

                        }
                    }
                    Toast.makeText(getActivity(), "Add question", Toast.LENGTH_SHORT).show();

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void randomQuestionID(){
        questionID = null;
        Random random = new Random(); // perhaps make it a class variable so you don't make a new one every time
        for(int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            questionID.append(c);
        }
    }

    private void AllQuestion() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please waiting . . .");
        progressDialog.show();

        final Query list_query = table_quest.child(subjectname).orderByChild("subjectID").equalTo(subjectID);
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
                    Log.e("Key all",listofKey.toString());
                    if(listofQuestion.size() == 0 || listofType.size() == 0){
                        Toast.makeText(getContext(), "You haven't any question yet", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else{
                        Intent allQuestion = new Intent(getContext(),ViewAllQuestionActivity.class);
                        allQuestion.putExtra("totalQuest",assign.getTotalQuest());
                        allQuestion.putExtra("listofQuestion",listofQuestion);
                        allQuestion.putExtra("listofType",listofType);
                        allQuestion.putExtra("listofKey",listofKey);
                        startActivity(allQuestion);
                        progressDialog.dismiss();
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


    private void clickButton() {
        btnAdd.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v == btnAdd) {
            addQuestionAns();
            //Toast.makeText(getActivity(), "Add a question", Toast.LENGTH_SHORT);
            //numberQuestion();
        } else if (v == btnReset) {
            Toast.makeText(getActivity(), "Reset this question", Toast.LENGTH_SHORT);
            edittextQuest.getText().clear();
            edittextA.getText().clear();
            edittextB.getText().clear();
            edittextC.getText().clear();
            edittextD.getText().clear();
            radioGroupChoice.clearCheck();
        } else if (v == btnSubmit) {
            Toast.makeText(getActivity(), "Submit assignment", Toast.LENGTH_SHORT);
            AllQuestion();
        }
    }

}
