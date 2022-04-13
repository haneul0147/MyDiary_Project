package com.kkh.mydiary;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class ValueFormater extends com.github.mikephil.charting.formatter.ValueFormatter
                                                                implements IAxisValueFormatter, IValueFormatter {

    // getFormattedValue(float, AxisBase) : 값 형태
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return getFormattedValue(value);
    }

    // getFormattedValue(float, Entry, int, ViewPortHandler) : 값 형태
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return getFormattedValue(value);
    }

    // getFormattedValue(float) : 값 형태 (숫자를 문자로 변경하여 반환 : 위의 getFormattedValu() 내에서 호출하여 사용.)
    @Override
    public String getFormattedValue(float value) {
        return String.valueOf(value);
    }

    // getAxisLabel(float, AxisBase)  : 축(x, y)

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return getFormattedValue(value);
    }


    // getBarLabel(BarEntry) : 막대 <====
    @Override
    public String getBarLabel(BarEntry barEntry) {
        return getFormattedValue(barEntry.getY());
    }


    // getBarStackedLabel(float, BarEntry) : 히스토그램 형태

    @Override
    public String getBarStackedLabel(float value, BarEntry stackedEntry) {
        return getFormattedValue(value);
    }

    // getPointLabel(Entry) : 포인트
    @Override
    public String getPointLabel(Entry entry) {
        return getFormattedValue(entry.getY());
    }


    // getPieLabel(float, PieEntry) : 파이 <====
    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {
        return super.getFormattedValue(value);
    }


    // getRadarLabel(RadarEntry) : 방사형
    @Override
    public String getRadarLabel(RadarEntry radarEntry) {
        return getFormattedValue(radarEntry.getY());
    }

    // getBubbleLabel(BubbleEntry)  : 분포
    @Override
    public String getBubbleLabel(BubbleEntry bubbleEntry) {
        return getFormattedValue(bubbleEntry.getSize());
    }


    // getCandleLabel(CandelEntry)  : 주식 차트
    @Override
    public String getCandleLabel(CandleEntry candleEntry) {
        return getFormattedValue(candleEntry.getHigh());
    }
}
