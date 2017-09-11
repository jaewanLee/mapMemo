package com.example.jaewanlee.mapmemo.Map;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jaewanlee.mapmemo.AddMemoView.AddMemoActivity;
import com.example.jaewanlee.mapmemo.Database.MemoDatabase;
import com.example.jaewanlee.mapmemo.Util.Logger;
import com.google.gson.GsonBuilder;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.jaewanlee.mapmemo.Util.Constant.ADD_MEMO_INTENT;
import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_NEW;

/**
 * Created by jaewanlee on 2017. 8. 6..
 */

public class CustomMapView extends MapView implements MapView.MapViewEventListener, MapView.POIItemEventListener

{

    TextView markerTitle;
    TextView markerCategory;
    TextView markerAddr;
    TextView markerPhone;
    TextView markerUrl;
    ImageButton imageButton;

    CustomMarker currentMarker;
    MapPoint currentLocation;
    Realm realm;

    Activity activity;

    public CustomMapView(Activity activity, LinearLayout linearLayout) {
        super(activity);
        this.activity = activity;
        this.setMapViewEventListener(this);
        this.setPOIItemEventListener(this);
        this.realm = Realm.getDefaultInstance();
    }

    public CustomMapView(Activity activity) {
        super(activity);
        this.activity = activity;
        this.setMapViewEventListener(this);
        this.setPOIItemEventListener(this);
        this.realm = Realm.getDefaultInstance();
    }

    public void setMarkerTitle(TextView markerTitle) {
        this.markerTitle = markerTitle;
    }

    public void setMarkerAddr(TextView markerAddr) {
        this.markerAddr = markerAddr;
    }

    public void setMarkerCategory(TextView markerCategory) {
        this.markerCategory = markerCategory;
    }

    public void setMarkerPhone(TextView markerPhone) {
        this.markerPhone = markerPhone;
    }

    public void setMarkerUrl(TextView markerUrl) {
        this.markerUrl = markerUrl;
    }

    public void setImageButton(ImageButton imageButton) {
        this.imageButton = imageButton;
    }

    //mapView 초기화시
    @Override
    public void onMapViewInitialized(MapView mapView) {

        //TODO
        Logger.d("mapView init");

//        markerDetailLayout.setVisibility(View.INVISIBLE);
        //TODO 첫 데이터베이스 가져와서 그 위치 중심으로 where절 만들기
        this.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
        RealmResults<MemoDatabase> memoDatabases = realm.where(MemoDatabase.class).findAll();
        for (MemoDatabase memoDatabase : memoDatabases) {
            this.addPOIItem(new CustomMarker(memoDatabase, memoDatabase.getMemo_category()));
        }
//        this.setCurrentLocationEventListener(this);
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
        Logger.d("omMapViewSingleTapped");
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

    }

    //마커 위에 말풍선 선택시
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        Logger.d("onCalloutBallonOfPOIItemTouched method with long parameter called on CustomMapView");

        if (mapPOIItem.getTag() == MARKER_TAG_NEW) {
            CustomMarker customMarker = (CustomMarker) mapPOIItem;
            Intent intent = new Intent(activity.getApplicationContext(), AddMemoActivity.class);
            intent.putExtra("keywordDocument", new GsonBuilder().serializeNulls().create().toJson(customMarker.getMemoDatabase()));
            intent.putExtra("Tag", MARKER_TAG_NEW);
            mapView.removePOIItem(mapPOIItem);
            activity.startActivityForResult(intent, ADD_MEMO_INTENT);
        } else {
            CustomMarker customMarker = (CustomMarker) mapPOIItem;
            Intent intent = new Intent(activity.getApplicationContext(), AddMemoActivity.class);
            intent.putExtra("memo_no", customMarker.getMemoDatabase().getMemo_no());
            intent.putExtra("Tag", customMarker.getTag());
            mapView.removePOIItem(mapPOIItem);
            activity.startActivityForResult(intent, ADD_MEMO_INTENT);
        }
    }

    //사용자가 POI item을 길게 눌러서, 이동가능한 POI item을 이동시킨 경우 호
    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    public CustomMarker getCurrentMarker() {
        return currentMarker;
    }

    //현재 내 위치 트래킹시, 위치 변경되었을 경우 사용
//    @Override
//    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
//        this.setMapCenterPoint(mapPoint,true);
//        currentLocation = mapPoint;
//        Logger.d("onCurrentLocationUpdate");
//        this.setCurrentLocationTrackingMode(CurrentLocationTrackingMode.TrackingModeOff);
//    }
//
//    //현재 내 위치 트래킹시, 헤드가 변경되었을 경우 사용
//    @Override
//    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
//        Logger.d("onCurrentLocationDeviceHeadingUpdate");
//    }
//
//    //현재 내 위치 트래킹시, 위치 변경되었을 경우 오류 나면 사용
//    @Override
//    public void onCurrentLocationUpdateFailed(MapView mapView) {
//        Logger.d("onCurrentLocationUpdateFailed");
//        Toast.makeText(getContext(), "에러가 발생하였습니다 다시 시도해주세", Toast.LENGTH_SHORT).show();
//    }
//
//    //현재 내 위치 트래킹시, 헤드가 변경되었을 경우 오류 나면 사용
//    @Override
//    public void onCurrentLocationUpdateCancelled(MapView mapView) {
//        Logger.d("onCurrentLocationUpdateCancelled");
//    }

}
