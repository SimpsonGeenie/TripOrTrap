package com.zinyoflamp.totmain2.TripActionFac;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.kakaonavi.Destination;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.zinyoflamp.totmain2.Connect.ServerAndURLReq;
import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.TrapActionFac.TrapGridViewDTO;
import com.zinyoflamp.totmain2.UTIL.AllDTO;
import com.zinyoflamp.totmain2.UTIL.MapApiConst;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TripMapViewActivity extends FragmentActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {

    ViewMaker vmaker=new ViewMaker();
    DownloadManager dm;
    SharedPreferences pref;
    String result;

    String picsrealurl;
    Bitmap mBitmap;
    double latichange;
    double longichange;

    ArrayList<String> finaddress = new ArrayList<>();
    ArrayList<String> addre = new ArrayList<>();
    ArrayList<String> latitude = new ArrayList<>();
    ArrayList<String> longitude = new ArrayList<>();
    ArrayList<String> pictureurl = new ArrayList<>();

    double myLat, myLog;
    LocationManager lManager;
    String provider = LocationManager.NETWORK_PROVIDER;
    AllDTO adto=new AllDTO();
    ServerAndURLReq sau = new ServerAndURLReq();
    ProgressDialog progressDialog;
    StringBuffer bf;
    List<Address> address;
    String currentLocationAddress;
    Geocoder coder;

    MapView mMapView;
    Button btnback;

    private static final String LOG_TAG = "WhereIamTemp";

    MapPOIItem marker = new MapPOIItem();
    MapPoint point;
    com.kakao.kakaonavi.Location destination;
    KakaoNaviParams params;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_mapview);
        mMapView = new MapView(TripMapViewActivity.this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mMapView);
        //mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.removeAllPOIItems();
        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
       // createCustomBitmapMarker(mMapView);

        btnback=(Button)findViewById(R.id.map_btnBack);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "GPS 사용 불가", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            updateLatitude(lManager.getLastKnownLocation(provider));
            updateLongitude(lManager.getLastKnownLocation(provider));
        }
        adto.setLatitude(updateLatitude(lManager.getLastKnownLocation(provider)));
        adto.setLongitude(updateLongitude(lManager.getLastKnownLocation(provider)));

        lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "GPS 사용 불가", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            updateLatitude(lManager.getLastKnownLocation(provider));
            updateLongitude(lManager.getLastKnownLocation(provider));
        }

        Log.i("mylat : ",""+myLat);
        Log.i("mylon : ",""+myLog);
        coder=new Geocoder(this);


        marker = new MapPOIItem();
        marker.setItemName("내위치");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(myLat, myLog));

        //marker.setMarkerType(MapPOIItem.MarkerType.CustomImag); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setCustomImageResourceId(R.drawable.custom_marker_red);
        //marker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mMapView.addPOIItem(marker);
        mMapView.selectPOIItem(marker, true);
        mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(myLat, myLog), false);

        vmaker.execute("plz");


    }


    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int resultCode, String resultMessage) {
        Log.i(LOG_TAG,	String.format("Open API Key Authentication Result : code=%d, message=%s", resultCode, resultMessage));
    }

    public void onMapViewInitialized(MapView mapView) {
        Log.i(LOG_TAG, "MapView had loaded. Now, MapView APIs could be called safely");
        //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(myLat,myLog), 2, true);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
        Log.i(LOG_TAG, String.format("MapView onMapViewCenterPointMoved (%f,%f)", myLat, myLog));
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
    }

    public View getCalloutBalloon(MapPOIItem mapPOIItem) {        return null;
    }

    public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
        return null;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Log.i("lat/log", myLat+" / "+myLog);

        params = KakaoNaviParams.newBuilder( Destination.newBuilder(
                mapPOIItem.getItemName(), // 마커의 아이템이름을 받아온다.
                mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude, // 경도
                mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude   // 위도 (카카오는 경도를 먼저 입력받는다.)
        ).build()).setNaviOptions( NaviOptions.newBuilder()
                .setCoordType(CoordType.WGS84) // 세계기준 좌표사용 (이문장이 없으면 카카오는 카카오전용좌표를 입력해줘야한다.)
                .setRouteInfo(true) // 전체경로안내(없을시 바로 길안내를 시작한다.)
                .build()).build();
        KakaoNaviService.shareDestination(this, params);

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


    public class ViewMaker extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(TripMapViewActivity.this);
            progressDialog.setMessage("근처의 덫을 찾는 중입니다.");
            progressDialog.show();
        }


        protected String doInBackground(String... data) {


            try {
                result=sau.requestconnection(9,adto);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result)
        {
            if(result!=null){
                try {

                    JSONObject json = null;
                    Log.i("받아온 값 : ",result);
                    dm=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                    pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    json=new JSONObject(result);
                    JSONArray jArr =json.getJSONArray("trap");

                    // 받아온 pRecvServerPage를 분석하는 부분
                    String[] jsonName = {"trapperaccount", "trappicaccount", "addre", "latitude", "longitude", "pictureurl"};
                    String[][] parseredData = new String[jArr.length()][jsonName.length];

                    for(int i=0; i<jArr.length(); i++) {
                        json = jArr.getJSONObject(i);
                        addre.add(json.getString("addre"));
                        latitude.add(json.getString("latitude"));
                        longitude.add(json.getString("longitude"));
                        pictureurl.add(json.getString("pictureurl"));

                    }

                for(int i=0; i<5; i++) {

                    latichange = Double.parseDouble(latitude.get(i));
                    longichange = Double.parseDouble(longitude.get(i));
                    Log.i("라티, 론기", latichange + " / " + longichange);
                    if (coder != null) {
                        bf = new StringBuffer();
                        // 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
                        address = coder.getFromLocation(latichange, longichange, 1);
                        // 설정한 데이터로 주소가 리턴된 데이터가 있으면
                        if (address != null && address.size() > 0) {
                            // 주소
                            currentLocationAddress = address.get(0).getAddressLine(0).toString();

                            // 전송할 주소 데이터 (위도/경도 포함 편집)
                            bf.append(currentLocationAddress);
                        }
                    }
                    String myaddre = bf.toString();
                    Log.i("주소 취득 ", myaddre);
                    finaddress.add(myaddre);
                    point = MapPoint.mapPointWithGeoCoord(latichange, longichange);
                    marker = new MapPOIItem();
                    marker.setItemName(finaddress.get(i));
                    marker.setTag(i);
                    marker.setMapPoint(point);
                    marker.setCustomImageResourceId(R.drawable.custom_marker_red);
                    marker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 기본으로 제공하는 RedPin 마커 모양.
                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.YellowPin);
                    mMapView.addPOIItem(marker);
                }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();

            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"근처 트랩이 존재하지 않거나 서버 점검중입니다.", Toast.LENGTH_SHORT).show();
                finish();
            }



        }
    }


}
