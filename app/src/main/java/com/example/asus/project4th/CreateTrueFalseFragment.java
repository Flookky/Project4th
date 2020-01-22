package com.example.asus.project4th;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class CreateTrueFalseFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener  {

    private View view;
    private ImageButton btnAdd;
    private ImageButton btnReset;
    private Button btnSubmit;
    private RadioGroup radioGroupChoice;
    private RadioButton radioA;
    private RadioButton radioB;
    private EditText edittextQuest;
    private EditText edittextA;
    private EditText edittextB;
    private EditText edittextC;
    private EditText edittextD;
    private TextView txtquestion,txtNo,txtTrue,txtFalse;
    private String subjectID;
    private String assignname;
    private String subjectname;
    private FirebaseDatabase database;
    private DatabaseReference table_assign;
    private Integer tmp;
    private String sel = " ";
    private String answeris;
    private final String truestr = "True";
    private final String falsestr = "False";
    private ArrayList<String> listofType = new ArrayList<>();
    private ArrayList<String> listofQuestion = new ArrayList<>();
    private ArrayList<String> listofKey = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_assign_truefalse, container, false);
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

    private void initInstance() {
        //Init Firebase
        database = FirebaseDatabase.getInstance();
        table_assign = database.getReference("Assign");

        radioGroupChoice = view.findViewById(R.id.radioGroupChoice);
        txtquestion = view.findViewById(R.id.checkquestion);
        edittextQuest = view.findViewById(R.id.edittextQuest);
        txtTrue = view.findViewById(R.id.Truetxt);
        txtFalse = view.findViewById(R.id.Falsetxt);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnReset = view.findViewById(R.id.btnReset);
        btnSubmit = view.findViewById(R.id.btnSubmit);
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

    private void addQuestionAns() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please waiting . . .");
        progressDialog.show();
        final DatabaseReference ref = table_assign.getRef().child(subjectname);;

        Query searchQuery = ref.orderByChild("subjectID").equalTo(subjectID);
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
                                answeris=txtTrue.getText().toString();
                                Choice2 choice2 = new Choice2(assign.getTotalQuest(),edittextQuest.getText().toString(),
                                        truestr,falsestr,"TrueFalse",sel,answeris,"prepare");
                                ref.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);
                                //ref.child(postSnapshot.getKey()).child("Quest").child(assign.getTotalQuest()).setValue(choice2);

                            }
                            else if(sel=="B"){
                                String key = ref.push().getKey();
                                answeris=txtFalse.getText().toString();
                                Choice2 choice2 = new Choice2(assign.getTotalQuest(),edittextQuest.getText().toString(),
                                        truestr,falsestr,"TrueFalse",sel,answeris,"prepare");
                                ref.child(postSnapshot.getKey()).child("Quest").child(key).setValue(choice2);
                                //ref.child(postSnapshot.getKey()).child("Quest").child(assign.getTotalQuest()).setValue(choice2);

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

    private void AllQuestion() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please waiting . . .");
        progressDialog.show();

        final Query list_query = table_assign.child(subjectname).orderByChild("subjectID").equalTo(subjectID);
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

    private void clickButton() {
        btnAdd.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

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
    @Override
    public void onClick(View v) {
        if (v == btnAdd) {
            addQuestionAns();
        } else if (v == btnReset) {
            Toast.makeText(getActivity(), "Reset this question", Toast.LENGTH_SHORT);
            edittextQuest.getText().clear();
            radioGroupChoice.clearCheck();
        } else if (v == btnSubmit) {
            Toast.makeText(getActivity(), "List of Question", Toast.LENGTH_SHORT);
            AllQuestion();
        }
    }
}
