package com.team.moundary.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-15.
 */
public class FindFriendActivityHandler  {

    public static ArrayList<FindFriendActivityEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {

        ArrayList<FindFriendActivityEntityObject> jsonAllList = null;
        JSONObject myStoryJSON = null;
        try {
            Log.e("서버데이터", buf.toString());
            jsonAllList = new ArrayList<FindFriendActivityEntityObject>();
            myStoryJSON = new JSONObject(buf.toString());

            String msg = myStoryJSON.optString("msg");
            if(msg.equalsIgnoreCase("success")){
                JSONArray data = myStoryJSON.getJSONArray("data");
                int dataSize = data.length();
                for(int i = 0 ; i < dataSize ;i++){
                    JSONObject  myStory = data.getJSONObject(i);

                    FindFriendActivityEntityObject myshopEntityObject = new FindFriendActivityEntityObject();

                    myshopEntityObject._id = myStory.optString("_id");
                    myshopEntityObject.nickname = myStory.optString("nickname");
                    myshopEntityObject.profileThumbnail = myStory.optString("profileThumbnail");


                    JSONObject addressObj = myStory.optJSONObject("userAddress");
                    String area1 = addressObj.optString("area1");
                    String area2 = addressObj.optString("area2");
                    String area3 = addressObj.optString("area3");
                    String area4 = addressObj.optString("area4");
                    String area5 = addressObj.optString("area5");

                    myshopEntityObject.userAddress = area1 + " " + area2 + " " + area3 + " " + area4 + " " + area5;

                    jsonAllList.add(myshopEntityObject);
                }
            }


        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }

}
