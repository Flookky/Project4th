package com.example.asus.project4th;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

public class MyPagerAdapterCheck extends FragmentStatePagerAdapter {
    private int Item;
    private String assignname,subjectID,subjectName,Username,StudentName;
    private ArrayList<String> listofType = new ArrayList<>();
    private ArrayList<String> listofKey = new ArrayList<>();

    public MyPagerAdapterCheck(FragmentManager fm, final String subjectID, final String subjectName, final String assignname, final int totalquest, ArrayList<String> listofType, ArrayList<String> listofKey
    ,String Username, String StudentName) {
        super(fm);
        //Item must +1 up because fist fragment is empty
        this.Item = totalquest+1;
        Log.d("subjectID", subjectID);
        Log.d("subjectName", subjectName);
        Log.d("assignment", assignname);

        this.subjectID = subjectID;
        this.subjectName = subjectName;
        this.assignname = assignname;
        this.Username = Username;
        this.StudentName = StudentName;
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
                        return ViewpagerChoiceCheckFragment.newInstance(i, assignname, subjectID, subjectName, listofKey.get(i-1), Username, StudentName);
                    } else if(listofType.get(i-1).equals("TrueFalse")){
                        return ViewpagerTrueFalseCheckFragment.newInstance(i, assignname, subjectID, subjectName, listofKey.get(i-1), Username, StudentName);
                    } else if(listofType.get(i-1).equals("write")){
                        return ViewpagerWriteCheckFragment.newInstance(i, assignname, subjectID, subjectName, listofKey.get(i-1), Username, StudentName);
                    }
                }
            }
        }
        return null;
    }

}
