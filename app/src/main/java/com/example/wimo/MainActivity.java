package com.example.wimo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    EditText edtName, edtNumber, edtNameResultm, edtNumberResult;
    Button btnInit, btnInsert, btnSelect;
    SQLiteDatabase sqlDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onButton1Clicked(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nid.naver.com/login/privacyQR"));
        startActivity(intent);

    }


    long currentMillsec = System.currentTimeMillis();
    Date curentDate = new Date(currentMillsec);
    SimpleDateFormat currnetTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String currentTime = currnetTimeFormat .format(curentDate);




}