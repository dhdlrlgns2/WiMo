package com.example.wimo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

/*
* 화면테마 다이얼로그 코드(추후 삭제 예정)
* */

public class AppThemeDialog extends Activity {
    private Button btnLight;
    private Button btnDark;
    private Button btnClose;
    private String themeMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_theme_dialog);

        btnLight = findViewById(R.id.btn_light);
        btnLight.setOnClickListener(onClickListener);

        btnDark = findViewById(R.id.btn_dark);
        btnDark.setOnClickListener(onClickListener);

        btnClose = findViewById(R.id.btn_close_theme);
        btnClose.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_light:
                    themeMode = AppTheme.LIGHT_MODE;
                    AppTheme.applyTheme(themeMode);
                    AppTheme.saveTheme(getApplicationContext(), themeMode);
                    Toast.makeText(AppThemeDialog.this, "라이트모드가 적용되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case R.id.btn_dark:
                    themeMode = AppTheme.DARK_MODE;
                    AppTheme.applyTheme(themeMode);
                    AppTheme.saveTheme(getApplicationContext(), themeMode);
                    Toast.makeText(AppThemeDialog.this, "다크모드가 적용되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case R.id.btn_close_theme:
                    finish();
                    break;
            }
        }
    };
}