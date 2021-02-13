package product.dp.io.mapmo.LockScreen.BoradcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import product.dp.io.mapmo.LockScreen.Service.ScreenService;

/**
 * Created by jaewanlee on 2017. 8. 2..
 */
public class RestartReceiver extends BroadcastReceiver {
    static public final String ACTION_RESTART_SERVICE = "RestartReceiver.restart";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_RESTART_SERVICE)){
            Intent i = new Intent(context, ScreenService.class);
            context.startService(i);
        }
    }

}