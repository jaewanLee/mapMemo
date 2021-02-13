package product.dp.io.mapmo.Shared;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.airbridge.internal.log.Logger;
import okhttp3.OkHttpClient;

/**
 * Created by jaewanlee on 2017. 8. 6..
 */

public class NetworkManager {

    private static NetworkManager instance;

    OkHttpClient client;

    public static NetworkManager getInstance(){
        if(instance==null){
            instance=new NetworkManager();
        }
        return instance;
    }

    private NetworkManager(){
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(10,TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);

        client=builder.build();

    }

    public OkHttpClient getClient() {
        return client;
    }

    static volatile ExecutorService SERIAL_EXECUTOR;

    /**
     * 순차 처리 네트워킹
     * @param runnable
     */
    public static void runSerialTask(final Runnable runnable) {
        if (SERIAL_EXECUTOR == null) {
            SERIAL_EXECUTOR = Executors.newSingleThreadExecutor();
        }
        SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();

                } catch (Throwable e) {
                    Logger.wtf("Error occurred on NetwokrManager with RunSerialTask", e);
                }
            }
        });
    }


}
