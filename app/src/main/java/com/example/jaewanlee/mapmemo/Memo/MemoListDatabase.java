package com.example.jaewanlee.mapmemo.Memo;

import com.example.jaewanlee.mapmemo.Database.MemoDatabase;

/**
 * Created by jaewanlee on 2017. 9. 20..
 */

public class MemoListDatabase {
    Boolean isChecked;
    MemoDatabase memoDatabase;

    public MemoListDatabase() {
        this.isChecked = false;
    }

    public MemoListDatabase(MemoDatabase memoDatabase) {
        this.isChecked = false;
        this.memoDatabase = memoDatabase;
    }

    public Boolean getIsChecked() {
        return this.isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public MemoDatabase getMemoDatabase() {
        return memoDatabase;
    }

    public void setMemoDatabase(MemoDatabase memoDatabase) {
        this.memoDatabase = memoDatabase;
    }
}
