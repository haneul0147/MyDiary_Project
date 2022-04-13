package com.kkh.mydiary;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

// 응답 처리를 위한 RequestQueue 를 사용하기 위한 클래스 ... : volley 라이브러리를 활용
public class MyApplication extends Application {

    public static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext(),new HurlStack() {

                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    // 기상청 요청 URL
                    HttpURLConnection connection = super.createConnection(url);
                    connection.setInstanceFollowRedirects(false); // 중복 금지

                    return super.createConnection(url);
                }
            });
        }

    } // onCreate


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static interface OnResponseListener{
        public void processResponse(int requestCode, int responseCode, String response);
    }

    // 실제 요청하는 메서드
    public static void send(final int requestCode, final int requestMethod, final String url,
                            final Map<String, String> params, final OnResponseListener listener){
            // https://www.kma.go.kr/wid/queryDFS.jsp?gridx=61&gridy=126 하나의 객체로 만들어 응답(response 에 대한 처리)
            // 웹 주소줄을 통해 문자열로 요청하기 때문에 : StringRequest 객체 생성
        StringRequest request = new StringRequest(
                requestMethod,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (listener != null){
                            listener.processResponse(requestCode,200,response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null){
                            listener.processResponse(requestCode,400,error.getMessage());
                        }
                    }
                }


        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };  // StringRequest 객체 생성 END

        request.setShouldCache(false);
        // 보안 관련 메서드
        request.setRetryPolicy(new DefaultRetryPolicy(10*1000,0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if(MyApplication.requestQueue != null){
            MyApplication.requestQueue.add(request);

        }else {
            Log.e("MyApplication : ", "Request Queue Null");
        }

    }
}



/*
* final int requestCode      : 응답 확인을 위한 코드
* final int requestMethod    : 요청 방식(GET/POST)
* final  String url       : 기상청 요청 주소
* final Map<String, String> params  : gridX, gridY 값을
* final OnResponseListener listener : 요청에 대한 이벤트 처리
*
*
*
*
* */


















