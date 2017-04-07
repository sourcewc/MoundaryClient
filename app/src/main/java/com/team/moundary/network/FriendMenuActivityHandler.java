package com.team.moundary.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-12.
 */
public class FriendMenuActivityHandler {

    public static ArrayList<FriendMenuActivityEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {

        ArrayList<FriendMenuActivityEntityObject> jsonAllList = null;
        JSONObject myStoryJSON = null;
        try {
            Log.e("서버데이터", buf.toString());
            jsonAllList = new ArrayList<FriendMenuActivityEntityObject>();
            myStoryJSON = new JSONObject(buf.toString());


            String msg = myStoryJSON.getString("msg");
            if(msg.equalsIgnoreCase("success")){
                JSONArray data = myStoryJSON.getJSONArray("data");
                int dataSize = data.length();
                for(int i = 0 ; i < dataSize ;i++){
                    JSONObject  myStory = data.getJSONObject(i);
                    FriendMenuActivityEntityObject myFriendMenuActivityEntityObject = new FriendMenuActivityEntityObject();
                    myFriendMenuActivityEntityObject._id = myStory.getString("_id");
                    myFriendMenuActivityEntityObject.profileThumbnail = myStory.getString("profileThumbnail");
                    myFriendMenuActivityEntityObject.nickname = myStory.getString("nickname");


                    JSONObject addressObj = myStory.optJSONObject("userAddress");
                    String area1 = addressObj.getString("area1");
                    String area2 = addressObj.getString("area2");
                    String area3 = addressObj.getString("area3");
                    String area4 = addressObj.getString("area4");
                    String area5 = addressObj.getString("area5");

                    myFriendMenuActivityEntityObject.userAddress = area1 + " " + area2 + " " + area3 + " " + area4 + " " + area5;

                    jsonAllList.add(myFriendMenuActivityEntityObject);
                }
            }

        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }
}
