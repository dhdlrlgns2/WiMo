package com.example.wimo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

/*
 * 화면테마 동작관련 코드
 * */

public class AppTheme {
    private static final String TAG = "AppTheme";
    public static final String LIGHT_MODE = "light";
    public static final String DARK_MODE = "dark";
    public static final String DEFAULT_MODE = "default";

    // 화면테마에서 라이트, 다크 눌렀을 경우 동작하는 코드
    public static void applyTheme(String themeMode) {
        switch (themeMode) {
            case LIGHT_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Log.d(TAG, "Light Mode Applied.");
                break;

            case DARK_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Log.d(TAG, "Dark Mode Applied.");
                break;

            case DEFAULT_MODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    Log.d(TAG, "OS 10 UP / System Mode Applied.");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    Log.d(TAG, "OS 10 DOWN / System Mode Applied.");
                }
                break;
        }
    }

    // 선택된 화면테마 저장하는 기능
    public static void saveTheme(Context context, String selectMode) {
        SharedPreferences sp;
        sp = context.getSharedPreferences("mode", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("mode", selectMode);
        editor.apply();
    }

    // 앱 구동시 저장된 화면테마 받아오는 기능
    public static String loadTheme(Context context) {
        SharedPreferences sp;
        sp = context.getSharedPreferences("mode", Context.MODE_PRIVATE);
        return sp.getString("mode", "light");
    }
}