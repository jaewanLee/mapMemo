package product.dp.io.ab180blog.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import product.dp.io.ab180blog.AddMemoView.AddMemoActivity;
import product.dp.io.ab180blog.Database.MemoDatabase;
import product.dp.io.ab180blog.Util.Constant;
import product.dp.io.ab180blog.Util.Logger;
import product.dp.io.ab180blog.Util.TranscHash;

/**
 * Created by jaewanlee on 2017. 8. 6..
 */

public class CustomMapView extends MapView implements MapView.MapViewEventListener, MapView.POIItemEventListener

{

    LinearLayout memoInfo_ll;
    TextView createdDate_tv;
    TextView category_tv;
    TextView memoName_tv;
    TextView memoContent_tv;
    Button memoDetail_tv;
    ImageButton call_ib;
    ImageButton share_ib;

    CustomMarker currentMarker;
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


    //mapView 초기화시
    @Override
    public void onMapViewInitialized(MapView mapView) {

        Logger.d("mapView init");

//        markerDetailLayout.setVisibility(View.INVISIBLE);
        //TODO 첫 데이터베이스 가져와서 그 위치 중심으로 where절 만들기
        this.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
        RealmResults<MemoDatabase> memoDatabases = realm.where(MemoDatabase.class).findAll();
        for (MemoDatabase memoDatabase : memoDatabases) {
            this.addPOIItem(new CustomMarker(memoDatabase, Constant.MARKER_TAG_SAVED));
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
        this.memoInfo_ll.setVisibility(INVISIBLE);
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
        //TODO POI item이 없거나 MemoDAtabse가 null인 경우 예외처리 하기
        currentMarker = (CustomMarker) mapPOIItem;

        this.memoInfo_ll.setVisibility(VISIBLE);

        if (mapPOIItem.getTag() == Constant.MARKER_TAG_NEW) {
            final MemoDatabase selectedMemoDatabase = ((CustomMarker) mapPOIItem).getMemoDatabase();
            this.createdDate_tv.setText(new Date(System.currentTimeMillis()).toString());
            String raw_category = selectedMemoDatabase.getMemo_document_category_group_code();
            this.category_tv.setText(TranscHash.rawToreFinedCategory(raw_category));
            this.memoName_tv.setText(selectedMemoDatabase.getMemo_document_place_name());
            this.memoContent_tv.setText("새로운 메모입니다");
            this.memoDetail_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity.getApplicationContext(), AddMemoActivity.class);
                    intent.putExtra("keywordDocument", new GsonBuilder().serializeNulls().create().toJson(currentMarker.getMemoDatabase()));
                    intent.putExtra("Tag", Constant.MARKER_TAG_NEW);
                    activity.startActivityForResult(intent, Constant.ADD_MEMO_INTENT);
                }
            });
            this.call_ib.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedMemoDatabase.getMemo_document_phone().equals("") || selectedMemoDatabase.getMemo_document_phone() == null)
                        Toast.makeText(activity, "연락처 정보가 등록되어 있지 않은 장소입니다", Toast.LENGTH_SHORT).show();
                    else {
                        String tel = "tel:" + selectedMemoDatabase.getMemo_document_phone();
                        activity.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                    }
                }
            });
            this.share_ib.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "먼저 메모를 저장해 주세요", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            final MemoDatabase memoDatabase = ((CustomMarker) mapPOIItem).getMemoDatabase();
            this.createdDate_tv.setText(new Date(memoDatabase.getMemo_createDate()).toString());
            String raw_category = memoDatabase.getMemo_document_category_group_code();
            this.category_tv.setText(TranscHash.rawToreFinedCategory(raw_category));
            this.memoName_tv.setText(memoDatabase.getMemo_document_place_name());
            this.memoContent_tv.setText(memoDatabase.getMemo_content());
            this.memoDetail_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity.getApplicationContext(), AddMemoActivity.class);
                    intent.putExtra("memo_no", currentMarker.getMemoDatabase().getMemo_no());
                    intent.putExtra("Tag", currentMarker.getTag());
                    activity.startActivityForResult(intent, Constant.ADD_MEMO_INTENT);
                }
            });
            this.call_ib.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (memoDatabase.getMemo_document_phone().equals("") || memoDatabase.getMemo_document_phone() == null)
                        Toast.makeText(activity, "연락처 정보가 등록되어 있지 않은 장소입니다", Toast.LENGTH_SHORT).show();
                    else {
                        String tel = "tel:" + memoDatabase.getMemo_document_phone();
                        activity.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                    }
                }
            });
            this.share_ib.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "shared!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    //마커 위에 말풍선 선택시
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
//        Logger.d("onCalloutBallonOfPOIItemTouched method with long parameter called on CustomMapView");
//        lastClickedPOI=mapPOIItem;
//
//        if (mapPOIItem.getTag() == MARKER_TAG_NEW) {
//            CustomMarker customMarker = (CustomMarker) mapPOIItem;
//            Intent intent = new Intent(activity.getApplicationContext(), AddMemoActivity.class);
//            intent.putExtra("keywordDocument", new GsonBuilder().serializeNulls().create().toJson(customMarker.getMemoDatabase()));
//            intent.putExtra("Tag", MARKER_TAG_NEW);
////            mapView.removePOIItem(mapPOIItem);
//            activity.startActivityForResult(intent, ADD_MEMO_INTENT);
//        } else {
//            CustomMarker customMarker = (CustomMarker) mapPOIItem;
//            Intent intent = new Intent(activity.getApplicationContext(), AddMemoActivity.class);
//            intent.putExtra("memo_no", customMarker.getMemoDatabase().getMemo_no());
//            intent.putExtra("Tag", customMarker.getTag());
////            mapView.removePOIItem(mapPOIItem);
//            activity.startActivityForResult(intent, ADD_MEMO_INTENT);
//        }
    }

    //사용자가 POI item을 길게 눌러서, 이동가능한 POI item을 이동시킨 경우 호
    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    public CustomMarker getCurrentMarker() {
        return currentMarker;
    }

    public void removeLastPOI() {
        this.removePOIItem(this.currentMarker);
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


    public void setCall_ib(ImageButton call_ib) {
        this.call_ib = call_ib;
    }

    public void setCategory_tv(TextView category_tv) {
        this.category_tv = category_tv;
    }

    public void setCreatedDate_tv(TextView createdDate_tv) {
        this.createdDate_tv = createdDate_tv;
    }

    public void setMemoContent_tv(TextView memoContent_tv) {
        this.memoContent_tv = memoContent_tv;
    }

    public void setMemoDetail_tv(Button memoDetail_tv) {
        this.memoDetail_tv = memoDetail_tv;
    }

    public void setMemoInfo_ll(LinearLayout memoInfo_ll) {
        this.memoInfo_ll = memoInfo_ll;
    }

    public void setMemoName_tv(TextView memoName_tv) {
        this.memoName_tv = memoName_tv;
    }

    public void setShare_ib(ImageButton share_ib) {
        this.share_ib = share_ib;
    }
}
