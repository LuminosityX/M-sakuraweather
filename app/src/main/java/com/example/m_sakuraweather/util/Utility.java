package com.example.m_sakuraweather.util;

import android.text.TextUtils;

import com.example.m_sakuraweather.db.City;
import com.example.m_sakuraweather.db.County;
import com.example.m_sakuraweather.db.Province;
import com.example.m_sakuraweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lhx on 2018/4/22.
 */

public class Utility {

    public static boolean handleProvinceResponse(String respose){
        if(!TextUtils.isEmpty(respose)){
            try{
                JSONArray allProvinces = new JSONArray(respose);
                for(int i=0;i<allProvinces.length();++i){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province =new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCitys=new JSONArray(response);
                for(int i=0;i<allCitys.length();++i){
                    JSONObject cityObject =allCitys.getJSONObject(i);
                    City city =new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setPeovinceId(provinceId);
                    city.save();
                }
                return true;
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountryRespense(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCountrys =new JSONArray(response);
                for(int i=0;i<allCountrys.length();++i){
                    JSONObject countryObject = allCountrys.getJSONObject(i);
                    County country =new County();
                    country.setCityId(cityId);
                    country.setCountyName(countryObject.getString("name"));
                    country.setWeatherId(countryObject.getString("weather_id"));
                    country.save();
                }
                return true;
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject= new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
