package com.example.wimo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.wimo.data.HostInfo;
import com.example.wimo.data.MessageInfo;

import java.util.ArrayList;


public class PrivacyInfoDB {

    private static PrivacyInfoDB mPrivacyInfoDB;

    public static PrivacyInfoDB getInstance() {
        if (mPrivacyInfoDB == null) mPrivacyInfoDB = new PrivacyInfoDB(MyApp.getInstance());
        return mPrivacyInfoDB;
    }

    private static final String LOG = "seo";
    public static final String DB_TABLE_NAME_PRIVACY_INFO = "privacy_info";
    public static final String DB_TABLE_NAME_MESSAGE_INFO = "message_info";
    public static final String DB_TABLE_NAME_HOST_INFO = "host_info";

    private static final String DB_COLUMN_NAME_ID = "id";

    private static final String DB_COLUMN_NAME_LOCATION = "location";
    private static final String DB_COLUMN_NAME_TIME = "time";
    private static final String DB_COLUMN_NAME_LAT = "lat";
    private static final String DB_COLUMN_NAME_LON = "lon";
    private static final String DB_COLUMN_NAME_MEMO = "memo";

    private static final String DB_COLUMN_MESSAGE_ID = "message_id";
    private static final String DB_COLUMN_MESSAGE_ADDRESS = "address";

    private static final String DB_COLUMN_HOST_ID = "host_id";
    private static final String DB_COLUMN_HOST_ID_CODE = "id_code";

    private static final String[] COLUMNS = new String[]
    {
            DB_COLUMN_NAME_ID,
            DB_COLUMN_NAME_LOCATION,
            DB_COLUMN_NAME_TIME,
            DB_COLUMN_NAME_LAT,
            DB_COLUMN_NAME_LON,
            DB_COLUMN_NAME_MEMO
    };

    private static final String[] COLUMNS_MESSAGE = new String[]{ DB_COLUMN_MESSAGE_ID, DB_COLUMN_MESSAGE_ADDRESS };
    private static final String[] COLUMNS_HOST = new String[]{ DB_COLUMN_HOST_ID, DB_COLUMN_HOST_ID_CODE };

    private static final int DB_COLUMN_INDEX_ID = 0;
    private static final int DB_COLUMN_INDEX_LOCATION = DB_COLUMN_INDEX_ID + 1;
    private static final int DB_COLUMN_INDEX_TIME = DB_COLUMN_INDEX_LOCATION + 1;
    private static final int DB_COLUMN_INDEX_LAT = DB_COLUMN_INDEX_TIME + 1;
    private static final int DB_COLUMN_INDEX_LON = DB_COLUMN_INDEX_LAT + 1;
    private static final int DB_COLUMN_INDEX_MEMO = DB_COLUMN_INDEX_LON + 1;

    private static final int DB_COLUMN_INDEX_MESSAGE_ID = 0;
    private static final int DB_COLUMN_INDEX_MESSAGE_ADDRESS = DB_COLUMN_INDEX_MESSAGE_ID + 1;

    private static final int DB_COLUMN_INDEX_HOST_ID = 0;
    private static final int DB_COLUMN_INDEX_HOST_ID_CODE = DB_COLUMN_INDEX_HOST_ID + 1;

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

    public boolean updateInfoDB(PrivacyInfo info)
    {
        boolean isSaved = false;

        if(info != null)
        {
            ContentValues contentValues = putContentFromInfo(info);
            String selection = DB_COLUMN_NAME_ID + "=" + info.getId();
            isSaved = updateValues(DB_TABLE_NAME_PRIVACY_INFO, contentValues, selection, null);
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

                        info.setId(cursor.getInt(DB_COLUMN_INDEX_ID));
                        info.setLocation(cursor.getString(DB_COLUMN_INDEX_LOCATION));
                        info.setTime(cursor.getString(DB_COLUMN_INDEX_TIME));
                        info.setLat(cursor.getString(DB_COLUMN_INDEX_LAT));
                        info.setLon(cursor.getString(DB_COLUMN_INDEX_LON));
                        info.setMemo(cursor.getString(DB_COLUMN_INDEX_MEMO));

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
        values.put(DB_COLUMN_NAME_MEMO, info.getMemo());

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

    private boolean updateValues(String tableName, ContentValues contentValues, String selection, String[] selectionArgs)
    {
        boolean updateResult = false;
        if(tableName != null && contentValues != null )
        {
            if(db.update(tableName, contentValues, selection, selectionArgs) != -1)
                updateResult = true;
        }

        return updateResult;
    }

    public long insertMessage(MessageInfo messageInfo) {
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_MESSAGE_ADDRESS, messageInfo.getAddress());
        return db.insert(DB_TABLE_NAME_MESSAGE_INFO, null, values);
    }

    public boolean updateMessage(MessageInfo messageInfo) {
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_MESSAGE_ADDRESS, messageInfo.getAddress());

        String selection = DB_COLUMN_MESSAGE_ID + "=" + messageInfo.getId();

        int numOfRows = db.update(DB_TABLE_NAME_MESSAGE_INFO, values, selection, null);
        return numOfRows > 0;
    }

