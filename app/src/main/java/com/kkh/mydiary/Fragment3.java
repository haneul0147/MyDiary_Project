package com.kkh.mydiary;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.TimeUtils;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

// 외부 차트 라이브러리 활용~~
public class Fragment3 extends Fragment {
    PieChart chart;
    BarChart chart2;
    LineChart chart3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);

        initUI(rootView);

        return rootView;
    }

    /* inflate() 후에, 각 위젯에 대한 기본 설정 처리 메서드 */
    private void initUI(ViewGroup rootView){
        // PieChart 설정
        chart = rootView.findViewById(R.id.chart1);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(true);
        chart.setCenterText("기분별 비율");

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(100);

        chart.setHoleRadius(58F);
        chart.setTransparentCircleRadius(61F);

        chart.setDrawCenterText(true);

        chart.setHighlightPerTapEnabled(true);

        Legend legend1 = chart.getLegend();
        legend1.setEnabled(false);

        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12F);

        setDataPie();

        // BarChart 설정
        chart2 = rootView.findViewById(R.id.chart2);
        chart2.setDrawValueAboveBar(true);

        chart2.getDescription().setEnabled(true);
        chart2.setDrawGridBackground(false);

        XAxis xAxis = chart2.getXAxis();
        xAxis.setEnabled(false);

        YAxis yAxis = chart2.getAxisLeft();
        yAxis.setLabelCount(6, false);
        yAxis.setAxisMinimum(0.0F);
        yAxis.setGranularityEnabled(true);
        yAxis.setGranularity(1F);

        YAxis yAxis1 = chart2.getAxisRight();
        yAxis1.setEnabled(false);

        Legend legend = chart2.getLegend();
        legend.setEnabled(false);

        chart2.animateXY(1500, 1500);

        setDataBar();

        // LineChart 설정
        chart3 = rootView.findViewById(R.id.chart3);

        chart3.getDescription().setEnabled(true);
        chart3.setDrawGridBackground(false);

        chart3.setBackgroundColor(Color.WHITE);
        chart3.setViewPortOffsets(0,0,0,0);

        Legend legend2 = chart3.getLegend();   // 범례
        legend2.setEnabled(false);

        // X 축 설정
        XAxis xAxis1 = chart3.getXAxis();
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis1.setTextSize(10F);
        xAxis1.setTextColor(Color.WHITE);
        xAxis1.setDrawAxisLine(false);
        xAxis1.setDrawGridLines(true);
        xAxis1.setTextColor(Color.rgb(255, 192, 56));
        xAxis1.setCenterAxisLabels(true);
        xAxis1.setGranularity(1F);
        xAxis1.setValueFormatter(new ValueFormater(){
            private final SimpleDateFormat mFormat = new SimpleDateFormat("MM-DD", Locale.KOREA);

            @Override
            public String getFormattedValue(float value) {
                long millis = TimeUnit.HOURS.toMillis((long) value);   // 단말기의 날짜 시간
                return mFormat.format(new Date(millis));
            }
        });

        // y 축 설정
        YAxis yAxis2 = chart3.getAxisLeft();
        yAxis2.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yAxis2.setTextColor(ColorTemplate.getHoloBlue());
        yAxis2.setDrawGridLines(true);
        yAxis2.setGranularityEnabled(true);
        yAxis2.setAxisMinimum(0F);
        yAxis2.setAxisMaximum(170F);
        yAxis2.setYOffset(-9F);
        yAxis2.setTextColor(Color.rgb(255, 192, 56));

        YAxis yAxis3 =chart3.getAxisRight();
        yAxis3.setEnabled(false);

        setDataLine();

    }

    // PieChart 값설정 메서드
    private void setDataPie(){
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // 테스트를 위한 Dummy데이터
        entries.add(new PieEntry(20.0F, "", getResources().getDrawable(R.drawable.smile1_24)));
        entries.add(new PieEntry(22.0F, "", getResources().getDrawable(R.drawable.smile1_24)));
        entries.add(new PieEntry(24.0F, "", getResources().getDrawable(R.drawable.smile1_24)));
        entries.add(new PieEntry(26.0F, "", getResources().getDrawable(R.drawable.smile1_24)));
        entries.add(new PieEntry(30.0F, "", getResources().getDrawable(R.drawable.smile1_24)));

        // PieChart 에 맞게 데이터 변형
        PieDataSet dataSet = new PieDataSet(entries, "기분별 비율");
        dataSet.setDrawIcons(true);
        dataSet.setSliceSpace(3F);
        dataSet.setIconsOffset(new MPPointF(0, -40));
        dataSet.setSelectionShift(5F);

        // 각 데이터에 관한 색상
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.JOYFUL_COLORS){
            colors.add(c);
        }
        dataSet.setColors(colors);

        // PieChart에 데이터 설정
        PieData data = new PieData(dataSet);
        data.setValueTextSize(22.0F);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.invalidate();
    }

    // BarChart 값설정 메서드
    private void setDataBar(){
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        // 테스트를 위한 Dummy 데이터
        entries.add(new BarEntry(1.0F, 20.0F, getResources().getDrawable(R.drawable.smile1_24)));
        entries.add(new BarEntry(2.0F, 40.0F, getResources().getDrawable(R.drawable.smile2_24)));
        entries.add(new BarEntry(3.0F, 60.0F, getResources().getDrawable(R.drawable.smile3_24)));
        entries.add(new BarEntry(4.0F, 80.0F, getResources().getDrawable(R.drawable.smile4_24)));
        entries.add(new BarEntry(5.0F, 100.0F, getResources().getDrawable(R.drawable.smile5_24)));

        // BarChart에 맞게 데이터 변형
        BarDataSet dataSet = new BarDataSet(entries, "분기별 기분");
        dataSet.setColors(Color.rgb(240, 120, 124));

        // 각 막대 그래프 색상
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.JOYFUL_COLORS){
            colors.add(c);
        }
        dataSet.setColors(colors);
        dataSet.setIconsOffset(new MPPointF(0, -10));

        BarData data = new BarData(dataSet);
        data.setValueTextSize(10F);
        data.setDrawValues(false);
        data.setBarWidth(0.8F);

        chart2.setData(data);
        chart2.invalidate();
    }

    // LineChart 값설정 메서드
    private void setDataLine(){
        ArrayList<Entry> values = new ArrayList<Entry>();

        // 테스트를 위한 Dummy 데이터
        values.add(new Entry(24F, 20.0F));
        values.add(new Entry(48F, 50.0F));
        values.add(new Entry(64F, 30.0F));
        values.add(new Entry(72F, 70.0F));
        values.add(new Entry(120F, 90.0F));

        LineDataSet dataSet = new LineDataSet(values, "DataSet-1");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(ColorTemplate.getHoloBlue());
        dataSet.setValueTextColor(ColorTemplate.getHoloBlue());
        dataSet.setLineWidth(1.5F);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(ColorTemplate.getHoloBlue());
        dataSet.setHighLightColor(Color.rgb(244,117,117));
        dataSet.setDrawCircleHole(false);

        LineData data = new LineData(dataSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9F);

        chart3.setData(data);
        chart3.invalidate();

    }
}
