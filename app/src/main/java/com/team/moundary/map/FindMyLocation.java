package com.team.moundary.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.team.moundary.R;
import com.team.moundary.activity.FontActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import okhttp3.Request;

public class FindMyLocation extends FontActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

    GoogleApiClient mClient;
    ImageView backBtnImage;
    TextView messageView;
    TextView infoView;
    ListView listView;
    ImageView locationUpdate;
    ArrayAdapter<POI> mAdapter;
    String saveAddress;
    String titleAdd;
    String areaAdd;
    //static final int MAP_ADDRESS=3;
    static int checkAddress;

    //마커에 대한 POI정보
    HashMap<Marker, POI> poiResolver = new HashMap<>();

    //POI정보에 대한 마커정보
    HashMap<POI, Marker> markerResolver = new HashMap<>();


    //실제 지도
    GoogleMap mMap;

    ImageView locationSetting;


    private static final int RC_PERMISSION = 1;

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_PERMISSION) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            /*getLocation();*/
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_main);

        //각 서비스를 사용하기 위한 설정을 진행한다.
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .build();
        //구글맵 설정(MapView를 이용하여 설정가능)
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        //반드시 메인쓰레드에서 맵을 동기화 시킨다.
        fragment.getMapAsync(this);

//        messageView = (TextView) findViewById(R.id.text_message);
        infoView = (TextView) findViewById(R.id.text_info);
        listView = (ListView) findViewById(R.id.listView);
        locationSetting =(ImageView)findViewById(R.id.location_setting);
        backBtnImage =(ImageView)findViewById(R.id.back_img_btn);
        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        locationUpdate = (ImageView) findViewById(R.id.location_update);
        locationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                if(saveAddress!=null) {
                    /*RecommendInfoFragment infoFragment = new RecommendInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("address", saveAddress);
                    infoFragment.setArguments(bundle);
                    Log.e("넘겨지는값", String.valueOf(bundle));
                    onBackPressed();*/


                    intent.putExtra("address",titleAdd+"/"+areaAdd);
                    setResult(RESULT_OK,intent);
                    Log.e("보내짐",saveAddress);
                    FindMyLocation.this.finish();
                } else {
                    Toast.makeText(FindMyLocation.this,"선택한 장소가 없습니다.",Toast.LENGTH_SHORT).show();
                    setResult(10000);
                }
            }
        });

        //위치에 대한 세부정보를 저장하는 어댑터
        mAdapter = new ArrayAdapter<POI>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //현재 선택된 리스트 아이템에서 POI정보를 가져온다.
                POI poi = (POI) listView.getItemAtPosition(position);
                //해쉬
                Marker m = markerResolver.get(poi);
                animateMap(m);
            }
        });



        locationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PoiSearchDialogFragment f = new PoiSearchDialogFragment();
                f.setOnPoiSearchResultCallback(new PoiSearchDialogFragment.OnPoiSearchResultCallback() {
                    @Override
                    public void onPoiSearchResult(SearchPOIInfo info) {
                        boolean isFirst = true;
                        clearAll(); //현재 POI정보를 모두 지운다.

                        //넘어온 POI의 위치정보로 맵을 이동시키고
                        //마커를 이동시킨다.
                        for (POI poi : info.pois.poiList) {
                            if (isFirst) {
                                moveMap(poi.getLatitude(), poi.getLongitude(), 15f);
                                isFirst = false;
                            }
                            addMarker(poi);
                        }
                    }
                });
                f.show(getSupportFragmentManager(), "검색");
            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_dialog_map);



    }

    @Override
    protected void onResume() {
        super.onResume();
        /*mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .build();*/
        //구글맵 설정(MapView를 이용하여 설정가능)
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        //반드시 메인쓰레드에서 맵을 동기화 시킨다.
        fragment.getMapAsync(this);

//        messageView = (TextView) findViewById(R.id.text_message);
        infoView = (TextView) findViewById(R.id.text_info);
        listView = (ListView) findViewById(R.id.listView);
        locationSetting =(ImageView)findViewById(R.id.location_setting);
        backBtnImage =(ImageView)findViewById(R.id.back_img_btn);
        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        locationUpdate = (ImageView) findViewById(R.id.location_update);
        locationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                if(saveAddress!=null) {
                    /*RecommendInfoFragment infoFragment = new RecommendInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("address", saveAddress);
                    infoFragment.setArguments(bundle);
                    Log.e("넘겨지는값", String.valueOf(bundle));
                    onBackPressed();*/


                    intent.putExtra("address",titleAdd+"/"+areaAdd);
                    setResult(RESULT_OK,intent);
                    Log.e("보내짐",saveAddress);
                    FindMyLocation.this.finish();
                } else {
                    Toast.makeText(FindMyLocation.this,"선택한 장소가 없습니다.",Toast.LENGTH_SHORT).show();
                    setResult(10000);
                }
            }
        });

        //위치에 대한 세부정보를 저장하는 어댑터
        mAdapter = new ArrayAdapter<POI>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //현재 선택된 리스트 아이템에서 POI정보를 가져온다.
                POI poi = (POI) listView.getItemAtPosition(position);
                //해쉬
                Marker m = markerResolver.get(poi);
                animateMap(m);
            }
        });



        locationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PoiSearchDialogFragment f = new PoiSearchDialogFragment();
                f.setOnPoiSearchResultCallback(new PoiSearchDialogFragment.OnPoiSearchResultCallback() {
                    @Override
                    public void onPoiSearchResult(SearchPOIInfo info) {
                        boolean isFirst = true;
                        clearAll(); //현재 POI정보를 모두 지운다.

                        //넘어온 POI의 위치정보로 맵을 이동시키고
                        //마커를 이동시킨다.
                        for (POI poi : info.pois.poiList) {
                            if (isFirst) {
                                moveMap(poi.getLatitude(), poi.getLongitude(), 15f);
                                isFirst = false;
                            }
                            addMarker(poi);
                        }
                    }
                });
                f.show(getSupportFragmentManager(), "검색");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //구글 플레이 서비스에 연결됐다면 다음과 같이 초기화 작업을 진행한다.
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //퍼미션 설정을 진행(Android 6.0 이상)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

               /* ActivityCompat.requestPermissions(FindMyLocation.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_STORAGE);*/
                return;
            } else {
                ActivityCompat.requestPermissions(FindMyLocation.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        RC_PERMISSION);
                //성능이 향상된 Google의 LocationService를.설정한다.
                Location location = LocationServices.FusedLocationApi.getLastLocation(mClient);

                displayMessage(location);

                //Location의 조건을 설정
                LocationRequest request = new LocationRequest();
                request.setInterval(10000);

                //Criteria를 설정
                request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                //등록한다.
//                LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, mListener);
            }


        }
    }
    /*LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            displayMessage(location);
            //
            ImageView image2 = new ImageView(FindMyLocation.this);

            image2.setImageResource(R.drawable.logo_moundary_gps3);
            //
        }
    };*/

    private void displayMessage(Location location) {

        if (location != null) {
//            messageView.setText("위도 : " + location.getLatitude() + ", 경도 : " + location.getLongitude());
            NetworkManager.getInstance().getTMapReverseGeocoding(this,
                    location.getLatitude(), location.getLongitude(),
                    new NetworkManager.OnResultListener<AddressInfo>() {




                        @Override
                        public void onSuccess(Request request, AddressInfo result) {
                            // messageView.setText(result.fullAddress);
                        }
                        @Override
                        public void onFail(Request request, IOException exception) {

                        }
                    });
            moveMap(location.getLatitude(), location.getLongitude(), 15f);
        }
    }
    //OnMapReadyCallback의 콜백메소드를 등록하여 GoogleMap을 초기화 한다.
    //이 메소드는 자동으로 호출된다.
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Explain to the user why we need to write the permission.
                    Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_STORAGE);


                return;
            }
        }
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //줌을 사용할 수 있도록 초기화 한다.
        mMap.getUiSettings().setZoomControlsEnabled(true);



        mMap.setMyLocationEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                saveAddress=marker.getSnippet() +" "+ marker.getTitle();
                areaAdd=marker.getTitle();
                titleAdd=marker.getSnippet();

                Log.e("위치 클릭 정보",saveAddress);
                infoView.setText(marker.getTitle() + "\n\r" + marker.getSnippet());
                marker.showInfoWindow();
                return true;
            }
        });
        LatLng seoul = new LatLng(37.54053225957781, 126.68777985338363);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,8));
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this, poiResolver));

    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //포이정보를 찾을 때
        if (id == R.id.menu_search) {
            PoiSearchDialogFragment f = new PoiSearchDialogFragment();
            f.setOnPoiSearchResultCallback(new PoiSearchDialogFragment.OnPoiSearchResultCallback() {
                @Override
                public void onPoiSearchResult(SearchPOIInfo info) {
                    boolean isFirst = true;
                    clearAll(); //현재 POI정보를 모두 지운다.

                    //넘어온 POI의 위치정보로 맵을 이동시키고
                    //마커를 이동시킨다.
                    for (POI poi : info.pois.poiList) {
                        if (isFirst) {
                            moveMap(poi.getLatitude(), poi.getLongitude(), 15f);
                            isFirst = false;
                        }
                        addMarker(poi);
                    }
                }
            });
            f.show(getSupportFragmentManager(), "검색");
            return true;
        }
        if (id == android.R.id.home) {
            if (listView.getVisibility() == View.GONE) {
                listView.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
                listView.startAnimation(anim);
            } else {
                listView.setVisibility(View.GONE);
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
                listView.startAnimation(anim);
                ViewPropertyAnimatorCompat animator = ViewCompat.animate(listView);
                animator.translationX(-listView.getMeasuredWidth());
                animator.setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }
                    @Override
                    public void onAnimationEnd(View view) {
                        listView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                });
                animator.start();
            }
        }
        return super.onOptionsItemSelected(item);
    }*/

    //POI 및 어댑터의 정보를 전부 없앤다.
    private void clearAll() {
        Set<Marker> marker = poiResolver.keySet();
        for (Marker m : marker) {
            m.remove();
        }
        mAdapter.clear();
        poiResolver.clear();
        markerResolver.clear();
    }
    //해당 포이에 마커등록
    public void addMarker(POI poi) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(poi.getLatitude(), poi.getLongitude()));
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo_moundary_gps2)));
        options.anchor(0.5f, 1);
        options.title(poi.name);
        options.snippet(poi.getAddress());
        Marker marker = mMap.addMarker(options);

        //어댑터에 등록한다
        mAdapter.add(poi);

        //HashMap에 등록한다.
        markerResolver.put(poi, marker);
        poiResolver.put(marker, poi);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        infoView.setText(marker.getTitle() + "\n\r" + marker.getSnippet());
        marker.showInfoWindow();
        return true;
    }

    //애니메이션을 적용해 이동시킨다.
    private void animateMap(final Marker marker) {
        CameraPosition position = new CameraPosition.Builder()
                .target(marker.getPosition())
                .zoom(15)
                //.bearing(30) //방위각
                // .tilt(30) //기울기 설정
                .build();

        //CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom);
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);

        if (mMap != null) {
            mMap.animateCamera(update, 1000, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    marker.showInfoWindow();
                }
                @Override
                public void onCancel() {

                }
            });
        }
    }
    //현재 위도/경도로 이동한다.


    private void moveMap(double lat, double lng, float zoom) {

        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(lat,lng))
                .zoom(zoom)
                //.bearing(37)
                // .tilt(126)
                .build();

        //      CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom);
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);

        if (mMap != null) {
            mMap.moveCamera(update);
        }
    }


    //Google Play Service 연결에 문제가 있다면 여기에서 상황에 대처한다.
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



}