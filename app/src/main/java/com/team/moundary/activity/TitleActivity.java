package com.team.moundary.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.team.moundary.R;

public class TitleActivity extends Activity {

    Handler hd;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_layout);

        PropertyManager propertyManager = PropertyManager.getInstance();
        final String userId = propertyManager.getUserId();
        hd = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(!TextUtils.isEmpty(userId) && userId.length() > 10){
                    Log.e("저장성공" ,userId);
                    Intent intent = new Intent(TitleActivity.this, MoundaryFriendNewsActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Log.e("저장실패" ,userId);
                    Intent intent = new Intent(TitleActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

    }
    @Override
    protected void onResume() {
        super.onResume();
        hd.sendEmptyMessageDelayed(0, 1500);
    }
    /*class LoginAsynck extends AsyncTask<String,Void,Boolean> {
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Intent i = new Intent(TitleActivity.this, MoundaryFriendNewsActivity.class);
                startActivity(i);
                finish();
            }
        }
        @Override
        protected Boolean doInBackground(String... strings) {
            Response loginresponse = null;
            boolean flagConn = false;
            boolean flagJson = false;
            String userId = strings[0];
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                RequestBody loginBody = new FormBody.Builder()
                        .add("uuid",userId ) //기본 쿼리
                        .build();
                Request request = new Request.Builder()
                        .url(LoginActivityNetWork.SERVER_URL_BLOOD_JSON_ALL_SELECT)
                        .post(loginBody)
                        .build();
                loginresponse = toServer.newCall(request).execute();

                flagConn = loginresponse.isSuccessful();

                if (flagConn) {
                    JSONObject jsonObject = new JSONObject(loginresponse.body().string());
                    String resultMessage = jsonObject.getString("msg");
                    if(resultMessage.equalsIgnoreCase("success")){
                        flagJson = true;
                    }else{
                        flagJson = false;
                    }
                }
                return flagJson;
            } catch (Exception e) {
                e("LoginAsynck", e.toString(),e);
            } finally {
                if (loginresponse != null) {
                    loginresponse.close();
                }
            }
            return flagJson;
        }
    }*/
}