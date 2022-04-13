package com.kkh.mydiary.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


// <data seq = "0"> ~~ <data> .. <data seq="14">~~</data> 데이터들을 저장
public class WeatherBody {

    // <data ~~> 를 검색
    @SerializedName("data")
    public ArrayList<WeatherItem> datas;
}
