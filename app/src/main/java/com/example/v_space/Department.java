package com.example.v_space;

import java.io.Serializable;

public class Department implements Serializable {
    private String collegeCode;
    private String collegeName;
    private String departmentCode;
    private String departmentName;
    private String hodName;
    // Constructors, getters, and setters
    public Department(String collegeCode, String collegeName, String departmentCode, String departmentName, String hodName) {
        // Default constructor
        this.collegeCode = collegeCode;
        this.collegeName = collegeName;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.hodName = hodName;
    }
    public Department(){

    }
    public String getCollegeCode() {
        return collegeCode;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getHodName() {
        return hodName;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setHodName(String hodName) {
        this.hodName = hodName;
    }
}
