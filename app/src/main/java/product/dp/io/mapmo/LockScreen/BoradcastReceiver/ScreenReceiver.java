package product.dp.io.mapmo.LockScreen.BoradcastReceiver;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import product.dp.io.mapmo.LockScreen.LockScreenActivity;

/**
 * Created by jaewanlee on 2017. 8. 2..
 */


public class ScreenReceiver extends BroadcastReceiver {

    private KeyguardManager keyguardManager=null;
    private KeyguardManager.KeyguardLock keyguardLock=null;
    private TelephonyManager telephonyManager = null;
    private boolean isPhoneIdle = true;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            if(keyguardManager==null){
                keyguardManager=(KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
            }
            if(keyguardLock==null){
                keyguardLock=keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
            }

            if(telephonyManager == null){
                telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            }


            disableKeyguard();

            Intent i = new Intent(context, LockScreenActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }

    public void reenableKeyguard() {
        keyguardLock.reenableKeyguard();
    }

    public void disableKeyguard() {
        keyguardLock.disableKeyguard();
    }

    private PhoneStateListener phoneListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber){
            switch(state){
                case TelephonyManager.CALL_STATE_IDLE :
                    isPhoneIdle = true;
                    break;
                case TelephonyManager.CALL_STATE_RINGING :
                    isPhoneIdle = false;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK :
                    isPhoneIdle = false;
                    break;
            }
        }
    };

}