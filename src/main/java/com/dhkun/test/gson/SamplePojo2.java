package com.dhkun.test.gson;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.JsonAdapter;

/**
 *
 */
public class SamplePojo2 implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /** 整数*/
    private int i1;
    private Integer i2;
    /** 长整数*/
    private long l1;
    private Long l2;
    /** 双精度*/
    private double d1;
    private Double d2;
    /** 浮点数*/
    private float f1;
    private Float f2;
    /** 字符*/
    private char c;
    private String str;
    /** 日期*/
    @JsonAdapter(DateJsonAdapter.class)
    private Date date;
    /** 集合*/
    private List<String> strList;

    public int getI1() {
        return i1;
    }

    public void setI1(int i1) {
        this.i1 = i1;
    }

    public Integer getI2() {
        return i2;
    }

    public void setI2(Integer i2) {
        this.i2 = i2;
    }

    public long getL1() {
        return l1;
    }

    public void setL1(long l1) {
        this.l1 = l1;
    }

    public Long getL2() {
        return l2;
    }

    public void setL2(Long l2) {
        this.l2 = l2;
    }

    public double getD1() {
        return d1;
    }

    public void setD1(double d1) {
        this.d1 = d1;
    }

    public Double getD2() {
        return d2;
    }

    public void setD2(Double d2) {
        this.d2 = d2;
    }

    public float getF1() {
        return f1;
    }

    public void setF1(float f1) {
        this.f1 = f1;
    }

    public Float getF2() {
        return f2;
    }

    public void setF2(Float f2) {
        this.f2 = f2;
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getStrList() {
        return strList;
    }

    public void setStrList(List<String> strList) {
        this.strList = strList;
    }

    @Override
    public String toString() {
        return "SamplePojo2 [i1=" + i1 + ", i2=" + i2 + ", l1=" + l1 + ", l2="
                + l2 + ", d1=" + d1 + ", d2=" + d2 + ", f1=" + f1 + ", f2="
                + f2 + ", c=" + c + ", str=" + str + ", date=" + date
                + ", strList=" + strList + "]";
    }
    
    
}