    public boolean deleteMessage(MessageInfo messageInfo) {
        String selection = DB_COLUMN_MESSAGE_ID + "=" + messageInfo.getId();
        int numOfRows = db.delete(DB_TABLE_NAME_MESSAGE_INFO, selection, null);
        return numOfRows > 0;
    }

    public ArrayList<MessageInfo> getMessages() {
        ArrayList<MessageInfo> messages = new ArrayList<>();

        Cursor cursor = null;

        try {
            cursor = db.query(DB_TABLE_NAME_MESSAGE_INFO, COLUMNS_MESSAGE, null, null, null, null, null);

            if(cursor != null) {
                if (cursor.moveToFirst()) {

                    do {
                        MessageInfo info = new MessageInfo();
                        info.setId(cursor.getLong(DB_COLUMN_INDEX_MESSAGE_ID));
                        info.setAddress(cursor.getString(DB_COLUMN_INDEX_MESSAGE_ADDRESS));
                        messages.add(info);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            if(cursor != null)
                cursor.close();
        }

        return messages;
    }

    public long insertHost(HostInfo hostInfo) {
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_HOST_ID_CODE, hostInfo.getIdCode());
        return db.insert(DB_TABLE_NAME_HOST_INFO, null, values);
    }

    public boolean updateHost(HostInfo hostInfo) {
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_HOST_ID_CODE, hostInfo.getIdCode());

        String selection = DB_COLUMN_HOST_ID + "=" + hostInfo.getId();

        int numOfRows = db.update(DB_TABLE_NAME_HOST_INFO, values, selection, null);
        return numOfRows > 0;
    }

    public boolean deleteHost(HostInfo hostInfo) {
        String selection = DB_COLUMN_HOST_ID + "=" + hostInfo.getId();
        int numOfRows = db.delete(DB_TABLE_NAME_HOST_INFO, selection, null);
        return numOfRows > 0;
    }

    public ArrayList<HostInfo> getHost() {
        ArrayList<HostInfo> hosts = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = db.query(DB_TABLE_NAME_HOST_INFO, COLUMNS_HOST, null, null, null, null, null);

            if(cursor != null) {
                if (cursor.moveToFirst()) {

                    do {
                        HostInfo info = new HostInfo();
                        info.setId(cursor.getLong(DB_COLUMN_INDEX_HOST_ID));
                        info.setIdCode(cursor.getString(DB_COLUMN_INDEX_HOST_ID_CODE));
                        hosts.add(info);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            if(cursor != null)
                cursor.close();
        }
        return hosts;
    }

    public static class PrivacyInfoDBHelper extends SQLiteOpenHelper
    {
        protected static final int DB_VER = 3;
        public static final String DB_NAME = "privacy.db";

        public static final String TABLE_FIELD_PRIVACY_INFO = "( " + DB_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                     DB_COLUMN_NAME_LOCATION + " STRING, " +
                                                                     DB_COLUMN_NAME_TIME + " STRING, " +
                                                                     DB_COLUMN_NAME_LAT + " STRING, " +
                                                                     DB_COLUMN_NAME_LON + " STRING, " +
                                                                     DB_COLUMN_NAME_MEMO + " STRING" +
                                                              " )";


        private static final String ALTER_TABLE_NAME_PRIVACY_INFO_TO_V2 =
                "ALTER TABLE " + DB_TABLE_NAME_PRIVACY_INFO + " ADD COLUMN " + DB_COLUMN_NAME_MEMO + " TEXT;";

        public static final String CREATE_TABLE_MESSAGE_INFO =
                "CREATE TABLE " + DB_TABLE_NAME_MESSAGE_INFO + "( " + DB_COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DB_COLUMN_MESSAGE_ADDRESS + " TEXT" + " );";

        public static final String CREATE_TABLE_HOST_INFO =
                "CREATE TABLE " + DB_TABLE_NAME_HOST_INFO + "( " + DB_COLUMN_HOST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DB_COLUMN_HOST_ID_CODE + " TEXT" + " );";


        public PrivacyInfoDBHelper(final Context context)
        {
            super(context, DB_NAME, null, DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase)
        {
            sqLiteDatabase.execSQL("CREATE TABLE " + DB_TABLE_NAME_PRIVACY_INFO + TABLE_FIELD_PRIVACY_INFO + ";");
            sqLiteDatabase.execSQL(CREATE_TABLE_MESSAGE_INFO);
            sqLiteDatabase.execSQL(CREATE_TABLE_HOST_INFO);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
        {
            if (oldVersion == 1) {
                sqLiteDatabase.execSQL(ALTER_TABLE_NAME_PRIVACY_INFO_TO_V2);
                sqLiteDatabase.execSQL(CREATE_TABLE_MESSAGE_INFO);
                sqLiteDatabase.execSQL(CREATE_TABLE_HOST_INFO);
            }
            if (oldVersion == 2) {
                sqLiteDatabase.execSQL(CREATE_TABLE_MESSAGE_INFO);
                sqLiteDatabase.execSQL(CREATE_TABLE_HOST_INFO);
            }
        }
    }

}
