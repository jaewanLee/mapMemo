package product.dp.io.mapmo.HomeView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import product.dp.io.mapmo.AddMemoView.AddMemoActivity;
import product.dp.io.mapmo.Database.MemoDatabase;
import product.dp.io.mapmo.KeywordSearchView.KeywordSearchActivity;
import product.dp.io.mapmo.MemoList.MemoListActivity;
import product.dp.io.mapmo.Util.Constant;
import product.dp.io.mapmo.Util.TranscHash;

import static android.view.View.VISIBLE;

/**
 * Created by jaewanlee on 2017. 10. 19..
 */

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    //상담 탭
    LinearLayout functionTool_LL;
    ImageButton menu_ib;
    EditText searchView_et;
    ImageButton erase_bt;

    FloatingActionButton list_fab;
    FloatingActionButton currentLocation_fab;

    //Map관련
    MapFragment mapFragment;
    GoogleMap mGoogleMap;
    Marker tempMarker;

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

    Boolean isPermissionGranted;
    SharedPreferences permissionShared;


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

        permissionShared = getSharedPreferences("permission", MODE_PRIVATE);
        isPermissionGranted = permissionShared.getBoolean("permission", false);
    }

    private void initCustomMap() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(product.dp.io.mapmo.R.id.main_map_replace);
        mapFragment.getMapAsync(this);

    }

    private void initButtonListener() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        searchView_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String searchKeyword = textView.getText().toString();
                Intent searchKeywordIntent = new Intent(HomeActivity.this, KeywordSearchActivity.class);
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
//        currentLocation_fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //GPs 퍼미션 여부GPS 설정 여부 확인
//                //TODO 현재위치 관련 내용
//                if (isGPSOn(locationManager)) {
//                    setCurrentLocation();
//                }
//            }
//        });

        //메뉴 버튼
        menu_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MenuActivity.class);
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
                SharedPreferences.Editor editor = permissionShared.edit();
                editor.putBoolean("permission", true);
                editor.commit();
                isPermissionGranted = true;
                setMyLocationEnable();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(HomeActivity.this, "권한 설정 거부로 사용이 제한되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\\n\\nPlease turn on permissions at [Setting] > [Permission\n")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }


//    private boolean isGPSOn(LocationManager locationManager) {
//        boolean isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if (isGPS) {
//            return true;
//        } else {
//            Toast.makeText(this, getString(R.string.permission_deny_message),
//                    Toast.LENGTH_LONG).show();
//            startActivity(new Intent(
//                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//        }
//        return false;
//    }

    //권한설정 확인한 뒤 현재 위치 알아오기
    //TODO 구글로 다바꿔야함
