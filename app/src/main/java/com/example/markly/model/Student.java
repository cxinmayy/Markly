package com.example.markly.model;

public class Student {
    int id;
    String name, regNo;
    int sectionId;
    boolean isPresent;

    public Student(int id, String name, String regNo, int sectionId) {
        this.id = id;
        this.name = name;
        this.regNo = regNo;
        this.sectionId = sectionId;
        this.isPresent = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRegNo() { return regNo; }
    public int getSectionId() { return sectionId; }

    public boolean isPresent() { return isPresent; }
    public void setPresent(boolean present) { isPresent = present; }
}