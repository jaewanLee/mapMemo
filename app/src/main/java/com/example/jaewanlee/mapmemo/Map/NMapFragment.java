package com.example.jaewanlee.mapmemo.Map;

/**
 * Created by jaewanlee on 2017. 8. 3..
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.Logger;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapView;

/**
 * NMapFragment 클래스는 NMapActivity를 상속하지 않고 NMapView만 사용하고자 하는 경우에 NMapContext를 이용한 예제임.
 * NMapView 사용시 필요한 초기화 및 리스너 등록은 NMapActivity 사용시와 동일함.
 */
public class NMapFragment extends Fragment {

    private NMapContext mMapContext;
    protected NMapView nMapView;
    private static final String CLIENT_ID = "Y2MW2vmMCF__Ei9HmPke";// 애플리케이션 클라이언트 아이디 값

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.naver_map_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapContext = new NMapContext(super.getActivity());
        mMapContext.onCreate();
        Logger.d("NMapFragment onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nMapView = (NMapView) getView().findViewById(R.id.mapView);
        nMapView.setClientId(CLIENT_ID);// 클라이언트 아이디 설정
        mMapContext.setupMapView(nMapView);
        Logger.d("NMapFragment onActivityCreated");

    }

    protected NMapView getNMapView() {
        if (nMapView != null) {
            return this.nMapView;
        } else return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapContext.onStart();
        Logger.d("NMapFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapContext.onResume();
        Logger.d("NMapFragment onResume");
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapContext.onPause();
        Logger.d("NMapFragment onPause");
    }

    @Override
    public void onStop() {
        mMapContext.onStop();
        super.onStop();
        Logger.d("NMapFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d("NMapFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        mMapContext.onDestroy();
        super.onDestroy();
        Logger.d("NMapFragment onDestroy");
    }

}
