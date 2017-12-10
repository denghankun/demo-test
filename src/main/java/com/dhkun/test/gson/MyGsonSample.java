package com.dhkun.test.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MyGsonSample {

    public static void main(String[] args) {
        String json = "{\"id\":1, \"name\":\"dhkun\"}";

        Gson gson = new GsonBuilder().create();
        SamplePojo pojo = gson.fromJson(json, SamplePojo.class);
        System.out.println(pojo);
    }
}
