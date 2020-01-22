package com.example.asus.project4th;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

class MyPageAdapterStu extends FragmentStatePagerAdapter {
    private int Item;
    private String assignname,subjectID,subjectName,Username,name;
    private ArrayList<String> listofType = new ArrayList<>();
    private ArrayList<String> listofQuestion = new ArrayList<>();
    private ArrayList<String> listofKey = new ArrayList<>();

    public MyPageAdapterStu(FragmentManager fm, final String subjectID, final String subjectName, final String assignname, final int totalquest
            , ArrayList<String> listofType, ArrayList<String> listofQuestion, String Username, String name, ArrayList<String> listofKey) {
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
        this.listofQuestion = listofQuestion;
        this.Username = Username;
        this.name = name;
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
                        int x = Integer.parseInt(listofQuestion.get(i-1));
                        return ViewpagerChoiceStuFragment.newInstance(x, assignname, subjectID, subjectName, Username, name, listofKey.get(i-1));
                    } else if(listofType.get(i-1).equals("TrueFalse")){
                        int x = Integer.parseInt(listofQuestion.get(i-1));
                        return ViewpagerTrueFalseStuFragment.newInstance(x, assignname, subjectID, subjectName, Username, name, listofKey.get(i-1));
                    } else if(listofType.get(i-1).equals("write")){
                        int x = Integer.parseInt(listofQuestion.get(i-1));
                        return ViewpagerWriteStuFragment.newInstance(x, assignname, subjectID, subjectName, Username, name , listofKey.get(i-1));
                    }
                }
            }
        }
        return null;
    }
}