package com.example.m_sakuraweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m_sakuraweather.db.City;
import com.example.m_sakuraweather.db.County;
import com.example.m_sakuraweather.db.Province;
import com.example.m_sakuraweather.util.HttpUtil;
import com.example.m_sakuraweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by lhx on 2018/4/22.
 */

public class ChooseAreaFragment extends Fragment {
    private static final int Level_Province=0;
    private static final int Level_City=1;
    private static final int Level_Country=2;

    private Province selectedProvince;
    private City selectedCity;
    private County selectedCountry;


    private TextView titletext;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countryList;

    private int currentLevel;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,Bundle savedInstanceState){
        View view =inflater.inflate(R.layout.choose_area,parent,false);
        titletext = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                if(currentLevel == Level_Province){
                    selectedProvince = provinceList.get(position);
                    queryCity();
                }
                if(currentLevel == Level_City){
                    selectedCity = cityList.get(position);
                    queryCountry();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(currentLevel == Level_City){
                    queryProvince();
                }
                if(currentLevel == Level_Country){
                    queryCity();
                }
            }
        });

        queryProvince();
    }

    private void queryProvince(){
        titletext.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=Level_Province;
        }
        else{
            String address ="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    private void queryCity(){
        titletext.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class); //id 在哪
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=Level_City;
        }
        else{
            String address ="http://guolin.tech/api/china/"+selectedProvince.getProvinceCode();
            queryFromServer(address,"city");
        }
    }

    private void queryCountry(){
        titletext.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityId=?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countryList.size()>0){
            dataList.clear();
            for(County country:countryList){
                dataList.add(country.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=Level_Country;
        }
        else {
            String address="http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
            queryFromServer(address,"country");
        }
    }

    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address,new Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException{
                String responseText = response.body().string();
                boolean result=true;
                if("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);
                }
                else if("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }
                else if("country".equals(type)){
                    result=Utility.handleCountryRespense(responseText,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }
                            else if("city".equals(type)){
                                queryCity();
                            }
                            else if("country".equals(type)){
                                queryCountry();
                            }
                        }
                    });

                }
                else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"hehe"+type,Toast.LENGTH_SHORT).show();
                            closeProgressDialog();
                        }
                    });

                }
            }
            @Override
            public void onFailure(Call call ,IOException e){
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }



}
