package com.example.jaewanlee.mapmemo.Map;

import android.app.Activity;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

/**
 * Created by jaewanlee on 2017. 8. 6..
 */

public class CustomMapView extends MapView
{
    public CustomMapView(Activity activity) {
        super(activity);
        initMarker();
    }

    private void initMarker(){
         this.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
    }
}
