package com.team.moundary.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
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
import com.team.moundary.activity.NetworkActivity;
import com.team.moundary.activity.NetworkDialog;
import com.team.moundary.activity.PropertyManager;
import com.team.moundary.network.ShopActivityEntityObject;
import com.team.moundary.network.ShopActivityHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class MoundaryInfoMap extends FontActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    GoogleApiClient mClient;
    ImageView backBtnImage;
    TextView messageView;
    TextView infoView;
    ListView listView;
    EditText mapEdit;
    ImageView locationUpdate;
    ArrayAdapter<POI> mAdapter;
    String saveAddress = null;
    String getAddress = null;
    String titleAdd = null;
    String areaAdd = null;
    String category = null;
    ImageView locationSetting;

    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId
    //static final int MAP_ADDRESS=3;
    static int checkAddress;

    /*public static Location findGeoPoint(Context mcontext, String address) {
        Location loc =new Location("");
        Geocoder coder=new Geocoder(mcontext);
//        List<Address> addr =null;

        try {
            addr = coder.getFromLocationName(address,1);
        } catch(IOException e) {
            e.printStackTrace();
        } if(addr!=null) {
            for(int i =0; i<addr.size(); i++) {
                Address lating =addr.get(i);
                double lat=lating.getLatitude();
                double lon=lating.getLongitude();
                loc.setLatitude(lat);
                loc.setLongitude(lon);
            }
        }
    }*/


    //마커에 대한 POI정보
    HashMap<Marker, POI> poiResolver = new HashMap<>();

    //POI정보에 대한 마커정보
    HashMap<POI, Marker> markerResolver = new HashMap<>();


    public interface OnPoiSearchResultCallback {
        void onPoiSearchResult(SearchPOIInfo info);
    }

    OnPoiSearchResultCallback listener;

    public void setOnPoiSearchResultCallback(OnPoiSearchResultCallback callback) {
        listener = callback;
    }

    //실제 지도
    GoogleMap mMap;


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

    int currentCategory;
    ArrayList<ShopActivityEntityObject> shopObjects;
    ShopActivityEntityObject shopActivityEntityObject;

    int resImageValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_main);
        Intent i = getIntent();
        mapEdit = (EditText) findViewById(R.id.edit_keyword);
        shopObjects = (ArrayList<ShopActivityEntityObject>) i.getSerializableExtra("shops");

        currentCategory = i.getIntExtra("category", -1);
        /*if(currentCategory == 1) {
            resImageValue = R.drawable.logo_moundary_gps3;
        }else if(currentCategory == 2){
            resImageValue = R.drawable.circle_minus;
        } else if(currentCategory ==3) {
            resImageValue =R.drawable.common_google_signin_btn_icon_dark_disabled;
        } else if(currentCategory ==4) {
            resImageValue = R.drawable.circle_minus;
        }*/
        //각 서비스를 사용하기 위한 설정을 진행한다.
        mClient = new GoogleApiClient.Builder(this)
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
        backBtnImage = (ImageView) findViewById(R.id.back_img_btn);
        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /*locationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (saveAddress != null) {
                    RecommendInfoFragment infoFragment = new RecommendInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("address", saveAddress);
                    infoFragment.setArguments(bundle);
                    Log.e("넘겨지는값", String.valueOf(bundle));
                    onBackPressed();


                    intent.putExtra("address", titleAdd + "/" + areaAdd);
                    setResult(RESULT_OK, intent);
                    Log.e("보내짐", saveAddress);
                    MoundaryInfoMap.this.finish();
                } else {
                    Toast.makeText(MoundaryInfoMap.this, "선택한 장소가 없습니다.", Toast.LENGTH_SHORT).show();
                    setResult(10000);
                }
            }
        });*/

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


        PoiSearchDialogFragment f = new PoiSearchDialogFragment();
        f.setOnPoiSearchResultCallback(new PoiSearchDialogFragment.OnPoiSearchResultCallback() {


            @Override
            public void onPoiSearchResult(SearchPOIInfo info) {
                mapEdit.setText(getAddress);
                boolean isFirst = true;
                clearAll(); //현재 POI정보를 모두 지운다.

                //넘어온 POI의 위치정보로 맵을 이동시키고
                //마커를 이동시킨다.
                for (POI poi : info.pois.poiList) {

                    if (info.pois.poiList != null) {
                        if (isFirst) {
                            moveMap(poi.getLatitude(), poi.getLongitude(), 15f);

                            isFirst = false;
                        }
                    } else {
                        Toast.makeText(MoundaryInfoMap.this, "지역정보를 불러올수없습니다 주소를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                    addMarker(poi);
                }
            }
        });

        /*if (!TextUtils.isEmpty(getAddress)) {
            try {
                //현재 이름으로 포이정보를 찾는 SK플래닛 오픈API를 호출한다.
                NetworkManager.getInstance().getTMapSearchPOI(MoundaryInfoMap.this,
                        getAddress, new NetworkManager.OnResultListener<SearchPOIInfo>() {
                            @Override
                            public void onSuccess(Request request, SearchPOIInfo result) {
                                if (listener != null) {
                                    listener.onPoiSearchResult(result);
                                }
                            }

                            @Override
                            public void onFail(Request request, IOException exception) {
                                Toast.makeText(MoundaryInfoMap.this, "fail", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }*/


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_dialog_map);

        new AsyncAddressJSONList().execute(String.valueOf(currentCategory));
    }

    @Override
    protected void onResume() {
        super.onResume();

        new AsyncAddressJSONList().execute("");


        //구글맵 설정(MapView를 이용하여 설정가능)
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        fragment.getMapAsync(this);


//        messageView = (TextView) findViewById(R.id.text_message);
        infoView = (TextView) findViewById(R.id.text_info);
        listView = (ListView) findViewById(R.id.listView);
        locationSetting = (ImageView) findViewById(R.id.location_setting);
        backBtnImage = (ImageView) findViewById(R.id.back_img_btn);
        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        locationUpdate = (ImageView) findViewById(R.id.location_update);
        /*locationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (saveAddress != null) {
                    *//*RecommendInfoFragment infoFragment = new RecommendInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("address", saveAddress);
                    infoFragment.setArguments(bundle);
                    Log.e("넘겨지는값", String.valueOf(bundle));
                    onBackPressed();*//*


                    intent.putExtra("address", titleAdd + "/" + areaAdd);
                    setResult(RESULT_OK, intent);
                    Log.e("보내짐", saveAddress);
                    MoundaryInfoMap.this.finish();
                } else {
                    Toast.makeText(MoundaryInfoMap.this, "선택한 장소가 없습니다.", Toast.LENGTH_SHORT).show();
                    setResult(10000);
                }
            }
        });*/

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

        /*locationSetting.setOnClickListener(new View.OnClickListener() {
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

                            if (info.pois.poiList != null) {
                                if (isFirst) {
                                    moveMap(poi.getLatitude(), poi.getLongitude(), 15f);

                                    isFirst = false;
                                }
                            } else {
                                Toast.makeText(MoundaryInfoMap.this, "지역정보를 불러올수없습니다 주소를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                            }
                            addMarker(poi);
                        }
                    }
                });
            }
        });*/


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //구글 플레이 서비스에 연결됐다면 다음과 같이 초기화 작업을 진행한다.
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //퍼미션 설정을 진행(Android 6.0 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

               /* ActivityCompat.requestPermissions(FindMyLocation.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_STORAGE);*/
                return;
            } else {
                ActivityCompat.requestPermissions(MoundaryInfoMap.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
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

    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            displayMessage(location);
            //
            ImageView image2 = new ImageView(MoundaryInfoMap.this);

            image2.setImageResource(R.drawable.logo_moundary_gps3);
            //
        }
    };

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

        if( currentCategory != -1){
            if(currentCategory ==3 ) {
                new AsyncAddressJSONList().execute(String.valueOf(currentCategory));
            } else if(currentCategory ==4) {
                new AsyncAddressJSONList().execute(String.valueOf(currentCategory));
            } else if(currentCategory ==2) {
                new AsyncAddressJSONList().execute(String.valueOf(currentCategory));
            } else if(currentCategory ==1) {
                new AsyncAddressJSONList().execute(String.valueOf(currentCategory));
            }
        }


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                saveAddress = marker.getSnippet() + " " + marker.getTitle();
                areaAdd = marker.getTitle();          //세부 주소
                titleAdd = marker.getSnippet();       //상세 주소 받는변수

                Log.e("위치 클릭 정보", saveAddress);
                infoView.setText(marker.getTitle() + "\n\r" + marker.getSnippet());
