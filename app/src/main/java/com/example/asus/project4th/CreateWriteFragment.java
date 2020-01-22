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
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice;
import com.example.asus.project4th.Model.Write;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreateWriteFragment extends Fragment implements View.OnClickListener {

    private View view;
    private String subjectID;
    private String assignname;
    private String subjectname;
    private FirebaseDatabase database;
    private DatabaseReference table_assign;
    private TextView txtNo,txtquestion,txtanswer;
    private EditText edittextQuest;
    private EditText edittextA;
    private ImageButton btnAdd;
    private ImageButton btnReset;
    private Button btnSubmit;
    private String specialChar = "[a-zA-Z0-9_ ]*";
    private ArrayList<String> listofType = new ArrayList<>();
    private ArrayList<String> listofQuestion = new ArrayList<>();
    private ArrayList<String> listofKey = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_assign_write, container, false);
        initInstance();
        clickButton();
        checkText();

        if (getArguments() != null) {
            subjectID = getArguments().getString("subjectID");
            assignname = getArguments().getString("assignname");
            subjectname = getArguments().getString("subjectName");
            //Toast.makeText(getContext(), Username, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Bundle == null", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void initInstance() {
        //Init Firebase
        database = FirebaseDatabase.getInstance();
        table_assign = database.getReference("Assign");

        //---------- Edit Text -----------//
        edittextQuest = view.findViewById(R.id.edittextQuest);
        edittextA = view.findViewById(R.id.edittextA);
        txtquestion = view.findViewById(R.id.txtquestion);
        txtanswer = view.findViewById(R.id.txtanswer);

        //---------- Button -------------//
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
                String question = edittextQuest.getText().toString();
                String answer = edittextA.getText().toString();
                //Check if editText is empty
                if (edittextQuest.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please enter question", Toast.LENGTH_SHORT).show();
                } else if (edittextA.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please enter answer", Toast.LENGTH_SHORT).show();
                } else if (question.length()>50){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Maximum length of Question is 50", Toast.LENGTH_SHORT).show();
                } else if (answer.length()>30){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Maximum length of Answer is 30", Toast.LENGTH_SHORT).show();
                }
                else {
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

                            String key = ref.push().getKey();
                            Write write = new Write(assign.getTotalQuest(), edittextQuest.getText().toString()
                                    , "write", edittextA.getText().toString(),"prepare");
                            ref.child(postSnapshot.getKey()).child("Quest").child(key).setValue(write);
                        }
                    }
                    Toast.makeText(getActivity(), "Add a question", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //-------------------------------------------------------------------------------//

    //--------------------------- Submit button -------------------------------------//
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
    //---------------------------------------------------------------------------------//


    private void clickButton() {
        btnAdd.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v == btnAdd) {
            addQuestionAns();
        } else if (v == btnReset) {
            Toast.makeText(getActivity(), "Reset this question", Toast.LENGTH_SHORT);
            edittextQuest.getText().clear();
            edittextA.getText().clear();
        } else if (v == btnSubmit) {
            Toast.makeText(getActivity(), "Submit assignment", Toast.LENGTH_SHORT);
            AllQuestion();
        }
    }

}
