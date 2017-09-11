package com.example.jaewanlee.mapmemo.Map;

import android.support.annotation.NonNull;

import com.example.jaewanlee.mapmemo.Database.MemoDatabase;
import com.example.jaewanlee.mapmemo.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_CAFFE;
import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_DEFAULT;
import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_NEW;
import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_PARK;
import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_PLACE;
import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_RESTAURNAT;

/**
 * Created by jaewanlee on 2017. 8. 5..
 */

public class CustomMarker extends MapPOIItem {

    private int markerTag;
    private MemoDatabase memoDatabase;
    public String markerName;
    public long markerDate;
    public int markerCategory;
    public String markerMemo;
    private KeywordSearchRepo.KeywordDocuments keywordDocuments;

    public CustomMarker(@NonNull MemoDatabase memoDatabase, int TAG) {
        this.memoDatabase = memoDatabase;
        this.markerTag = TAG;
        this.markerName=memoDatabase.getMemo_document_place_name();
        this.markerDate=memoDatabase.getMemo_createDate();
        markerCategory=memoDatabase.getMemo_category();
        markerMemo=memoDatabase.getMemo_content();
        initMarkerWithMemoData();
    }

    public CustomMarker(@NonNull KeywordSearchRepo.KeywordDocuments keywordDocuments, int TAG) {
        this.keywordDocuments = keywordDocuments;
        this.markerTag = TAG;
        initMarekrWithKeyDocument();
    }

    //memoData기반의 마커는 기존의 디비에서 저장된 정보
    private void initMarkerWithMemoData() {
        this.setItemName(memoDatabase.getMemo_document_place_name());
        this.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.valueOf(memoDatabase.getMemo_document_y()), Double.valueOf(memoDatabase.getMemo_document_x())));
        this.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        this.setTag(this.markerTag);

        //marker Type에 따른 marekr image setting
        switch (this.markerTag) {
            case MARKER_TAG_RESTAURNAT:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                this.setCustomImageAutoscale(false);
                this.setCustomImageResourceId(R.drawable.ic_marker_black);
                break;
            case MARKER_TAG_CAFFE:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                this.setCustomImageAutoscale(false);
                this.setCustomImageResourceId(R.drawable.ic_marker_blue);
                break;
            case MARKER_TAG_PARK:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                this.setCustomImageAutoscale(false);
                this.setCustomImageResourceId(R.drawable.ic_marker_red);
                break;
            case MARKER_TAG_PLACE:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                this.setCustomImageAutoscale(false);
                this.setCustomImageResourceId(R.drawable.ic_restaurant_marker);
                break;
            case MARKER_TAG_NEW:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                this.setCustomImageAutoscale(false);
                this.setCustomImageResourceId(R.drawable.ic_pin_01);
                break;
            case MARKER_TAG_DEFAULT:
                this.setMarkerType(MapPOIItem.MarkerType.BluePin);
                break;
        }
    }

    //keydocument base는 검색을 통해 새롭게 추가된 마커
    private void initMarekrWithKeyDocument() {
        this.setItemName(keywordDocuments.getPlace_name());
        this.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.valueOf(keywordDocuments.getY()), Double.valueOf(keywordDocuments.getX())));
        this.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        this.setTag(this.markerTag);

        //marker Type에 따른 marekr image setting
        switch (this.markerTag) {
            case MARKER_TAG_RESTAURNAT:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                this.setCustomImageAutoscale(false);
                this.setCustomImageResourceId(R.drawable.ic_marker_black);
                break;
            case MARKER_TAG_CAFFE:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                this.setCustomImageAutoscale(false);
                this.setCustomImageResourceId(R.drawable.ic_marker_blue);
                break;
            case MARKER_TAG_PARK:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                this.setCustomImageAutoscale(false);
                this.setCustomImageResourceId(R.drawable.ic_marker_red);
                break;
            case MARKER_TAG_PLACE:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                this.setCustomImageAutoscale(false);
                this.setCustomImageResourceId(R.drawable.ic_restaurant_marker);
                break;
            case MARKER_TAG_NEW:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                this.setCustomImageAutoscale(false);
                this.setCustomImageResourceId(R.drawable.ic_pin_01);
                break;
            case MARKER_TAG_DEFAULT:
                this.setMarkerType(MapPOIItem.MarkerType.BluePin);
                break;
        }
    }

    public KeywordSearchRepo.KeywordDocuments getKeywordDocuments() {
        return keywordDocuments;
    }

    public MemoDatabase getMemoDatabase() {
        return memoDatabase;
    }
}
