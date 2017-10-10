package com.example.jaewanlee.mapmemo.HomeView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jaewanlee.mapmemo.Database.MemoDatabase;
import com.example.jaewanlee.mapmemo.KeywordSearchView.KeywordSearchActivity;
import com.example.jaewanlee.mapmemo.Map.CustomMapView;
import com.example.jaewanlee.mapmemo.Map.CustomMarker;
import com.example.jaewanlee.mapmemo.Memo.MemoListActivity;
import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.Logger;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.tsengvn.typekit.TypekitContextWrapper;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

import static com.example.jaewanlee.mapmemo.Util.Constant.ADD_MEMO_INTENT;
import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_NEW;
import static com.example.jaewanlee.mapmemo.Util.Constant.SEARCH_QUERY_INTENT;

public class MainActivity extends AppCompatActivity {

    //상단 바
    ImageButton fullScreen_ib;
    ImageButton close_ib;

    //상담 탭
    LinearLayout functionTool_LL;
    ImageButton menu_ib;
    EditText searchView_et;
    ImageButton searchHistory_bt;

    Button list_button;
    ImageButton search_ib;

    FrameLayout mainMap_fl;
    CustomMapView customMapView;

    CustomMarker tempCustomMarker;

    FloatingActionButton currentLocation_fab;

    //하단탭
    LinearLayout memoInfo_ll;
    TextView createdDate_tv;
    TextView category_tv;
    TextView memoName_tv;
    TextView memoContent_tv;
    Button memoDetail_bt;
    ImageButton call_ib;
    ImageButton share_ib;

    Realm realm;

    PermissionListener permissionListener;

    CircleProgressBar circleProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        functionTool_LL = (LinearLayout) findViewById(R.id.main_fuctionTool_LinearLayout);
        searchView_et = (EditText) findViewById(R.id.main_search_editText);
        menu_ib = (ImageButton) findViewById(R.id.main_menu_ImageButton);
        mainMap_fl = (FrameLayout) findViewById(R.id.main_map_replace);
        currentLocation_fab = (FloatingActionButton) findViewById(R.id.main_currentLocation_floatButton);
        search_ib = (ImageButton) findViewById(R.id.main_search_ImageButton);
        circleProgressBar = (CircleProgressBar) findViewById(R.id.main_progressbar);
        fullScreen_ib = (ImageButton) findViewById(R.id.main_fullscreen_ImageButton);
        close_ib = (ImageButton) findViewById(R.id.main_close_ImageButton);
        searchHistory_bt = (ImageButton) findViewById(R.id.main_history_ImageButton);
        list_button = (Button) findViewById(R.id.main_memoList_Button);
        memoInfo_ll=(LinearLayout)findViewById(R.id.main_memoInfo_LinearLayout);
        createdDate_tv=(TextView)findViewById(R.id.main_createDate_textView);
        category_tv=(TextView)findViewById(R.id.main_category_textView);
        memoName_tv=(TextView)findViewById(R.id.main_memoTitle_textView);
        memoContent_tv=(TextView)findViewById(R.id.main_memoContent_textView);
        memoDetail_bt=(Button)findViewById(R.id.main_memoDetail_textView);
        call_ib=(ImageButton)findViewById(R.id.main_call_imageButton);
        share_ib=(ImageButton)findViewById(R.id.main_share_imageButton);


        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //퍼미션 설정

        permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "permission Denied", Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\\n\\nPlease turn on permissions at [Setting] > [Permission\n")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();

        realm = Realm.getDefaultInstance();

        //다음 지도 초기화
        customMapView = new CustomMapView(this);
        customMapView.setMemoInfo_ll(this.memoInfo_ll);
        customMapView.setCreatedDate_tv(this.createdDate_tv);
        customMapView.setCategory_tv(this.category_tv);
        customMapView.setMemoName_tv(this.memoName_tv);
        customMapView.setMemoContent_tv(this.memoContent_tv);
        customMapView.setMemoDetail_tv(this.memoDetail_bt);
        customMapView.setCall_ib(this.call_ib);
        customMapView.setShare_ib(this.share_ib);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.main_map_replace);
        mapViewContainer.addView(customMapView);
        customMapView.setCurrentLocationEventListener(currentLocationEventListener);
//        customMapView.setCalloutBalloonAdapter(new CustomBallonAdapter());

