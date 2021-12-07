package com.example.wimo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
* 생성한 설정창 띄울 베이스 액티비티 + 재난문자 기능 코드
* */

public class SettingActivity extends AppCompatActivity {
    private final String DEFAULT = "DEFAULT";

    SharedPreferences sp;   //지역 설정 불러오기(서버 리퀘스트)
    String setArea;         //지역 설정 코드 저장(서버 리퀘스트)

    Boolean setAlertTF; // 알림 유무 토글 값 받아옴. 추후 서버 리퀘스트 유무 결정

    private PrivacyInfoDB.PrivacyInfoDBHelper dbHelper;
    private SQLiteDatabase db;

    String fromSer; // 서버에서 받아온 문자열 저장

    ContentValues values;// 서버에서 받아오기

    public static Context mcontext; // 지정시간 반복 브로드캐스트리시버에서 메소드 호출위해 컨텍스트 선언

    private static SettingActivity instance;

    private static final String startFore = "startForeground"; // 포그라운드 서비스 시작위한 문자열
    private static final String stopFore = "stopForeground"; // 포그라운드 서비스 종료위한 문자열

    public String firstRun="yes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new PrivacyInfoDB.PrivacyInfoDBHelper(this);
        db = dbHelper.getWritableDatabase();
        values = new ContentValues();// 서버에서 받아온 문자열 DB에 넣을 때 사용

        sp = getSharedPreferences("prefs", MODE_PRIVATE);   //지역 설정 불러오기(서버 리퀘스트)
        setArea = sp.getString("alertArea", "서울");   //지역 설정 저장 기본값 선택안됨(서버 리퀘스트)

        setAlertTF = sp.getBoolean("alertTF", false); // 알림 유무 토글 값 받아옴. 추후 서버 리퀘스트 유무 결정

