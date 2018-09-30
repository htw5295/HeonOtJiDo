package kr.ac.cnu.heonotjido.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import kr.ac.cnu.heonotjido.gson.GeoCode;
import kr.ac.cnu.heonotjido.map.GpsInfo;
import kr.ac.cnu.heonotjido.map.NMapPOIflagType;
import kr.ac.cnu.heonotjido.map.NMapViewerResourceProvider;
import kr.ac.cnu.heonotjido.retrofit.RetrofitClient;
import kr.ac.cnu.heonotjido.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends NMapActivity {
    private NMapView mMapView;// 지도 화면 View

    private String clientId = "IpBXd7ltF_UT1FBfuoal";//애플리케이션 클라이언트 아이디값";
    private String clientSecret = "8HI2LCVep8";//애플리케이션 클라이언트 시크릿값";

    private NMapController mMapController;
    private NMapViewerResourceProvider mMapViewerResourceProvider;
    private NMapOverlayManager mOverlayManager;
    private NMapLocationManager mMapLocationManager;

    private String TAG = "TAG";

    ArrayList<String> addressList = new ArrayList<>();

    private RetrofitClient retrofitClient;
    private RetrofitService retrofitService;

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retrofitClient = new RetrofitClient();
        retrofitService = retrofitClient.getClient().create(RetrofitService.class);

        try {
            InputStream is = getBaseContext().getResources().getAssets().open("addressList.xls");
            Workbook wb = Workbook.getWorkbook(is);

            if(wb != null) {
                Sheet sheet = wb.getSheet(0);   // 시트 불러오기
                if(sheet != null) {
                    int colTotal = sheet.getColumns();    // 전체 컬럼
                    int rowIndexStart = 1;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal-1).length;

                    StringBuilder sb;
                    for(int row=rowIndexStart;row<rowTotal;row++) {
                        sb = new StringBuilder();
                        for(int col=0;col<colTotal;col++) {
                            String contents = sheet.getCell(col, row).getContents();
                            if(col==0){
                                sb.append(contents+" ");
                            }else{
                                sb.append(contents);
                            }

                            if(col==1){
                                addressList.add(sb.toString());
                                sb.setLength(0);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

        geoCode(index);

        mMapView = new NMapView(this);
        setContentView(mMapView);

        mMapController = mMapView.getMapController();

        mMapView.setClientId(clientId); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.setScalingFactor(5.0f);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setMarker();
                GpsInfo gpsInfo = new GpsInfo(MainActivity.this);
                double lat = gpsInfo.getLatitude();
                double lon = gpsInfo.getLongitude();
                mMapController.setMapCenter(lon, lat);
                mMapView.requestFocus();
            }
        }, 1000);


        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

//        mMapLocationManager = new NMapLocationManager(this);
//        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
    }

//    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
//        @Override
//        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
//            Toast.makeText(MainActivity.this, "진입!", Toast.LENGTH_SHORT).show();
//            if (mMapController != null) {
//                mMapController.setMapCenter(myLocation);
//            }
//            return true;
//        }
//
//        @Override
//        public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {
//
//        }
//
//        @Override
//        public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
//
//        }
//    };

    private void geoCode(int index) {
        String address = addressList.get(index);
        try {
            address = URLEncoder.encode(addressList.get(index), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Call<GeoCode> call = retrofitService.geoCode(clientId, clientSecret, address);
        call.enqueue(new Callback<GeoCode>() {
            @Override
            public void onResponse(Call<GeoCode> call, Response<GeoCode> response) {
                GeoCodeCallBack(response);
            }

            @Override
            public void onFailure(Call<GeoCode> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GeoCodeCallBack(Response<GeoCode> response) {
        if (response.isSuccessful()) {
            GeoCode body = response.body();
            Log.d("test", body.result.items.get(0).point.x + ", " + body.result.items.get(0).point.y);
        }else{
            Log.d("test", "fail"+", "+response.message());
        }
        if(index++ != addressList.size()){
            geoCode(index);
        }
    }

    private void setMarker() {
        int markerId = NMapPOIflagType.PIN;

        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poiData.beginPOIdata(2);

        poiData.addPOIitem(127.0630205, 37.5091300, "말풍선 클릭시 뿅", markerId, 0);
        poiData.addPOIitem(127.061, 37.51, "네이버맵 입니다", markerId, 0);
        poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        poiDataOverlay.showAllPOIdata(0);
        poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);  //좌표 클릭시 말풍선 리스
    }

    private NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            // [[TEMP]] handle a click event of the callout
        }

        public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            if (item != null) {
                Log.i(TAG, "onFocusChanged: " + item.toString());
            } else {
                Log.i(TAG, "onFocusChanged: ");
            }
        }
    };

    private NMapView.OnMapStateChangeListener changeListener = new NMapView.OnMapStateChangeListener() {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
            Log.e(TAG, "OnMapStateChangeListener onMapInitHandler : ");
        }

        @Override
        public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {
            Log.e(TAG, "OnMapStateChangeListener onMapCenterChange : " + nGeoPoint.getLatitude() + " ㅡ  " + nGeoPoint.getLongitude());
        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {
            Log.e(TAG, "OnMapStateChangeListener onMapCenterChangeFine : ");
        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int i) {
            Log.e(TAG, "OnMapStateChangeListener onZoomLevelChange : " + i);
        }

        @Override
        public void onAnimationStateChange(NMapView nMapView, int i, int i1) {
            Log.e(TAG, "OnMapStateChangeListener onAnimationStateChange : ");
        }
    };

    private NMapView.OnMapViewTouchEventListener mapListener = new NMapView.OnMapViewTouchEventListener() {
        @Override
        public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onLongPress : ");
        }

        @Override
        public void onLongPressCanceled(NMapView nMapView) {
            Log.e(TAG, "OnMapViewTouchEventListener onLongPressCanceled : ");
        }

        @Override
        public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onTouchDown : ");
        }

        @Override
        public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onTouchUp : ");
        }

        @Override
        public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {
            Log.e(TAG, "OnMapViewTouchEventListener onScroll : ");
        }

        @Override
        public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onSingleTapUp : ");
        }
    };
}
