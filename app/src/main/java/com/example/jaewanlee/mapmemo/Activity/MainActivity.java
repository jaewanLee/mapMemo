package com.example.jaewanlee.mapmemo.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.Switch;
import android.widget.Toast;

import com.example.jaewanlee.mapmemo.Core.MainApplication;
import com.example.jaewanlee.mapmemo.Core.Service.ScreenService;
import com.example.jaewanlee.mapmemo.Map.CustomMapView;
import com.example.jaewanlee.mapmemo.Map.CustomMarker;
import com.example.jaewanlee.mapmemo.Network.KeywordSearchInterface;
import com.example.jaewanlee.mapmemo.Network.KeywordSearchRepo;
import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.Logger;
import com.tsengvn.typekit.TypekitContextWrapper;

import net.daum.mf.map.api.MapPoint;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.jaewanlee.mapmemo.Util.Constant.MARKER_TAG_DEFAULT;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    android.support.v7.widget.SearchView searchView;
    CustomMapView customMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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
        customMapView=new CustomMapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.main_map_replace);
        mapViewContainer.addView(customMapView);
        initMap();
//        SearchLocation searchLocation=SearchLocation.getInstance();
//        Logger.d(searchLocation.run("영등포구청 탐앤탐스"));

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
                Logger.d("on QuesryText Submit");
                MainApplication mainApplication=(MainApplication)getApplication();
                KeywordSearchInterface keywordSearchInterface = mainApplication.getKeywordSearchInterface();
                Call<KeywordSearchRepo> call=keywordSearchInterface.getKeywordSearchRepo(query);
                call.enqueue(new Callback<KeywordSearchRepo>() {
                    @Override
                    public void onResponse(Call<KeywordSearchRepo> call, Response<KeywordSearchRepo> response) {
                        if(response.isSuccessful()){
                            Logger.d(response.body().getKeywordDocuments().get(0).getPlace_name());
                            KeywordSearchRepo.KeywordDocuments keywordDocuments=response.body().getKeywordDocuments().get(0);
                            Logger.d("X: "+keywordDocuments.getX()+" Y : "+keywordDocuments.getY());
                            customMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.valueOf(keywordDocuments.getY()),Double.valueOf(keywordDocuments.getX())),false);

                        }
                        else{
                            Logger.d(response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<KeywordSearchRepo> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "에러가 발생하였습니다 잠시후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                    }
                });

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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

    private void initMap(){
        //저장되어 있는 마커 추가
        CustomMarker customMarker=new CustomMarker("테스트 마커",MARKER_TAG_DEFAULT, MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633));
        customMapView.addPOIItem(customMarker);
    }

}
