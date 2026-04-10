package com.example.markly.model;

public class Attendance {
    int studentId;
    int sectionId;
    String date;
    int status;

    public Attendance(int studentId, int sectionId, String date, int status) {
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.date = date;
        this.status = status;
    }
}