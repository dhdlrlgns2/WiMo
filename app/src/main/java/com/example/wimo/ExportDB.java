package com.example.wimo;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * DB 내보내기 동작관련 코드
 * */

public class ExportDB {
    private final PrivacyInfoDB.PrivacyInfoDBHelper dbHelper;
    private SQLiteDatabase db;

    private static final String TAG = "ExportDB";

    private final File expFileDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
    private final String nowDateTime = DateTimeFormatter.ofPattern("yyMMdd_HHmmss_").format(LocalDateTime.now());
    private final String fileName = nowDateTime + "WIMO기록";

    public ExportDB(Context context) {
        dbHelper = new PrivacyInfoDB.PrivacyInfoDBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // CSV 파일 형식으로 내보내는 기능
    public void exportDBtoCSV() {
        File file = new File(expFileDir, fileName + ".csv");
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM privacy_info", null);
            csvWriter.writeNext(cursor.getColumnNames());
            while (cursor.moveToNext()) {
                String[] arrStr = {cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)};
                csvWriter.writeNext(arrStr);
            }
            csvWriter.close();
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    // XLS(엑셀) 파일 형식으로 내보내는 기능
    public void exportDBtoXLS(Context context) {
        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(context, "privacy.db", expFileDir.toString());
        sqliteToExcel.exportSingleTable("privacy_info", fileName + ".xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "Start exporting. ");
            }

            @Override
            public void onCompleted(String filePath) {
                Log.d(TAG, "Exporting to XLS done. File location: " + filePath);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }
}