package com.kkh.mydiary;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import lib.kingja.switchbutton.SwitchMultiButton;

// fragment1.xml의 SwitchMultiButton은  res => drawable =>  select_button.xml 의 디자인 적용
// 하나의 항목(일기) : note_item.xml
public class Fragment1 extends Fragment {
    RecyclerView recyclerView;
    NoteAdapter adapter;

    Context context;
    OnTabItemSelectedListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        initUI(rootView);

        return rootView;
    }

    /* inflate() 후에, 각 위젯에 대한 기본 설정 처리 메서드 */
    private void initUI(ViewGroup rootView){
        // 오늘 작성 버튼
        Button todayWriteButton = rootView.findViewById(R.id.todayWriteButton);
        todayWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onTabSelected(1);
                }
            }
        });

        // 내용/사진 스위치버튼
        SwitchMultiButton switchMultiButton = rootView.findViewById(R.id.switchButton);
        switchMultiButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                adapter.switchLayout(position);
                adapter.notifyDataSetChanged();
            }
        });

        // 리사클러뷰
        recyclerView = rootView.findViewById(R.id.recyclerView);

        // note_ite.xml 에 의해 생성되는 CardView 객체를 리스트 형태로 배치하기 위한 레이아웃
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NoteAdapter();

        // 테스트용 Dummy 데이터 작성
        adapter.addItem(new Note(0, "0", "인천 서곶로", "", "", "오늘 코드 길어", "0", null, "2월 14일"));
        adapter.addItem(new Note(1, "1", "서울 강남", "", "", "코드 길어", "1", null, "2월 14일"));
        adapter.addItem(new Note(2, "2", "경기 일산", "", "", "오늘 길어", "2", null, "2월 14일"));

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnNoteItemClickListener() {
            @Override
            public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position) {
                Note item = adapter.getItem(position);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
        if(context instanceof OnTabItemSelectedListener){
            listener = (OnTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(context != null){
            context = null;
            listener = null;
        }
    }
}
