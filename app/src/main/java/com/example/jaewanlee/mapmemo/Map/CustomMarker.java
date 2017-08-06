package com.example.jaewanlee.mapmemo.Map;

import android.support.annotation.NonNull;

import com.example.jaewanlee.mapmemo.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_CAFFE;
import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_DEFAULT;
import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_RESTAURNAT;

/**
 * Created by jaewanlee on 2017. 8. 5..
 */

public class CustomMarker extends MapPOIItem {

    int markerTag;
    MapPoint markerPoint;
    String marekrName;

    public CustomMarker(@NonNull String marekrName, @NonNull int markerTag, @NonNull MapPoint markerPoint){
        super();
        this.marekrName =marekrName;
        this.markerTag=markerTag;
        this.markerPoint = markerPoint;
        initMarker();
    }

    private void initMarker(){
        this.setItemName(marekrName);
        this.setMapPoint(markerPoint);
        this.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        this.setTag(markerTag);

        //marker Type에 따른 marekr image setting
        switch (this.markerTag){
            case MARKER_TAG_RESTAURNAT:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                this.setCustomImageAutoscale(false);
                this.setCustomImageResourceId(R.drawable.ic_restaurant_marker);
                this.setCustomImageAnchor(0.5f, 1.0f);
                break;
            case MARKER_TAG_CAFFE:
                this.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                break;
            case MARKER_TAG_DEFAULT:
                this.setMarkerType(MapPOIItem.MarkerType.BluePin);
                break;
        }
    }




}
