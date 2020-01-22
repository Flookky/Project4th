package com.example.asus.project4th;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

class MyPageAdapter extends FragmentStatePagerAdapter {
    private int Item;
    private String assignname,subjectID,subjectName;
    private ArrayList<String> listofType = new ArrayList<>();
    private ArrayList<String> listofKey = new ArrayList<>();

    public MyPageAdapter(FragmentManager fm, final String subjectID, final String subjectName, final String assignname, final int totalquest, ArrayList<String> listofType, ArrayList<String> listofKey) {
        super(fm);
        //Item must +1 up because fist fragment is empty
        this.Item = totalquest+1;
        Log.d("subjectID", subjectID);
        Log.d("subjectName", subjectName);
        Log.d("assignment", assignname);

        this.subjectID = subjectID;
        this.subjectName = subjectName;
        this.assignname = assignname;
        this.listofType = listofType;
        this.listofKey = listofKey;

    }

    public int getCount() {
        return Item;
    }

    public Fragment getItem(int position) {
        for(int i = 0; i < Item; i++){
            if(i==0){
                if(position == i){
                    return ViewpagerNoQuestion.newInstance();
                }
            } else{
                if(position == i){
                    if(listofType.get(i-1).equals("choice")){
                        return ViewpagerChoiceFragment.newInstance(i, assignname, subjectID, subjectName, listofKey.get(i-1));
                    } else if(listofType.get(i-1).equals("TrueFalse")){
                        return ViewpagerTrueFalseFragment.newInstance(i, assignname, subjectID, subjectName, listofKey.get(i-1));
                    } else if(listofType.get(i-1).equals("write")){
                        return ViewpagerWriteFragment.newInstance(i, assignname, subjectID, subjectName, listofKey.get(i-1));
                    }
                }
            }
        }
        return null;
    }
}