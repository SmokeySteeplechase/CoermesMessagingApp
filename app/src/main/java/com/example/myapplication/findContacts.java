package com.example.myapplication;

public class findContacts {
    public String fullname, school, username;
    public findContacts(){

    }
    public findContacts(String fullname, String school, String username) {
        this.fullname = fullname;
        this.school = school;
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
