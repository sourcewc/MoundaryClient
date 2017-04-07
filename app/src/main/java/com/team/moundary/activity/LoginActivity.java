package com.team.moundary.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;
import com.team.moundary.R;
import com.team.moundary.map.FindMyLocation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks {
    int year, month, day, hour, minute;

    String date;
    String area1 = null;
    String area2 = null;
    String area3 = "";
    String area4 = null;
    String area5 = null;

    private SharedPreferences mPrefs;
    String userAddress;
    String parsingUser;
    String getParsingUser;
    GoogleApiClient mClient;
    static String finalParsingUser;

    private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");

    private ArrayList<UpLoadValueObject> upLoadfiles = new ArrayList<>();
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int MAP_ADDRESS = 3;

    private Uri mImageCaptureUri;
    private Uri bImageCaptureUri;


    Uri currentSelectedUri;
    File myImageDir; //카메라로 찍은 사진을 저장할 디렉토리
    String currentFileName;  //파일이름
    String userId;
    String titleAddress;
    String smallAddress;
    String address;

    final int SIGNUP_DIALOG_RADIO = 1;
    EditText login_ninkname_edit;
    TextView login_address_edit;
    ImageView login_coverimg_upload;
    ImageView login_profile_upload;
    ImageView login;
    TextView login_age_edit;
    private ImageView mPhotoImageView;

    private ImageView bPhotoImageView;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        GregorianCalendar calendar = new GregorianCalendar();

        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);

        day = calendar.get(Calendar.DAY_OF_MONTH);

        hour = calendar.get(Calendar.HOUR_OF_DAY);

        minute = calendar.get(Calendar.MINUTE);

        login_age_edit = (TextView) findViewById(R.id.login_age_edit);

        login_age_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(LoginActivity.this, dateSetListener, year, month, day).show();
//                date=String.valueOf(year)+String.valueOf(month)+String.valueOf(day);
            }
        });

        login = (ImageView) findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //login();
                String fcmToken = FirebaseInstanceId.getInstance().getToken();
                Log.e("fcmToken",String.valueOf(fcmToken));
                if(fcmToken == null  ){
                    return;
                }
                String nickname = login_ninkname_edit.getText().toString().trim();
                String address = login_address_edit.getText().toString();
                String userId = UUID.randomUUID().toString().replace('-', 'a');
                String date = login_age_edit.getText().toString().trim();
                //StringTokenizer slashTokenizer=new StringTokenizer(address,"/");
                int i = 0;
                StringTokenizer st = new StringTokenizer(address, "/");

                while(st.hasMoreTokens()) {
                    if(i==1) {
                        smallAddress = st.nextToken();
                        Log.e("스몰토큰", smallAddress);
                    }
                    else if(i==2) {
                        area5 = st.nextToken();
                        Log.e("스몰토큰",smallAddress);
                    }

                    i++;
                }
                i=0;
                StringTokenizer addressToken =new StringTokenizer(smallAddress," ");
                int count=addressToken.countTokens();
                if(count==3) {
                    while (addressToken.hasMoreTokens()) {
                        if (i == 1) {
                            area1 = addressToken.nextToken();
                            Log.e("area1", area1);
                        } else if (i == 2) {
                            area2 = addressToken.nextToken();
                            Log.e("area2", area2);
                        } else if (i==3) {
                            area4 = addressToken.nextToken();
                            area3= "";
                            Log.e("area4", area4);
                        }
                        i++;
                    }
                } else {
                    while (addressToken.hasMoreTokens()) {
                        if (i == 1) {
                            area1 = addressToken.nextToken();
                            Log.e("area1", area1);
                        } else if (i == 2) {
                            area2 = addressToken.nextToken();
                            Log.e("area2", area2);
                        } else if (i == 3) {
                            area3 = addressToken.nextToken();
                            Log.e("area3", area3);
                        } else if(i==4){
                            area4 = addressToken.nextToken();
                            Log.e("area4", area4);
                        }
                        i++;
                    }
                }

//                titleAddress=st.
//                address=stringTokenizer.nextToken();



                    /*Log.e("tokenid",fcmToken);
                    Log.e("uuid",userId);
                    Log.e("area입력",area1);
                    Log.e("area입력",area2);
//                    Log.e("area입력",area3);
                    Log.e("area입력",area4);
                    Log.e("area입력",area5);*/
                    if(userId!=null && nickname!=null && date!=null &&area4!=null&&!upLoadfiles.isEmpty()) {
                        new MemberInsert().execute(userId, nickname, date, fcmToken, area1, area2, area3, area4, area5);

                    } else {
                        Toast.makeText(LoginActivity.this,"모든 정보를 입력해주세요",Toast.LENGTH_SHORT).show();
                    }

            }
        });

        mPhotoImageView = (ImageView) findViewById(R.id.login_profile_upload);
        bPhotoImageView = (ImageView) findViewById(R.id.login_coverimg_upload);
        login_ninkname_edit = (EditText) findViewById(R.id.login_nickname_edit);
        login_ninkname_edit.requestFocus();

        login_address_edit = (TextView) findViewById(R.id.login_address_edit);
        login_address_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindMyLocation.class);
                Log.e("주소 인텐트", "넘어감");
                LoginActivity.this.startActivityForResult(intent, MAP_ADDRESS);
                //login_address_edit.setText(address);

