package com.example.wimo.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wimo.Map.TMap;
import com.example.wimo.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;

public class MapFragment extends Fragment {
    private LinearLayout linearLayoutTmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);

        linearLayoutTmap = viewGroup.findViewById(R.id.linearLayoutTmap);
        assert getArguments() != null;

        String address = getArguments().getString("address");

        TMap tMap = new TMap(getContext(), linearLayoutTmap);
        TMapData tmapdata = new TMapData();
        tmapdata.findAllPOI(address, poiItem -> {
            getActivity().runOnUiThread(() -> {


                if (poiItem.size() > 0) {
                    TMapPOIItem tMapPOIItem = poiItem.get(0);

                    Double lat = Double.parseDouble(tMapPOIItem.getPOIPoint().toString().split(" ")[1]);
                    Double lon = Double.parseDouble(tMapPOIItem.getPOIPoint().toString().split(" ")[3]);
                    tMap.createTMap(address, lat, lon);
                }
                else{
                    Toast.makeText(getContext(), "일치하는 주소가 없습니다.",Toast.LENGTH_SHORT).show();
                }
            });
        });

        return viewGroup;
    }
}
