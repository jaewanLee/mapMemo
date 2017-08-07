package com.example.jaewanlee.mapmemo.Map;

import android.app.Activity;

import com.example.jaewanlee.mapmemo.Util.Logger;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

/**
 * Created by jaewanlee on 2017. 8. 6..
 */

public class CustomMapView extends MapView implements MapView.MapViewEventListener,MapView.POIItemEventListener


{
    public CustomMapView(Activity activity) {
        super(activity);
    }

    //mapView 초기화시
    @Override
    public void onMapViewInitialized(MapView mapView) {
        this.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
    }
    //지도 중심좌표가 이동한 경우
    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }
    //줌인 레벨이 달라졌을 경우
    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }
    //지도 터치
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }
    //지도 두번 터
    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }
    //지도 길게 누르기
    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }
    //지도 드래그
    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }
    //지도 드래그 끝날시
    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }
    //지도 움직임이 끝낱을 꼉우
    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
    //POI item을 선택할 시
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Logger.d("onCalloutBallonOfPOIItemTouched method with short parameter called on CustomMapView");
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        Logger.d("onCalloutBallonOfPOIItemTouched method with long parameter called on CustomMapView");
    }
    //사용자가 POI item을 길게 눌러서, 이동가능한 POI item을 이동시킨 경우 호
    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}