//                Log.e("address",address);
            }
        });
        login_address_edit.requestFocus();

        login_coverimg_upload = (ImageView) findViewById(R.id.login_coverimg_upload);
        login_coverimg_upload.setOnClickListener(this);
        login_coverimg_upload.requestFocus();

        login_profile_upload = (ImageView) findViewById(R.id.login_profile_upload);
        login_profile_upload.setOnClickListener(this);
        login_profile_upload.requestFocus();

        // uuidtest = (TextView) findViewById(R.id.uuidtest);


    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {


        @Override

        public void onDateSet(DatePicker view, int year, int monthOfYear,

                              int dayOfMonth) {

            // TODO Auto-generated method stub
            String msg;
            if (monthOfYear > 9 && dayOfMonth > 9) {
                msg = String.format("%d%d%d", year, monthOfYear + 1, dayOfMonth);
            } else if (monthOfYear < 10 && dayOfMonth > 9) {
                msg = String.format("%d" + "0" + "%d" + "%d", year, monthOfYear + 1, dayOfMonth);
            } else if (monthOfYear > 9 && dayOfMonth < 10) {
                msg = String.format("%d" + "%d" + "0" + "%d", year, monthOfYear + 1, dayOfMonth);
            } else {
                msg = String.format("%d" + "0" + "%d" + "0" + "%d", year, monthOfYear + 1, dayOfMonth);
            }
            login_age_edit.setText(msg);

        }

    };

    private void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        bImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, bImageCaptureUri);

        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    // 앨범에서 이미지 가져오기
    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("req code, res code", requestCode + "" + resultCode + "");
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CROP_FROM_CAMERA: {

                final Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    mPhotoImageView.setImageBitmap(photo);
                    bPhotoImageView.setImageBitmap(photo);
                }
                break;
            }
            case PICK_FROM_ALBUM: {
                {

                    currentSelectedUri = data.getData();
                    if (currentSelectedUri != null) {
                        if (findImageFileNameFromUri(currentSelectedUri)) {
                            upLoadfiles.add(new UpLoadValueObject(new File(currentFileName), true));
                        }
                    } else {
                        Bundle extras = data.getExtras();
                        Bitmap returedBitmap = (Bitmap) extras.get("data");
                        if (tempSavedBitmapFile(returedBitmap)) {
                            Log.e("임시이미지파일저장", "저장됨");
                        } else {
                            Log.e("임시이미지파일저장", "실패");
                        }
                    }
                    cropIntent(currentSelectedUri);
                    break;

                }
            }
            case PICK_FROM_CAMERA: {
                upLoadfiles.add(new UpLoadValueObject(new File(myImageDir, currentFileName), false));
                cropIntent(currentSelectedUri);
                break;
            }
            case MAP_ADDRESS: {
                Log.e("주소정보 받아옴", "로그인");
                address = data.getStringExtra("address");
                login_address_edit.setText(address);

            }
        }
    }

    private boolean tempSavedBitmapFile(Bitmap tempBitmap) {
        boolean flag = false;
        try {
            currentFileName = "upload_" + (System.currentTimeMillis() / 1000);
            String fileSuffix = ".jpg";
            //임시파일을 실행한다.
            File tempFile = File.createTempFile(
                    currentFileName,            // prefix
                    fileSuffix,                   // suffix
                    myImageDir                   // directory
            );
            final FileOutputStream bitmapStream = new FileOutputStream(tempFile);
            tempBitmap.compress(Bitmap.CompressFormat.JPEG, 0, bitmapStream);
            upLoadfiles.add(new UpLoadValueObject(tempFile, true));
            if (bitmapStream != null) {
                bitmapStream.close();
            }
            currentSelectedUri = Uri.fromFile(tempFile);
            flag = true;
        } catch (IOException i) {
            Log.e("저장중 문제발생", i.toString(), i);
        }
        return flag;
    }

    private static final int RC_PERMISSION = 1;
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

               /* ActivityCompat.requestPermissions(FindMyLocation.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_STORAGE);*/
                return;
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        RC_PERMISSION);
                //성능이 향상된 Google의 LocationService를.설정한다.
                Location location = LocationServices.FusedLocationApi.getLastLocation(mClient);

                /*displayMessage(location);*/

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

    @Override
    public void onConnectionSuspended(int i) {

    }

    class UpLoadValueObject {
        File file; //업로드할 파일
        boolean tempFiles; //임시파일 유무

        public UpLoadValueObject(File file, boolean tempFiles) {
            this.file = file;
            this.tempFiles = tempFiles;
        }
    }


    private boolean findImageFileNameFromUri(Uri tempUri) {
        boolean flag = false;

        String[] IMAGE_DB_COLUMN = {MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = null;
        try {
            String imagePK = String.valueOf(ContentUris.parseId(tempUri));

            cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_DB_COLUMN,
                    MediaStore.Images.Media._ID + "=?",
                    new String[]{imagePK}, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                currentFileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                Log.e("fileName", String.valueOf(currentFileName));
                flag = true;
            }
        } catch (SQLiteException sqle) {
            Log.e("findImage....", sqle.toString(), sqle);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return flag;
    }

    private void cropIntent(Uri cropUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(cropUri, "image/*");

        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CROP_FROM_CAMERA);
    }

    @Override
    public void onClick(View v) {
        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakePhotoAction();
            }
        };
        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeAlbumAction();
            }
        };
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(this).
                setTitle("사진을 선택해주세요").
                setIcon(R.drawable.profileedit_camera).
                setNegativeButton("앨범선택", albumListener).
                setNeutralButton("취소", cancelListener).
                show();
    }

    protected void onStop() {
        super.onStop();
    }


    class MemberInsert extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
            //new LoginAsnc().execute(userId);
            if (aBoolean) {
                Intent intent = new Intent(LoginActivity.this, MoundaryFriendNewsActivity.class);
                PropertyManager.getInstance().setUserId(finalParsingUser);
                startActivity(intent);
                finish();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            File file = upLoadfiles.get(0).file;
            Response response = null;
            boolean flag = false;
            String userId = params[0];
            String nickName = params[1];
            String babyAge = params[2];
            String fcmToken =params[3];
            String area1=params[4];
            String area2=params[5];
            String area3=params[6];
            String area4=params[7];
            String area5=params[8];
            try {

                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                MultipartBody signupBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("fcmToken", fcmToken)
                        .addFormDataPart("uuid", userId)
                        .addFormDataPart("nickname", nickName)
                        .addFormDataPart("babyAge", babyAge)
                        .addFormDataPart("profileImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file))
                        .addFormDataPart("coverImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file))
                        .addFormDataPart("area1",area1)
                        .addFormDataPart("area2",area2)
                        .addFormDataPart("area3",area3)
                        .addFormDataPart("area4",area4)
                        .addFormDataPart("area5",area5)
                        .build();

                Request request = new Request.Builder()
                        .url(NetworkActivity.SERVER_LOGIN_URL)
                        .post(signupBody) //반드시 post로
                        .build();
                response = toServer.newCall(request).execute();

                flag = response.isSuccessful();

                //JSONObject jsonObject = new JSONObject(response.body().string());

                if (flag) {
                    e("가입결과1", response.message()); //읃답에 대한 메세지(OK)
                    parsingUser=response.body().string();
                    StringTokenizer stringTokenizer=new StringTokenizer(parsingUser,":");
                    while(stringTokenizer.hasMoreTokens()) {
                        getParsingUser = stringTokenizer.nextToken();
                        e("userId 파싱", getParsingUser);
                    }
                    finalParsingUser=getParsingUser.substring(1,25);
                    Log.e("서브스트링 후",finalParsingUser);

//                    e("가입응답바디", response.body().string());//json으로 변신

                } else {
                    e("가입결과2", response.message()); //읃답에 대한 메세지(OK)
                }
                return flag;
            } catch (UnknownHostException une) {
                e("signup", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("signup", uee.toString());
            } catch (Exception e) {
                e("signup", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return flag;
        }
    }
    private static final String TAG = "AppPermission";
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Log.d(TAG, "Permission always deny");

                }
                break;
        }

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to write the permission.
                    Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_STORAGE);
            } else {
                //사용자가 언제나 허락
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (!isSDCardAvailable()) {
            Toast.makeText(this, "SD 카드가 없어 종료 합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String currentAppPackage = getPackageName();

        myImageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), currentAppPackage);

        checkPermission();

        if (!myImageDir.exists()) {
            if (myImageDir.mkdirs()) {
                Toast.makeText(getApplication(), " 저장할 디렉토리가 생성 됨", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isSDCardAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }





}