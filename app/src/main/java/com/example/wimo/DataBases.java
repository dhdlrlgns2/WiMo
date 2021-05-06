package com.example.wimo;

import android.location.Location;
import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns{
        public static final String TIME = "time";
        public static final String LOCATION = "location";
        public static final String _TABLENAME = "wimo";
        public static final String _CREATE =
                "create table " + _TABLENAME+"("
                +_ID+" integer primary key autoincrement, "
                +TIME+" text not null , "
                + LOCATION+"text not null);";



    }

}
