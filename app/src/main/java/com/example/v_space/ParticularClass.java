package com.example.v_space;

import java.io.Serializable;

public class ParticularClass implements Serializable {
    private String classCode;
    private String degree;
    private String yearOfStudy;
    private String section;
    private String departmentCode;

    // Default constructor required for calls to DataSnapshot.getValue(Class.class)
    public ParticularClass() {
    }

    public ParticularClass(String classCode, String degree, String yearOfStudy, String section, String departmentCode) {
        this.classCode = classCode;
        this.degree = degree;
        this.yearOfStudy = yearOfStudy;
        this.section = section;
        this.departmentCode = departmentCode;
    }

    // Public getters
    public String getClassCode() {
        return classCode;
    }

    public String getDegree() {
        return degree;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public String getSection() {
        return section;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    // Public setters
    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }
}