//    private void setCurrentLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            new TedPermission(this)
//                    .setPermissionListener(permissionListener)
//                    .setDeniedMessage("If you reject permission,you can not use this service\\n\\nPlease turn on permissions at [Setting] > [Permission\n")
//                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    .check();
//            Logger.d("if state ment");
//        }
//        customMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
//        circleProgressBar.setVisibility(VISIBLE);
//        circleProgressBar.onAnimationStart();
//    }
//
//    MapView.CurrentLocationEventListener currentLocationEventListener = new MapView.CurrentLocationEventListener() {
//        @Override
//        public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
//            customMapView.setMapCenterPoint(mapPoint, true);
//            circleProgressBar.setVisibility(View.INVISIBLE);
//            circleProgressBar.onAnimationEnd();
//            customMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
//
//        }
//
//        @Override
//        public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
//        }
//
//        @Override
//        public void onCurrentLocationUpdateFailed(MapView mapView) {
//            circleProgressBar.setVisibility(View.INVISIBLE);
//            circleProgressBar.onAnimationEnd();
//        }
//
//        @Override
//        public void onCurrentLocationUpdateCancelled(MapView mapView) {
//            circleProgressBar.setVisibility(View.INVISIBLE);
//            circleProgressBar.onAnimationEnd();
//        }
//    };

    //인텐트 결과
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.SEARCH_QUERY_INTENT && resultCode == Constant.SEARCH_QUERY_INTENT) {
            if (data.hasExtra("searchResult")) {
                MemoDatabase memoDatabase = new Gson().fromJson(data.getStringExtra("searchResult"), MemoDatabase.class);
                LatLng tempLatLng = new LatLng(Double.valueOf(memoDatabase.getMemo_document_x()), Double.valueOf(memoDatabase.getMemo_document_y()));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(tempLatLng));
                tempMarker = makeAndAddMarker(memoDatabase, tempLatLng);
            } else {
                Toast.makeText(this, "통신오류가 발생하였습니다 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constant.ADD_MEMO_INTENT && resultCode == Constant.ADD_MEMO_INTENT) {
            if (data.hasExtra("addMemoResult")) {
                MemoDatabase newMemo = realm.where(MemoDatabase.class).equalTo("memo_no", data.getIntExtra("addMemoResult", 0)).findFirst();
                tempMarker.remove();
                makeAndAddMarker(newMemo, new LatLng(Double.valueOf(newMemo.getMemo_document_x()), Double.valueOf(newMemo.getMemo_document_y())));

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //메모 데이터를 init 해주기
        this.mGoogleMap = googleMap;
        MemoDatabaseInit(googleMap);
        googleMap.setOnMarkerClickListener(this);
        setMyLocationEnable();


    }

    public void MemoDatabaseInit(GoogleMap googleMap) {
        //TODO 첫 데이터베이스 가져와서 그 위치 중심으로 where절 만들기

        RealmResults<MemoDatabase> memoDatabases = realm.where(MemoDatabase.class).findAll();
        for (MemoDatabase memoDatabase : memoDatabases) {
            LatLng savedDataLat = new LatLng(Long.valueOf(memoDatabase.getMemo_document_x()), Long.valueOf(memoDatabase.getMemo_document_y()));
            makeAndAddMarker(memoDatabase, savedDataLat);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //마커에서 데이터베이스 빼내옴
        //빼낸 데이터베이스를 바탕으로 레이아웃 수정 및 클릭 리스너 등록

        MemoDatabase markerDatabase = (MemoDatabase) marker.getTag();
        memoDetailLayoutInit(markerDatabase);

        this.memoDetail_bt.show();
        this.currentLocation_fab.hide();
        this.list_fab.hide();
        return true;

        //TODO 그냥 맵을 클릭한 경우 확인
    }

    public void memoDetailLayoutInit(MemoDatabase memoDatabase) {

        final MemoDatabase markerDatabase = memoDatabase;
        if (markerDatabase.getMemo_no() == -1) {
            this.memoInfo_ll.setVisibility(VISIBLE);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
            Date currentTime = new Date(System.currentTimeMillis());
            String dTime = formatter.format(currentTime);
            this.createdDate_tv.setText(dTime);
            String raw_category = markerDatabase.getMemo_document_category_group_code();
            this.category_tv.setText(TranscHash.rawToreFinedCategory(raw_category));
            this.memoName_tv.setText(markerDatabase.getMemo_document_place_name());
            //TODO 새로운 마커 추가될때 마커 컨텐츠에다가 새로운 메모입니다라는 내용 넣기
            this.memoContent_tv.setText("새로운 메모입니다");
            this.memoDetail_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), AddMemoActivity.class);
                    intent.putExtra("keywordDocument", new GsonBuilder().serializeNulls().create().toJson(markerDatabase));
                    intent.putExtra("Tag", Constant.MARKER_TAG_NEW);
                    startActivityForResult(intent, Constant.ADD_MEMO_INTENT);
                }
            });

        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
            Date currentTime = new Date(markerDatabase.getMemo_createDate());
            String dTime = formatter.format(currentTime);
            this.createdDate_tv.setText(dTime);
            String raw_category = markerDatabase.getMemo_document_category_group_code();
            this.category_tv.setText(TranscHash.rawToreFinedCategory(raw_category));
            this.memoName_tv.setText(markerDatabase.getMemo_document_place_name());
            this.memoContent_tv.setText(markerDatabase.getMemo_content());
            this.memoDetail_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, AddMemoActivity.class);
                    intent.putExtra("memo_no", markerDatabase.getMemo_no());
                    intent.putExtra("Tag", Constant.MARKER_TAG_SAVED);
                    startActivityForResult(intent, Constant.ADD_MEMO_INTENT);
                }
            });
            this.call_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (markerDatabase.getMemo_document_phone().equals("") || markerDatabase.getMemo_document_phone() == null)
                        Toast.makeText(HomeActivity.this, "연락처 정보가 등록되어 있지 않은 장소입니다", Toast.LENGTH_SHORT).show();
                    else {
                        String tel = "tel:" + markerDatabase.getMemo_document_phone();
                        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                    }
                }
            });
            this.share_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO 공유하기 기능 추가
                    Toast.makeText(HomeActivity.this, "shared!", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    public Marker makeAndAddMarker(MemoDatabase memoDatabase, LatLng latLng) {
        Marker newMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng));
        newMarker.setTag(memoDatabase);
        return newMarker;
    }

    public void setMyLocationEnable() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }else{
            if(mGoogleMap!=null){
                mGoogleMap.setMyLocationEnabled(true);
            }
        }

    }


    //글꼴 설정
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
//    }


}


