package com.example.wimo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;

/*
* PreferenceFragment로 구현한 설정창 내부 생성하는 코드
* */

public class SettingsPreferenceFragment extends PreferenceFragment {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context context;

    ListPreference alertAreaPreference;
    PreferenceScreen alertScreen;

    ListPreference exportPreference;
    ListPreference themePreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        prefs = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        editor = prefs.edit();
        addPreferencesFromResource(R.xml.settings_preference);
        
        // 설정창 내부 리스트 구현 코드
        alertAreaPreference = (ListPreference) findPreference("alert_area_list");
        alertScreen = (PreferenceScreen) findPreference("alert_screen");

        exportPreference = (ListPreference) findPreference("export_list");
        themePreference = (ListPreference) findPreference("theme_list");

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (!prefs.getString("alert_area_list", "").equals("")) {
            alertAreaPreference.setSummary(prefs.getString("alert_area_list", "서울"));
        }

        if (!prefs.getBoolean("alert", false)) {
            alertScreen.setSummary("사용안함");
        }

        if (!prefs.getString("export_list", "").equals("")) {
            exportPreference.setSummary(prefs.getString("export_list", "CSV"));
        }

        if (!prefs.getString("theme_list", "").equals("")) {
            themePreference.setSummary(prefs.getString("theme_list", "Light"));
        }

        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }
    
    // 내부 설정 변경되었을 경우 동작 코드
    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("alert_area_list")) {
                alertAreaPreference.setSummary(prefs.getString("alert_area_list", "서울"));
                editor.putString("alertArea",prefs.getString("alert_area_list","")); // 추후 서버 리퀘스트에 사용
                editor.apply();
            }

            if (key.equals("alert")) {
                if (prefs.getBoolean("alert", false)) {
                    alertScreen.setSummary("사용함");
                    editor.putBoolean("alertTF", true); //알림 유무 저장해서 VisitPlaceCheck 에서 확인 추후 서버 리퀘스트 할지말지
                    ((SettingActivity)SettingActivity.mcontext).changeSetting(true);
                } else {
                    alertScreen.setSummary("사용안함");
                    editor.putBoolean("alertTF", false); //알림 유무 저장해서 VisitPlaceCheck 에서 확인 추후 서버 리퀘스트 할지말지
                    ((SettingActivity)SettingActivity.mcontext).changeSetting(false);
                }
                //2뎁스 PreferenceScreen 내부에서 발생한 환경설정 내용을 2뎁스 PreferenceScreen에 적용하기 위한 코드
                ((BaseAdapter) getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
                editor.apply();//알림 유무 저장
            }

            if (key.equals("export_list")) {
                exportPreference.setSummary(prefs.getString("export_list", "CSV"));
                editor.putString("expType", prefs.getString("export_list", ""));
                editor.apply();
            }

            if (key.equals("theme_list")) {
                themePreference.setSummary(prefs.getString("theme_list", "Light"));
                if (prefs.getString("theme_list", "").equals("Light")) {
                    AppTheme.applyTheme("light");
                    AppTheme.saveTheme(context, "light");
                }
                if (prefs.getString("theme_list", "").equals("Dark")) {
                    AppTheme.applyTheme("dark");
                    AppTheme.saveTheme(context, "dark");
                }
            }
        }
    };
}
