package com.example.jaewanlee.mapmemo.Map;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jaewanlee.mapmemo.Database.MemoDatabase;
import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.Logger;
import com.example.jaewanlee.mapmemo.Util.TranscHash;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_NEW;

/**
 * Created by jaewanlee on 2017. 8. 6..
 */

public class CustomMapView extends MapView implements MapView.MapViewEventListener, MapView.POIItemEventListener

{
    LinearLayout markerDetailLayout;
    TextView markerTitle;
    TextView markerCategory;
    TextView markerAddr;
    TextView markerPhone;
    TextView markerUrl;
    ImageButton imageButton;

    CustomMarker currentMarker;
    Realm realm;

    public CustomMapView(Activity activity, LinearLayout linearLayout) {
        super(activity);
        this.markerDetailLayout = linearLayout;
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

        markerDetailLayout.setVisibility(View.INVISIBLE);
        //TODO 첫 데이터베이스 가져와서 그 위치 중심으로 where절 만들기
        this.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
        RealmResults<MemoDatabase> memoDatabases = realm.where(MemoDatabase.class).findAll();
        for (MemoDatabase memoDatabase : memoDatabases) {
            this.addPOIItem(new CustomMarker(memoDatabase, memoDatabase.getMemo_category()));
        }
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
        markerDetailLayout.setVisibility(View.INVISIBLE);
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
        Logger.d("onPOIItemSelected method");
        currentMarker = (CustomMarker) mapPOIItem;
        if (mapPOIItem.getTag() == MARKER_TAG_NEW) {
            markerTitle.setText(((CustomMarker) mapPOIItem).getKeywordDocuments().getPlace_name());
            if (((CustomMarker) mapPOIItem).getKeywordDocuments().getCategory_group_code().equals(""))
                markerCategory.setText("");
            else
                markerCategory.setText(TranscHash.categoryHash.get(((CustomMarker) mapPOIItem).getKeywordDocuments().getCategory_group_code()));
            if (((CustomMarker) mapPOIItem).getKeywordDocuments().getRoad_address_name().equals("")) {
                markerAddr.setText(((CustomMarker) mapPOIItem).getKeywordDocuments().getAddress_name());
            } else {
                markerAddr.setText(((CustomMarker) mapPOIItem).getKeywordDocuments().getRoad_address_name());
            }
            if (((CustomMarker) mapPOIItem).getKeywordDocuments().getPhone().equals(""))
                markerPhone.setText("업데이트 중");
            else markerPhone.setText(((CustomMarker) mapPOIItem).getKeywordDocuments().getPhone());
            if (((CustomMarker) mapPOIItem).getKeywordDocuments().getPlace_url().equals(""))
                markerUrl.setText("업데이트 중");
            else
                markerUrl.setText(((CustomMarker) mapPOIItem).getKeywordDocuments().getPlace_url());
            imageButton.setImageResource(R.drawable.ic_add_new_memo);
        }
        else {
            markerTitle.setText(((CustomMarker) mapPOIItem).getMemoDatabase().getMemo_document_place_name());
            if (((CustomMarker) mapPOIItem).getMemoDatabase().getMemo_document_category_group_code().equals(""))
                markerCategory.setText("");
            else
                markerCategory.setText(TranscHash.categoryHash.get(((CustomMarker) mapPOIItem).getMemoDatabase().getMemo_document_category_group_code()));
            if (((CustomMarker) mapPOIItem).getMemoDatabase().getMemo_document_road_address_name().equals("")) {
                markerAddr.setText(((CustomMarker) mapPOIItem).getMemoDatabase().getMemo_document_address_name());
            } else {
                markerAddr.setText(((CustomMarker) mapPOIItem).getMemoDatabase().getMemo_document_road_address_name());
            }
            if (((CustomMarker) mapPOIItem).getMemoDatabase().getMemo_document_phone().equals(""))
                markerPhone.setText("업데이트 중");
            else
                markerPhone.setText(((CustomMarker) mapPOIItem).getMemoDatabase().getMemo_document_phone());
            if (((CustomMarker) mapPOIItem).getMemoDatabase().getMemo_document_place_url().equals(""))
                markerUrl.setText("업데이트 중");
            else
                markerUrl.setText(((CustomMarker) mapPOIItem).getMemoDatabase().getMemo_document_place_url());
            imageButton.setImageResource(R.drawable.ic_detail_memo);
        }
        markerDetailLayout.setVisibility(View.VISIBLE);
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

    public CustomMarker getCurrentMarker() {
        return currentMarker;
    }
}
