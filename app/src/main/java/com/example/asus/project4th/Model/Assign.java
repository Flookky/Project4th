package com.example.asus.project4th.Model;

public class Assign {

    private String assignname;
    private String subjectID;
    private String location_status;
    private String assign_status;
    private String time;
    private String totalQuest;

    public Assign() {
    }

    public Assign(String assignname, String subjectID, String time) {
        this.assignname = assignname;
        this.subjectID = subjectID;
        this.time = time;
        this.totalQuest = "0";
    }

    public Assign(String assignname, String subjectID, String location_status, String assign_status, String time) {
        this.assignname = assignname;
        this.subjectID = subjectID;
        this.location_status = location_status;
        this.assign_status = assign_status;
        this.time = time;
        this.totalQuest = "0";
    }

    public String getAssignname() {
        return assignname;
    }

    public void setAssignname(String assignname) {
        this.assignname = assignname;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getLocation_status() {
        return location_status;
    }

    public void setLocation_status(String location_status) { this.location_status = location_status; }

    public String getAssign_status() {
        return assign_status;
    }

    public void setAssign_status(String assign_status) { this.assign_status = assign_status; }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalQuest(){
        return  totalQuest;
    }

    public void setTotalQuest(String totalQuest) {
        this.totalQuest = totalQuest;
    }
}
