package com.example.asus.project4th;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.asus.project4th.Model.Choice;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ViewpagerChoiceFragment extends Fragment {
    private int questionNumber;
    private String assignname, subjectID, subjectName;
    private String numberquest;
    private String key;
    private TextView txtNo,txtQuestion;
    private RadioButton radioA;
    private RadioButton radioB;
    private RadioButton radioC;
    private RadioButton radioD;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference table_quest = database.getReference("Assign");

    public static ViewpagerChoiceFragment newInstance(int questionNo, String assignname, String subjectID, String subjectName, String key) {
        ViewpagerChoiceFragment fragment = new ViewpagerChoiceFragment();
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

    public ViewpagerChoiceFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignname = getArguments().getString("assignname");
        subjectID = getArguments().getString("subjectID");
        subjectName = getArguments().getString("subjectName");
        questionNumber = getArguments().getInt("questionNo");
        key = getArguments().getString("key");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_choice, container, false);

        txtQuestion = view.findViewById(R.id.txtQuest);
        txtNo = view.findViewById(R.id.txtNo);
        radioA = view.findViewById(R.id.radioA);
        radioB = view.findViewById(R.id.radioB);
        radioC = view.findViewById(R.id.radioC);
        radioD = view.findViewById(R.id.radioD);

        numberquest = String.valueOf(questionNumber);
        txtNo.setText(numberquest);
        initFirebase();

        return view;
    }

    void initFirebase(){
        DatabaseReference ref = table_quest.getRef().child(subjectName).child(assignname).child("Quest").child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Choice choice = new Choice();
                choice = dataSnapshot.getValue(Choice.class);
                txtQuestion.setText(choice.getQuestion());
                radioA.setText(choice.getChoiceA());
                radioB.setText(choice.getChoiceB());
                radioC.setText(choice.getChoiceC());
                radioD.setText(choice.getChoiceD());
                if(choice.getAnswer().equals("A")){
                    radioA.setChecked(true);
                    radioB.setEnabled(false);
                    radioC.setEnabled(false);
                    radioD.setEnabled(false);
                } else if(choice.getAnswer().equals("B")){
                    radioB.setChecked(true);
                    radioA.setEnabled(false);
                    radioC.setEnabled(false);
                    radioD.setEnabled(false);
                } else if(choice.getAnswer().equals("C")){
                    radioC.setChecked(true);
                    radioA.setEnabled(false);
                    radioB.setEnabled(false);
                    radioD.setEnabled(false);
                } else if(choice.getAnswer().equals("D")){
                    radioD.setChecked(true);
                    radioA.setEnabled(false);
                    radioB.setEnabled(false);
                    radioC.setEnabled(false);
                }

                Log.e("Choice A",choice.getChoiceA());
                Log.e("Choice B",choice.getChoiceB());
                Log.e("Choice C",choice.getChoiceC());
                Log.e("Choice D",choice.getChoiceD());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
