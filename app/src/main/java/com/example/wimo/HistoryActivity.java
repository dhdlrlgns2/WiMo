package com.example.wimo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.wimo.Fragment.CalendarFragment;
import com.example.wimo.Fragment.ListFragment;
import com.example.wimo.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private final ArrayList<PrivacyInfo> mPrivacyInfoList = new ArrayList<>();
    public ArrayList<PrivacyInfo> getPrivacyInfoList() {
        return mPrivacyInfoList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mPrivacyInfoList.addAll(PrivacyInfoDB.getInstance().loadInfoDB());

        BottomNavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onNavigationMenuSelected(item);
            }
        });

        if (savedInstanceState == null) navigationView.setSelectedItemId(R.id.search);
    }

    private boolean onNavigationMenuSelected(MenuItem item) {
        boolean result = true;
        int id = item.getItemId();
        if (id == R.id.search) {
            replaceFragment(new SearchFragment());
        } else if (id == R.id.calendar) {
            replaceFragment(new CalendarFragment());
        } else if (id == R.id.list) {
            replaceFragment(new ListFragment());
        } else {
            result = false;
        }
        return result;
    }

    public void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void updateDB(PrivacyInfo info) {
        PrivacyInfoDB.getInstance().updateInfoDB(info);
    }
}