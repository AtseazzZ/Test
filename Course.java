package com.atsea.entity;


public class Course {

    private String cno;
    private String cname;
    private int credit;
    private String teacher;

    public Course(){}

    public Course(String cno,String cname,int credit,String teacher){

        this.cno=cno;
        this.cname=cname;
        this.credit=credit;
        this.teacher=teacher;
    }

    public String getCno(){ return cno; }
    public String getCname(){ return cname; }
    public int getCredit(){ return credit; }
    public String getTeacher(){ return teacher; }
}