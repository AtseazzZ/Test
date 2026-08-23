package com.atsea.entity;

public class Score {

    private String sno;
    private String cno;
    private double grade;

    public Score(){}

    public Score(String sno,String cno,double grade){

        this.sno=sno;
        this.cno=cno;
        this.grade=grade;
    }

    public String getSno(){ return sno; }
    public String getCno(){ return cno; }
    public double getGrade(){ return grade; }
}