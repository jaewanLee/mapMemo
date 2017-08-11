package com.example.jaewanlee.mapmemo.HomeView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jaewanlee.mapmemo.AddMemoView.AddMemoActivity;
import com.example.jaewanlee.mapmemo.Database.MemoDatabase;
import com.example.jaewanlee.mapmemo.KeywordSearchView.KeywordSearchActivity;
import com.example.jaewanlee.mapmemo.LockScreen.Service.ScreenService;
import com.example.jaewanlee.mapmemo.Map.CustomMapView;
import com.example.jaewanlee.mapmemo.Map.CustomMarker;
import com.example.jaewanlee.mapmemo.Map.KeywordSearchRepo;
import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.tsengvn.typekit.TypekitContextWrapper;

import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;

import io.realm.Realm;

import static com.example.jaewanlee.mapmemo.Util.Constant.ADD_MEMO_INTENT;
import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_NEW;
import static com.example.jaewanlee.mapmemo.Util.Constant.SEARCH_QUERY_INTENT;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    android.support.v7.widget.SearchView searchView;
    CustomMapView customMapView;
    LinearLayout markerDetailLayout;
    TextView markerTitle;
    TextView markerCategory;
    TextView markerAddr;
    TextView markerPhone;
    TextView markerUrl;

    ImageButton markerDetailImageButton;
    CustomMarker tempCustomMarker;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

               Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //퍼미션 설정

        PermissionListener permissionListener=new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "permission Denied", Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\\n\\nPlease turn on permissions at [Setting] > [Permission\n")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();

        realm = Realm.getDefaultInstance();

        markerDetailLayout = (LinearLayout) findViewById(R.id.main_map_marker_detail_linearLayout);
//        markerDetailLayout.setVisibility(View.INVISIBLE);

        markerTitle = (TextView) findViewById(R.id.main_map_name_text);
        markerCategory = (TextView) findViewById(R.id.main_map_category_text);
        markerAddr = (TextView) findViewById(R.id.main_map_addr_text);
        markerPhone = (TextView) findViewById(R.id.main_map_phone_text);
        markerUrl = (TextView) findViewById(R.id.main_map_url_text);
        markerDetailImageButton = (ImageButton) findViewById(R.id.main_map_add_memo_imageButton);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.drawer_lock_screen).setActionView(new Switch(this));

        ((Switch) navigationView.getMenu().findItem(R.id.drawer_lock_screen).getActionView()).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                    startService(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                    stopService(intent);
                }
            }
        });

        //다음 지도 초기화
        customMapView = new CustomMapView(this, markerDetailLayout);
        customMapView.setMarkerTitle(markerTitle);
        customMapView.setMarkerCategory(markerCategory);
        customMapView.setMarkerAddr(markerAddr);
        customMapView.setMarkerPhone(markerPhone);
        customMapView.setMarkerUrl(markerUrl);
        customMapView.setImageButton(markerDetailImageButton);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.main_map_replace);
        mapViewContainer.addView(customMapView);

        markerDetailImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "마커 추가 이벤트", Toast.LENGTH_SHORT).show();

                if (customMapView.getCurrentMarker().getTag() == MARKER_TAG_NEW) {
                    Intent intent = new Intent(MainActivity.this, AddMemoActivity.class);
                    intent.putExtra("keywordDocument", new GsonBuilder().serializeNulls().create().toJson(customMapView.getCurrentMarker().getKeywordDocuments()));
                    intent.putExtra("Tag", MARKER_TAG_NEW);
                    if (tempCustomMarker != null) {
                        customMapView.removePOIItem(tempCustomMarker);
                    }
                    startActivityForResult(intent, ADD_MEMO_INTENT);
                } else {
                    Intent intent = new Intent(MainActivity.this, AddMemoActivity.class);
                    intent.putExtra("Tag", customMapView.getCurrentMarker().getTag());
                    intent.putExtra("memo_no", customMapView.getCurrentMarker().getMemoDatabase().getMemo_no());
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        customMapView.onMapViewInitialized(customMapView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("지번 도 주소 입력하세요");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent searchKeywordIntent = new Intent(MainActivity.this, KeywordSearchActivity.class);
                searchKeywordIntent.putExtra("search_query", query);
                startActivityForResult(searchKeywordIntent, SEARCH_QUERY_INTENT);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Logger.d("on QueryText Change");
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handㅏle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_location_base) {
            Intent intent = new Intent(getApplicationContext(), LocationBaseActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.drawer_time_base) {
            Intent intent = new Intent(getApplicationContext(), TimeBaseActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.drawer_setting) {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.drawer_faq) {
            Intent intent = new Intent(getApplicationContext(), FAQActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.drawer_access_terms) {
            Intent intent = new Intent(getApplicationContext(), AccessTermActivity.class);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(intent);
            finish();

        } else if (id == R.id.drawer_lock_screen) {
            boolean status = ((Switch) item.getActionView()).isChecked();
            ((Switch) item.getActionView()).setChecked(!status);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_QUERY_INTENT && resultCode == SEARCH_QUERY_INTENT && data.hasExtra("searchResult")) {
            if (requestCode == SEARCH_QUERY_INTENT && data.hasExtra("searchResult")) {
                KeywordSearchRepo.KeywordDocuments searchResultDocument = new Gson().fromJson(data.getStringExtra("searchResult"), KeywordSearchRepo.KeywordDocuments.class);
                customMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.valueOf(searchResultDocument.getY()), Double.valueOf(searchResultDocument.getX())), true);
                tempCustomMarker = new CustomMarker(searchResultDocument, MARKER_TAG_NEW);
                customMapView.addPOIItem(tempCustomMarker);
            }
        } else if (requestCode == ADD_MEMO_INTENT) {
            if (resultCode == ADD_MEMO_INTENT && data.hasExtra("addMemoResult")) {
                MemoDatabase newMemo = realm.where(MemoDatabase.class).equalTo("memo_no", data.getIntExtra("addMemoResult", 0)).findFirst();
                customMapView.addPOIItem(new CustomMarker(newMemo, newMemo.getMemo_category()));
            }
        } else {
            Toast.makeText(this, "통신오류가 발생하였습니다 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
