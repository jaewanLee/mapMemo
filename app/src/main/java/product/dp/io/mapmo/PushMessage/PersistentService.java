package product.dp.io.mapmo.PushMessage;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmResults;
import product.dp.io.mapmo.Database.MemoDatabase;
import product.dp.io.mapmo.HomeView.HomeActivity;
import product.dp.io.mapmo.LockScreen.CalculateDistance;
import product.dp.io.mapmo.R;
import product.dp.io.mapmo.Util.Logger;

/**
 * Created by jaewanlee on 2017. 11. 1..
 */

public class PersistentService extends Service {

    private static final int MILLISINFUTURE = 1000 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;

    private CountDownTimer countDownTimer;

    double longtitude;
    double latitude;
    CalculateDistance calculateDistance;

    Realm realm;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        unregisterRestartAlarm();
        super.onCreate();
        realm = Realm.getDefaultInstance();

        Logger.d("service create");
        initData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("service onStartCommand");
        int notification_id = 0;
        /**
         * startForeground 를 사용하면 notification 을 보여주어야 하는데 없애기 위한 코드
         */
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.launchicon)
                .setContentTitle("")
                .setContentText("")
                .build();
        startForeground(notification_id, notification);

        nm.notify(notification_id, notification);
        nm.cancel(notification_id);
        Logger.d("default notification deleted");

        //location Manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        calculateDistance = new CalculateDistance();
        Boolean isNetworEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworEnable) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this, "정상적인 사용을 위해 권한설정을 해주세요", Toast.LENGTH_SHORT).show();

            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*20, 1000, locationListener);
            Logger.d("location Listener on");
        }

        return super.onStartCommand(intent, flags, startId);

    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Logger.d("location change");
            longtitude = location.getLongitude();
            latitude = location.getLatitude();
            calculateDistance.setCurrentLat(latitude);
            calculateDistance.setCurrentLon(longtitude);

            RealmResults<MemoDatabase> memoDatabaseRealmResults = realm.where(MemoDatabase.class).findAll();
            TreeMap<Double, MemoDatabase> memoHashMap = new TreeMap<>();
            for (MemoDatabase memoDatabase : memoDatabaseRealmResults) {
                Double distance = calculateDistance.calculate(Double.valueOf(memoDatabase.getMemo_document_y()), Double.valueOf(memoDatabase.getMemo_document_x()));
                if (distance < 10) {
                    memoHashMap.put(distance, memoDatabase);
                }
            }
            if (memoHashMap.size() > 0) {
                Logger.d("notification on");
                Double closetValue = memoHashMap.keySet().iterator().next();
                MemoDatabase closeMemo = memoHashMap.get(closetValue);

                Bitmap launchicon = BitmapFactory.decodeResource(getResources(), R.drawable.launchicon);
                Intent notificationIntent = new Intent(getApplicationContext(), HomeActivity.class);
                notificationIntent.putExtra("notification_memo_x", closeMemo.getMemo_document_x());
                notificationIntent.putExtra("notification_memo_y", closeMemo.getMemo_document_y());
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification =
                        new Notification.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.launchicon)
                                .setContentTitle(closeMemo.getMemo_document_place_name())
                                .setContentText(closeMemo.getMemo_content())
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .build();

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
            }
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.d("service destory");
        Log.i("PersistentService", "onDestroy");
        countDownTimer.cancel();

        /**
         * 서비스 종료 시 알람 등록을 통해 서비스 재 실행
         */
        registerRestartAlarm();
    }

    @Override
    public boolean stopService(Intent name) {

        return super.stopService(name);

    }

    /**
     * 데이터 초기화
     */
    private void initData() {

        countDownTimer();
        countDownTimer.start();
    }

    public void countDownTimer() {

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {

                Log.i("PersistentService", "onTick");
            }

            public void onFinish() {

                Log.i("PersistentService", "onFinish");
            }
        };
    }


    /**
     * 알람 매니져에 서비스 등록
     */
    private void registerRestartAlarm() {

        Log.i("000 PersistentService", "registerRestartAlarm");
        Intent intent = new Intent(PersistentService.this, RestartService.class);
        intent.setAction("ACTION.RESTART.PersistentService");
        PendingIntent sender = PendingIntent.getBroadcast(PersistentService.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 1 * 1000;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        /**
         * 알람 등록
         */
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1 * 1000, sender);

    }

    /**
     * 알람 매니져에 서비스 해제
     */
    private void unregisterRestartAlarm() {

        Log.i("000 PersistentService", "unregisterRestartAlarm");

        Intent intent = new Intent(PersistentService.this, RestartService.class);
        intent.setAction("ACTION.RESTART.PersistentService");
        PendingIntent sender = PendingIntent.getBroadcast(PersistentService.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        /**
         * 알람 취소
         */
        alarmManager.cancel(sender);


    }


}