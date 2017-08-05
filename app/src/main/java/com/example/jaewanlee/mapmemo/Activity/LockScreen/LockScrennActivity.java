package com.example.jaewanlee.mapmemo.Activity.LockScreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.jaewanlee.mapmemo.R;

import io.airbridge.statistics.page.DontTrack;

/**
 * Created by jaewanlee on 2017. 8. 2..
 */

@DontTrack
public class LockScrennActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);


    }
}
