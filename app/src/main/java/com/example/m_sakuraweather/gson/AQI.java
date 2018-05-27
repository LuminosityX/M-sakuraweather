package com.example.m_sakuraweather.gson;

/**
 * Created by lhx on 2018/4/24.
 */

public class AQI {
    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
