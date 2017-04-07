package com.team.moundary.activity;

import android.Manifest;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.team.moundary.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

public class What_Think extends FontActivity {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private Uri mImageCaptureUri;
    private ImageView mPhotoImageView;
    private ImageView mButton;
    ImageView imageView1;
    EditText memberName1;
    ImageView senderBtn;
    String getPostId;

    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId

    ImageView backBtnImage;

    Uri currentSelectedUri; //업로드할 현재 이미지에 대한 Uri
    File myImageDir; //카메라로 찍은 사진을 저장할 디렉토리
    String currentFileName;  //파일이름

    private static final int IMAGE1 = 1;
    private int imageNumber;
    private UpLoadValueObject image1;

    private ArrayList<UpLoadValueObject> upLoadfiles = new ArrayList<>();

    class UpLoadValueObject {
        File file; //업로드할 파일
        boolean tempFiles; //임시파일 유무

        public UpLoadValueObject(File file, boolean tempFiles) {
            this.file = file;
            this.tempFiles = tempFiles;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.what_think);


        imageView1 = (ImageView) findViewById(R.id.img_area);
        memberName1 = (EditText) findViewById(R.id.edit_text);

        Intent intent = getIntent();
        getPostId = intent.getStringExtra("postId");

        senderBtn = (ImageView) findViewById(R.id.recommed_text_upload);
        senderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(image1 != null) {
                    upLoadfiles.add(image1);
                }
                Log.e("사진 파일이 있니?", ""+upLoadfiles.size());

