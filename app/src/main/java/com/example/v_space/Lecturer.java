package com.example.v_space;

public class Lecturer {
    private String L_Id;  // Use private access for better encapsulation
    private String mail;
    private String pw1;
    private String mobile;
    private String dcode;
    private String college_code;
    // Default constructor required for Firebase
    public Lecturer() {
    }

    // Parameterized constructor
    public Lecturer(String L_Id,String college_code, String mail, String pw1, String mobile, String dcode) {
        this.L_Id = L_Id;
        this.mail = mail;
        this.pw1 = pw1;
        this.mobile = mobile;
        this.dcode = dcode;
        this.college_code = college_code;
    }

    // Getter and Setter methods
    public String getL_Id() {
        return L_Id;
    }

    public String getCollege_code() {return college_code;}

    public void setL_Id(String L_Id) {
        this.L_Id = L_Id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPw1() {
        return pw1;
    }

    public void setPw1(String pw1) {
        this.pw1 = pw1;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public void setCollege_code(String college_code){
        this.college_code=college_code;
    }
    public String getDcode() {
        return dcode;
    }

    public void setDcode(String dcode) {
        this.dcode = dcode;
    }
}
