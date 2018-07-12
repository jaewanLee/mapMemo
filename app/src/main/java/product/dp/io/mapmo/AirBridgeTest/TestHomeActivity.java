package product.dp.io.mapmo.AirBridgeTest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import product.dp.io.mapmo.AddMemoView.AddMemoActivity;
import product.dp.io.mapmo.Core.MainApplication;
import product.dp.io.mapmo.Database.MemoDatabase;
import product.dp.io.mapmo.Database.UserDatabase;
import product.dp.io.mapmo.KeywordSearchView.KeywordSearchActivity;
import product.dp.io.mapmo.MemoList.MemoListActivity;
import product.dp.io.mapmo.MemoList.MemoListDatabase;
import product.dp.io.mapmo.Menu.MenuActivity;
import product.dp.io.mapmo.R;
import product.dp.io.mapmo.Shared.NetworkManager;
import product.dp.io.mapmo.Util.Constant;
import product.dp.io.mapmo.Util.Logger;
import product.dp.io.mapmo.Util.TranscHash;

import static android.view.View.VISIBLE;
import static product.dp.io.mapmo.Util.Constant.MENU_INTENT;

/**
 * Created by jaewanlee on 2017. 10. 19..
 */

public class TestHomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    //상담 탭
    LinearLayout functionTool_LL;
    ImageButton back_ib;
    ImageButton menu_ib;
    EditText searchView_et;
    ImageButton erase_ib;
    ImageButton init_ib;

    //Map관련
    MapFragment mapFragment;
    GoogleMap mGoogleMap;
    Marker tempMarker;
    LocationManager locationManager;
    LocationListener locationListener;

    //메모 디테일 탭
    LinearLayout memoInfo_ll;
    TextView createdDate_tv;
    TextView category_tv;
    TextView memoName_tv;
    TextView memoContent_tv;
    ImageButton memoDetail_bt;
    ImageButton call_bt;
    ImageButton share_bt;

    //하단 탭
    LinearLayout bottom_ll;
    ImageButton memoList_ib;
    ImageView splitbar_iv;
    TextView simpleMemoTitle_tv;

    Realm realm;

    PermissionListener permissionListener;

    CircleProgressBar circleProgressBar;

    Boolean isPermissionGranted;
    SharedPreferences permissionShared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        manageABL();

    }

    private void initLayout() {
        functionTool_LL = (LinearLayout) findViewById(R.id.main_fuctionTool_LinearLayout);
        searchView_et = (EditText) findViewById(R.id.main_search_editText);
        menu_ib = (ImageButton) findViewById(R.id.main_menu_ImageButton);
        circleProgressBar = (CircleProgressBar) findViewById(R.id.main_progressbar);
        erase_ib = (ImageButton) findViewById(R.id.main_erase_ImageButton);
        memoInfo_ll = (LinearLayout) findViewById(R.id.main_memoInfo_LinearLayout);
        createdDate_tv = (TextView) findViewById(R.id.main_createDate_textView);
        category_tv = (TextView) findViewById(R.id.main_category_textView);
        memoName_tv = (TextView) findViewById(R.id.main_memoTitle_textView);
        memoContent_tv = (TextView) findViewById(R.id.main_memoContent_textView);
        memoDetail_bt = (ImageButton) findViewById(R.id.main_memoDetail_textView);
        memoDetail_bt.setVisibility(View.INVISIBLE);
        back_ib = (ImageButton) findViewById(R.id.main_back_ImageButton);
        init_ib = (ImageButton) findViewById(R.id.main_initSearch_ImageButton);
        bottom_ll = (LinearLayout) findViewById(R.id.main_bottom_linearlayout);
        memoList_ib = (ImageButton) findViewById(R.id.main_memoList_imageButton);
        splitbar_iv = (ImageView) findViewById(R.id.main_splitBar_ImgaeView);
        simpleMemoTitle_tv = (TextView) findViewById(R.id.main_simpleMemoTitle_TextView);
        call_bt = (ImageButton) findViewById(R.id.main_call_floatingbutton);
        call_bt.setVisibility(View.INVISIBLE);
        share_bt = (ImageButton) findViewById(R.id.main_share_floatingbutton);
        share_bt.setVisibility(View.INVISIBLE);

        memoInfo_ll.setVisibility(View.INVISIBLE);

        permissionShared = getSharedPreferences("permission", MODE_PRIVATE);
        isPermissionGranted = permissionShared.getBoolean("permission", false);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lon = location.getLongitude();
                double lat = location.getLatitude();
                LatLng currentLatLng = new LatLng(lat, lon);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                circleProgressBar.onAnimationEnd();
                circleProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                startActivity(new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                circleProgressBar.onAnimationEnd();
                circleProgressBar.setVisibility(View.INVISIBLE);
            }
        };

    }

    private void initCustomMap() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.main_map_replace);
        mapFragment.getMapAsync(this);

    }

    private void initButtonListener() {


        searchView_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                String searchKeyword = textView.getText().toString();

                Map<String,Object> eventValue=new HashMap<String,Object>();
                eventValue.put(AFInAppEventParameterName.SEARCH_STRING,searchKeyword);
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),AFInAppEventType.SEARCH,eventValue);

                Intent searchKeywordIntent = new Intent(TestHomeActivity.this, KeywordSearchActivity.class);
                searchKeywordIntent.putExtra("search_query", searchKeyword);
                startActivityForResult(searchKeywordIntent, Constant.SEARCH_QUERY_INTENT);
                return true;
            }
        });
        searchView_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_ib.setVisibility(View.INVISIBLE);
                back_ib.setVisibility(VISIBLE);
            }
        });

        searchView_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0)
                    erase_ib.setVisibility(VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //메뉴 버튼
        menu_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestHomeActivity.this, MenuActivity.class);
                startActivityForResult(intent, MENU_INTENT);
            }
        });

        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_ib.setVisibility(VISIBLE);
                back_ib.setVisibility(View.INVISIBLE);
                erase_ib.setVisibility(View.INVISIBLE);
                init_ib.setVisibility(View.INVISIBLE);
                searchView_et.setText("");
            }
        });

        erase_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 현재 적은 text의 내용과 아래 layout이 올라와 있따면 다지우기
                searchView_et.setText("");
            }
        });

        init_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView_et.setText("");
                init_ib.setVisibility(View.INVISIBLE);
            }
        });

        bottom_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                startActivity(intent);
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
                Toast.makeText(TestHomeActivity.this, "권한 설정 거부로 사용이 제한되었습니다", Toast.LENGTH_SHORT).show();
                finish();
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
            Toast.makeText(this, "GPS를 켜주세요",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        return false;
    }

    //권한설정 확인한 뒤 현재 위치 알아오기
    //TODO 구글로 다바꿔야함
    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new TedPermission(this)
                    .setPermissionListener(permissionListener)
                    .setDeniedMessage("If you reject permission,you can not use this service\\n\\nPlease turn on permissions at [Setting] > [Permission\n")
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .check();
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        Toast.makeText(this, String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60, 10, locationListener);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);

        circleProgressBar.setVisibility(VISIBLE);
        circleProgressBar.onAnimationStart();
    }
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
                LatLng tempLatLng = new LatLng(Double.valueOf(memoDatabase.getMemo_document_y()), Double.valueOf(memoDatabase.getMemo_document_x()));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(tempLatLng));
                if (!data.hasExtra("existed")) {
                    tempMarker = makeAndtempMarker(memoDatabase, tempLatLng);
                    onMarkerClick(tempMarker);
                    memoDetailLayoutInit(memoDatabase);
                } else if (data.getIntExtra("existed", -1) != -1) {
                    MemoDatabase existMemo = realm.where(MemoDatabase.class).equalTo("memo_no", data.getIntExtra("existed", -1)).findFirst();
                    memoDetailLayoutInit(existMemo);
                }
                menu_ib.setVisibility(VISIBLE);
                back_ib.setVisibility(View.INVISIBLE);
                erase_ib.setVisibility(View.INVISIBLE);
                init_ib.setVisibility(VISIBLE);

                this.memoDetail_bt.setVisibility(VISIBLE);
                this.call_bt.setVisibility(VISIBLE);
                this.share_bt.setVisibility(VISIBLE);

            } else {
                Toast.makeText(this, "통신오류가 발생하였습니다 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constant.ADD_MEMO_INTENT && resultCode == Constant.ADD_MEMO_INTENT) {
            if (data.hasExtra("addMemoResult")) {
                MemoDatabase newMemo = realm.where(MemoDatabase.class).equalTo("memo_no", data.getIntExtra("addMemoResult", 0)).findFirst();
                if (tempMarker != null) {
                    tempMarker.remove();
                }
                Marker addedMarker = makeAndAddMarker(newMemo, new LatLng(Double.valueOf(newMemo.getMemo_document_y()), Double.valueOf(newMemo.getMemo_document_x())));

                menu_ib.setVisibility(VISIBLE);
                back_ib.setVisibility(View.INVISIBLE);
                erase_ib.setVisibility(View.INVISIBLE);
                init_ib.setVisibility(VISIBLE);

                simpleMemoTitle_tv.setText(newMemo.getMemo_document_place_name());
                simpleMemoTitle_tv.setVisibility(VISIBLE);
                splitbar_iv.setVisibility(VISIBLE);
                bottom_ll.setVisibility(View.VISIBLE);

                memoDetailLayoutInit(newMemo);
                this.memoDetail_bt.setVisibility(View.INVISIBLE);
                this.call_bt.setVisibility(View.INVISIBLE);
                this.share_bt.setVisibility(View.INVISIBLE);
                memoInfo_ll.setVisibility(View.INVISIBLE);

                onMarkerClick(addedMarker);
            } else {
                Toast.makeText(this, "통신오류가 발생하였습니다 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constant.MENU_INTENT) {
            RealmResults realmResults = realm.where(MemoDatabase.class).findAll();
            if (realmResults.size() < 1) {

                mGoogleMap.clear();
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
        googleMap.setOnMapClickListener(this);
    }

    public void MemoDatabaseInit(GoogleMap googleMap) {
        //TODO 첫 데이터베이스 가져와서 그 위치 중심으로 where절 만들기

        RealmResults<MemoDatabase> memoDatabases = realm.where(MemoDatabase.class).findAll();
        for (MemoDatabase memoDatabase : memoDatabases) {
            LatLng savedDataLat = new LatLng(Double.valueOf(memoDatabase.getMemo_document_y()), Double.valueOf(memoDatabase.getMemo_document_x()));
            makeAndAddMarker(memoDatabase, savedDataLat);
        }

        LatLng lastMemoLocation = null;
        if (memoDatabases.size() > 0) {
            MemoDatabase lastMemo = memoDatabases.get(memoDatabases.size() - 1);
            lastMemoLocation = new LatLng(Double.valueOf(lastMemo.getMemo_document_y()), Double.valueOf(lastMemo.getMemo_document_x()));
            simpleMemoTitle_tv.setText(lastMemo.getMemo_document_place_name());
        } else {
            lastMemoLocation = new LatLng(37.525535, 126.896801);
            splitbar_iv.setVisibility(View.INVISIBLE);
            simpleMemoTitle_tv.setVisibility(View.INVISIBLE);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastMemoLocation, 16));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //마커에서 데이터베이스 빼내옴
        //빼낸 데이터베이스를 바탕으로 레이아웃 수정 및 클릭 리스너 등록

        MemoDatabase markerDatabase = (MemoDatabase) marker.getTag();
        memoDetailLayoutInit(markerDatabase);

        if (markerDatabase.getMemo_no() != -1 && markerDatabase.getMemo_no()!=-2) {
            this.memoDetail_bt.setImageResource(R.drawable.edit_memo_btn);
        } else {
            this.memoDetail_bt.setImageResource(R.drawable.add_memo_btn);
        }
        this.memoDetail_bt.setVisibility(VISIBLE);
        this.call_bt.setVisibility(VISIBLE);
        this.share_bt.setVisibility(VISIBLE);
        return true;

        //TODO 그냥 맵을 클릭한 경우 확인
    }


    public void memoDetailLayoutInit(final MemoDatabase memoDatabase) {

        final MemoDatabase markerDatabase = memoDatabase;
        if (markerDatabase.getMemo_no() == -1) {
            this.memoInfo_ll.setVisibility(VISIBLE);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);
            Date currentTime = new Date(System.currentTimeMillis());
            String dTime = formatter.format(currentTime);
            this.createdDate_tv.setText(dTime);
            String raw_category = markerDatabase.getMemo_document_category_group_code();
            if (raw_category.equals("")) {
                this.category_tv.setText("장소");
            } else {
                this.category_tv.setText(TranscHash.rawToreFinedCategory(raw_category));
                this.memoName_tv.setText(markerDatabase.getMemo_document_place_name());
            }
            //TODO 새로운 마커 추가될때 마커 컨텐츠에다가 새로운 메모입니다라는 내용 넣기
            this.memoContent_tv.setText("이곳에 새로운 메모를 해주세요.");
            this.memoDetail_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), AddMemoActivity.class);
                    intent.putExtra("keywordDocument", new GsonBuilder().serializeNulls().create().toJson(markerDatabase));
                    intent.putExtra("Tag", Constant.MARKER_TAG_NEW);
                    startActivityForResult(intent, Constant.ADD_MEMO_INTENT);
                }
            });
            share_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TestHomeActivity.this, "메모를 먼저 저장해 주세요", Toast.LENGTH_SHORT).show();
                }
            });
            call_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (markerDatabase.getMemo_document_phone().equals("") || markerDatabase.getMemo_document_phone() == null)
                        Toast.makeText(TestHomeActivity.this, "연락처 정보가 등록되어 있지 않은 장소입니다", Toast.LENGTH_SHORT).show();
                    else {
                        String tel = "tel:" + markerDatabase.getMemo_document_phone();
                        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                    }
                }
            });
