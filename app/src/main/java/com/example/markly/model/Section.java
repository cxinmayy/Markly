package com.example.markly.model;

public class Section {
    int id;
    String department, section, subject;

    public Section(int id, String department, String section, String subject) {
        this.id = id;
        this.department = department;
        this.section = section;
        this.subject = subject;
    }

    public int getId() { return id; }
    public String getDepartment() { return department; }
    public String getSection() { return section; }
    public String getSubject() { return subject; }
}