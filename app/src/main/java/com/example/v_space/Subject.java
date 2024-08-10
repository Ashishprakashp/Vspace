package com.example.v_space;

import java.io.Serializable;

public class Subject implements Serializable {
    private String subjectName;
    private String subjectCode;
    private String lecturerName;

    public Subject() {

    }
    public Subject(String subjectName, String subjectCode, String lecturerName){
        this.subjectName=subjectName;
        this.subjectCode=subjectCode;
        this.lecturerName=lecturerName;
    }
    public String getSubjectName(){
        return subjectName;
    }
    public String getSubjectCode(){
        return subjectCode;
    }
    public String getLecturerName(){
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
