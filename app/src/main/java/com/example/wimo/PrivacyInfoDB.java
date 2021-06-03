package com.example.wimo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class PrivacyInfoDB {
//    private static final String LOG = PrivacyInfoDB.class.getSimpleName();
    private static final String LOG = "seo";


    public static final String DB_TABLE_NAME_PRIVACY_INFO = "privacy_info";

    private static final String DB_COLUMN_NAME_ID = "id";

    private static final String DB_COLUMN_NAME_LOCATION = "location";
    private static final String DB_COLUMN_NAME_TIME = "time";
    private static final String DB_COLUMN_NAME_LAT = "lat";
    private static final String DB_COLUMN_NAME_LON = "lon";


    private static final String[] COLUMNS = new String[]
    {
            DB_COLUMN_NAME_ID,
            DB_COLUMN_NAME_LOCATION,
            DB_COLUMN_NAME_TIME,
            DB_COLUMN_NAME_LAT,
            DB_COLUMN_NAME_LON
    };

    private static final int DB_COLUMN_INDEX_ID = 0;
    private static final int DB_COLUMN_INDEX_LOCATION = DB_COLUMN_INDEX_ID + 1;
    private static final int DB_COLUMN_INDEX_TIME = DB_COLUMN_INDEX_LOCATION + 1;
    private static final int DB_COLUMN_INDEX_LAT = DB_COLUMN_INDEX_TIME + 1;
    private static final int DB_COLUMN_INDEX_LON = DB_COLUMN_INDEX_LAT + 1;


    private PrivacyInfoDBHelper dbHelper;
    private SQLiteDatabase db;

    public PrivacyInfoDB(Context context) {
        dbHelper = new PrivacyInfoDBHelper(context);
        db = dbHelper.getWritableDatabase();
    }


    public boolean insertInfoDB(PrivacyInfo info)
    {
        boolean isSaved = false;

        if(info != null)
        {
            ContentValues contentValues = putContentFromInfo(info);
            Log.i("seo", "insertInfoDB: contentValues.lat : " + contentValues.get("lat"));
            Log.i("seo", "insertInfoDB: contentValues.lon : " + contentValues.get("lon"));
            isSaved = insertToDB(DB_TABLE_NAME_PRIVACY_INFO, contentValues);
        }

        return isSaved;
    }

    public void clearTable() {
        db.delete(DB_TABLE_NAME_PRIVACY_INFO, null, null);
    }

    public ArrayList<PrivacyInfo> loadInfoDB()
    {
        ArrayList<PrivacyInfo> dailyFootInfoList = new ArrayList<>();

        Cursor cursor = null;

        try
        {
            cursor = db.query(DB_TABLE_NAME_PRIVACY_INFO, COLUMNS, null, null, null, null, null);

            if(cursor != null)
            {
                if (cursor.moveToFirst()) {

                    do {
                        PrivacyInfo info = new PrivacyInfo();

                        info.setLocation(cursor.getString(DB_COLUMN_INDEX_LOCATION));
                        info.setTime(cursor.getString(DB_COLUMN_INDEX_TIME));
                        info.setLat(cursor.getString(DB_COLUMN_INDEX_LAT));
                        info.setLon(cursor.getString(DB_COLUMN_INDEX_LON));

                        Log.i(LOG, "loadInfoDB: lat : " + cursor.getString(DB_COLUMN_INDEX_LAT));
                        Log.i(LOG, "loadInfoDB: lat : " + cursor.getString(DB_COLUMN_INDEX_LON));

                        dailyFootInfoList.add(info);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
        catch (Exception e)
        {
            if(cursor != null)
                cursor.close();

            Log.e(LOG, "getInfoDB() Exception : " + e.toString());
        }

        return dailyFootInfoList;
    }

    private ContentValues putContentFromInfo(PrivacyInfo info)
    {
        ContentValues values = new ContentValues();

        values.put(DB_COLUMN_NAME_LOCATION, info.getLocation());
        values.put(DB_COLUMN_NAME_TIME, info.getTime());
        values.put(DB_COLUMN_NAME_LAT, info.getLat());
        values.put(DB_COLUMN_NAME_LON, info.getLon());

        Log.e("seo", "==========> DB_COLUMN_NAME_LOCATION : " + info.getLocation());
        Log.e("seo", "==========> DB_COLUMN_NAME_TIME : " + info.getTime());
        Log.e("seo", "==========> DB_COLUMN_NAME_LAT : " + info.getLat());
        Log.e("seo", "==========> DB_COLUMN_NAME_LONE : " + info.getLon());

        return values;
    }

    private boolean insertToDB(String tableName, ContentValues contentValues)
    {
        boolean insertResult = false;

        Log.i("seo", "insertToDB: lat : " + contentValues.get("lat").toString());
        Log.i("seo", "insertToDB: lon : " + contentValues.get("lon").toString());


        if(tableName != null && contentValues != null)
        {
            if(db.insert(tableName, null, contentValues) != -1)
                insertResult = true;
        }

        return insertResult;
    }



    public static class PrivacyInfoDBHelper extends SQLiteOpenHelper
    {
        protected static final int DB_VER = 1;
        public static final String DB_NAME = "privacy.db";

        public static final String TABLE_FIELD_PRIVACY_INFO = "( " + DB_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                     DB_COLUMN_NAME_LOCATION + " STRING, " +
                                                                     DB_COLUMN_NAME_TIME + " STRING, " +
                                                                     DB_COLUMN_NAME_LAT + " STRING, " +
                                                                     DB_COLUMN_NAME_LON + " STRING" +
                                                              " )";

        public PrivacyInfoDBHelper(final Context context)
        {
            super(context, DB_NAME, null, DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase)
        {
            sqLiteDatabase.execSQL("CREATE TABLE " + DB_TABLE_NAME_PRIVACY_INFO + TABLE_FIELD_PRIVACY_INFO + ";");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
        {
        }
    }

}
