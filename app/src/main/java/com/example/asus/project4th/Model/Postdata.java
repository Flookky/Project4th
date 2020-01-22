package com.example.asus.project4th.Model;

public class Postdata {

    private String subjectName;
    private String subjectID;
    private String time;

    public Postdata() {
    }

    public Postdata(String subjectName, String subjectID, String time) {
        this.subjectName = subjectName;
        this.subjectID = subjectID;
        this.time = time;
    }
    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
