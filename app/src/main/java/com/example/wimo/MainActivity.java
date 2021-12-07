package com.example.wimo;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.wimo.activities.HostActivity;
import com.example.wimo.activities.MessageActivity;
import com.example.wimo.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    EditText edtName, edtNumber, edtNameResultm, edtNumberResult;
    Button btnInit, btnInsert, btnSelect;
    SQLiteDatabase sqlDB;

    SharedPreferences sp;
    String expType;

    private PrivacyInfoDB privacyInfoDB;
    private ExportDB exportDB;
    private GpsTracker gpsTracker;

    private Button btnPrivacy;
    private Button btnList;
    private Button btnDelList;
    private Button btnMap; // @Dev 맵이동 버튼
    private Button btnSettings;
    private Button btnMessage;
    private Button btnHost;

    private String themeMode;

    private TextView textLocation;
    private TextView textTime;

    private RecyclerView recyclerView;

    private List<PrivacyInfo> privacyInfoList = new ArrayList<>();

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    Button mRefreshBtn;
    private final String DEFAULT = "DEFAULT";

    public static Context mcontext;
    private PrivacyInfoDB.PrivacyInfoDBHelper dbHelper;
    private SQLiteDatabase db;
    public String[] a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        privacyInfoList.addAll(PrivacyInfoDB.getInstance().loadInfoDB());

        sp = getSharedPreferences("prefs", MODE_PRIVATE);
        exportDB = new ExportDB(this);

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        btnPrivacy = findViewById(R.id.btn_privacy);
        btnPrivacy.setOnClickListener(onClickListener);

        btnList = findViewById(R.id.btn_list);
        btnList.setOnClickListener(onClickListener);

        btnDelList = findViewById(R.id.btn_del_list);
        btnDelList.setOnClickListener(onClickListener);

        // @Dev
        btnMap = findViewById(R.id.btn_map);
        btnMap.setOnClickListener(onClickListener);

        btnSettings = findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(onClickListener);

        btnMessage = findViewById(R.id.btn_message);
        btnMessage.setOnClickListener(onClickListener);

        btnHost = findViewById(R.id.btn_host);
        btnHost.setOnClickListener(onClickListener);

        themeMode = AppTheme.loadTheme(getApplicationContext());
        AppTheme.applyTheme(themeMode);

        textTime = findViewById(R.id.text_time);
        textLocation = findViewById(R.id.text_location);

        recyclerView = findViewById(R.id.recycler_view);

        mcontext = this;

        dbHelper = new PrivacyInfoDB.PrivacyInfoDBHelper(this);
        db = dbHelper.getReadableDatabase();

        System.out.println("dbCheck 시작");
        String msql = "select address from message_info";
        Cursor cursor = db.rawQuery(msql, null);
        a = new String[cursor.getCount()];

        int i = 0;
        System.out.println("확진 while 시작");
        while (cursor.moveToNext()) {
            a[i] = cursor.getString(0);
            System.out.println(a[i]);
            i++;
        }
    }

    private String getTime() {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        //String getTime = mFormat.format(mDate);
        return mFormat.format(mDate);

    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.INVISIBLE);
        } else
            super.onBackPressed();
    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString();

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void savePrivacyInfoDB(String location, String latitude, String longitude, long currentTime) {
        PrivacyInfo info = new PrivacyInfo();
        info.setLocation(location);
        info.setTime(Utils.timeToDate(currentTime));
        info.setLat(latitude);
        info.setLon(longitude);

        PrivacyInfoDB.getInstance().insertInfoDB(info);
    }

    private void initRecyclerView() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PrivacyListAdapter adapter = new PrivacyListAdapter(privacyInfoList);
        recyclerView.setAdapter(adapter);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_privacy:
                    showSelectionDialog();

                    gpsTracker = new GpsTracker(MainActivity.this);

                    double latitude = gpsTracker.getLatitude();
                    double longitude = gpsTracker.getLongitude();

                    String address = getCurrentAddress(latitude, longitude);
                    long currentTime = System.currentTimeMillis();

                    textLocation.setText(address);
                    textTime.setText(Utils.timeToDate(currentTime));

                    savePrivacyInfoDB(address, latitude+"", longitude+"", currentTime);
                    break;

                case R.id.btn_list:
                    privacyInfoList = PrivacyInfoDB.getInstance().loadInfoDB();

                    if (privacyInfoList.isEmpty()) {
                        Toast.makeText(MainActivity.this, "저장된 기록이 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // @Dev : 기록확인 버튼을 눌렀을때, 현재 화면에서 History(기록확인) 화면으로 이동합니다.
                        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                    }
                    break;

                case R.id.btn_del_list:
                    PrivacyInfoDB.getInstance().clearTable();
                    privacyInfoList.clear();
                    Toast.makeText(MainActivity.this, "기록을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btn_map:
                    // @Dev : 전체위치 버튼을 눌렀을때, 현재 화면에서 Map(전체 위치) 화면으로 이동합니다.
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                    break;

                case R.id.btn_message:
                    startActivity(new Intent(MainActivity.this, MessageActivity.class));
                    break;

                case R.id.btn_host:
                    startActivity(new Intent(MainActivity.this, HostActivity.class));
                    break;

                case R.id.btn_settings: // 설정창으로 이동 (+재난문자 기능 포함)
                    startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                    break;

            }
        }
    };

    // DB 내보내기 기능 다이얼로그(설정 선택 기능 X)
    public void DialogClick(View view) {
        expType = sp.getString("expType", "CSV");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("내보내기").setMessage("기록을 파일로 내보내겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (expType.equals("CSV")) {
                    exportDB.exportDBtoCSV();
                    Toast.makeText(getApplicationContext(), "CSV 파일로 내보냈습니다.", Toast.LENGTH_SHORT).show();
                } else if (expType.equals("XLS")) {
                    exportDB.exportDBtoXLS(MainActivity.this);
                    Toast.makeText(getApplicationContext(), "XLS 파일로 내보냈습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("QR 코드 보기");
        builder.setMessage("QR 코드를 불러오시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nid.naver.com/login/privacyQR"));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, "브라우저를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
