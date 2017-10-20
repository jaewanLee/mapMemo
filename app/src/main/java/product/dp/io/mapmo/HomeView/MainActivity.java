package product.dp.io.mapmo.HomeView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import io.realm.Realm;
import product.dp.io.mapmo.Database.MemoDatabase;
import product.dp.io.mapmo.KeywordSearchView.KeywordSearchActivity;
import product.dp.io.mapmo.Map.CustomMapView;
import product.dp.io.mapmo.Map.CustomMarker;
import product.dp.io.mapmo.MemoList.MemoListActivity;
import product.dp.io.mapmo.Util.Constant;
import product.dp.io.mapmo.Util.Logger;

public class MainActivity extends AppCompatActivity {

    //상담 탭
    LinearLayout functionTool_LL;
    ImageButton menu_ib;
    EditText searchView_et;
    ImageButton erase_bt;

    FloatingActionButton list_fab;
    FloatingActionButton currentLocation_fab;

    //Map관련
    FrameLayout mainMap_fl;
    CustomMapView customMapView;
    CustomMarker tempCustomMarker;

    //하단탭
    LinearLayout memoInfo_ll;
    TextView createdDate_tv;
    TextView category_tv;
    TextView memoName_tv;
    TextView memoContent_tv;
    FloatingActionButton memoDetail_bt;
    ImageButton call_ib;
    ImageButton share_ib;

    Realm realm;

    PermissionListener permissionListener;

    CircleProgressBar circleProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(product.dp.io.mapmo.R.layout.activity_main);

        // 레이아웃 init
        initLayout();

        // 버튼리스터 init
        initButtonListener();

        // 퍼미션 세팅
        setPermissionIssue();

        // 커스텀 맵 init
        initCustomMap();

        // realm init
        realm = Realm.getDefaultInstance();


    }

    private void initLayout() {
        functionTool_LL = (LinearLayout) findViewById(product.dp.io.mapmo.R.id.main_fuctionTool_LinearLayout);
        searchView_et = (EditText) findViewById(product.dp.io.mapmo.R.id.main_search_editText);
        menu_ib = (ImageButton) findViewById(product.dp.io.mapmo.R.id.main_menu_ImageButton);
        mainMap_fl = (FrameLayout) findViewById(product.dp.io.mapmo.R.id.main_map_replace);
        currentLocation_fab = (FloatingActionButton) findViewById(product.dp.io.mapmo.R.id.main_currentLocation_floatButton);
        circleProgressBar = (CircleProgressBar) findViewById(product.dp.io.mapmo.R.id.main_progressbar);
        erase_bt = (ImageButton) findViewById(product.dp.io.mapmo.R.id.main_erase_ImageButton);
        list_fab = (FloatingActionButton) findViewById(product.dp.io.mapmo.R.id.main_memoList_Button);
        memoInfo_ll = (LinearLayout) findViewById(product.dp.io.mapmo.R.id.main_memoInfo_LinearLayout);
        createdDate_tv = (TextView) findViewById(product.dp.io.mapmo.R.id.main_createDate_textView);
        category_tv = (TextView) findViewById(product.dp.io.mapmo.R.id.main_category_textView);
        memoName_tv = (TextView) findViewById(product.dp.io.mapmo.R.id.main_memoTitle_textView);
        memoContent_tv = (TextView) findViewById(product.dp.io.mapmo.R.id.main_memoContent_textView);
        memoDetail_bt = (FloatingActionButton) findViewById(product.dp.io.mapmo.R.id.main_memoDetail_textView);
        memoDetail_bt.hide();
        call_ib = (ImageButton) findViewById(product.dp.io.mapmo.R.id.main_call_imageButton);
        share_ib = (ImageButton) findViewById(product.dp.io.mapmo.R.id.main_share_imageButton);

        memoInfo_ll.setVisibility(View.INVISIBLE);
    }

    private void initCustomMap() {
        customMapView = new CustomMapView(this);
        customMapView.setMemoInfo_ll(this.memoInfo_ll);
        customMapView.setCreatedDate_tv(this.createdDate_tv);
        customMapView.setCategory_tv(this.category_tv);
        customMapView.setMemoName_tv(this.memoName_tv);
        customMapView.setMemoContent_tv(this.memoContent_tv);
        customMapView.setMemoDetail_bt(this.memoDetail_bt);
        customMapView.setCall_ib(this.call_ib);
        customMapView.setShare_ib(this.share_ib);
        customMapView.setList_fab(this.list_fab);
        customMapView.setCurrentLocation_fab(this.currentLocation_fab);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(product.dp.io.mapmo.R.id.main_map_replace);
        mapViewContainer.addView(customMapView);
        customMapView.setCurrentLocationEventListener(currentLocationEventListener);
        //        customMapView.setCalloutBalloonAdapter(new CustomBallonAdapter());
    }

    private void initButtonListener() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        searchView_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String searchKeyword = textView.getText().toString();
                Intent searchKeywordIntent = new Intent(MainActivity.this, KeywordSearchActivity.class);
                searchKeywordIntent.putExtra("search_query", searchKeyword);
                startActivityForResult(searchKeywordIntent, Constant.SEARCH_QUERY_INTENT);
                return true;
            }
        });

        //memo list 버튼 클릭
        list_fab.setOnClickListener(new View.OnClickListener() {
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

        //메뉴 버튼
        menu_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        erase_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 현재 적은 text의 내용과 아래 layout이 올라와 있따면 다지우기
           }
        });

    }

    private void setPermissionIssue() {

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
        if (requestCode == Constant.SEARCH_QUERY_INTENT && resultCode == Constant.SEARCH_QUERY_INTENT) {
            if (data.hasExtra("searchResult")) {
                MemoDatabase memoDatabase = new Gson().fromJson(data.getStringExtra("searchResult"), MemoDatabase.class);
                customMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.valueOf(memoDatabase.getMemo_document_y()), Double.valueOf(memoDatabase.getMemo_document_x())), true);
                tempCustomMarker = new CustomMarker(memoDatabase, Constant.MARKER_TAG_NEW);
                customMapView.addPOIItem(tempCustomMarker);
//                customMapView.notify();
            } else {
                Toast.makeText(this, "통신오류가 발생하였습니다 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constant.ADD_MEMO_INTENT && resultCode == Constant.ADD_MEMO_INTENT) {
            if (data.hasExtra("addMemoResult")) {
                MemoDatabase newMemo = realm.where(MemoDatabase.class).equalTo("memo_no", data.getIntExtra("addMemoResult", 0)).findFirst();
                customMapView.removeLastPOI();
                customMapView.addPOIItem(new CustomMarker(newMemo, Constant.MARKER_TAG_SAVED));

            } else {
                Toast.makeText(this, "통신오류가 발생하였습니다 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        } else {

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

    protected void setStatusBarTranslucent(boolean makeTranslucent) {

        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    //글꼴 설정
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
//    }


}
