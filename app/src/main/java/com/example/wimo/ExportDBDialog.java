package com.example.wimo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

/*
* 내보내기 다이얼로그 코드(추후 삭제 예정)
* */

public class ExportDBDialog extends Activity {
    private ExportDB exportDB;
    private Button btnCSV;
    private Button btnXLS;
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exportDB = new ExportDB(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_export_dialog);

        btnCSV = findViewById(R.id.btn_csv);
        btnCSV.setOnClickListener(onClickListener);

        btnXLS = findViewById(R.id.btn_xls);
        btnXLS.setOnClickListener(onClickListener);

        btnClose = findViewById(R.id.btn_close_export);
        btnClose.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_csv:
                    exportDB.exportDBtoCSV();
                    Toast.makeText(ExportDBDialog.this, "CSV 파일로 내보냈습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case R.id.btn_xls:
                    exportDB.exportDBtoXLS(ExportDBDialog.this);
                    Toast.makeText(ExportDBDialog.this, "XLS 파일로 내보냈습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case R.id.btn_close_export:
                    finish();
                    break;
            }
        }
    };
}