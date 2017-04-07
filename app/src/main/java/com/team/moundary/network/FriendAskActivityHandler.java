package com.team.moundary.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-16.
 */
public class FriendAskActivityHandler {

    public static ArrayList<FriendAskActivityEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {

        ArrayList<FriendAskActivityEntityObject> jsonAllList = null;
        JSONObject myStoryJSON = null;
        try {
            Log.e("서버데이터", buf.toString());
            jsonAllList = new ArrayList<FriendAskActivityEntityObject>();
            myStoryJSON = new JSONObject(buf.toString());


            String msg = myStoryJSON.getString("msg");
            if(msg.equalsIgnoreCase("success")){
                JSONArray data = myStoryJSON.getJSONArray("data");
                int dataSize = data.length();
                for(int i = 0 ; i < dataSize ;i++){
                    JSONObject  myStory = data.getJSONObject(i);
                    FriendAskActivityEntityObject myFriendAskActivityEntityObject = new FriendAskActivityEntityObject();
                    myFriendAskActivityEntityObject._id = myStory.getString("_id");
                    myFriendAskActivityEntityObject.profileThumbnail = myStory.getString("profileThumbnail");
                    myFriendAskActivityEntityObject.nickname = myStory.getString("nickname");




                    jsonAllList.add(myFriendAskActivityEntityObject);
                }
            }

        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }
}
