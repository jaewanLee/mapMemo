package com.example.jaewanlee.mapmemo.KeywordSearchView;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jaewanlee on 2017. 8. 8..
 */

public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {
    private int divHeight;
    public RecyclerViewDecoration(int divHeight){
        this.divHeight=divHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top=divHeight;
    }
}
