package com.example.m_sakuraweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lhx on 2018/4/24.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;
    }
}
