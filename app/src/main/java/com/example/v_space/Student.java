package com.example.v_space;

public class Student {
    String rollno,mail,pw1,mobile;
    Student(String rollno,String mail,String pw1,String mobile){
        this.rollno=rollno;
        this.mail=mail;
        this.mobile=mobile;
        this.pw1=pw1;

    }
    public Student(){

    }
    public String getRollno(){
        return rollno;
    }
    public String getMail(){
        return mail;
    }
    public String getPw1() {
        return pw1;
    }

    public String getMobile() {
        return mobile;
    }
    public void setRollno(String L_Id){
        this.rollno=rollno;
    }
    public void setMail(String mail){
        this.mail=mail;
    }
    public void setPw1(String pw1) {
        this.pw1 = pw1;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
