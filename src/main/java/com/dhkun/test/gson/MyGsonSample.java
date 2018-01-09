package com.dhkun.test.gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MyGsonSample {

    @Test
    public void test1() {
        String json = "{\"id\":1, \"name\":\"dhkun\"}";

        Gson gson = new GsonBuilder().create();
        SampleA pojo = gson.fromJson(json, SampleA.class);
        System.out.println(pojo);
    }
    
    @Test
    public void test2() {
        Gson gson = new GsonBuilder().create();
        SampleB sampleB = new SampleB();
        sampleB.setI1(1);
        sampleB.setI2(1);
        sampleB.setL1(1L);
        sampleB.setL2(1L);
        sampleB.setD1(1.0);
        sampleB.setD2(1.0);
        sampleB.setF1(1.0f);
        sampleB.setF2(1.0f);
        sampleB.setC('1');
        sampleB.setStr("1");
        sampleB.setDate(new Date(1515202807657l));
        // 成员对象
        SampleC sampleC = new SampleC();
        sampleC.setStr("1");
        sampleB.setSampleC(sampleC);
        
        List<String> strList = new ArrayList<String>();
        strList.add("A");
        strList.add("B");
        sampleB.setStrList(strList);
        /** 设置子对象*/
        List<SampleC> objList = new ArrayList<SampleC>();
        SampleC sampleC0 = new SampleC();
        sampleC0.setStr("1_1");
        objList.add(sampleC0);
        SampleC sampleC1 = new SampleC();
        sampleC1.setStr("1_2");
        objList.add(sampleC1);
        sampleB.setObjList(objList);
        
        String json = gson.toJson(sampleB);
        System.out.println(json);
        String expectedJson = "{\"i1\":1,\"i2\":1,\"l1\":1,\"l2\":1,\"d1\":1.0,\"d2\":1.0,\"f1\":1.0,\"f2\":1.0,\"c\":\"1\",\"str\":\"1\",\"date\":1515202807657"
                + ",\"sampleC\":{\"i1\":0,\"l1\":0,\"d1\":0.0,\"f1\":0.0,\"c\":\"\\u0000\",\"str\":\"1\"}"
                + ",\"strList\":[\"A\",\"B\"],\"objList\":["
                + "{\"i1\":0,\"l1\":0,\"d1\":0.0,\"f1\":0.0,\"c\":\"\\u0000\",\"str\":\"1_1\"}"
                + ",{\"i1\":0,\"l1\":0,\"d1\":0.0,\"f1\":0.0,\"c\":\"\\u0000\",\"str\":\"1_2\"}"
                + "]}";
        Assert.assertEquals(expectedJson, json);
        
        SampleB targetSampleB = gson.fromJson(expectedJson, SampleB.class);
        //System.out.println(ePojo2);
    }
}
