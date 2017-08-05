package com.example.jaewanlee.mapmemo.Map;

import android.os.Bundle;

import com.example.jaewanlee.mapmemo.R;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;

public class TestActivity extends NMapActivity {

    String LOG_TAG="NAVER MAP ERROR";
    NMapController nMapController;
    NMapView nMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        nMapView=new NMapView(this);
        setContentView(nMapView);
        nMapView.setClientId("Y2MW2vmMCF__Ei9HmPke");
        nMapView.setClickable(true);
        nMapView.setEnabled(true);
    }

}