//                marker.showInfoWindow();
                return true;
            }
        });
        LatLng seoul = new LatLng(37.54053225957781, 126.68777985338363);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 8));
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
//        Intent intent =getIntent();
//        intent.getStringExtra("category");
        if (currentCategory == 1) {
            options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.salegps)));
        } else if (currentCategory == 2) {
            options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sharegps)));
        } else if (currentCategory == 3) {
            options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.eventgps)));
        } else {
            options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.shopgps)));
        }

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


    //4종류의 컨텐츠 마커
   /* public void saleMarker(POI poi) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(poi.getLatitude(), poi.getLongitude()));
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo_moundary_delete)));//이미지 새로받아야함
        options.anchor(0.5f, 1);
        options.title(poi.name);
        options.snippet(poi.getAddress());
        Marker marker = mMap.addMarker(options);
        mAdapter.add(poi);
        //HashMap에 등록한다.
        markerResolver.put(poi, marker);
        poiResolver.put(marker, poi);
    }

    public void eventMarker(POI poi) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(poi.getLatitude(), poi.getLongitude()));
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo_moundary_add_baby)));//이미지 새로받아야함
        options.anchor(0.5f, 1);
        options.title(poi.name);
        options.snippet(poi.getAddress());
        Marker marker = mMap.addMarker(options);
        mAdapter.add(poi);

        //HashMap에 등록한다.
        markerResolver.put(poi, marker);
        poiResolver.put(marker, poi);
    }

    public void nanumMarker(POI poi) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(poi.getLatitude(), poi.getLongitude()));
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo_moundary_delete)));//이미지 새로받아야함
        options.anchor(0.5f, 1);
        options.title(poi.name);
        options.snippet(poi.getAddress());
        Marker marker = mMap.addMarker(options);
        mAdapter.add(poi);

        //HashMap에 등록한다.
        markerResolver.put(poi, marker);
        poiResolver.put(marker, poi);
    }

    public void shopMarker(POI poi) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(poi.getLatitude(), poi.getLongitude()));
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo_moundary_add_baby)));//이미지 새로받아야함
        options.anchor(0.5f, 1);
        options.title(poi.name);
        options.snippet(poi.getAddress());
        Marker marker = mMap.addMarker(options);
        mAdapter.add(poi);

        //HashMap에 등록한다.
        markerResolver.put(poi, marker);
        poiResolver.put(marker, poi);
    }*/


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
                .target(new LatLng(lat, lng))
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

    class AsyncAddressJSONList extends AsyncTask<String, Integer,
            ArrayList<ShopActivityEntityObject>> {
        NetworkDialog netwrokDialog = new NetworkDialog(MoundaryInfoMap.this);

        @Override
        protected ArrayList<ShopActivityEntityObject> doInBackground(
                String... params) {
            String categoryValue = params[0];
            Response response = null;
            try {
                String targetURL = String.format(NetworkActivity.SHOP_NETWORK, userId, categoryValue);
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(targetURL)
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                // responseBody.string();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ShopActivityHandler.getJSONBloodRequestAllList(
                            new StringBuilder(responseBody.string()));
                }
            } catch (UnknownHostException une) {
                e("aaa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("bbb", uee.toString());
            } catch (Exception e) {
                e("ccc", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netwrokDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<ShopActivityEntityObject> result) {
            netwrokDialog.dismiss();

            for (int i = 0; i < result.size(); i++) {
                getAddress = result.get(i).postAddress.toString();

//                getAddress.toString();
                if (!TextUtils.isEmpty(getAddress)) {

                   /* try {
                        //현재 이름으로 포이정보를 찾는 SK플래닛 오픈API를 호출한다.
                        NetworkManager.getInstance().getTMapSearchPOI(MoundaryInfoMap.this,
                                getAddress, new NetworkManager.OnResultListener<SearchPOIInfo>() {
                                    @Override
                                    public void onSuccess(Request request, SearchPOIInfo result) {
                                        if (listener != null) {
                                            listener.onPoiSearchResult(result);
                                        }
                                    }

                                    @Override
                                    public void onFail(Request request, IOException exception) {
                                        Toast.makeText(MoundaryInfoMap.this, "fail", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }*/
                    Geocoder coder = new Geocoder(getApplicationContext());

                    try {
                        List<Address> addrList = coder.getFromLocationName(getAddress,2);

                        Iterator<Address> addrs = addrList.iterator();
                        String infoAddr = "";
                        double lat = 0f;
                        double lng = 0f;

                        while (addrs.hasNext())

                        {

                            Address loc = addrs.next();
                            infoAddr += String.format("Coord : %f. %f ", loc.getLatitude(), loc.getLongitude());
                            lat = loc.getLatitude();
                            lng = loc.getLongitude();
                            Log.e("위도 경도", String.valueOf(lat));

                            MarkerOptions optMarker = new MarkerOptions();
                            optMarker.position(new LatLng(lat, lng));
                            optMarker.title(optMarker.getTitle());
                            optMarker.snippet(optMarker.getSnippet());

                            if (currentCategory == 1 ) {
                                optMarker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.salegps)));
                            } else if (currentCategory == 2) {
                                optMarker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sharegps)));
                            } else if (currentCategory == 3) {
                                optMarker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.eventgps)));
                            } else {
                                optMarker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.shopgps)));
                            }

                            mMap.addMarker(optMarker);
                            LatLng markerMap = new LatLng(lat, lng);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerMap, 14));
                           /* mAdapter.add(getAddress);

                            //HashMap에 등록한다.
                            markerResolver.put(poi, marker);
                            poiResolver.put(marker, poi);*/
                        }


                        final String gURIForm = String.format("geo: %f, %f ", lat, lng);

           /* Uri gURI = Uri.parse(gURIForm);
            Intent gMapIntent = new Intent(Intent.ACTION_VIEW, gURI);

            startActivity(gMapIntent);
*/
                    } catch (IOException e) {

                    }
                }

            }
            /*locationSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PoiSearchDialogFragment f = new PoiSearchDialogFragment();
                    f.setOnPoiSearchResultCallback(new PoiSearchDialogFragment.OnPoiSearchResultCallback() {
                        @Override
                        public void onPoiSearchResult(SearchPOIInfo info) {
//                            mapEdit.setText(getAddress);
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
            });*/




            /*if (result != null && result.size() > 0) {
                EventAdapter bloodListAdapter =
                        new EventAdapter(
                                result, ShopActivity.this);
                bloodListAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(bloodListAdapter);
                nofriend.setVisibility(View.GONE);
            }
            if(result == null) {
                nofriend.setVisibility(View.VISIBLE);
            }*/
        }
    }


}
