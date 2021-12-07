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

    public TMap(Context context, LinearLayout view) {
        this.lat = 37.570841;
        this.lon = 126.985302;
        this.view = view;
        this.context = context;

        tMapView =  new TMapView(context);
        tMapView.setSKTMapApiKey("l7xxf1ae5e123bf94ba0a636baaf24928eac");

    }

    private TMapMarkerItem getMarker(String name, double lat, double lon){
        TMapMarkerItem markerItem = new TMapMarkerItem();
        TMapPoint tMapPoint = new TMapPoint(lat, lon);


        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.poi_dot);
        markerItem.setIcon(bitmap);
        markerItem.setPosition(0.5f, 1.0f);
        markerItem.setTMapPoint( tMapPoint );
        markerItem.setName("123132");
        markerItem.setCalloutTitle(name);
        markerItem.setEnableClustering(true);

        return markerItem;
    }

    private MarkerOverlay getMarker2(String name, double lat, double lon){
        MarkerOverlay marker = new MarkerOverlay(this.context, name, "");

        marker.setPosition(0.2f,0.2f);
        marker.getTMapPoint();
        marker.setID(name);
        marker.setIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.poi_dot));
        marker.setTMapPoint(new TMapPoint(lat, lon));

        return marker;
    }

    public void createTMap(String name, double lat, double lon){
        this.view.addView( tMapView );
        tMapView.addMarkerItem("markerItem1", getMarker(name, lat, lon));
        tMapView.setCenterPoint( lon, lat );
    }

    public void createTMap(ArrayList<PrivacyInfo> datas){
        tMapView.setZoomLevel(7);
        tMapView.setEnableClustering(true);
        this.view.addView( tMapView );
        for (PrivacyInfo data : datas) {
            String time = data.getTime();
            double lat = Double.parseDouble(data.getLat());
            double lon = Double.parseDouble(data.getLon());

            tMapView.addMarkerItem2("TMapMarkerItem2", getMarker2(time, lat, lon));
        }

        tMapView.setCenterPoint( 127.85510, 36.72770 );
    }
}