//            bottom_ll.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
            memoInfo_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AddMemoActivity.class);
                    intent.putExtra("keywordDocument", new GsonBuilder().serializeNulls().create().toJson(markerDatabase));
                    intent.putExtra("Tag", Constant.MARKER_TAG_NEW);
                    startActivityForResult(intent, Constant.ADD_MEMO_INTENT);
                }
            });

        }
        //지도 클릭해서 만든 메모일 경우
        else if (memoDatabase.getMemo_no() == -2) {
            this.memoInfo_ll.setVisibility(VISIBLE);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);
            Date currentTime = new Date(System.currentTimeMillis());
            String dTime = formatter.format(currentTime);
            this.createdDate_tv.setText(dTime);
            String raw_category = markerDatabase.getMemo_document_category_group_code();
            if (raw_category.equals("")) {
                this.category_tv.setText("내가 찍은 장소");
            } else {
                this.category_tv.setText(TranscHash.rawToreFinedCategory(raw_category));
                this.memoName_tv.setText(markerDatabase.getMemo_document_place_name());
            }
            //TODO 새로운 마커 추가될때 마커 컨텐츠에다가 새로운 메모입니다라는 내용 넣기
            this.memoContent_tv.setText("이곳에 새로운 메모를 해주세요.");
            this.memoDetail_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), AddMemoActivity.class);
                    intent.putExtra("keywordDocument", new GsonBuilder().serializeNulls().create().toJson(markerDatabase));
                    intent.putExtra("Tag", Constant.MARKER_TAG_CUSTOM);
                    startActivityForResult(intent, Constant.ADD_MEMO_INTENT);
                }
            });
            share_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TestHomeActivity.this, "메모를 먼저 저장해 주세요", Toast.LENGTH_SHORT).show();
                }
            });
            call_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TestHomeActivity.this, "연락처 정보가 등록되어 있지 않은 장소입니다", Toast.LENGTH_SHORT).show();

                }
            });
