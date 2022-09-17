package com.zinyoflamp.totmain2.TrapActionFac;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.zinyoflamp.totmain2.Connect.ServerAndURLMulti;
import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.Member.LoginDTO;
import com.zinyoflamp.totmain2.Member.LoginSessionHandler;
import com.zinyoflamp.totmain2.Member.Loginact1;
import com.zinyoflamp.totmain2.QnaBbs.QnaList;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.*;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UnlockATrap extends Activity implements SensorEventListener {
    private static final String TAG = "UnlockATrap";
    UnlockPreview preview;
    Camera camera;
    Context ctx;
    Button btnback;
    TrapDTO tdto=new TrapDTO();
    AllDTO adto=new AllDTO();
    ServerAndURLMulti toserver=new ServerAndURLMulti();
    String serverconnectok=null;
    File outFile;
    File dir;
    FileInputStream fis;
    boolean timetowait=true;
    ArrayList<String> unlockpercent = new ArrayList<>();
    Spinner optionspinner;

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
    int w,h;
    int imgwidth, imgheight;
    Sensor gyroSensor;

    AlertDialog ald=null;

    //지도 확인하기
    LocationManager lManager;
    String provider=LocationManager.NETWORK_PROVIDER;


    private final static int PERMISSIONS_REQUEST_CODE = 100;
    // Camera.CameraInfo.CAMERA_FACING_FRONT or Camera.CameraInfo.CAMERA_FACING_BACK
    private final static int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;
    private AppCompatActivity mActivity;

    Camera.AutoFocusCallback myAutoFocusCallback=new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean b, Camera camera) {
            //Toast.makeText(getApplicationContext(),"오토포커싱 시작", Toast.LENGTH_SHORT).show();
        }
    };


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

    public void startCamera() {
        Intent intent=getIntent();
        String formakeatrapurl=intent.getStringExtra("formakeatrapurl");

        if ( preview == null ) {
            try {
                preview = new UnlockPreview(this, (SurfaceView)findViewById(R.id.surfaceView), formakeatrapurl);
                preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                ((FrameLayout) findViewById(R.id.layout)).addView(preview);
                preview.setKeepScreenOn(true);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }



        preview.setCamera(null);
        if (camera != null) {
            camera.release();
            camera = null;
        }

        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = Camera.open(CAMERA_FACING);
                // camera orientation
                camera.setDisplayOrientation(setCameraDisplayOrientation(this, CAMERA_FACING,
                        camera));
                // get Camera parameters
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                params.setAutoWhiteBalanceLock(true);
                params.setAutoExposureLock(true);
                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                //WHITE_BALANCE_FLUORESCENT

                //WHITE_BALANCE_DAYLIGHT

                //WHITE_BALANCE_CLOUDY_DAYLIGHT

                //WHITE_BALANCE_INCANDESCENT

                //WHITE_BALANCE_SHADE : 약간 어둡다.

                //WHITE_BALANCE_WARM_FLUORESCENT : 약간 어둡다.
                int param=0;
                param=params.getExposureCompensation();
                Log.i("밝기 값 : ", param+"");

                //params.setExposureCompensation(param);

//                if(params.getExposureCompensation()>20){
//                    params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT);
//                }




                // picture image orientation
                params.setRotation(setCameraDisplayOrientation(this, CAMERA_FACING, camera));
                camera.startPreview();

            } catch (RuntimeException ex) {
                Toast.makeText(ctx, "camera_not_found " + ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "camera_not_found " + ex.getMessage().toString());
            }
        }

        preview.setCamera(camera);
        camera.startFaceDetection();
        camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                if(faces.length>0){
                    ald=unlockdialog(2);
                    ald.show();
                }
            }
        });

    }



    Camera.PictureCallback pngCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            w = camera.getParameters().getPictureSize().width;
            h = camera.getParameters().getPictureSize().height;
            int orientation = setCameraDisplayOrientation(UnlockATrap.this,
                    CAMERA_FACING, camera);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data,0,data.length, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;

            // Get the dimensions of the View
            int targetW = w;
            int targetH = h;

// Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            //BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

// Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

// Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            //Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length, options);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length, bmOptions);

            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            bitmap =  Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            currentData = stream.toByteArray();
            SaveImageTask simg= new SaveImageTask();
            simg.execute(currentData);
            resetCam();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trap_unlockatrap);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());



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
                trapperAccount=myinfo.getTrapperaccount();
            }else{
                ald=unlockdialog(0);
                Intent myaccountintent=new Intent(getApplicationContext(),Loginact1.class);
                startActivity(myaccountintent);
            }
        }
        btnback=(Button)findViewById(R.id.map_btnBack);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        optionspinner=(Spinner)findViewById(R.id.optionspinner);
        optionspinner.setPrompt("모드 변경");
        optionspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String whatmode[]={"낮", "밤", "흐린날", "자동","플래쉬ON"};
                Camera.Parameters params = camera.getParameters();
                Toast.makeText(getApplicationContext(),whatmode[i],Toast.LENGTH_SHORT).show();
                switch (i){
                    case 0:
                        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_DAYLIGHT);
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        break;
                    case 1:
                        params.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        break;
                    case 2:
                        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT);
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        break;
                    case 3:
                        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        break;
                    case 4:
                        params.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);
                        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT);
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        break;
                }
                camera.setParameters(params);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ctx = this;

        lManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Toast.makeText(getApplicationContext(),"GPS 사용 불가",Toast.LENGTH_SHORT).show();
        }else{
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
        Geocoder coder=new Geocoder(getApplicationContext());
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
            Log.i("주소 취득 실패","ㅠㅠ");
            e.printStackTrace();
        }
        String myaddre=bf.toString();
        Log.i("주소 취득 ",myaddre);
        addre=myaddre;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button button = (Button)findViewById(R.id.btnCapture);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {

                    imgwidth=camera.getParameters().getPictureSize().width;
                    imgheight=camera.getParameters().getPictureSize().height;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                camera.takePicture(shutterCallback, rawCallback, pngCallback);
                Intent intent=getIntent();
                String formakeatrapurl=intent.getStringExtra("formakeatrapurl");
                String trappicaccount=intent.getStringExtra("trappicaccount");


                tdto.setTrapperaccount(trapperAccount);
                tdto.setAddre(addre);
                double myLat1=Double.parseDouble(String.format("%.6f",myLat));
                double myLog1=Double.parseDouble(String.format("%.6f",myLog));
                double xAxis1=Double.parseDouble(String.format("%.6f",xAxis));
                double yAxis1=Double.parseDouble(String.format("%.6f",yAxis));
                double zAxis1=Double.parseDouble(String.format("%.6f",zAxis));
                double heading1=Double.parseDouble(String.format("%.6f",heading));
                double pitch1=Double.parseDouble(String.format("%.6f",pitch));
                double roll1=Double.parseDouble(String.format("%.6f",roll));

                tdto.setLatitude(myLat1);
                tdto.setLongitude(myLog1);
                tdto.setxAxis(xAxis1);
                tdto.setyAxis(yAxis1);
                tdto.setzAxis(zAxis1);
                tdto.setHeading(heading1);
                tdto.setPitch(pitch1);
                tdto.setRoll(roll1);
                tdto.setImgwidth(imgwidth);
                tdto.setImgheight(imgheight);
                tdto.setSelectedpictureurl(formakeatrapurl);
                tdto.setTrappicaccount(trappicaccount);
                tdto.setUnlockeraccount(trapperAccount+"");

            }
        });


        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //API 23 이상이면
                // 런타임 퍼미션 처리 필요

                int hasCameraPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA);
                int hasWriteExternalStoragePermission =
                        ContextCompat.checkSelfPermission(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if ( hasCameraPermission == PackageManager.PERMISSION_GRANTED
                        && hasWriteExternalStoragePermission==PackageManager.PERMISSION_GRANTED){
                    ;//이미 퍼미션을 가지고 있음
                }
                else {
                    //퍼미션 요청
                    ActivityCompat.requestPermissions( this,
                            new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_CODE);
                }
            }
            else{
            }


        } else {
            Toast.makeText(UnlockATrap.this, "Camera not supported",
                    Toast.LENGTH_LONG).show();
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

    SaveImageTask saving;


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
        startCamera();
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
        if(camera != null) {
            // Call stopPreview() to stop updating the preview surface
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }

        ((FrameLayout) findViewById(R.id.layout)).removeView(preview);
        preview = null;

    }

    private void resetCam() {
        startCamera();
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    public static int setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        public SimpleDateFormat mydate(){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
            return sdf;
        }

        String fileName;
        String now=mydate().format(new Date(System.currentTimeMillis()));

        public void trapFilename(){

            adto.setTrappicaccount(tdto.getTrappicaccount());
            adto.setTrapperaccount(tdto.getTrapperaccount());
            adto.setAddre(tdto.getAddre());
            adto.setLatitude(tdto.getLatitude());
            adto.setLongitude(tdto.getLongitude());
            adto.setImgwidth(tdto.getImgwidth());
            adto.setImgheight(tdto.getImgheight());
            adto.setSelectedpictureurl(tdto.getSelectedpictureurl());
            adto.setUnlockeraccount(tdto.getUnlockeraccount());
            Log.i("Check Point : ", "point 6 : "+adto.getAddre());
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                dir = new File (sdCard.getAbsolutePath() + "/camtest");
                dir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
            fileName = String.format("myUnlock"+now+".jpg");
            adto.setPictureurl(dir+"/"+fileName);
        }

        @Override
        public Void doInBackground(byte[]... data) {

            FileOutputStream outStream = null;
            try {
                trapFilename();
                outFile = new File(adto.getPictureurl());
                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);

                outStream.flush();
                outStream.close();
                fis = new FileInputStream(outFile);
                Log.i("onPictureTaken", " wrote bytes: "+data.length + " to " + outFile.getAbsolutePath());

                adto.setPictureurlspath(outFile.getAbsolutePath()+"");

                refreshGallery(outFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {

            serverconnectok=toserver.doFileUpload(3, adto);
            Log.i("퍼센트 : ",serverconnectok);

            JSONObject json = new JSONObject();
            JSONArray jArr =new JSONArray();

            try{
                json=new JSONObject(serverconnectok);
                jArr =json.getJSONArray("trap");

                for(int i=0; i<jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    unlockpercent.add(json.getString("unlockpercent"));
                    Log.i("언락 퍼센트",unlockpercent.get(i));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            int unlockedq=Integer.parseInt(unlockpercent.get(0).toString());
            if(serverconnectok!=null){
                ald=createDialog(unlockedq);
                ald.show();
            }else{
                Toast.makeText(getApplicationContext(), "언락 요청 중 문제가 발생했습니다.", Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grandResults) {

        if ( requestCode == PERMISSIONS_REQUEST_CODE && grandResults.length > 0) {

            int hasCameraPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);
            int hasWriteExternalStoragePermission =
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if ( hasCameraPermission == PackageManager.PERMISSION_GRANTED
                    && hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED ){

                //이미 퍼미션을 가지고 있음
                doRestart(this);
            }
            else{
                checkPermissions();
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int hasWriteExternalStoragePermission =
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

        boolean cameraRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA);
        boolean writeExternalStorageRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);


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

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(UnlockATrap.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //퍼미션 요청
                ActivityCompat.requestPermissions( UnlockATrap.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
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

        AlertDialog.Builder builder = new AlertDialog.Builder(UnlockATrap.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
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


    private AlertDialog createDialog(int unlockedq) {

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("언락 결과");
        try {
            Log.i("문제 확인 : ", unlockedq+"");
            if(unlockedq>=75){
                ab.setMessage("유사도 "+unlockedq+" 퍼센트로 해제되었습니다.");
            }else{
                ab.setMessage("유사도 "+unlockedq+" 퍼센트로 해제되지 않았습니다.");
            }
        }catch (Exception ex){
            ab.setMessage("서버와의 통신 문제로 확인이 안됩니다.");
        }


        ab.setCancelable(false);
        ab.setIcon(getResources().getDrawable(R.drawable.totlogo4));

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
finish();
                setDismiss(ald);
            }
        });
        return ab.create();
    }

    private AlertDialog unlockdialog(final int warnnum) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        String warningmsg[]={"TOT 회원이어야 이용하실 수 있습니다. 회원 로그인을 해주세요.",
                "권한 거부",
                "얼굴 모양의 물체 또는 사람이 있어 초상권에 저촉될 수 있으므로 다른 곳을 찍어주세요."};


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

                    case 2 :
                        resetCam();
                        break;


                }


                setDismiss(ald);
            }
        });

//        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                setDismiss(ald);
//            }
//        });

        return ab.create();
    }

    private ProgressDialog pdialog() {
        ProgressDialog pd=ProgressDialog.show(UnlockATrap.this,"언락 요청중","잠시만 기다려 주세요.",true);


        return pd;
    }


    private void setDismiss(Dialog dialog){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }






}