package com.example.wimo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.wimo.Map.TMap;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {
    private PrivacyInfoDB privacyInfoDB;
    private LinearLayout linearLayoutTmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        linearLayoutTmap = findViewById(R.id.linearLayoutTmap);

        // SQLite DB Load
        privacyInfoDB = new PrivacyInfoDB(this);

        final ArrayList<PrivacyInfo> privacyInfos = privacyInfoDB.loadInfoDB();

        final TMap tMap = new TMap(getApplicationContext(), linearLayoutTmap);

        tMap.createTMap(privacyInfos);
    }
}