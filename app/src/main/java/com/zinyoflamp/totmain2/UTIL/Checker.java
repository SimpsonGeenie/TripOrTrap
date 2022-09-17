package com.zinyoflamp.totmain2.UTIL;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.zinyoflamp.totmain2.Connect.ServerAndURLMulti;
import com.zinyoflamp.totmain2.Connect.ServerConnectCheck;
import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.Member.LoginDTO;
import com.zinyoflamp.totmain2.Member.LoginSessionHandler;
import com.zinyoflamp.totmain2.Member.Loginact1;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.TrapActionFac.MakeaTrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Checker extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "TOT";

    Camera camera;
    Context ctx;
    ServerAndURLMulti toserver=new ServerAndURLMulti();
    ServerConnectCheck scc=new ServerConnectCheck();
    Boolean isconnect=false;
    String connectresult=null;

    TrapDTO tdto=new TrapDTO();
    AllDTO adto=new AllDTO();
    String serverconnectok=null;
    String isconnectok=null;
    AlertDialog ald;

    File outFile;
    File dir;
    FileInputStream fis;


    LoginSessionHandler loginSessionhandler;
    Cursor cursor;
    StringBuffer sb;
    LoginDTO myinfo=new LoginDTO();

    String trapperAccount=null;
    String addre=null;

    byte[] currentData;

    //헤딩 확인
    SensorManager sensorManager = null;
    Sensor orientationSensor = null;
    Sensor accelerometerSensor = null;

    String str = null, str1= null, str2= null;
    double heading,pitch, roll;
    double xAxis,yAxis, zAxis;
    double gyroX, gyroY, gyroZ;
    double myLat, myLog;
    Sensor gyroSensor;

    //지도 확인하기
    LocationManager lManager;
    String provider=LocationManager.NETWORK_PROVIDER;

    private final static int PERMISSIONS_REQUEST_CODE = 100;
    // Camera.CameraInfo.CAMERA_FACING_FRONT or Camera.CameraInfo.CAMERA_FACING_BACK
    private final static int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;
    private AppCompatActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    public void checklist(){
//        loginSessionhandler = LoginSessionHandler.open(getApplicationContext());
//        sb = new StringBuffer();
//        cursor = loginSessionhandler.select();
//
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                myinfo.setTrapperaccount(cursor.getString(0));
//                myinfo.setTrapperid(cursor.getString(1));
//                myinfo.setTrapperpw(cursor.getString(2));
//            }
//            if (myinfo.getTrapperid() != null) {
//                trapperAccount = myinfo.getTrapperaccount();
//            } else {
//                createDialog(0);
//            }
//        }
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
                createDialog(1);
                Toast.makeText(getApplicationContext(), "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };

        ctx = getApplicationContext();

        TedPermission.with(ctx)
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
            try {
                isconnectok = scc.requestconnection();
                if (isconnectok.equals("ok")) {
                    isconnect = true;
                } else {
                    isconnect = false;

                    createDialog(3);

                }
            } catch (IOException e) {
                e.printStackTrace();
                isconnect = false;

                createDialog(4);
            }


            updateLatitude(lManager.getLastKnownLocation(provider));
            updateLongitude(lManager.getLastKnownLocation(provider));
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }


        StringBuffer bf = new StringBuffer();
        List<Address> address;
        String currentLocationAddress;
        Geocoder coder = new Geocoder(getApplicationContext());
        try {
            if (coder != null) {
                // 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
                address = coder.getFromLocation(myLat, myLog, 1);
                // 설정한 데이터로 주소가 리턴된 데이터가 있으면
                if (address != null && address.size() > 0) {
                    // 주소
                    currentLocationAddress = address.get(0).getAddressLine(0).toString();

                    // 전송할 주소 데이터 (위도/경도 포함 편집)
                    bf.append(currentLocationAddress);
                }
            }

        } catch (IOException e) {
            Log.i("주소 취득 실패", "ㅠㅠ");
            ald=createDialog(2);
            ald.show();
            e.printStackTrace();
        }
        String myaddre = bf.toString();
        addre = myaddre;
    }


    private AlertDialog createDialog(final int warnnum) {
        AlertDialog.Builder ab = new AlertDialog.Builder(ctx);
        String warningmsg[]={"TOT 회원이어야 이용하실 수 있습니다. 회원 로그인을 해주세요.",
                "권한 거부",
                "주소 취득 실패",
                "네트워크 연결 에러. TOT 서버와 연결이 원활하지 않습니다.",
                "네트워크 연결 에러. 네트워크 상태를 확인해 주세요."};


        ab.setTitle("Warnning!");
        ab.setMessage(warningmsg[warnnum]);
        ab.setCancelable(false);
        ab.setIcon(getResources().getDrawable(R.drawable.totlogo4));

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                switch (warnnum){
                    case 0:
                        Intent intentmain=new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intentmain);
                        break;



                    case 1 :
                        Intent myaccountintent=new Intent(getApplicationContext(),Loginact1.class);
                        startActivity(myaccountintent);
                        break;

                }


                setDismiss(ald);
            }
        });

        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setDismiss(ald);
            }
        });

        return ab.create();
    }

    private void setDismiss(Dialog dialog){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grandResults) {

        if ( requestCode == PERMISSIONS_REQUEST_CODE && grandResults.length > 0) {

            int hasCameraPermission = ContextCompat.checkSelfPermission(ctx,
                    Manifest.permission.CAMERA);
            int hasWriteExternalStoragePermission =
                    ContextCompat.checkSelfPermission(ctx,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if ( hasCameraPermission == PackageManager.PERMISSION_GRANTED
                    && hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED ){

                //이미 퍼미션을 가지고 있음
                doRestart(ctx);
            }
            else{
                checkPermissions();
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.CAMERA);
        int hasWriteExternalStoragePermission =
                ContextCompat.checkSelfPermission(ctx,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locpermissionCheck = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int coapermissionCheck = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        boolean cameraRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA);
        boolean writeExternalStorageRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean locRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        boolean coaRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if ( (hasCameraPermission == PackageManager.PERMISSION_DENIED && cameraRationale)
                || (hasWriteExternalStoragePermission== PackageManager.PERMISSION_DENIED
                && writeExternalStorageRationale))
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if ( (hasCameraPermission == PackageManager.PERMISSION_DENIED && !cameraRationale)
                || (hasWriteExternalStoragePermission== PackageManager.PERMISSION_DENIED
                && !writeExternalStorageRationale))
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");

        else if ( hasCameraPermission == PackageManager.PERMISSION_GRANTED
                || hasWriteExternalStoragePermission== PackageManager.PERMISSION_GRANTED ) {
            doRestart(this);
        }
    }
    public static void doRestart(Context c) {
        try {
            if (c != null) {
                PackageManager pm = c.getPackageManager();
                if (pm != null) {
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr =
                                (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    } else {
                        Log.e(TAG, "Was not able to restart application, " +
                                "mStartActivity null");
                    }
                } else {
                    Log.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                Log.e(TAG, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Was not able to restart application");
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setIcon(getResources().getDrawable(R.drawable.totlogo4));
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //퍼미션 요청
                ActivityCompat.requestPermissions( Checker.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.BODY_SENSORS
                        },
                        PERMISSIONS_REQUEST_CODE);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setIcon(getResources().getDrawable(R.drawable.totlogo4));
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }



    public Double updateLatitude(Location myLoc){
        myLat=myLoc.getLatitude();
        return myLat;
    }
    public Double updateLongitude(Location myLoc){
        myLog=myLoc.getLongitude();
        return myLog;
    }




    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                str="xAxis : "+event.values[0]+"yAxis : "+event.values[1]+"zAxis : "+event.values[2];
                xAxis=event.values[0];
                yAxis=event.values[1];
                zAxis=event.values[2];
            }else if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
                str1="Heading : "+event.values[0]+"Pitch : "+event.values[1]+"Roll : "+event.values[2];
                heading=event.values[0];
                pitch=event.values[1];
                roll=event.values[2];
            }else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
                str2="gyroX : "+event.values[0]+"gyroY : "+event.values[1]+"gyroZ : "+event.values[2];
                gyroX=event.values[0];
                gyroY=event.values[1];
                gyroZ=event.values[2];
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onStart() {
        super.onStart();

        //for game rate
        if(accelerometerSensor != null)
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        if(orientationSensor != null)
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(accelerometerSensor != null)
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        if(orientationSensor != null)
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onDestroy() {

        if(accelerometerSensor != null || orientationSensor != null)
            sensorManager.unregisterListener(this);


        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(accelerometerSensor != null || orientationSensor != null)
            sensorManager.unregisterListener(this);

        // Surface will be destroyed when we return, so stop the preview.

        }



}
