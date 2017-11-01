package product.dp.io.mapmo.LockScreen.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import product.dp.io.mapmo.LockScreen.BoradcastReceiver.RestartReceiver;
import product.dp.io.mapmo.R;

/**
 * Created by jaewanlee on 2017. 8. 2..
 */
public class ScreenService extends Service {




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerRestartAlarm(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

//        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification.Builder mBuilder = new Notification.Builder(this);
//        BitmapDrawable drawable=(BitmapDrawable)getResources().getDrawable(R.drawable.launchicon);
//        Bitmap bitmap=drawable.getBitmap();
//        mBuilder.setLargeIcon(bitmap);
//        mBuilder.setWhen(System.currentTimeMillis());
//        mBuilder.setContentTitle("Notification.Builder Title");
//        mBuilder.setContentText("Notification.Builder Massage");
//        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
//        mBuilder.setContentIntent(pendingIntent);
//        mBuilder.setAutoCancel(true);
//        nm.notify(111, mBuilder.build());
//

        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.launchicon) // notification icon
                .setContentTitle("MemoTitle") // title for notification
                .setContentText("Memo Context") // message for notification
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
        Notification notification=mBuilder.build();
        startForeground(111,notification);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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
