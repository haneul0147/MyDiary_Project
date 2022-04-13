package com.kkh.mydiary;

import java.text.SimpleDateFormat;

// 앱에서 사용되는 상수, 날짜 포맷...
public class AppConstants {
    public static final int REG_LOCATION_BY_ADDRESS = 101;
    public static final int REG_WEATHER_BY_GRID = 102;

    public static final int REG_PHOTO_CAPTURE = 103;
    public static final int REG_PHOTO_SELECTION = 104;

    public static final int CONTENT_PHOTO = 105;
    public static final int CONTENT_PHOTO_EX = 106;

    public static String FOLDER_PHOTO;

    public static final String KEY_URI_PHTO = "URI_PHOTO";

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH시");
    public static SimpleDateFormat dateFormat2 = new SimpleDateFormat("YYYYMMHHdd HH시");
    public static SimpleDateFormat dateFormat3 = new SimpleDateFormat("MM월 dd일");



}
