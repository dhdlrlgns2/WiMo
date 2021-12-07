package com.example.wimo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/*
* 서비스 관련 코드. 재난문자 백그라운드 실행위해서 포그라운드 서비스 사용
* */
public class TempService extends Service {
    static int count=0;

    public TempService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    public void startForegroundService() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("재난 문자 서비스");
        builder.setContentText("매일 오전 확진자 방문 장소와 대조하여 알림 제공");

        Intent notificationIntent = new Intent(this, SettingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));

        startForeground(1, builder.build());
    }

    public void stopForegroundService(){
        Log.e("Clear", "StopForegroundService");
        if (count!=0) {
            stopForeground(true);
            stopSelf();
            count=0;
        }
        System.out.println(count);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if ("startForeground".equals(intent.getAction())){ // 단순 실행과 프로세스 진행 나누기 위한 조건문
            startForegroundService();
            count++;
            System.out.println(count);
        }else if ("stopForeground".equals(intent.getAction())){
            stopForegroundService();
        }
        else {
            Log.e("ClearService", "Service Started");
            processCommand(intent);
            count++;
            System.out.println(count);
        }
        return START_STICKY;
    }

    private void processCommand(Intent intent){
        System.out.println("프로세스 커맨드");
        Intent showIntent = new Intent(getApplicationContext(), SettingActivity.class);
        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((SettingActivity)SettingActivity.mcontext).takeStr(); // 재난문자 관련 기능 실행
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e("ClearService", "Service Destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearService", "END");
    }
}