package com.zinyoflamp.totmain2.MainView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.zinyoflamp.totmain2.Connect.ServerConnectCheck;
import com.zinyoflamp.totmain2.Member.LoginDTO;
import com.zinyoflamp.totmain2.Member.LoginSessionHandler;
import com.zinyoflamp.totmain2.R;

import java.io.IOException;
import java.util.ArrayList;


public class Intro extends Activity {
    ServerConnectCheck scc=new ServerConnectCheck();
    String isconnectok=null;
    Thread t;
    LoginDTO myinfo = new LoginDTO();
    LoginSessionHandler loginSessionhandler;
    int value = 0;
    int add = 1;
    Handler handler = new Handler();
    Cursor cursor;
    StringBuffer sb;
    ProgressBar loadingtot;
    LocationManager lManager;
    String provider=LocationManager.NETWORK_PROVIDER;
    double myLat, myLog;

    Runnable r = new Runnable() {
        @Override
        public void run() {
            // Activity 화면 제거

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            if (myinfo.getTrapperaccount() != null) {
                intent.putExtra("trapperaccount", myinfo.getTrapperaccount());
                intent.putExtra("trapperid", myinfo.getTrapperid());
                intent.putExtra("trapperpw", myinfo.getTrapperpw());
            }
            t.interrupt();
            startActivity(intent); // 다음화면으로 넘어가기
            finish();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_intro);

        loadingtot=(ProgressBar)findViewById(R.id.loadingtot);
        loadingtot.setMax(100);
        loadingtot.setProgress(0);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE);

        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            Log.i("권한 확인","없음");
        }else{
            Log.i("권한 확인","있음");
        }
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {


                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED
                        ) {
                    return;
                }

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }

        };


        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("TOT는 사용자의 위치 정보를 확인하여 사진을 찍어 생성할 수 있습니다. 따라서 TOT를 이용하기 위해서는 기기의 설정 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용/해제 하실 수 있습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setPermissions(Manifest.permission.CAMERA)
                .setPermissions(Manifest.permission.BODY_SENSORS)
                .check();

        lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "GPS 사용 불가", Toast.LENGTH_SHORT).show();


        } else {

            updateLatitude(lManager.getLastKnownLocation(provider));
            updateLongitude(lManager.getLastKnownLocation(provider));

        }


        setUp();
        t = new Thread(new Runnable() {
            @Override
            public void run() { // Thread 로 작업할 내용을 구현
                try {
                    isconnectok=scc.requestconnection();
                    if (isconnectok.equals("ok")){
                        Log.i("연결","성공");
                    }else{
                        Log.i("연결","실패");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("연결","실패");
                }
                while(true) {
                    value = value + add;
                    if (value>100) {
                        add = 0;
                        value=100;
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() { // 화면에 변경하는 작업을 구현
                            loadingtot.setProgress(value);
                        }
                    });

                    try {
                        Thread.sleep(40); // 시간지연
                    } catch (InterruptedException e) {    }
                } // end of while
            }
        });
        t.start();

    } // end of onCreate



    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(r, 4000);
    }



    @Override
    protected void onPause() {
        super.onPause();

// 화면을 벗어나면, handler 에 예약해놓은 작업을 취소하자
        handler.removeCallbacks(r); // 예약 취소

    }




    private void setUp(){
        loginSessionhandler=LoginSessionHandler.open(getApplicationContext());

        sb = new StringBuffer();
        cursor = loginSessionhandler.select();

        if (cursor!=null){
            while (cursor.moveToNext()){
                myinfo.setTrapperaccount(cursor.getString(0));
                myinfo.setTrapperid(cursor.getString(1));
                myinfo.setTrapperpw(cursor.getString(2));
            }
            if(myinfo.getTrapperid()!=null){
                Toast.makeText(getApplicationContext(),myinfo.getTrapperid()+"님 환영합니다.",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),"최초 사용자님 환영합니다.",Toast.LENGTH_LONG).show();
            }


        }
    }

    public Double updateLatitude(Location myLoc){
        myLat=myLoc.getLatitude();
        return myLat;
    }
    public Double updateLongitude(Location myLoc){
        myLog=myLoc.getLongitude();
        return myLog;
    }



}