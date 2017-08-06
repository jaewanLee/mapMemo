package com.example.jaewanlee.mapmemo.Map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.Logger;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;

/**
 * Created by jaewanlee on 2017. 8. 4..
 */

public class NMapCustomFragment extends NMapFragment  implements NMapView.OnMapStateChangeListener{


    NMapController nMapController;
    Context context;

    public NMapCustomFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View baseView=inflater.inflate(R.layout.naver_map_fragment, container, false);
//        NMapView nMapView=(NMapView)baseView.findViewById(R.id.mapView);
//        nMapView.setClientId("Y2MW2vmMCF__Ei9HmPke");
//        nMapView.setClickable(true);
//        nMapView.setOnMapStateChangeListener(this);
        Logger.d("NMapCustomFragment onCreatedView");
        return baseView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.d("NMapCustomFragment onActivityCreated");
        this.nMapView.setOnMapStateChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
        if(nMapError==null){
            //TODO 최근에 저장한 메모 또는 현재 위치 근처로 옮기기
            nMapView.setClickable(true);
            nMapView.setBuiltInZoomControls(true,null);
            nMapController=nMapView.getMapController();
            nMapController.setMapCenter(new NGeoPoint(126.9885253,37.4755809),13);
        }else{
            Logger.e("Naver Map Error : "+nMapError.toString());
        }

    }

    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {
        //TODO 여기서 위도경도 받아서 메모내용 페이징하기

    }

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {

    }

    @Override
    public void onZoomLevelChange(NMapView nMapView, int i) {

    }

    @Override
    public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

    }
}
