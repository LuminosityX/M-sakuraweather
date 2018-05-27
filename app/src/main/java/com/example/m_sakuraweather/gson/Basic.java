package com.example.m_sakuraweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lhx on 2018/4/24.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{

        @SerializedName("loc")
        public String updateTime;
    }
}
