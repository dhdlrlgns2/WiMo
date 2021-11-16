package com.example.wimo;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
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


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    EditText edtName, edtNumber, edtNameResultm, edtNumberResult;
    Button btnInit, btnInsert, btnSelect;
    SQLiteDatabase sqlDB;

    private PrivacyInfoDB privacyInfoDB;

    private GpsTracker gpsTracker;

    private Button btnPrivacy;
    private Button btnList;
    private Button btnDelList;
    private Button btnMap; // @Dev 맵이동 버튼
    private Button btnExport;
    private Button btnTheme;

    private String themeMode;

    //네비게이션 추가 코드
    private Toolbar toolbar;//네비게이션 드로어 추가 코드
    private DrawerLayout drawerLayout;//네비게이션 드로어 추가 코드
    private NavigationView navigationView;//네비게이션 드로어 추가 코드
    private FragmentManager fragmentManager = getSupportFragmentManager();//하단 네비게이션 추가 코드

    private TextView textLocation;
    private TextView textTime;

    private RecyclerView recyclerView;

    private List<PrivacyInfo> privacyInfoList = new ArrayList<>();

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    Button mRefreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ex_activity_main);

        //Button imageButton = (Button) findViewById(R.id.btn1);

        privacyInfoDB = new PrivacyInfoDB(this);

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
        //새로운 레이아웃 적용 위해서 잠시 비활성화
        /*
        btnPrivacy = findViewById(R.id.btn_privacy);
        btnPrivacy.setOnClickListener(onClickListener);

        btnList = findViewById(R.id.btn_list);
        btnList.setOnClickListener(onClickListener);

        btnDelList = findViewById(R.id.btn_del_list);
        btnDelList.setOnClickListener(onClickListener);

        // @Dev
        btnMap = findViewById(R.id.btn_map);
        btnMap.setOnClickListener(onClickListener);

        btnExport = findViewById(R.id.btn_export);
        btnExport.setOnClickListener(onClickListener);

        themeMode = AppTheme.loadTheme(getApplicationContext());
        AppTheme.applyTheme(themeMode);
        btnTheme = findViewById(R.id.btn_theme);
        btnTheme.setOnClickListener(onClickListener);

        textTime = findViewById(R.id.text_time);
        textLocation = findViewById(R.id.text_location);
*/
        recyclerView = findViewById(R.id.recycler_view);

        //하단 네비게이션 추가 코드
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        //네비게이션 드로어 추가 코드
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); //왼쪽 상단 버튼 아이콘 지정

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        navigationView = (NavigationView)findViewById(R.id.navigationView);
        // 네비게이션 드로어 추가 코드
        /*this.InitializeLayout();
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.item_list:
                        Toast.makeText(getApplicationContext(), "기록확인 선택", Toast.LENGTH_SHORT).show();
                    case R.id.item_del_list:
                        Toast.makeText(getApplicationContext(), "기록삭제 선택", Toast.LENGTH_SHORT).show();
                    case R.id.item_map:
                        Toast.makeText(getApplicationContext(), "전체위치 선택", Toast.LENGTH_SHORT).show();
                    case R.id.item_export:
                        Toast.makeText(getApplicationContext(), "내보내기 선택", Toast.LENGTH_SHORT).show();
                    case R.id.item_qr_generator:
                        Toast.makeText(getApplicationContext(), "개인QR생성 선택", Toast.LENGTH_SHORT).show();
                    case R.id.item_setting:
                        Toast.makeText(getApplicationContext(), "설정 선택", Toast.LENGTH_SHORT).show();
                }
                drawerLayout = findViewById(R.id.drawerLayout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });*/

    }
    //네비게이션 드로어 추가 코드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { // 왼쪽 상단 버튼 눌렀을 때
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //네비게이션 드로어 추가 코드
   /* public void InitializeLayout()
    {
        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 메뉴버튼 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); // 메뉴 버튼 모양 설정
    }*/


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
        //네비게이션 드로어 추가 코드
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        return address.getAddressLine(0).toString() + "\n";

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

    private void savePrivacyInfoDB(String location, String latitude, String longitude) {
        PrivacyInfo info = new PrivacyInfo();
        info.setLocation(location);
        info.setTime(getTime());
        info.setLat(latitude);
        info.setLon(longitude);

        privacyInfoDB.insertInfoDB(info);
    }

    private void initRecyclerView() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PrivacyListAdapter adapter = new PrivacyListAdapter(privacyInfoList);
        recyclerView.setAdapter(adapter);
    }

    //하단 네비게이션 추가 코드
    private class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.item_home:
                    Toast.makeText(getApplicationContext(), "홈 선택", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.item_calendar:
                    Toast.makeText(getApplicationContext(), "캘린더 선택", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.item_qrcode:r:
                Toast.makeText(getApplicationContext(), "QR코드 선택", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_privacy:
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nid.naver.com/login/privacyQR"));
                    startActivity(intent);

                    gpsTracker = new GpsTracker(MainActivity.this);

                    double latitude = gpsTracker.getLatitude();
                    double longitude = gpsTracker.getLongitude();

                    String address = getCurrentAddress(latitude, longitude);

                    textLocation.setText(address);
                    textTime.setText(getTime());

                    // @Dev lat과 lon도  함께 DB에 저장하도록 변경
                    savePrivacyInfoDB(address, latitude + "", longitude + "");
                    break;

                case R.id.btn_list:
                    privacyInfoList = privacyInfoDB.loadInfoDB();

                    if (privacyInfoList.isEmpty()) {
                        Toast.makeText(MainActivity.this, "저장된 기록이 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // @Dev : 기록확인 버튼을 눌렀을때, 현재 화면에서 History(기록확인) 화면으로 이동합니다.
                        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                    }
                    break;

                case R.id.btn_del_list:
                    privacyInfoDB.clearTable();
                    Toast.makeText(MainActivity.this, "기록을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btn_map:
                    // @Dev : 전체위치 버튼을 눌렀을때, 현재 화면에서 Map(전체 위치) 화면으로 이동합니다.
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                    break;

                case R.id.btn_export:
                    startActivity(new Intent(getApplicationContext(), ExportDBDialog.class));
                    break;

                case R.id.btn_theme:
                    startActivity(new Intent(getApplicationContext(), AppThemeDialog.class));
                    break;
            }
        }
    };

}
