package com.example.wimo.Fragment;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.wimo.Map.TMap;
import com.example.wimo.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;


public class DialogMap extends AppCompatDialogFragment {

    public static DialogMap newInstance(String address) {
        DialogMap dialog = new DialogMap();
        Bundle bundle = new Bundle();
        bundle.putString("address", address);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(AppCompatDialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout linearLayoutTmap = view.findViewById(R.id.linearLayoutTmap);
        assert getArguments() != null;
        String address = getArguments().getString("address");

        TMap tMap = new TMap(getContext(), linearLayoutTmap);
        TMapData tmapdata = new TMapData();

        tmapdata.findAllPOI(address, poiItem -> {
            if (getActivity() != null) getActivity().runOnUiThread(() -> {

                if (poiItem.size() > 0) {
                    TMapPOIItem tMapPOIItem = poiItem.get(0);

                    double lat = Double.parseDouble(tMapPOIItem.getPOIPoint().toString().split(" ")[1]);
                    double lon = Double.parseDouble(tMapPOIItem.getPOIPoint().toString().split(" ")[3]);

                    tMap.createTMap(address, lat, lon);
                }
                else{
                    Toast.makeText(getContext(), "일치하는 주소가 없습니다.",Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getContext() != null) {
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getContext().getApplicationContext().getTheme();
                theme.resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true);
                int color = typedValue.data;
                window.setStatusBarColor(color);
            }
        }
    }


}
