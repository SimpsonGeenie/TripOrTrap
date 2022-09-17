package com.zinyoflamp.totmain2.TrapActionFac;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.Member.LoginDTO;
import com.zinyoflamp.totmain2.Member.LoginSessionHandler;
import com.zinyoflamp.totmain2.Member.Loginact1;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.AllDTO;
import com.zinyoflamp.totmain2.Connect.ServerAndURLReq;

import org.json.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class TrapUnlocklist extends AppCompatActivity implements LocationListener{
    GridView gview;
    Button btnback;

    DownloadManager dm;
    SharedPreferences pref;
    String result;
    ServerAndURLReq sau = new ServerAndURLReq();
    AllDTO adto = new AllDTO();
    double myLat, myLog;
    ViewMaker vmaker=new ViewMaker();
    Context ctx;
    LocationManager lManager;
    String provider = LocationManager.NETWORK_PROVIDER;


    LoginSessionHandler loginSessionhandler;
    Cursor cursor;
    StringBuffer sb;
    LoginDTO myinfo=new LoginDTO();
    String myaccount=null;

    ArrayList<String> trappicaccount = new ArrayList<>();
    ArrayList<String> trapperaccount = new ArrayList<>();
    ArrayList<String> addre = new ArrayList<>();
    ArrayList<String> latitude = new ArrayList<>();
    ArrayList<String> longitude = new ArrayList<>();
    ArrayList<String> pictureurl = new ArrayList<>();

    ArrayList<TrapGridViewDTO> dtolist = new ArrayList<TrapGridViewDTO>();

    String picsrealurl;
    Bitmap mBitmap;

    @Override
    protected void onStop() {
//        mBitmap.recycle();
//        gview.setAdapter(null);
        super.onStop();
    }

    private void recycleView(View view) {

        if(view != null) {

            Drawable bg = view.getBackground();

            if(bg != null) {

                bg.setCallback(null);

                ((BitmapDrawable)bg).getBitmap().recycle();

                view.setBackgroundDrawable(null);

            }
        }
    }

    private void recyclegridiew(Bitmap bitmap) {

        if(bitmap != null) {

            bitmap.recycle();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trap_forunlocktraplist);


        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());

        loginSessionhandler= LoginSessionHandler.open(getApplicationContext());
        sb = new StringBuffer();
        cursor = loginSessionhandler.select();

        if (cursor!=null){
            while (cursor.moveToNext()){
                myinfo.setTrapperaccount(cursor.getString(0));
                myinfo.setTrapperid(cursor.getString(1));
                myinfo.setTrapperpw(cursor.getString(2));
            }
            if(myinfo.getTrapperid()!=null){
                myaccount=myinfo.getTrapperaccount();
            }else{
                Toast.makeText(getApplicationContext(),"잘못된 접근입니다.",Toast.LENGTH_LONG).show();
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
        Intent intent=getIntent();
        double intentlat=intent.getDoubleExtra("myLat", 0.1);
        double intentlog=intent.getDoubleExtra("myLog", 0.1);
        Log.i("lat/log", intentlat+" / "+intentlog);
        adto.setLatitude(intentlat);
        adto.setLongitude(intentlog);

//        adto.setLatitude(myLat);
//        adto.setLongitude(myLog);

        ctx = this;

        lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "GPS 사용 불가", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            updateLatitude(lManager.getLastKnownLocation(provider));
            updateLongitude(lManager.getLastKnownLocation(provider));
        }


        String txtLatitude, txtLongitude;

        Log.i("mylat : ",""+myLat);
        Log.i("mylon : ",""+myLog);

        double diffLatitude = LatitudeInDifference(500);
        double diffLongitude = LongitudeInDifference(myLat, 500);
        txtLatitude=""+(myLat-diffLatitude)+" ~ "+(myLat+diffLatitude);
        txtLongitude=""+(myLog-diffLongitude)+" ~ "+(myLog+diffLongitude);

        Log.i("mylat 반경 : ",""+txtLatitude);
        Log.i("mylon 반경 : ",""+txtLongitude);

        ////////////////////////////////////

        adto.setTrapperaccount(myaccount);
        adto.setLatitude(updateLatitude(lManager.getLastKnownLocation(provider)));
        adto.setLongitude(updateLongitude(lManager.getLastKnownLocation(provider)));


        vmaker.execute("PLZ WIN");

    }

    public double LatitudeInDifference(int diff){
        //지구반지름
        final int earth = 6371000;    //단위m

        return (diff*360.0) / (2*Math.PI*earth);
    }

    //반경 m이내의 경도차(degree)
    public double LongitudeInDifference(double _latitude, int diff){
        //지구반지름
        final int earth = 6371000;    //단위m

        double ddd = Math.cos(0);
        double ddf = Math.cos(Math.toRadians(_latitude));

        return (diff*360.0) / (2*Math.PI*earth*Math.cos(Math.toRadians(_latitude)));
    }

    public Double updateLatitude(Location myLoc){
        Geocoder coder=new Geocoder(getApplicationContext());
        myLat=myLoc.getLatitude();
        return myLat;
    }
    public Double updateLongitude(Location myLoc){
        Geocoder coder=new Geocoder(getApplicationContext());
        myLog=myLoc.getLongitude();
        return myLog;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }



    @Override
    protected void onDestroy() {

        try
        {
            if (vmaker.getStatus() == AsyncTask.Status.RUNNING)
            {
                vmaker.cancel(true);
            }
            else
            {
            }
        }
        catch (Exception e)
        {
        }
        //recyclegridiew(mBitmap);
        super.onDestroy();
    }


    public class ViewMaker extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(TrapUnlocklist.this);
            progressDialog.setMessage("근처의 덫을 찾는 중입니다.");
            progressDialog.show();
        }


        protected Bitmap doInBackground(String... data) {

            /////////////////////////////
            //result=requestPost();  /////수정할 부분
            /////////////////////////////
            try {
                result=sau.requestconnection(1,adto);

                JSONObject json = null;
                Log.i("받아온 값 : ",result);
                dm=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = 4;
                options.inPurgeable = true;


                json=new JSONObject(result);
                JSONArray jArr =json.getJSONArray("trap");

                // 받아온 pRecvServerPage를 분석하는 부분
                String[] jsonName = {"trapperaccount", "trappicaccount", "addre", "latitude", "longitude", "pictureurl"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];

                for(int i=0; i<jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    trappicaccount.add(json.getString("trappicaccount"));
                    trapperaccount.add(json.getString("trapperaccount"));
                    addre.add(json.getString("addre"));
                    latitude.add(json.getString("latitude"));
                    longitude.add(json.getString("longitude"));
                    pictureurl.add(json.getString("pictureurl"));

                    picsrealurl=sau.urllist(5)+pictureurl.get(i);
                    mBitmap = BitmapFactory.decodeStream((InputStream) new URL(picsrealurl).getContent(), null, options);
                    dtolist.add(new TrapGridViewDTO(mBitmap, (i+1)+"번째 Trap"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return mBitmap;
        }

        protected void onPostExecute(Bitmap image)
        {
            if(image!=null){


                gview=(GridView)findViewById(R.id.forunlock);

                TrapUnLockListAdapter myadapter=new TrapUnLockListAdapter(getApplicationContext(),
                        R.layout.trap_traplistrow,
                        dtolist);
                gview.setAdapter(myadapter);
                progressDialog.dismiss();
                gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long ld) {
                        Toast.makeText(getApplicationContext(),"position"+position,Toast.LENGTH_SHORT).show();
                        String formakeatrapurl=pictureurl.get(position);
                        String trappicaccountforcompare=trappicaccount.get(position);
                        Log.i("url / trappicaccount : ", formakeatrapurl+" / "+trappicaccountforcompare);
                        Intent formakeatrapintent=new Intent(getApplicationContext(),UnlockATrap.class);
                        formakeatrapintent.putExtra("trappicaccount", trappicaccountforcompare+"");
                        formakeatrapintent.putExtra("formakeatrapurl", formakeatrapurl+"");
                        startActivity(formakeatrapintent);
                        finish();
                    }
                });
            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"근처 트랩이 존재하지 않거나 서버 점검중입니다.", Toast.LENGTH_SHORT).show();
                finish();
            }



        }
    }
}
