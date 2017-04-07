package com.team.moundary.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.moundary.R;
import com.team.moundary.network.MyInfoPageActivityEdit;
import com.team.moundary.network.MyInfoPageEntityObject;
import com.team.moundary.network.MyInfoPageHandler;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei on 2016-07-28.
 */
public class MyInfoPageActivity extends FontActivity {

    ImageView backBtnImage;
    ArrayAdapter<String> adapter;

    MyInfoPageEntityObject myInfoPageEntityObjects;
    ImageView coverImg;
    ImageView criclephoto;
    TextView friendname;
    TextView babyAge;
    TextView friendaddress;

    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info_page);

        backBtnImage = (ImageView) findViewById(R.id.back_img_btn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.info_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(null);


        coverImg=(ImageView)findViewById(R.id.coverimg);
        criclephoto=(ImageView)findViewById(R.id.my_profile_photo) ;
        friendname=(TextView)findViewById(R.id.user_nickname);
        babyAge=(TextView)findViewById(R.id.baby_age);
        friendaddress=(TextView)findViewById(R.id.user_location);



        Intent intent=getIntent();
        String check=intent.getStringExtra("주소");
        friendaddress.setText(String.valueOf(check));
        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncBloodJSONList().execute("");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class AsyncBloodJSONList extends AsyncTask<String, Integer, MyInfoPageEntityObject> {
        NetworkDialog netwrokDialog = new NetworkDialog(MyInfoPageActivity.this);
        String targetURL= String.format(NetworkActivity.MY_PAGE_NETWORK,userId,userId);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netwrokDialog.show();
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
            }finally{
                if(response != null){
                    response.close();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(MyInfoPageEntityObject result) {
            netwrokDialog.dismiss();
            if (result != null) {
                Glide.with(MyInfoPageActivity.this).load(result.profileImg).override(510, 510).centerCrop().into(criclephoto);
                Glide.with(MyInfoPageActivity.this).load(result.coverImg).override(510, 510).centerCrop().into(coverImg);
                babyAge.setText(result.babyAge);
                friendname.setText(result.nickname);
                friendaddress.setText(result.userAddress);
            }
        }
    }

    public void edit_btn(View v){
        Intent intent=new Intent(getApplicationContext(),MyInfoPageActivityEdit.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}





