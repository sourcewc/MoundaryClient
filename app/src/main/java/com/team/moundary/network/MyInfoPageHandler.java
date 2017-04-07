package com.team.moundary.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ccei on 2016-08-09.
 */
public class MyInfoPageHandler {

    public static MyInfoPageEntityObject getJSONBloodRequestAllList(StringBuilder buf) {


        JSONObject myStoryJSON = null;
        MyInfoPageEntityObject myMyInfoPageEntityObject = null;
        try {
            Log.e("서버데이터", buf.toString());

            myStoryJSON = new JSONObject(buf.toString());

            String msg = myStoryJSON.optString("msg");
            if(msg.equalsIgnoreCase("success")){
                JSONObject data = myStoryJSON.optJSONObject("data");
                myMyInfoPageEntityObject = new MyInfoPageEntityObject();
                myMyInfoPageEntityObject.id = data.optString("_id");
                myMyInfoPageEntityObject.profileImg = data.optString("profileImg");
                myMyInfoPageEntityObject.coverImg = data.optString("coverImg");
                myMyInfoPageEntityObject.nickname = data.optString("nickname");
                myMyInfoPageEntityObject.babyAge = data.optString("babyAge");

                JSONObject addressObj = data.optJSONObject("userAddress");
                String area1 = addressObj.optString("area1");
                String area2 = addressObj.optString("area2");
                String area3 = addressObj.optString("area3");
                String area4 = addressObj.optString("area4");
                String area5 = addressObj.optString("area5");

                myMyInfoPageEntityObject.userAddress = area1 + " " + area2 + " " + area3 + " " + area4 + " " + area5;

            }


        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return myMyInfoPageEntityObject;
    }
}