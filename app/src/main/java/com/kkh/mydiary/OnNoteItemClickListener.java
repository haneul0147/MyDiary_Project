package com.kkh.mydiary;

import android.view.View;

// NoteAdapter에 구현되는 인터페이스
public interface OnNoteItemClickListener {
    public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position);
}
