package com.zinyoflamp.totmain2.TrapActionFac;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.MapApiConst;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class WhereIAm extends FragmentActivity
        implements MapView.OpenAPIKeyAuthenticationResultListener, MapView.MapViewEventListener, CalloutBalloonAdapter, MapView.POIItemEventListener {
    MapView mMapView;
    Button btnback;
    double myLat, myLog;
    LocationManager lManager;
    String provider = LocationManager.NETWORK_PROVIDER;


    private static final String LOG_TAG = "WhereIamTemp";



    MapPOIItem marker = new MapPOIItem();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trap_whereistraps);


        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.removeAllPOIItems();
        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        createCustomBitmapMarker(mMapView);

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

        Log.i("mylat : ",""+myLat);
        Log.i("mylon : ",""+myLog);



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

    public void createCustomBitmapMarker(MapView mapView) {
        marker = new MapPOIItem();
        String name = "Custom Bitmap Marker";
        marker.setItemName(name);
        marker.setTag(2);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(myLat, myLog));

        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.custom_marker_star);
        marker.setCustomImageBitmap(bm);
        marker.setCustomImageAutoscale(false);
        marker.setCustomImageAnchor(0.5f, 0.5f);

        mMapView.addPOIItem(marker);
        mMapView.selectPOIItem(marker, true);
        mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(myLat, myLog), false);
    }

	@Override
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

    @Override
    public View getCalloutBalloon(MapPOIItem mapPOIItem) {        return null;
    }

    @Override
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
        Intent intenttounlocklist=new Intent(getApplicationContext(),TrapUnlocklist.class);
        intenttounlocklist.putExtra("myLat",myLat);
        intenttounlocklist.putExtra("myLog",myLog);
        startActivity(intenttounlocklist);
        finish();
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}
