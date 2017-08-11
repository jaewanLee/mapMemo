package com.example.jaewanlee.mapmemo.LockScreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.jaewanlee.mapmemo.Database.MemoDatabase;
import com.example.jaewanlee.mapmemo.KeywordSearchView.RecyclerViewDecoration;
import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.Logger;

import io.airbridge.statistics.page.DontTrack;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by jaewanlee on 2017. 8. 2..
 */

@DontTrack
public class LockScrennActivity extends AppCompatActivity {

    LocationManager locationManager;

    RecyclerView recyclerView;
    LockScreenAdapter lockScreenAdapter;
    RecyclerView.LayoutManager recyclerView_manager;

    double longtitude;
    double latitude;

    Realm realm;

    ImageButton deepLinkImageView;

    CalculateDistance calculateDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen);

        recyclerView = (RecyclerView) findViewById(R.id.lockscreen_mymemo_cardview);
        deepLinkImageView = (ImageButton) findViewById(R.id.lockscreen_deep_link);

        recyclerView.setHasFixedSize(true);
        recyclerView_manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerView_manager);
        lockScreenAdapter = new LockScreenAdapter(LockScrennActivity.this);
        recyclerView.setAdapter(lockScreenAdapter);
        recyclerView.addItemDecoration(new RecyclerViewDecoration(25));

        realm = Realm.getDefaultInstance();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        calculateDistance=new CalculateDistance();

//        RealmResults<MemoDatabase> memoDatabaseRealmResults = realm.where(MemoDatabase.class).findAll();
//        lockScreenAdapter.setData(memoDatabaseRealmResults);
//        lockScreenAdapter.notifyDataSetChanged();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "퍼미션 체크 안됬는데", Toast.LENGTH_SHORT).show();
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);

        deepLinkImageView.setClickable(true);
        deepLinkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("Enter in Lockscreen image");
                Toast.makeText(LockScrennActivity.this, "클릭잼", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("ablog://main");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(intent);
            }
        });

    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longtitude = location.getLongitude();
            latitude = location.getLatitude();
            calculateDistance.setCurrentLat(latitude);
            calculateDistance.setCurrentLon(longtitude);

            Toast.makeText(LockScrennActivity.this, "변경 잼", Toast.LENGTH_SHORT).show();

            RealmResults<MemoDatabase> memoDatabaseRealmResults = realm.where(MemoDatabase.class).findAll();
            for(MemoDatabase memoDatabase:memoDatabaseRealmResults){
                if(calculateDistance.calculate(Double.valueOf(memoDatabase.getMemo_document_y()),Double.valueOf(memoDatabase.getMemo_document_x()))){
                    lockScreenAdapter.addData(memoDatabase);
                }
            }
//            lockScreenAdapter.setData(memoDatabaseRealmResults);
            lockScreenAdapter.notifyDataSetChanged();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