//            bottom_ll.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
            memoInfo_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AddMemoActivity.class);
                    intent.putExtra("keywordDocument", new GsonBuilder().serializeNulls().create().toJson(markerDatabase));
                    intent.putExtra("Tag", Constant.MARKER_TAG_CUSTOM);
                    startActivityForResult(intent, Constant.ADD_MEMO_INTENT);
                }
            });
        } else {
            this.memoInfo_ll.setVisibility(VISIBLE);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);
            Date currentTime = new Date(markerDatabase.getMemo_createDate());
            String dTime = formatter.format(currentTime);
            this.createdDate_tv.setText(dTime);
            String raw_category = markerDatabase.getMemo_document_category_group_code();
            if (raw_category.equals("")) {
                this.category_tv.setText("장소");
            } else {
                this.category_tv.setText(TranscHash.rawToreFinedCategory(raw_category));
                this.memoName_tv.setText(markerDatabase.getMemo_document_place_name());
            }
            this.memoName_tv.setText(markerDatabase.getMemo_document_place_name());
            this.memoContent_tv.setText(markerDatabase.getMemo_content());
            memoInfo_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TestHomeActivity.this, AddMemoActivity.class);
                    intent.putExtra("memo_no", markerDatabase.getMemo_no());
                    intent.putExtra("Tag", Constant.MARKER_TAG_SAVED);
                    startActivityForResult(intent, Constant.ADD_MEMO_INTENT);
                }
            });
            this.memoDetail_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TestHomeActivity.this, AddMemoActivity.class);
                    intent.putExtra("memo_no", markerDatabase.getMemo_no());
                    intent.putExtra("Tag", Constant.MARKER_TAG_SAVED);
                    startActivityForResult(intent, Constant.ADD_MEMO_INTENT);
                }
            });
            this.call_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (markerDatabase.getMemo_document_phone().equals("") || markerDatabase.getMemo_document_phone() == null)
                        Toast.makeText(TestHomeActivity.this, "연락처 정보가 등록되어 있지 않은 장소입니다", Toast.LENGTH_SHORT).show();
                    else {
                        String tel = "tel:" + markerDatabase.getMemo_document_phone();
                        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                    }
                }
            });
            this.share_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 공유하기 기능 추가
                    final UserDatabase userDatabase = MainApplication.getMainApplicationInstance().getOnUserDatabase();
                    final String user_id=userDatabase.getUser_email();
                    if (!userDatabase.getUser_email().equals("guest") && userDatabase.getUser_email() != null) {
                        Logger.d("on HomeActivity shared action launch");
                        final String shared_memo_key = userDatabase.getUser_email() + System.currentTimeMillis();

                        final ArrayList<MemoListDatabase> requestValue = new ArrayList<>();

                        requestValue.add(new MemoListDatabase(markerDatabase));

                        NetworkManager networkManager = NetworkManager.getInstance();
                        OkHttpClient client = networkManager.getClient();
                        final HttpUrl.Builder builder = new HttpUrl.Builder();

                        builder.scheme("http");
                        builder.host("ec2-52-199-177-224.ap-northeast-1.compute.amazonaws.com");
                        builder.port(80);
                        builder.addPathSegment("mapmo");
                        builder.addPathSegment("memo");
                        builder.addPathSegment("share");
                        builder.addPathSegment("post.php");

                        String requestString = new Gson().toJson(requestValue);
                        //TODO myValue에다가 내 아이디랑 시간 써서 넣기
                        FormBody.Builder formBuilder = new FormBody.Builder()
                                .add("key", shared_memo_key)
                                .add("value", requestString);
                        RequestBody body = formBuilder.build();

                        final Request request = new Request.Builder()
                                .url(builder.build())
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(okhttp3.Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Logger.d("on HomeActivity client.newCall err");
                                        Toast.makeText(TestHomeActivity.this, "메모 공유 중 에러가 발생하였습니다 다시 시도해 주세", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                                final String result = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result.contains("200")) {
                                            Logger.d("response of sharing : " + result);
                                            Toast.makeText(TestHomeActivity.this, "메모 공유가 완료되었습니다", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Logger.d("response of sharing : request err 400");
                                            Toast.makeText(TestHomeActivity.this, "메모공유에 실패하였습니다 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                Bundle bundle=new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,"On HomeActivity");
                                bundle.putString(FirebaseAnalytics.Param.ITEM_ID,user_id);
                                MainApplication.getMainApplicationInstance().getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SHARE,bundle);


                                //이러고 나서 카카오쪽으로 보내기
                                String kakao_link_title = "";

                                for (MemoListDatabase requestMemoList : requestValue) {
                                    kakao_link_title = kakao_link_title + " # " + requestMemoList.getMemo_document_place_name();
                                }
                                sendCustomFeedTemplat(shared_memo_key, kakao_link_title);
//                                sendDefaultFeedTemplate(shared_memo_key, kakao_link_title);

                            }
                        });
                    } else
                        Toast.makeText(TestHomeActivity.this, "로그인을 해주세요", Toast.LENGTH_SHORT).show();
                }
            });
        }