        //검색 버튼 클릭
        search_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchKeyword = searchView_et.getText().toString();
                Intent searchKeywordIntent = new Intent(MainActivity.this, KeywordSearchActivity.class);
                searchKeywordIntent.putExtra("search_query", searchKeyword);
                startActivityForResult(searchKeywordIntent, SEARCH_QUERY_INTENT);
            }
        });
        //memo list 버튼 클릭
        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                startActivity(intent);
            }
        });

        //현재 위치로 이동
        currentLocation_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //GPs 퍼미션 여부GPS 설정 여부 확인
                if (isGPSOn(locationManager)) {
                    setCurrentLocation(locationManager);
                }
            }
        });
        //창모드 on/off
        fullScreen_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (functionTool_LL.getVisibility() == View.VISIBLE)
                    functionTool_LL.setVisibility(View.INVISIBLE);
                else functionTool_LL.setVisibility(View.VISIBLE);
            }
        });

        //앱종료
        close_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //메뉴 버튼
        menu_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean isGPSOn(LocationManager locationManager) {
        boolean isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPS) {
            return true;
        } else {
            Toast.makeText(this, "GPS 설정을 켜주십시오",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        return false;
    }

    //권한설정 확인한 뒤 현재 위치 알아오기
    private void setCurrentLocation(LocationManager locationManager) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new TedPermission(this)
                    .setPermissionListener(permissionListener)
                    .setDeniedMessage("If you reject permission,you can not use this service\\n\\nPlease turn on permissions at [Setting] > [Permission\n")
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .check();
            Logger.d("if state ment");
        }
        customMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        circleProgressBar.setVisibility(View.VISIBLE);
        circleProgressBar.onAnimationStart();
    }

    MapView.CurrentLocationEventListener currentLocationEventListener = new MapView.CurrentLocationEventListener() {
        @Override
        public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
            customMapView.setMapCenterPoint(mapPoint, true);
            circleProgressBar.setVisibility(View.INVISIBLE);
            circleProgressBar.onAnimationEnd();
            customMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

        }

        @Override
        public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
        }

        @Override
        public void onCurrentLocationUpdateFailed(MapView mapView) {
            circleProgressBar.setVisibility(View.INVISIBLE);
            circleProgressBar.onAnimationEnd();
        }

        @Override
        public void onCurrentLocationUpdateCancelled(MapView mapView) {
            circleProgressBar.setVisibility(View.INVISIBLE);
            circleProgressBar.onAnimationEnd();
        }
    };

    //인텐트 결과
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_QUERY_INTENT && resultCode == SEARCH_QUERY_INTENT) {
            if ( data.hasExtra("searchResult")) {
                MemoDatabase memoDatabase = new Gson().fromJson(data.getStringExtra("searchResult"), MemoDatabase.class);
                customMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.valueOf(memoDatabase.getMemo_document_y()), Double.valueOf(memoDatabase.getMemo_document_x())), true);
                tempCustomMarker = new CustomMarker(memoDatabase, MARKER_TAG_NEW);
                customMapView.addPOIItem(tempCustomMarker);
//                customMapView.notify();
            }else{
                Toast.makeText(this, "통신오류가 발생하였습니다 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == ADD_MEMO_INTENT &&resultCode == ADD_MEMO_INTENT ) {
            if (data.hasExtra("addMemoResult")) {
                MemoDatabase newMemo = realm.where(MemoDatabase.class).equalTo("memo_no", data.getIntExtra("addMemoResult", 0)).findFirst();
                customMapView.removeLastPOI();
                customMapView.addPOIItem(new CustomMarker(newMemo, newMemo.getMemo_category()));

            }else{
                Toast.makeText(this, "통신오류가 발생하였습니다 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        } else{

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed())
            realm.close();
    }

    //글꼴 설정
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    public class CustomBallonAdapter implements CalloutBalloonAdapter {

        private final View customCalloutBalloon;

        public CustomBallonAdapter() {
            customCalloutBalloon = getLayoutInflater().inflate(R.layout.customballon_interface, null);

        }

        @Override
        public View getCalloutBalloon(MapPOIItem mapPOIItem) {
            CustomMarker selectedMarker = (CustomMarker) mapPOIItem;
            Logger.d("getCalloutBalloon");
            ((TextView) customCalloutBalloon.findViewById(R.id.customballon_name_textView)).setText(selectedMarker.markerName);
            //새로운 마커에 대한 말풍선
            if (selectedMarker.getTag() != MARKER_TAG_NEW) {
                ((ImageView) customCalloutBalloon.findViewById(R.id.customballon_category_imageView)).setImageResource(R.drawable.ic_action_back);
                ((TextView) customCalloutBalloon.findViewById(R.id.customballon_time_textView)).setText(new Date(selectedMarker.markerDate).toString());
                ((TextView) customCalloutBalloon.findViewById(R.id.customballon_memo_textView)).setText(selectedMarker.markerMemo);
            }
            //기존 마커테 대한 말풍선
            else {
                ((ImageView) customCalloutBalloon.findViewById(R.id.customballon_category_imageView)).setImageResource(R.drawable.ic_action_back);
                ((TextView) customCalloutBalloon.findViewById(R.id.customballon_time_textView)).setText("New");
                ((TextView) customCalloutBalloon.findViewById(R.id.customballon_memo_textView)).setText("새로운 위치");
            }

            return customCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
            Logger.d("getPressedCalloutBalloon");
            return null;
        }
    }

}
