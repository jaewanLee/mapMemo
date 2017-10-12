package product.dp.io.ab180blog.LockScreen;

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

import io.airbridge.statistics.page.DontTrack;
import io.realm.Realm;
import io.realm.RealmResults;
import product.dp.io.ab180blog.Database.MemoDatabase;
import product.dp.io.ab180blog.KeywordSearchView.RecyclerViewDecoration;
import product.dp.io.ab180blog.Util.Logger;

/**
 * Created by jaewanlee on 2017. 8. 2..
 */

@DontTrack
public class LockScreenActivity extends AppCompatActivity {

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
        setContentView(product.dp.io.ab180blog.R.layout.activity_lockscreen);

        recyclerView = (RecyclerView) findViewById(product.dp.io.ab180blog.R.id.lockscreen_mymemo_cardview);
        deepLinkImageView = (ImageButton) findViewById(product.dp.io.ab180blog.R.id.lockscreen_deep_link);

        recyclerView.setHasFixedSize(true);
        recyclerView_manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerView_manager);
        lockScreenAdapter = new LockScreenAdapter(LockScreenActivity.this);
        recyclerView.setAdapter(lockScreenAdapter);
        recyclerView.addItemDecoration(new RecyclerViewDecoration(25));

        realm = Realm.getDefaultInstance();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        calculateDistance = new CalculateDistance();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO 펴미션 체크가 안되어 있다면 다시 퍼미션 체크 하기
            Toast.makeText(this, "퍼미션 체크 안됬는데", Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 1000, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);

        deepLinkImageView.setClickable(true);
        deepLinkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("Enter in Lockscreen image");
                Toast.makeText(LockScreenActivity.this, "클릭잼", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(LockScreenActivity.this, "변경 잼", Toast.LENGTH_SHORT).show();

            RealmResults<MemoDatabase> memoDatabaseRealmResults = realm.where(MemoDatabase.class).findAll();
            for (MemoDatabase memoDatabase : memoDatabaseRealmResults) {
                if (calculateDistance.calculate(Double.valueOf(memoDatabase.getMemo_document_y()), Double.valueOf(memoDatabase.getMemo_document_x()))) {
                    lockScreenAdapter.addData(memoDatabase);
                }
            }
            lockScreenAdapter.notifyDataSetChanged();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

            Logger.d("onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String s) {
            Logger.d("onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String s) {
            Logger.d("onProviderDisabled");
        }
    };
}
