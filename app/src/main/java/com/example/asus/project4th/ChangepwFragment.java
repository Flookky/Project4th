package com.example.asus.project4th;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangepwFragment extends Fragment implements View.OnClickListener {

    final String TAG = "TTwTT";

    private View view;
    private EditText editextPassword;
    private EditText editextNewPassword;
    private EditText editextConfirmPassword;
    private Button buttonChangepw;
    private String oldPassword;
    private String Username;
    private String Password;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_changepw, container, false);

        if (getArguments() != null) {
            Username = getArguments().getString("Username");
            Password = getArguments().getString("Password");

            //Toast.makeText(getContext(), Username+" "+Password, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), "Bundle == null", Toast.LENGTH_SHORT).show();
        }

        initInstance();
        return view;

    }

    private void initInstance() {
        editextPassword = view.findViewById(R.id.editextPassword);
        editextNewPassword = view.findViewById(R.id.editextNewPassword);
        editextConfirmPassword = view.findViewById(R.id.editextConfirmPassword);
        buttonChangepw = view.findViewById(R.id.buttonChangepw);
        buttonChangepw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonChangepw) {
            initFirebase();
        }
    }

    private void initFirebase() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please waiting . . .");
        progressDialog.show();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_Teacher = database.getReference("Member").child("Teacher");
        final DatabaseReference table_Student = database.getReference("Member").child("Student");

        table_Teacher.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (editextPassword.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                } else if (editextNewPassword.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please enter your new password", Toast.LENGTH_SHORT).show();
                } else if (editextConfirmPassword.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please confirm your password", Toast.LENGTH_SHORT).show();
                } else if(!editextConfirmPassword.getText().toString().equals(editextNewPassword.getText().toString())) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Your confirm password is incorrect", Toast.LENGTH_SHORT).show();
                }
                  else {
                    oldPassword = editextPassword.getText().toString();
                    if (oldPassword.equals(Password)) {
                        //Toast.makeText(getContext(), "Your Password is correct", Toast.LENGTH_SHORT).show();
                        table_Teacher.child(Username).child("password").setValue(editextNewPassword.getText().toString());
                        Toast.makeText(getContext(), "Change Password Complete", Toast.LENGTH_SHORT).show();
                        Intent chgpw = new Intent(getActivity(), Signin1Activity.class);
                        startActivity(chgpw);
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Your Password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        table_Student.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (editextPassword.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                } else if (editextNewPassword.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please enter your new password", Toast.LENGTH_SHORT).show();
                } else if (editextConfirmPassword.getText().toString().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please confirm your password", Toast.LENGTH_SHORT).show();
                } else if(!editextConfirmPassword.getText().toString().equals(editextNewPassword.getText().toString())) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Your confirm password is incorrect", Toast.LENGTH_SHORT).show();
                }
                else {
                    oldPassword = editextPassword.getText().toString();
                    if (oldPassword.equals(Password)) {
                        //Toast.makeText(getContext(), "Your Password is correct", Toast.LENGTH_SHORT).show();
                        table_Teacher.child(Username).child("password").setValue(editextNewPassword.getText().toString());
                        Toast.makeText(getContext(), "Change Password Complete", Toast.LENGTH_SHORT).show();
                        Intent chgpw = new Intent(getActivity(), Signin1Activity.class);
                        startActivity(chgpw);
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Your Password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}