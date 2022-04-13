package com.kkh.mydiary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kkh.mydiary.data.WeatherItem;
import com.kkh.mydiary.data.WeatherResult;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnTabItemSelectedListener, OnRequestListener, MyApplication.OnResponseListener{
    // activity_main.xml 의 FrameLayout
    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;

    // activity_main.xml 의 라이브러리를 이용한 위젯
    BottomNavigationView bottomNavigationView;

    // 위치
    Location currentLocation;
    GPSListener gpsListener;     // 아래 쪽에 리스너 구현

    int locationCount = 0 ;

    // 날씨
    String currentWeather;
    String currentAddress;
    String currentDateString;
    Date currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();

        // 기본 플래그먼트 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

        // 메인화면 하단의 탭들을 구성하는 네비게이션
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 하단의 탭을 선택했을 때, 이벤트를 처리
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tab1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

                        return true;
                    case R.id.tab2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();

                        return true;
                    case R.id.tab3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit();

                        return true;
                }
                return false;
            }
        });

        /* 권한 확인 및 요청 코드 */
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        // 권한 여부
        checkPermission(permissions);

        // 사진 경로 설정
        setPicturePath();

    } // onCreate() END



    /* 권한 확인 메서드 */
    public void checkPermission(String[] permission){
        // 재요청 권한 메서드
        ArrayList<String> targetList = new ArrayList<String>();

        // 권한 확인
        for (int i=0;i<permission.length;i++){
            String curPermission = permission[i];
            int permissionCheck = ContextCompat.checkSelfPermission(this,curPermission);
            if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,curPermission+"권한있음",Toast.LENGTH_SHORT).show();
            }else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,curPermission)){
                    Toast.makeText(this,curPermission+"권환 설명 필요",Toast.LENGTH_SHORT).show();
                }else {
                    targetList.add(curPermission);   // 재요청 권한 추가
                }
            }
        }

        // 재요청
        String[] targets = new String[targetList.size()];
        targetList.toArray(targets);
        ActivityCompat.requestPermissions(this,targets,101);
    }// checkPermsiiton() END

    /* 권한 재요청 메서드*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 101:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){

                }else {

                }
                return;
        }
    } // onRequsetPermissionResult() END


    public void onTabSelected(int position){
        if(position == 0){
            bottomNavigationView.setSelectedItemId(R.id.tab1);
        }else if(position == 1){
            bottomNavigationView.setSelectedItemId(R.id.tab2);
        }else if(position == 2){
            bottomNavigationView.setSelectedItemId(R.id.tab3);
        }
    } // onTabSelected() END


    @Override
    public void onRequest(String command) {
        if (command != null){
            if(command.equals("getCurrentLocation")){
                getCurrentLocation();
            }
        }
    } // onRequest() END
    /* 현재 위치 정보 추출 메서드*/
    public void getCurrentLocation(){
        // 현재 시간
        currentDate = new Date();
        currentDateString = AppConstants.dateFormat3.format(currentDate);
        if(fragment2 != null){
            fragment2.setDataString(currentDateString);
        }

        // 위치 정보 서비스 요청
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        try{
            currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLocation != null){
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();

                getCurrentWeather();
                getCurrentAddress();
            }

            gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDistance,gpsListener);

        }catch (SecurityException e){
            Log.e("MainAcitivty :  ", e.getMessage());
        }
    }   // getCurrentLocation() END

    /* 위정보 요청 중지 메서드 */
    public void stopLocationService(){
        LocationManager manager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            manager.removeUpdates(gpsListener);

        }catch (SecurityException e){
            Log.e("MainAcitivty : ", e.getMessage());
        }
    }

    /* 현재 주소 추출 메서드 */
    public void getCurrentAddress(){
        // 구글 : 좌표 => 주소, 주소 => 좌표
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        // 강서구 ~~~, 명동
        List<Address> address= null;

        try{
            address = geocoder.getFromLocation(currentLocation.getLatitude(),
                               currentLocation.getLongitude(),1);

        }catch (Exception e){
            // ~~~
        }

        if(address != null && address.size()>0 ){
            currentAddress = null;

            Address address1 = address.get(0);
            if(address1.getLocality() != null){
                currentAddress = address1.getLocality();
            }
            if(address1.getSubLocality() != null){
                if(currentAddress != null){
                    currentAddress = currentAddress + " " + address1.getSubLocality();
                }else {
                    currentAddress = address1.getSubLocality();
                }
            }

            String adminArea = address1.getAdminArea();
            String country = address1.getCountryName();

            if(fragment2 != null){
                fragment2.setAddress(currentAddress);
            }
        }
    } // getCurrentAddress() END

    /* 현재 날씨 추출 메서드 */
    public void getCurrentWeather(){
        Map<String,Double> girdMap = GridUtil.getGrid(currentLocation.getLatitude(),currentLocation.getLongitude());

        double girdX = girdMap.get("x");
        double gridY = girdMap.get("y");

        sendLocalWeatherReq(girdX,gridY);
    }

    /* 지역날씨 요청 보내기 메서드 */
    public void sendLocalWeatherReq(double gridX, double gridY){
        String url = "https:/www.kma.go.kr/wid/queryDFS.jsp?";
        url = url+"?gridx=" + Math.round(gridX);
        url = url+"?gridy=" + Math.round(gridY);

        Map<String, String> params = new HashMap<String, String>();

        MyApplication.send(AppConstants.REG_WEATHER_BY_GRID, Request.Method.GET,url,params,this);
    }

    @Override
    public void processResponse(int requestCode, int responseCode, String response) {
        if (responseCode == 200){     // 200 응답이 성공했을 경우, 해당 서버가 전달 해주는 값....
            if(requestCode == AppConstants.REG_WEATHER_BY_GRID){
                // XML 문서형태의 데이터
                XmlParserCreator parserCreator = new XmlParserCreator() {
                    @Override
                    public XmlPullParser createParser() {
                        try{
                            return XmlPullParserFactory.newInstance().newPullParser();
                        }catch (Exception e){
                            // ~~
                            return null;
                        }
                    }
                };

                // XML => GSON
                GsonXml gsonXml = new GsonXmlBuilder().setXmlParserCreator(parserCreator)
                                                    .setSameNameLists(true).create();
                WeatherResult weather = gsonXml.fromXml(response, WeatherResult.class);

                try {
                    // 현재 기준 시간
                    Date tmDate = AppConstants.dateFormat.parse(weather.header.tm);
                    String tmDateText = AppConstants.dateFormat2.format(tmDate);

                    // <data seq=0>~~</data>들
                    for (int i=0; i<weather.body.datas.size(); i++){
                        WeatherItem item = weather.body.datas.get(i);
                        // 시간 : item.hour
                        // 날짜 : item.day
                        // 날씨 : item.wfKor
                        // 기온 : item.temp + " C"
                        // 강수확률 : item.pop. + "%"
                        // 풍속 : (int)Math.round(item.ws * 10) + " m/s"


                    }

                    WeatherItem item = weather.body.datas.get(0);
                    currentWeather = item.wfKor;
                    if(fragment2 != null){
                        fragment2.setWeather(item.wfKor);
                    }

                    // 위치정보 중지
                    if (locationCount > 1){
                        stopLocationService();
                    }

                }catch (Exception e){
                    // ~~~
                }

            }else {
                Log.d("알수 없는 requestCode : ", requestCode + "");
            }
        }else {
            Log.e("Response CODE : ", responseCode+"");
        }
    }

    /* GPS 리스너 선언 */
    class GPSListener implements LocationListener{
        @Override
        public void onLocationChanged(@NonNull Location location) {
            currentLocation = location;

            locationCount++;

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String message = "현재 좌표 : " + latitude + " : " + longitude;
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

            getCurrentWeather();
            getCurrentAddress();
       }
        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    /* 사진 관련 메서드 */
    public void setPicturePath(){
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        AppConstants.FOLDER_PHOTO = sdcardPath + File.separator + "photo";
        // File.separator / ~~ /
    }
}