                senderBtn.setEnabled(false);
                if (getPostId == null&& upLoadfiles != null && upLoadfiles.size() > 0 ) {
                    String query1 = memberName1.getText().toString().trim();
                    new FileUpLoadAsyncTask(query1).execute(upLoadfiles);
                } else if(getPostId!=null && upLoadfiles != null && upLoadfiles.size() > 0){
                    String query1 = memberName1.getText().toString().trim();
                    new FileEditAsyncTask(query1,getPostId).execute(upLoadfiles);


                } else {
                    Toast.makeText(What_Think.this, "사진 파일이 없습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mButton = (ImageView) findViewById(R.id.img_area);

        mPhotoImageView = (ImageView) findViewById(R.id.img_area);

        mButton.setOnClickListener(new PhotoClickListener(IMAGE1));

        backBtnImage = (ImageView) findViewById(R.id.back_img_btn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.setup_toolbar);

        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
        private String query1; //보낼쿼리
        String targetURL = String.format(NetworkActivity.MOUNDARY_WRITE_POST, userId);
        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");

        public FileUpLoadAsyncTask() {
        }

        public FileUpLoadAsyncTask(String query1) {
            this.query1 = query1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<UpLoadValueObject>... arrayLists) {
            Response response = null;
            File file = arrayLists[0].get(0).file;
            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();





                MultipartBody builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("postContent", query1)
                        .addFormDataPart("postImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file))
                        .build();
               /* builder.addFormDataPart("postContent", query1);
                builder.addFormDataPart("postImg" + i, file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));*/
//                int fileSize = arrayLists[0].size();

               /* for (int i = 0; i < fileSize; i++) {
                    File file = arrayLists[0].get(i).file;
                    builder.addFormDataPart("postImg" + i, file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                }*/
                RequestBody fileUploadBody = builder;
                //요청 세팅
                Request request = new Request.Builder()
                        .url(targetURL)
                        .post(fileUploadBody) //반드시 post로
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                   /* //비동기 방식
                          toServer.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response res) throws IOException {

                            }
                        });*/
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
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
            if (s.equalsIgnoreCase("success")) {
                Toast.makeText(What_Think.this, "파일업로드에 성공했습니다", Toast.LENGTH_LONG).show();

                int fileSize = upLoadfiles.size();

                for (int i = 0; i < fileSize; i++) {
                    UpLoadValueObject fileValue = upLoadfiles.get(i);
                    if (fileValue.tempFiles) {
                        fileValue.file.deleteOnExit(); //임시파일을 삭제한다
                    }
                }
                finish();
            } else {
                Toast.makeText(What_Think.this, "파일업로드에 실패했습니다", Toast.LENGTH_LONG).show();
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
        currentFileName = "upload_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        currentSelectedUri = Uri.fromFile(new File(myImageDir,currentFileName));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentSelectedUri);

        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    //앨범에서 이미지 가져오기
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
                // 임시 파일 삭제
                /*File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }*/
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
                        Toast.makeText(What_Think.this, "무언가 에러가 났엉ㅋ", Toast.LENGTH_LONG).show();
                    }
                }

                cropIntent(currentSelectedUri);
                break;
                //mImageCaptureUri = data.getData();
            }
            case PICK_FROM_CAMERA: {


                if(imageNumber ==IMAGE1 ){
                    image1 = new UpLoadValueObject(new File(myImageDir, currentFileName), false);
                }else{
                    Toast.makeText(What_Think.this, "무언가 에러가 났엉ㅋ", Toast.LENGTH_LONG).show();
                }

                cropIntent(currentSelectedUri);
                break;
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

        //실제 Image Uri의 절대이름
        String[] IMAGE_DB_COLUMN = {MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = null;
        try {
            //Primary Key값을 추출
            String imagePK = String.valueOf(ContentUris.parseId(tempUri));
            //Image DB에 쿼리를 날린다.
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

            dialog = new android.app.AlertDialog.Builder(What_Think.this).
                    setIcon(R.drawable.profileedit_camera).
                    setTitle("사진을 선택해주세요")
                    .setPositiveButton("앨범선택", albumListener)
                    .setNegativeButton("사진촬영", cameraListener)
                    .setNeutralButton("취소", cancelListener).create();

            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class FileEditAsyncTask extends AsyncTask<ArrayList<UpLoadValueObject>, Void, String> {
        private String query1; //보낼쿼리
        private String postId;
        String targetURL = String.format(NetworkActivity.MOUNDARY_MY_POST_EDIT, userId);
        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");

        public FileEditAsyncTask() {
        }

        public FileEditAsyncTask(String query1,String postId) {
            this.query1 = query1;
            this.postId =postId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<UpLoadValueObject>... arrayLists) {
            Response response = null;
            File file = arrayLists[0].get(0).file;

            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();


                MultipartBody.Builder builder = new MultipartBody.Builder();
    /*            MultipartBody builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("postId",postId)
                        .addFormDataPart("postContent", query1)
                        .addFormDataPart("postImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file))
                        .build();*/
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("postId",postId);
                builder.addFormDataPart("postContent", query1);
                builder.addFormDataPart("postImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));

                /*int fileSize = arrayLists[0].size();

                for (int i = 0; i < fileSize; i++) {
                    File file = arrayLists[0].get(i).file;
                    builder.addFormDataPart("postImg" + i, file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                }
*/












                RequestBody fileUploadBody = builder.build();
                //요청 세팅
                Request request = new Request.Builder()
                        .url(targetURL)
                        .put(fileUploadBody) //반드시 post로
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                   /* //비동기 방식
                          toServer.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response res) throws IOException {

                            }
                        });*/
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
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
            if (s.equalsIgnoreCase("success")) {
                Toast.makeText(What_Think.this, "파일업로드에 성공했습니다", Toast.LENGTH_LONG).show();

                int fileSize = upLoadfiles.size();

                for (int i = 0; i < fileSize; i++) {
                    UpLoadValueObject fileValue = upLoadfiles.get(i);
                    if (fileValue.tempFiles) {
                        fileValue.file.deleteOnExit(); //임시파일을 삭제한다
                    }
                }
                finish();
            } else {
                Toast.makeText(What_Think.this, "파일업로드에 실패했습니다", Toast.LENGTH_LONG).show();
            }
            senderBtn.setEnabled(true);
        }
    }
}