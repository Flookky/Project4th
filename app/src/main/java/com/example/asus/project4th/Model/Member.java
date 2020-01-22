package com.example.asus.project4th.Model;

public class Member {

    private String Name;
    private String Password;
    private String Status;
    private String Username;

    public Member() {
    }

    public Member(String name, String password, String status, String username) {
        Name = name;
        Password = password;
        Status = status;
        Username = username;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