//        bottom_ll.setVisibility(View.INVISIBLE);
    }

    private void sendDefaultFeedTemplate(String memo_no, String memo_titles) {
        //TODO templet 이름 및 이미지 바꾸기
        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("MapMo",
                        "http://ec2-52-199-177-224.ap-northeast-1.compute.amazonaws.com/mapmo/appstore_mapmo.png",
                        LinkObject.newBuilder()
                                .setAndroidExecutionParams("key=" + memo_no)
                                .build())
                        .setDescrption(memo_titles)
                        .build())
                .addButton(new ButtonObject("앱으로 보기", LinkObject.newBuilder()
                        .setAndroidExecutionParams("key=" + memo_no)
                        .build()))
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                com.kakao.util.helper.log.Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }

    private void sendCustomFeedTemplat(String memo_no, String memo_title) {
        String templateId = "6494";

        Map<String, String> templateArgs = new HashMap<String, String>();
        templateArgs.put("MemoTitle", memo_title);
        templateArgs.put("link_key", "key=" + memo_no);

        KakaoLinkService.getInstance().sendCustom(this, templateId, templateArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다.
            }
        });
    }

    public Marker makeAndAddMarker(MemoDatabase memoDatabase, LatLng latLng) {
        Marker newMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_memo_add)));
        newMarker.setTag(memoDatabase);
        return newMarker;
    }

    public Marker makeAndtempMarker(MemoDatabase memoDatabase, LatLng latLng) {
        Marker newMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_memo)));
        newMarker.setTag(memoDatabase);
        return newMarker;
    }

    public void setMyLocationEnable() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        } else {
            if (mGoogleMap != null) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {
        memoInfo_ll.setVisibility(View.INVISIBLE);
        this.memoDetail_bt.setVisibility(View.INVISIBLE);
        this.call_bt.setVisibility(View.INVISIBLE);
        this.share_bt.setVisibility(View.INVISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView_et.getWindowToken(), 0);
        back_ib.setVisibility(View.INVISIBLE);
        menu_ib.setVisibility(VISIBLE);
//        bottom_ll.setVisibility(VISIBLE);

        //찍은곳의 위경도 가자오고
        //이걸 주소값으로 변경시킨 다음에

        //TODO 내위치 메모 추가 내용
        if (tempMarker != null) {
            tempMarker.remove();
        }

        MemoDatabase clickedMemoDatabase = new MemoDatabase();
        clickedMemoDatabase.setMemo_document_x(String.valueOf(latLng.longitude));
        clickedMemoDatabase.setMemo_document_y(String.valueOf(latLng.latitude));
        clickedMemoDatabase.setMemo_no(-2);
        clickedMemoDatabase.setMemo_document_category_group_code("CM");
        clickedMemoDatabase.setMemo_document_place_name("새 메모");

        tempMarker = makeAndtempMarker(clickedMemoDatabase, latLng);

    }

    public void manageABL(){

        SharedPreferences firstTimeShared = getSharedPreferences("Config", MODE_PRIVATE);

        int myABLAmount = firstTimeShared.getInt("myABLAmount", 0);
        SharedPreferences.Editor editor = firstTimeShared.edit();
        int addedValue=(int)Math.random()*(30-5+1)+3;
        editor.putInt("myABLAmount", myABLAmount+addedValue);
        editor.commit();

    }



    //글꼴 설정
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
//    }


}


