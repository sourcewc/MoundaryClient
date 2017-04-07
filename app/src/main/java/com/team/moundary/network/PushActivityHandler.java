package com.team.moundary.network;

import android.util.Log;

import com.team.moundary.network.PushActivityEntityObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-23.
 */
public class PushActivityHandler {

    public static ArrayList<PushActivityEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {

        ArrayList<PushActivityEntityObject> jsonAllList = null;
        JSONObject myStoryJSON = null;
        try {
            Log.e("서버데이터", buf.toString());
            jsonAllList = new ArrayList<PushActivityEntityObject>();
            myStoryJSON = new JSONObject(buf.toString());

            String msg = myStoryJSON.optString("msg");
            if(msg.equalsIgnoreCase("success")){
                JSONArray data = myStoryJSON.getJSONArray("data");
                int dataSize = data.length();
                for(int i = 0 ; i < dataSize ;i++){
                    JSONObject  myStory = data.getJSONObject(i);
                    PushActivityEntityObject myStoreEntityObject = new PushActivityEntityObject();
                    myStoreEntityObject.pushType = myStory.optString("pushType");
                    myStoreEntityObject.postId = myStory.optString("postId");
                    myStoreEntityObject.category = myStory.optString("category");
                    myStoreEntityObject.pushDate = myStory.optString("pushDate");
                    myStoreEntityObject.pusherId = myStory.optString("pusherId");
                    myStoreEntityObject.pusherNickname = myStory.optString("pusherNickname");
                    myStoreEntityObject.img = myStory.optString("img");
                    myStoreEntityObject.confirmed =myStory.optString("confirmed");
                    myStoreEntityObject.pullerId = myStory.optString("pullerId");
                    myStoreEntityObject.content = myStory.optString("content");

                    jsonAllList.add(myStoreEntityObject);
                }
            }


        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }
}