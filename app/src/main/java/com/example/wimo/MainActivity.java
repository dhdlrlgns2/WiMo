package com.example.wimo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtName, edtNumber, edtNameResultm, edtNumberResult;
    Button btnInit, btnInsert, btnSelect;
    SQLiteDatabase sqlDB;

    TextView mTextView;
    Button mRefreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);
        mRefreshBtn = (Button) findViewById(R.id.refreshBtn);
        mRefreshBtn.setOnClickListener(this);

    }
    private String getTime(){
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        //String getTime = mFormat.format(mDate);
        return mFormat.format(mDate);

    }


    public void onButton1Clicked(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nid.naver.com/login/privacyQR"));
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.refreshBtn:
                mTextView.setText(getTime());
                break;
            default:
                break;
        }
    }
}