package com.zinyoflamp.totmain2.TripActionFac;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.kakaonavi.Destination;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.MapApiConst;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TripRecommendMapView extends AppCompatActivity implements LocationListener,MapView.POIItemEventListener,MapView.MapViewEventListener {
    double longitude;
    double latitude;
    double myLat, myLog;
    MapPoint point1;
    String addr;
    LocationManager locationManager;
    String provider = LocationManager.NETWORK_PROVIDER;
    MapPOIItem marker = new MapPOIItem();
    Button button;
    KakaoNaviParams params;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_webview);

        Intent intent = getIntent();
        addr = intent.getStringExtra("mTitle");
        Log.i("받아온 값", addr);
        List<Address> address = null;
        Geocoder coder = new Geocoder(getApplicationContext());

        if (coder != null) {
            try {
                address = coder.getFromLocationName(addr, 10);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (address != null) {
                if (address.size() == 0) {
                    //
                } else {
                    latitude = address.get(0).getLatitude();        // 위도
                    longitude = address.get(0).getLongitude();
                    Log.i("변환된 위경도 ", latitude+" , "+longitude);// 경도
                }
            }
        }
        point1 = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        Log.i("point1 ", "완료");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "GPS를 켜고 실행해주세요", Toast.LENGTH_SHORT).show();
        } else {
            updateLatitude(locationManager.getLastKnownLocation(provider));
            updateLongitude(locationManager.getLastKnownLocation(provider));
        }

        button = (Button) findViewById(R.id.btnBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Log.i("버튼까지 ", "완료");
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.removeAllPOIItems();
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        Log.i("맵뷰 ", "완료");
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 8, true);

        marker = new MapPOIItem();
        marker.setItemName("내위치");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(myLat, myLog));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);
        mapView.selectPOIItem(marker, true);
        Log.i("내 위치 ", "완료");

        marker = new MapPOIItem();
        marker.setItemName(addr);
        marker.setTag(1);
        marker.setMapPoint(point1);
        marker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 기본으로 제공하는 RedPin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.YellowPin); // 마커를 클릭했을때, 기본으로 제공하는 YellowPin 마커 모양.
        mapView.addPOIItem(marker);
        Log.i("여행지 마커 ", "완료");

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                if (ActivityCompat.checkSelfPermission(TripRecommendMapView.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TripRecommendMapView.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

            }




            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(TripRecommendMapView.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("지도 서비스를 사용하기 위해서는 위치 접근 권한이 필요해요")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

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
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

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
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        params = KakaoNaviParams.newBuilder( Destination.newBuilder(
                mapPOIItem.getItemName(), // 마커의 아이템이름을 받아온다.
                mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude, // 경도
                mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude   // 위도 (카카오는 경도를 먼저 입력받는다.)
        ).build()).setNaviOptions( NaviOptions.newBuilder()
                .setCoordType(CoordType.WGS84) // 세계기준 좌표사용 (이문장이 없으면 카카오는 카카오전용좌표를 입력해줘야한다.)
                .setRouteInfo(true) // 전체경로안내(없을시 바로 길안내를 시작한다.)
                .build()).build();
        //KakaoNaviService.shareDestination(this, params);
        Toast.makeText(TripRecommendMapView.this,"message"+mapPOIItem.getItemName(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
