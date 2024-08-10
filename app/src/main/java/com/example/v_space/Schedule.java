package com.example.v_space;

import java.util.List;

public class Schedule {
    private List<String> day;
    private List<String> hour;
    private List<List<String>> subject_code;
    private List<List<String>> subject_name;
    private List<List<String>> lecturer_name;
    private List<List<String>> room_number;
    private String department_code;
    private String class_code;

    // Default constructor required for calls to DataSnapshot.getValue(Schedule.class)
    public Schedule() {
    }

    public Schedule(List<String> day, List<String> hour, List<List<String>> subject_code,
                    List<List<String>> subject_name, List<List<String>> lecturer_name,
                    List<List<String>> room_number, String department_code, String class_code) {
        this.day = day;
        this.hour = hour;
        this.subject_code = subject_code;
        this.subject_name = subject_name;
        this.lecturer_name = lecturer_name;
        this.room_number = room_number;
        this.department_code = department_code;
        this.class_code = class_code;
    }

    // Public getters
    public List<String> getDay() {
        return day;
    }

    public List<String> getHour() {
        return hour;
    }

    public List<List<String>> getSubject_code() {
        return subject_code;
    }

    public List<List<String>> getSubject_name() {
        return subject_name;
    }

    public List<List<String>> getLecturer_name() {
        return lecturer_name;
    }

    public List<List<String>> getRoom_number() {
        return room_number;
    }

    public String getDepartment_code() {
        return department_code;
    }

    public String getClass_code() {
        return class_code;
    }

    // Public setters
    public void setDay(List<String> day) {
        this.day = day;
    }

    public void setHour(List<String> hour) {
        this.hour = hour;
    }

    public void setSubject_code(List<List<String>> subject_code) {
        this.subject_code = subject_code;
    }

    public void setSubject_name(List<List<String>> subject_name) {
        this.subject_name = subject_name;
    }

    public void setLecturer_name(List<List<String>> lecturer_name) {
        this.lecturer_name = lecturer_name;
    }

    public void setRoom_number(List<List<String>> room_number) {
        this.room_number = room_number;
    }

    public void setDepartment_code(String department_code) {
        this.department_code = department_code;
    }

    public void setClass_code(String class_code) {
        this.class_code = class_code;
    }
}
