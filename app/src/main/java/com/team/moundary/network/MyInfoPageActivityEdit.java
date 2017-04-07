package com.team.moundary.network;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.team.moundary.R;
import com.team.moundary.activity.FontActivity;
import com.team.moundary.activity.NetworkActivity;
import com.team.moundary.activity.PropertyManager;
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
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class MyInfoPageActivityEdit extends FontActivity {
    ArrayAdapter<String> adapter;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int MAP_ADDRESS = 3;

    private static final int IMAGE1 = 1;
    private int imageNumber;
    private UpLoadValueObject image1;

    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId

    int year, month, day, hour, minute;
    private Uri mImageCaptureUri;
    private Uri bImageCaptureUri;
    Uri currentSelectedUri;
    File myImageDir; //카메라로 찍은 사진을 저장할 디렉토리
    String currentFileName;  //파일이름
    private ImageView mPhotoImageView;
    private ImageView bPhotoImageView;
    private ImageView mButton;
    private ImageView bButton;
    EditText friendname;
    TextView friendaddress;
    TextView myBabyInfo;
    ImageView coverImg;
    ImageView criclephoto;
    ImageView senderBtn;
    String smallAddress;
    String address;
    String area1 = "";
    String area2 = "";
    String area3 = "";
    String area4 = "";
    String area5 = "";
    private ArrayList<UpLoadValueObject> upLoadfiles = new ArrayList<>();

    class UpLoadValueObject {
        File file; //업로드할 파일
        boolean tempFiles; //임시파일 유무

        public UpLoadValueObject(File file, boolean tempFiles) {
            this.file = file;
            this.tempFiles = tempFiles;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info_page_edit);


        mButton = (ImageView) findViewById(R.id.my_profile_photo);
        bButton = (ImageView) findViewById(R.id.coverimg);



        bButton.setOnClickListener(new PhotoClickListener(IMAGE1));
        mButton.setOnClickListener(new PhotoClickListener(IMAGE1));

        mPhotoImageView = (ImageView) findViewById(R.id.my_profile_photo);
        bPhotoImageView = (ImageView) findViewById(R.id.coverimg);

        coverImg = (ImageView) findViewById(R.id.coverimg);
        criclephoto = (ImageView) findViewById(R.id.my_profile_photo);
        friendname = (EditText) findViewById(R.id.user_nickname);

        friendaddress = (TextView) findViewById(R.id.user_location);
        friendaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), FindMyLocation.class);
                startActivityForResult(intent, MAP_ADDRESS);
            }
        });

        GregorianCalendar calendar = new GregorianCalendar();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        myBabyInfo = (TextView) findViewById(R.id.baby_age_selecter);

        myBabyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {new DatePickerDialog(MyInfoPageActivityEdit.this, dateSetListener, year, month, day).show();}
        });

        senderBtn = (ImageView) findViewById(R.id.recommed_text_upload);
        senderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String query1 = friendname.getText().toString().trim();
                String query2 = myBabyInfo.getText().toString().trim();
                String userAddress = friendaddress.getText().toString();
                if(image1 != null) {
                    upLoadfiles.add(image1);
                }

                senderBtn.setEnabled(false);
                int i = 0;
                if(address!=null) {


                    StringTokenizer st = new StringTokenizer(address, "/");
                    while (st.hasMoreTokens()) {
                        if (i == 1) {
                            smallAddress = st.nextToken();
                            Log.e("스몰토큰", smallAddress);
                        } else if (i == 2) {
                            area5 = st.nextToken();
                            Log.e("스몰토큰", smallAddress);
                        }

                        i++;
                    }
                } else {
                    Toast.makeText(MyInfoPageActivityEdit.this,"주소가 입력되지 않았습니다",Toast.LENGTH_SHORT).show();
                }
                i=0;

                if(smallAddress!=null) {
                    StringTokenizer addressToken = new StringTokenizer(smallAddress, " ");
                    int count = addressToken.countTokens();
                    if (count == 3) {
                        while (addressToken.hasMoreTokens()) {
                            if (i == 1) {
                                area1 = addressToken.nextToken();
                                Log.e("area1", area1);
                            } else if (i == 2) {
                                area2 = addressToken.nextToken();
                                Log.e("area2", area2);
                            } else if (i == 3) {
                                area4 = addressToken.nextToken();
                                area3 = "";
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
                            } else if (i == 4) {
                                area4 = addressToken.nextToken();
                                Log.e("area4", area4);
                            }
                            i++;
                        }
                    }
                }else {
                    Toast.makeText(MyInfoPageActivityEdit.this,"주소가 입력되지 않았습니다",Toast.LENGTH_SHORT).show();
                }

                new FileUpLoadAsyncTask(query1,query2,area1,area2,area3,area4,area5).execute(upLoadfiles);
            }
        });

        new AsyncBloodJSONList().execute("");
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
            myBabyInfo.setText(msg);
        }

    };

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

    @Override
    protected void onResume() {
        super.onResume();

        //new AsyncBloodJSONList().execute("");

        if (!isSDCardAvailable()) {
            Toast.makeText(this, "SD 카드가 없어 종료 합니다.", Toast.LENGTH_SHORT).show();
            //finish();
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

    public boolean isSDCardAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    boolean flag;

    private class FileUpLoadAsyncTask extends AsyncTask<ArrayList<UpLoadValueObject>, Void, String> {
        private String query1;
        private String query2;//보낼쿼리
        private String area1;
        private String area2;
        private String area3;
        private String area4;
        private String area5;
        String targetURL = String.format(NetworkActivity.MOUNDARY_MY_INFO_EDIT,userId);
        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");

        public FileUpLoadAsyncTask() {}

        public FileUpLoadAsyncTask(String query1, String query2,String area1,String area2,String area3,String area4,String area5) {
            this.query1 = query1;
            this.query2 = query2;
            this.area1=area1;
            this.area2=area2;
            this.area3=area3;
            this.area4=area4;
            this.area5=area5;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<UpLoadValueObject>... arrayLists) {
            Response response = null;
            if (arrayLists[0].size() < 1)
                return null;
            File file = arrayLists[0].get(0).file;
            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                MultipartBody builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("nickname", query1)
                        .addFormDataPart("babyAge",query2)
                        .addFormDataPart("profileImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file))
                        .addFormDataPart("coverImg",file.getName(),RequestBody.create(IMAGE_MIME_TYPE,file))
                        .addFormDataPart("area1",area1)
                        .addFormDataPart("area2",area2)
                        .addFormDataPart("area3",area3)
                        .addFormDataPart("area4",area4)
                        .addFormDataPart("area5",area5)
                        .build();

                RequestBody fileUploadBody = builder;
                //요청 세팅
                Request request = new Request.Builder()
                        .url(targetURL)
                        .put(fileUploadBody) //반드시 post로
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();

                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                Log.i("leelog responseCode", responseCode + "");
                if (flag) {
                    e("response결과", responseCode + "---" + response.message()); //읃답에 대한 메세지(OK)
                    e("response응답바디", response.body().string()); //json으로 변신
                    return "success";
                }
            } catch (UnknownHostException une) {
                e("aa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("bb", uee.toString());
            } catch (Exception e) {
                e("cc", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return "fail";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                Toast.makeText(getApplicationContext(), "사진을 수정해주세요.", Toast.LENGTH_LONG).show();
                senderBtn.setEnabled(true);
                return;
            }
            if (s.equalsIgnoreCase("success")) {
                Toast.makeText(MyInfoPageActivityEdit.this, "파일업로드에 성공했습니다", Toast.LENGTH_LONG).show();
                int fileSize = upLoadfiles.size();

                for (int i = 0; i < fileSize; i++) {
                    UpLoadValueObject fileValue = upLoadfiles.get(i);
                    if (fileValue.tempFiles) {
                        fileValue.file.deleteOnExit(); //임시파일을 삭제한다
                    }
                }
                finish();
            } else {
                Toast.makeText(MyInfoPageActivityEdit.this, "파일업로드에 실패했습니다", Toast.LENGTH_LONG).show();
            }
            senderBtn.setEnabled(true);
        }
    }

    public boolean isAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        currentSelectedUri = Uri.fromFile(new File(myImageDir, currentFileName));

        currentSelectedUri = Uri.fromFile(new File(myImageDir, currentFileName));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentSelectedUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentSelectedUri);

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
        if (resultCode != RESULT_OK) {
            if (imageNumber == IMAGE1) {
                image1 =null;
            }
            return;
        }
        switch (requestCode) {
            case CROP_FROM_CAMERA: {

                final Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    mPhotoImageView.setImageBitmap(photo);
                    Log.e("asdasdasdadad", ""+mPhotoImageView);
                }

                break;
            }
            case PICK_FROM_ALBUM: {
                currentSelectedUri = data.getData();
                if (currentSelectedUri == null) {
                    Bundle extras = data.getExtras();
                    Bitmap returedBitmap = (Bitmap) extras.get("data");
                    if (tempSavedBitmapFile(returedBitmap)) {
                        Log.e("임시이미지파일저장", "저장됨");
                    } else {
                        Log.e("임시이미지파일저장", "실패");
                    }
                }
                if (findImageFileNameFromUri(currentSelectedUri)) {
                    if (imageNumber == IMAGE1) {
                        image1 = new UpLoadValueObject(new File(currentFileName), false);
                    } else{
                        Toast.makeText(MyInfoPageActivityEdit.this, "무언가 에러가 났엉ㅋ", Toast.LENGTH_LONG).show();
                    }
                }

                cropIntent(currentSelectedUri);
                break;
            }
            case PICK_FROM_CAMERA: {
                if(imageNumber ==IMAGE1 ){
                    image1 = new UpLoadValueObject(new File(myImageDir, currentFileName), false);
                }else{
                    Toast.makeText(MyInfoPageActivityEdit.this, "무언가 에러가 났엉ㅋ", Toast.LENGTH_LONG).show();
                }

                cropIntent(currentSelectedUri);
                break;
            }case MAP_ADDRESS: {
                Log.e("주소정보 받아옴","수정");
                address=data.getStringExtra("address");
                friendaddress.setText(address);
            }
        }
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

    class PhotoClickListener implements View.OnClickListener {
        int type;

        public PhotoClickListener(int type) {
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            android.app.AlertDialog dialog = null;

            mPhotoImageView = (ImageView) v;
            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    imageNumber = type;
                    doTakePhotoAction();
                }
            };

            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    imageNumber = type;
                    doTakeAlbumAction();
                }
            };

            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };

            dialog = new android.app.AlertDialog.Builder(MyInfoPageActivityEdit.this).
                    setIcon(R.drawable.profileedit_camera).
                    setTitle("사진을 선택해주세요")
                    .setPositiveButton("앨범선택", albumListener)
                    //  .setNegativeButton("사진촬영", cameraListener)
                    .setNeutralButton("취소", cancelListener).create();

            dialog.show();
        }
    }

    class AsyncBloodJSONList extends AsyncTask<String, Integer, MyInfoPageEntityObject> {
        ProgressDialog dialog;
        String targetURL = String.format(NetworkActivity.MY_PAGE_NETWORK,userId,userId);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MyInfoPageActivityEdit.this,
                    "", "잠시만 기다려 주세요 ...", true);
        }

        @Override
        protected MyInfoPageEntityObject doInBackground(
                String... params) {
            Response response = null;
            try {
                //OKHttp3사용ㄴ
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
                    return MyInfoPageHandler.getJSONBloodRequestAllList(new StringBuilder(responseBody.string()));
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
        protected void onPostExecute(MyInfoPageEntityObject result) {
            dialog.dismiss();
            if (result != null) {
//                Glide.with(MyInfoPageActivityEdit.this).load(result.profileImg).override(510, 510).centerCrop().into(criclephoto);
//                Glide.with(MyInfoPageActivityEdit.this).load(result.coverImg).override(510, 510).centerCrop().into(coverImg);
                myBabyInfo.setText(result.babyAge);
                friendname.setText(result.nickname);
                friendaddress.setText(result.userAddress);
            }
        }
    }
}