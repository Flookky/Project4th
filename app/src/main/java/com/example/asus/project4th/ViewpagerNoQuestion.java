package com.example.asus.project4th;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewpagerNoQuestion extends Fragment {

    public static ViewpagerNoQuestion newInstance() {
        ViewpagerNoQuestion fragment = new ViewpagerNoQuestion();
        return fragment;
    }

    public ViewpagerNoQuestion() { }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viewpager_no_question, container, false);
        return rootView;
    }
}