        mcontext = this; // 지정시간 반복 브로드캐스트리시버에서 메소드 호출위해 액티비티 컨텍스트 저장
        instance = this;
    }

    public void changeSetting(boolean a){ // 토글에 따라 포그라운드 서비스 온오프 결정 위한 메소트
        if (a==true){
            onStartForegroundService(startFore); // 현재는 설정창에서 알림 토글 설정할 경우 포그라운드 서비스 실행(추후 메인으로?) 켜져있어야 앱 종료 후에도 알림 가능
        }else {
            onStartForegroundService(stopFore); // 설정창에서 알림 토글 오프할 때 포그라운드 서비스 종료
        }
        repeatFun(a,""); // 예약
        setAlertTF = a; // 토글 변경시 온오프 변수 값 변경
    }

    // 서비스 코드 앱 종료되어 있을 경우에도 알림 주기 위한 코드
    public void onStartForegroundService(String a) {
        Intent intent = new Intent(this, TempService.class);
        if (a.equals(startFore)) { // 포그라운드 서비스만 실행상태로 두기 위한 조건문
            intent.setAction(startFore);
        } else if (a.equals(stopFore)) { // 포그라운드 중지 위한 조건
            intent.setAction(stopFore);
        } else { // 브로드캐스트리시버가 실행하는 코드 지정시간에 알림 제공
            intent.setAction("no"); // 문자열은 임의로 넣은것 아무거나 상관없음
        }
        startForegroundService(intent); // 포그라운드 서비스 실행
    }

    public static SettingActivity getInstance(){
        return instance;
    }

    // 알림 생성 코드
    void createNotificationChannel(String channelId, String channelName, int importance)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
        }
    }

    // 알림 세부사항 설정 및 내용 작성 코드
    void createNotification(String channelId, int id, String title, String text, Intent intent)
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)    // 클릭시 설정된 PendingIntent가 실행된다
                .setAutoCancel(true)                // true이면 클릭시 알림이 삭제된다
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    // 알림 삭제 코드
    void destroyNotification(int id)
    {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    //서버에서 문자열 받아오는 함수(테스트) 추후에 시간에 맞춰 서버에서 받아오기(RepeatTask.java에서 연결) + 동시에 db에 저장된 내용 삭제
    public void takeStr(){
        fromSer = "대한민국 광주광역시 동구 구성로 220, 대한민국 광주광역시 북구 서하로 328-1";
        processToDB(fromSer);
    }

    //서버에서 받아온 문자열 처리 및 DB에 저장 함수(테스트)
    public void processToDB(String s){
        db.execSQL("DELETE FROM message_info"); // 받아온 문자열 저장 DB 처음에 삭제해서 리스트 정리
        String [] array = s.split(", "); //받아온 문자열 쪼개기 "," 기준
        for (int i=0;i<array.length;i++){
            values.put("message_id", i); // DB 재난문자 테이블에 id 추가
            values.put("address",array[i]); // DB 재난문자 테이블에 주소 추가
            System.out.println(i+1);
            System.out.println(array[i]);
            db.insert("message_info",null,values); // 최종적으로 재난문자 테이블에 추가
        }
        dbCheck();
    }

    // 확진자 DB 사용자 DB 비교 함수(테스트)
    public void dbCheck() {
        // 확진자 DB에서 주소 꺼내와서 String Array에 저장
        db = dbHelper.getReadableDatabase();
        System.out.println("dbCheck 시작");
        String msql = "select address from message_info";
        Cursor cursor = db.rawQuery(msql, null);
        String[] take = new String[cursor.getCount()];

        int i = 0;
        System.out.println("확진 while 시작");
        while (cursor.moveToNext()) {
            take[i] = cursor.getString(0);
            System.out.println(take[i]);
            i++;
        }

        // 사용자 DB에서 주소 꺼내와서 String Array에 저장
        String usql = "select location from privacy_info";
        Cursor ucursor = db.rawQuery(usql, null);
        String[] user = new String[ucursor.getCount()];
        int j = 0;
        System.out.println("사용 while 시작");
        while (ucursor.moveToNext()) {
            user[j] = ucursor.getString(0);
            System.out.println(user[j]);
            j++;
        }

        String [] match = new String[cursor.getCount()]; // 겹치는 장소는 최대 받아온 주소보다 작음. 받아온 주소 개수로 초기화

        for (i=0; i<take.length;i++){
            for (j=0;j<user.length;j++){
                if(take[i].equals(user[j])){
                    match[i] = user[j];
                    System.out.println("일치하는 주소: "+match[i]); // 일치하는 주소 array. 리스트뷰 쪽으로 넘겨주면 됨.
                }
            }
        }
        alertCheck(match);
    }

    // 설정창 알림 토글 켜져있을 때만 알림 울리도록 체크하는 함수
    public void alertCheck(String [] s){
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        System.out.println("alertCheck 실행");
        StringBuilder sb = new StringBuilder();
        int count=0;
        if(setAlertTF) { // 알림 토글 켜져있을 때
            for (int i = 0; i<s.length; i++) {
                if (s[i]==null) // 일치항목 저장 배열 중간이나 끝에 null 값 생기는 경우 있어서 제거 코드
                    continue;
                sb.append(s[i]+"\n");
                count++;
                createNotificationChannel(DEFAULT, "default channel", NotificationManager.IMPORTANCE_HIGH);
                createNotification(DEFAULT, 0, "경고", "확진자 방문 장소와 일치하는 " + count + "건의 기록을 찾았습니다.\n" + sb + "설정된 지역: " + setArea, intent);
            }
        }else if (!setAlertTF) // 알림 토글 꺼져있을 때
            Toast.makeText(SettingActivity.this, "알림이 꺼져있습니다.", Toast.LENGTH_SHORT).show();
    }

    // 지정시간 반복 실행 위한 함수(서버에서 매일 확진자 주소 받아올 떄)
    public void repeatFun(boolean tf, String a) {
        System.out.println("시작");
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        Intent intent = new Intent(this, SettingActivity.RepeatTaskReceiver.class);
        intent.setAction("com.project.action.ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9); // 시 설정(24시간)
        calendar.set(Calendar.MINUTE, 0); // 분 설정
        calendar.set(Calendar.SECOND, 0); // 초 설정
        calendar.set(Calendar.MILLISECOND, 0);  // 밀리초 설정

        if (tf == true) { // 알람 토글 온 일 때만 실행
            if (alarmManager != null) { // 전에 저장해둔 지정시간 삭제(테스트할 때만 넣고 구현 끝나면 삭제)
                alarmManager.cancel(pendingIntent);
            }

            if (firstRun == "yes") {
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(System.currentTimeMillis() + 5 * 1000, pendingIntent), pendingIntent);
            }

            if (a == "re") {
                if (Calendar.getInstance().after(calendar)) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(System.currentTimeMillis() + 5 * 1000, pendingIntent), pendingIntent);
                //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Log.e("재예약", "지정된 시간: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            }
        }else { // 알람 토글 오프이면 예약 삭제
            if (alarmManager != null) { // 전에 저장해둔 지정시간 삭제(테스트할 때만 넣고 구현 끝나면 삭제)
                alarmManager.cancel(pendingIntent);
                calendar.clear();
                System.out.println("알람취소");
                Log.e("예약삭제", "지정된 시간: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            }
        }
    }

    // 지정시간에 실행되도록 하는 브로드캐스트 리시버 클래스
    public static class RepeatTaskReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (((SettingActivity) SettingActivity.mcontext).firstRun == "yes") {
                ((SettingActivity) SettingActivity.mcontext).firstRun = "";
                ((SettingActivity) SettingActivity.mcontext).repeatFun(true, "re"); // 재예약 코드
            } else {
                System.out.println("도착");

                Log.e("RepeatTask", "지정시간 반복 테스트");

                //((SettingActivity) SettingActivity.mcontext).repeatFun(true, "re"); // 재예약 코드
                ((SettingActivity) SettingActivity.mcontext).onStartForegroundService("no"); // 지정 시간되면 실행되어서 포그라운드 서비스통해서 재난문자 기능 실행
            }
        }
    }
}
