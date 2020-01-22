package com.example.asus.project4th;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.asus.project4th.Model.Choice;
import com.example.asus.project4th.Model.Write;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewpagerWriteFragment extends Fragment{
    private int questionNumber;
    private String assignname, subjectID, subjectName;
    private String numberquest;
    private String key;
    private TextView txtNo,txtQuestion;
    private EditText editTextA;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference table_quest = database.getReference("Assign");

    public static ViewpagerWriteFragment newInstance(int questionNo, String assignname, String subjectID, String subjectName, String key) {
        ViewpagerWriteFragment fragment = new ViewpagerWriteFragment();
        Bundle args = new Bundle();
        args.putInt("questionNo", questionNo);
        args.putString("assignname", assignname);
        args.putString("subjectID", subjectID);
        args.putString("subjectName", subjectName);
        args.putString("key",key);
        Log.d("SUBJECT_ID",subjectID);
        fragment.setArguments(args);

        return fragment;
    }

    public ViewpagerWriteFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignname = getArguments().getString("assignname");
        subjectID = getArguments().getString("subjectID");
        subjectName = getArguments().getString("subjectName");
        questionNumber = getArguments().getInt("questionNo");
        key = getArguments().getString("key");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_write, container, false);
        txtQuestion = view.findViewById(R.id.txtQuest);
        txtNo = view.findViewById(R.id.txtNo);
        editTextA = view.findViewById(R.id.edittextA);

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
                Write write = new Write();
                write = dataSnapshot.getValue(Write.class);
                txtQuestion.setText(write.getQuestion());
                editTextA.setText(write.getAnswer());
                editTextA.setEnabled(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}












