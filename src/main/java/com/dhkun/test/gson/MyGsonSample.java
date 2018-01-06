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
        SamplePojo pojo = gson.fromJson(json, SamplePojo.class);
        System.out.println(pojo);
    }
    
    @Test
    public void test2() {
        Gson gson = new GsonBuilder().create();
        SamplePojo2 pojo2 = new SamplePojo2();
        pojo2.setI1(1);
        pojo2.setI2(1);
        pojo2.setL1(1L);
        pojo2.setL2(1L);
        pojo2.setD1(1.0);
        pojo2.setD2(1.0);
        pojo2.setF1(1.0f);
        pojo2.setF2(1.0f);
        pojo2.setC('1');
        pojo2.setStr("1");
        pojo2.setDate(new Date(1515202807657l));
        List<String> strList = new ArrayList<String>();
        strList.add("A");
        strList.add("B");
        pojo2.setStrList(strList);
        String json = gson.toJson(pojo2);
        System.out.println(json);
        String expectedJson = "{\"i1\":1,\"i2\":1,\"l1\":1,\"l2\":1,\"d1\":1.0,\"d2\":1.0,\"f1\":1.0,\"f2\":1.0,\"c\":\"1\",\"str\":\"1\",\"date\":1515202807657,\"strList\":[\"A\",\"B\"]}";
        Assert.assertEquals(expectedJson, json);
        
        SamplePojo2 ePojo2 = gson.fromJson(expectedJson, SamplePojo2.class);
        System.out.println(ePojo2);
    }
}
