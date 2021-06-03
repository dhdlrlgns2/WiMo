package com.example.wimo.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.wimo.PrivacyInfo;
import com.example.wimo.R;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TMap {
    private Context context;
    private double lat;
    private double lon;
    private LinearLayout view;
    TMapView tMapView;

    /**
     * TMap()
     *  객체 초기화를할때 여러가지 속성을 설정합니다
     *  1. 위도, 경도가 입력이 안되면 오류가 뜨므로, 아무 위도,경도를 일단 넣는다 (현재 보신각 근처의 위도,경도로 작성되어있음)
     *  2. view : 실제로 map을 그릴 레이아웃입니다.
     *  3. tmapView : 실제로 tMap을 그리기 위한 객체입니다.
     *  4. API Key 설정:  setSKTMapApiKey()함수를 통해 api Key를 설정합니다.
     *          지금 쓰고 있는 키는 제가 사용중인 Api Key 입니다.
     */

    public TMap(Context context, LinearLayout view) {
        this.lat = 37.570841;
        this.lon = 126.985302;
        this.view = view;
        this.context = context;

        tMapView =  new TMapView(context);
        tMapView.setSKTMapApiKey("l7xxf1ae5e123bf94ba0a636baaf24928eac");   // API 키

    }

    /**
     * getMarker()
     * 마커 정보를 리턴합니다.
     * 이름과, 위도 , 경도를 파라메타로 받아서
     * 마커의 이미지, 마커의 위치, 마커의 이름 등등을 통해 설정한후 , 이 마커객체를 반환합니다.
     * 이 마커는 tMap을 설정할때 쓰입니다. (전체 위치를 보는 Map에서 쓰임)
     */
    private TMapMarkerItem getMarker(String name, double lat, double lon){
        TMapMarkerItem markerItem = new TMapMarkerItem();
        TMapPoint tMapPoint = new TMapPoint(lat, lon);

        // 마커 설정
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.poi_dot);
        markerItem.setIcon(bitmap);                            // 마커 아이콘 지정
        markerItem.setPosition(0.5f, 1.0f);            // 마커의 중심점을 중앙, 하단으로 설정
        markerItem.setTMapPoint( tMapPoint );                  // 마커의 좌표 지정
        markerItem.setName("123132");                                 // 마커의 타이틀 지정
        markerItem.setCalloutTitle(name);
        markerItem.setEnableClustering(true);

        return markerItem;
    }

    /**
     * getMarker2()
     * 마커2 정보를 리턴합니다.
     * 마커의 종류가 두가지가 있는데, 하나는 일반 핀이고,
     * 하나는 Custom을 할수 있게 제공합니다.
     *
     * 비슷하게 이름, 위도,경도등등을 받지만
     * 여기서는 핀 대신 시간값을 텍스트로 보이는 말풍선을 리턴하도록 되어있습니다 (위치확인 화면의 tMap에 쓰임)
     */
    private MarkerOverlay getMarker2(String name, double lat, double lon){
        MarkerOverlay marker = new MarkerOverlay(this.context, name, "");

        marker.setPosition(0.2f,0.2f);
        marker.getTMapPoint();
        marker.setID(name);
        marker.setIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.poi_dot));
        marker.setTMapPoint(new TMapPoint(lat, lon));

        return marker;
    }


    /**
     * createTMap()
     * 이름, 위도, 경도를 전달받아 실제로 tMap을 그립니다.
     * 이건 "기록확인"에서 쓰입니다.
     */
    public void createTMap(String name, double lat, double lon){
        this.view.addView( tMapView );
        tMapView.addMarkerItem("markerItem1", getMarker(name, lat, lon));
        tMapView.setCenterPoint( lon, lat );   // 맵 가운데 좌표
    }

    /**
     * createTMap()
     * DB에 저장되어있는 Row들을 입력받아 tMap을 그립니다.
     * 여러개(Arraylist)의 입력을 받아 처리하므로, for문을 돌면서 하나하나 마커(시간 말풍선)을 찍습니다.
     * 이건 "전체위치"에서 쓰입니다.
     */
    public void createTMap(ArrayList<PrivacyInfo> datas){
        tMapView.setZoomLevel(7);
        tMapView.setEnableClustering(true);
        this.view.addView( tMapView );
        for (PrivacyInfo data : datas) {
            String time = data.getTime();
            double lat = Double.parseDouble(data.getLat());
            double lon = Double.parseDouble(data.getLon());
            tMapView.addMarkerItem2(time, getMarker2(time, lat, lon));
        }

        tMapView.setCenterPoint( 127.85510, 36.72770 );   // 맵 가운데 좌표
    }
}
