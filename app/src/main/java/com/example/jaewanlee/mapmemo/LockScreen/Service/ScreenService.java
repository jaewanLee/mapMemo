package com.example.jaewanlee.mapmemo.LockScreen.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.jaewanlee.mapmemo.LockScreen.BoradcastReceiver.RestartReceiver;
import com.example.jaewanlee.mapmemo.LockScreen.BoradcastReceiver.ScreenReceiver;
import com.example.jaewanlee.mapmemo.R;

/**
 * Created by jaewanlee on 2017. 8. 2..
 */
public class ScreenService extends Service {


    private ScreenReceiver screenRecevier = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        screenRecevier = new ScreenReceiver();
        IntentFilter intentFilte = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenRecevier, intentFilte);

        registerRestartAlarm(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_action_name) // notification icon
                .setContentTitle("Notification!") // title for notification
                .setContentText("Hello word") // message for notification
                .setAutoCancel(true);
        Notification notification=mBuilder.build();
        startForeground(1,notification);

        if (intent != null) {
            if (intent.getAction() == null) {
                if (screenRecevier == null) {
                    screenRecevier = new ScreenReceiver();
                    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                    registerReceiver(screenRecevier, intentFilter);
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(screenRecevier!=null){
            unregisterReceiver(screenRecevier);
        }

        registerRestartAlarm(false);
    }

    public void registerRestartAlarm(boolean isOn){

        Intent intent = new Intent(ScreenService.this, RestartReceiver.class);
        intent.setAction(RestartReceiver.ACTION_RESTART_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        if(isOn){
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, 300000, sender);
        }else{
            am.cancel(sender);
        }
    }


}
