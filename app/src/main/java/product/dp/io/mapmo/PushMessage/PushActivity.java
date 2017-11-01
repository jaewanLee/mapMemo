package product.dp.io.mapmo.PushMessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import product.dp.io.mapmo.HomeView.HomeActivity;
import product.dp.io.mapmo.R;

/**
 * Created by jaewanlee on 2017. 11. 1..
 */

public class PushActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder mBuilder = new Notification.Builder(this);
        BitmapDrawable drawable=(BitmapDrawable)getResources().getDrawable(R.drawable.launchicon);
        Bitmap bitmap=drawable.getBitmap();
        mBuilder.setLargeIcon(bitmap);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setContentTitle("Notification.Builder Title");
        mBuilder.setContentText("Notification.Builder Massage");
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);

        nm.notify(111, mBuilder.build());

        finish();
    }


}
