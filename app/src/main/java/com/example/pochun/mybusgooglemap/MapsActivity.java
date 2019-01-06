package com.example.pochun.mybusgooglemap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    String busDataUrl = "https://tcgbusfs.blob.core.windows.net/blobbus/GetBusData.gz";
    String busEstimateTimeUrl = "https://tcgbusfs.blob.core.windows.net/blobbus/GetEstimateTime.gz";
    String busStopUrl = "https://tcgbusfs.blob.core.windows.net/blobbus/GetStop.gz";

    String retStr = "";
    String currUrlStr = "";

    ObjectMapper mapper = new ObjectMapper();

    private GoogleMap mMap;
    public BusDataRoot busDataRoot = null;
    public BusStopRoot busStopRoot = null;
    public BusEstimateTimeRoot busEstimateTimeRoot = null;
    List<Marker> myMarker = new ArrayList<Marker>();
    List<Marker> myTimeMarker = new ArrayList<Marker>();

    Thread busDataThread;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        while (hasPermissions(this, PERMISSIONS) == false) ;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void getBusTimeInfo() {
        currUrlStr=busEstimateTimeUrl;
        getBusDataFromServer();
        try {
            busEstimateTimeRoot = mapper.readValue(retStr, BusEstimateTimeRoot.class);
            System.out.println("[PCC]"+busEstimateTimeRoot.essentialInfo.UpdateTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getStopInfo() {
        currUrlStr=busStopUrl;
        getBusDataFromServer();
        try {
            busStopRoot = mapper.readValue(retStr, BusStopRoot.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getBusInfo() {
        currUrlStr=busDataUrl;
        getBusDataFromServer();
        try {
            busDataRoot = mapper.readValue(retStr, BusDataRoot.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putBusStopInMap() {
        MapsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (Marker tmpMarker : myTimeMarker) {
                    tmpMarker.remove();
                }
                if (myTimeMarker.size() > 0)
                    myTimeMarker.clear();

                if (busStopRoot != null) {
                    for (int i = 0; i < busStopRoot.busInfos.size(); i++) {
                        if (busStopRoot.busInfos.get(i).routeId == 15191) {
                            if (busStopRoot.busInfos.get(i).goBack.compareTo("0") == 0) {
                                String tmpTime="";
                                for(int j=0;j<busEstimateTimeRoot.busInfos.size();j++)
                                {
                                    if(busStopRoot.busInfos.get(i).Id.compareTo(busEstimateTimeRoot.busInfos.get(j).StopID)==0) {
                                        LatLng tmpLaLng = new LatLng(busStopRoot.busInfos.get(i).latitude, busStopRoot.busInfos.get(i).longitude);
                                        Marker tmpMarker = mMap.addMarker(new MarkerOptions()
                                                .position(tmpLaLng)
                                                .title(busStopRoot.busInfos.get(i).nameZh + ":" + busEstimateTimeRoot.busInfos.get(j).EstimateTime + "秒")
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                        myTimeMarker.add(tmpMarker);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public void putBusInfoInMap()
    {
        MapsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Marker tmpMarker : myMarker) {
                    tmpMarker.remove();
                }
                if (myMarker.size() > 0)
                    myMarker.clear();

                if (busDataRoot != null) {
                    for (int i = 0; i < busDataRoot.busInfos.size(); i++) {
                        if (busDataRoot.busInfos.get(i).RouteID == 15191) {
                            if (busDataRoot.busInfos.get(i).GoBack.compareTo("0") == 0) {
                                LatLng busLocation = new LatLng(busDataRoot.busInfos.get(i).Latitude, busDataRoot.busInfos.get(i).Longitude);
                                Marker tmpMaker = mMap.addMarker(new MarkerOptions().position(busLocation).title("車牌"+busDataRoot.busInfos.get(i).BusID + ";時速:" + String.valueOf(busDataRoot.busInfos.get(i).Speed)));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busLocation, 15.0f));
                                myMarker.add(tmpMaker);
                            }
                        }
                    }
                }
            }
        });
    }

    public void wait10s() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getStopInfo();
                while (true) {
                    getBusInfo();
                    putBusInfoInMap();

                    getBusTimeInfo();
                    putBusStopInMap();
                    wait10s();
                }
            }
        }).start();
    }

    public String decompression(InputStream inputStream) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final int bufferSize = 4096;
            int length = 0;
            GZIPInputStream ungzip  = new GZIPInputStream(inputStream);
            byte[] buffer = new byte[bufferSize];
            while ((length = ungzip.read(buffer, 0, bufferSize)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    public void getBusDataFromServer() {
        busDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    retStr = "";
                    URL url = new URL(currUrlStr);
                    InputStream inputStream = url.openStream();
                    retStr = decompression(inputStream);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        busDataThread.start();
        try {
            busDataThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}