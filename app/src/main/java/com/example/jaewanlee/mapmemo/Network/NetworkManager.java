package com.example.jaewanlee.mapmemo.Network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.airbridge.internal.log.Logger;

/**
 * Created by jaewanlee on 2017. 8. 6..
 */

public class NetworkManager {

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
