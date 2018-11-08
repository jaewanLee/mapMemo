package product.dp.io.mapmo.Util;

import android.util.Log;

import java.util.MissingFormatArgumentException;

import static product.dp.io.mapmo.Util.Constant.LOG_TAG;

/**
 * Created by jaewanlee on 2017. 8. 4..
 */

public class Logger {


    public static void v(String format, Object ...args) {
        String message;
        try {
            message = String.format(format, args);

        } catch (MissingFormatArgumentException e) {
            message = format;
        }

        //TODO restore airbridge
//        if (!AirBridge.isDebugMode) return;
        Log.v(LOG_TAG, message);
    }

    public static void d(String format, Object ...args) {
        String message;
        try {
            message = String.format(format, args);

        } catch (MissingFormatArgumentException e) {
            message = format;
        }

//        if (!AirBridge.isAppDebuggable) return;
        Log.d(LOG_TAG, message);
    }

    public static void i(String format, Object ...args) {
        String message;
        try {
            message = String.format(format, args);

        } catch (MissingFormatArgumentException e) {
            message = format;
        }
        Log.i(LOG_TAG, message);
    }

    public static void w(String message) {
        Log.w(LOG_TAG, message);
    }

    public static void w(String message, Throwable error) {


        Log.w(LOG_TAG, "Warning: " + message, error);
    }

    public static void e(String message, Throwable error) {

        Log.e(LOG_TAG, message, error);
    }

    public static void e(String message) {
    }

    /**
     * 이슈가 될 수 있는 중대한 에러를 로깅하고, 서버에 리포트한다.
     * 중대한 에러가 아니라면 {@link io.airbridge.internal.log.Logger#e} 혹은 {@link io.airbridge.internal.log.Logger#w}의 사용을 권고한다.
     * @param message 에러 메세지
     * @param error 에러
     */
    public static void wtf(String message, Throwable error) {
        try{
            Log.e(LOG_TAG, message, error);
        }
        catch (Exception e){
        }
    }

}
