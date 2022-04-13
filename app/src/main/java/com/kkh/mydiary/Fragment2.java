package com.kkh.mydiary;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.channguyen.rsv.RangeSliderView;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

// fragment2.xml 사용 : 작성하기 탭 선택
public class Fragment2 extends Fragment {

    Context context;
    OnTabItemSelectedListener listener;
    OnRequestListener requestListener;

    ImageView weatherIcon;
    TextView dateTextView;
    TextView locationTextView;

    EditText contentsInput;
    ImageView pictureImageView;


    boolean isPhotoCaptured;
    boolean isPhotoFileSaved;
    boolean isPhotoCanceled;

    int selectedPhotoMenu;

    Uri uri;
    File file;
    Bitmap resultPhotoBitmap;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        initUI(rootView);

        if(requestListener != null){
            requestListener.onRequest("getCurrentLocation");

        }

        return rootView;
    }

    /* inflate() 후에, 각 위젯에 대한 기본 설정 처리 메서드 */
    private void initUI(ViewGroup rootView){
        weatherIcon = rootView.findViewById(R.id.weatherIcon);
        dateTextView = rootView.findViewById(R.id.dateTextView);
        locationTextView = rootView.findViewById(R.id.locationTextView);

        contentsInput = rootView.findViewById(R.id.contentsInput);
        pictureImageView = rootView.findViewById(R.id.pictureImageView);
        pictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 카메라로 사진찍기 또는 사진앱에서 선택하기...
                if (isPhotoCaptured || isPhotoFileSaved){
                    showDialog(AppConstants.CONTENT_PHOTO_EX);
                }else {
                    showDialog(AppConstants.CONTENT_PHOTO);
                }
            }
        });

        // 저장버튼
        Button saveButton = rootView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onTabSelected(0);
                }
            }
        });

        // 삭제 버튼
        Button deleteButton = rootView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onTabSelected(0);
                }
            }
        });

        // 닫기버튼
        Button closeButton = rootView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onTabSelected(0);
                }
            }
        });

        // 기분 선택
        RangeSliderView sliderView = rootView.findViewById(R.id.sliderView);
        final RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                Toast.makeText(context, "Mood Index Changed : " + index, Toast.LENGTH_SHORT).show();
            }
        };

        sliderView.setOnSlideListener(listener);
        sliderView.setInitialIndex(2);

    } // initUI() END

    /* 날씨에 따른 날씨 아이콘 설정 메서드 */
    public void setWeather(String data){
        if (data != null){
            if(data.equals("a많음")){
                weatherIcon.setImageResource(R.drawable.weather_icon_1);
            }else if(data.equals("구름 조금")){
                weatherIcon.setImageResource(R.drawable.weather_icon_2);
            }else if(data.equals("구름 많음")){
                weatherIcon.setImageResource(R.drawable.weather_icon_3);
            }else if(data.equals("흐림")){
                weatherIcon.setImageResource(R.drawable.weather_icon_4);
            }else if(data.equals("비")){
                weatherIcon.setImageResource(R.drawable.weather_icon_5);
            }else if(data.equals("눈/비")){
                weatherIcon.setImageResource(R.drawable.weather_icon_6);
            }else if(data.equals("눈")){
                weatherIcon.setImageResource(R.drawable.weather_icon_7);
            }else {
                Log.e("Fragment2 : ","날씨 정보 없음 : "+ data);
            }
        }

    } // setWeather() END

    /* 주소 설정 메서드 */
    public void setAddress(String data){
        locationTextView.setText(data);
    }

    /* 날짜 설정 메서드 */
    public  void setDataString(String dataString){
        locationTextView.setText(dataString);
    }

    //--------------------------------------------------------//

    /* 사진 찍을 것인지, 선택할 것인지 과련 내용 처리 메서드 */
    public void showDialog(int id){
        // 사용자 선택권 : AlertDialog
        AlertDialog.Builder builder =null;

        switch (id){
            case AppConstants.CONTENT_PHOTO:
                builder = new AlertDialog.Builder(context);

                builder.setTitle("사진 메뉴 선택 ");
                builder.setSingleChoiceItems(R.array.array_photo, 0, new DialogInterface.OnClickListener() {
                    // R.array.array_photo : arrays.xml => 사진 찍기 앨범 에서 선택하기
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedPhotoMenu = i;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(selectedPhotoMenu == 0){
                            showPhotoCaptureActivity();;
                        }else if(selectedPhotoMenu == 1 ){
                            showPhotoSelectionActivity();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                         // 경고 창이 자동으로 사라지기 때문에 별로의 처리 불필요
                    }
                });
                break;


            case AppConstants.CONTENT_PHOTO_EX:
                builder = new AlertDialog.Builder(context);

                builder.setTitle("사진 메뉴 선택");
                builder.setSingleChoiceItems(R.array.array_photo_ex, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedPhotoMenu = 1;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (selectedPhotoMenu == 0){
                            showPhotoCaptureActivity();
                        }else if (selectedPhotoMenu == 1){
                            showPhotoSelectionActivity();;
                        }else if (selectedPhotoMenu == 2){
                            isPhotoCanceled = true;
                            isPhotoCaptured = false;

                            pictureImageView.setImageResource(R.drawable.picture1);

                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 경고창이 자동으로 사라지기 때문에 처리 내용 없음
                    }
                });
                break;
            default:
                break;
        }
        AlertDialog dialog = builder.create();
        dialog.show();

    } // showDialog(int id) END

    /* 사진 찍기 메서드 */
    public void showPhotoCaptureActivity(){
        try{
            file = createFile();
            if (file.exists()){
                file.delete();
            }
        }catch (Exception e){
            Log.e("Fragment2 Photo ERR : ", e.getMessage());
        }
        // SDK 버전에 따른 파일 경로 설정
        if (Build.VERSION.SDK_INT >= 24){
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID,file);
        }else {
            uri=Uri.fromFile(file);
        }

        // 미디어 스토어
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);

        startActivityForResult(intent,AppConstants.REG_PHOTO_CAPTURE);
    }

    /* 이미지 파일 생성 메서드 */
    public File createFile(){
        String fileName = createFileName();
        File outFile = new File(context.getFilesDir(),fileName);

        return outFile;
    }

    /* 사진 선택 메서드 */
    public void showPhotoSelectionActivity(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,AppConstants.REG_PHOTO_SELECTION);
    }

    /* 사진 찍기, 사진 선택 엑티비티로 부터 응답 처리 메서드 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            switch (requestCode){
                case AppConstants.REG_PHOTO_CAPTURE:   // 카메라 사용
                    resultPhotoBitmap = decodeSampleFromResource(file,pictureImageView.getWidth(),pictureImageView.getHeight());
                    pictureImageView.setImageBitmap(resultPhotoBitmap);

                    break;
                case AppConstants.REG_PHOTO_SELECTION:   // 이미지 선택
                    Uri fileUri = data.getData();
                    ContentResolver resolver = context.getContentResolver();

                    try {
                        InputStream inputStream = resolver.openInputStream(fileUri);
                        resultPhotoBitmap = BitmapFactory.decodeStream(inputStream);
                        pictureImageView.setImageBitmap(resultPhotoBitmap);

                        inputStream.close();

                        isPhotoCaptured = true;
                    }catch (Exception e){
                        Log.e("Fragment2 : ERR", e.getMessage());
                    }
                    break;
            }
        }
    } // onActivityResult() END

    /* 바이트 배열 데이터를 비트맵으로 변환 메서드*/
    public static Bitmap decodeSampleFromResource(File res, int reqWidth, int reqHeight){
        // 디코딩 범위 (테두리)
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res.getAbsolutePath(), options);

        // 이미지 크기
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

        // 이미지 크기 설정
        options.inJustDecodeBounds = false;


        return BitmapFactory.decodeFile(res.getAbsolutePath(),options);
    }

    /* 비트맵 이미지 사이즈 개선 메서드 */
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
       // rew 가로, 세로
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth){
            final int halfHeight = height;
            final int halfWidth = width;

            while ((halfHeight/inSampleSize) >= reqHeight && (halfWidth/inSampleSize) >= reqWidth){
                inSampleSize = inSampleSize *2;
            }

        }

        return inSampleSize;
    } // calculateInSampleSize() END

    /* 파일 이름 생성 메서드*/
    private String createFileName(){
        Date curDate = new Date();  // 1970.1.1.00.00.00 ~~ 현재까지 시간을 정수로
        String curDateStr = String.valueOf(curDate);

        return curDateStr; // 파일명을 현재 날짜시간의 정수로 설정..
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;

        if(context instanceof OnTabItemSelectedListener){
            listener = (OnTabItemSelectedListener) context;
        }

        if(context instanceof OnRequestListener){
            requestListener = (OnRequestListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(context != null){
            context = null;
            listener = null;
            requestListener = null;
        }
    }
}
