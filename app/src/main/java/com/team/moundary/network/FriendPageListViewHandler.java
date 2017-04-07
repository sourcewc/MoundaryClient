package com.team.moundary.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-09.
 */
public class FriendPageListViewHandler {

    public static ArrayList<FriendPageListViewEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {

        ArrayList<FriendPageListViewEntityObject> jsonAllList = null;
        JSONObject myStoryJSON = null;
        try {
            Log.e("서버데이터", buf.toString());
            jsonAllList = new ArrayList<FriendPageListViewEntityObject>();
            myStoryJSON = new JSONObject(buf.toString());

            String msg = myStoryJSON.optString("msg");
            if(msg.equalsIgnoreCase("success")){
                JSONArray data = myStoryJSON.getJSONArray("data");
                int dataSize = data.length();
                for(int i = 0 ; i < dataSize ;i++){
                    JSONObject  myStory = data.getJSONObject(i);
                    FriendPageListViewEntityObject myStoreEntityObject = new FriendPageListViewEntityObject();
                    myStoreEntityObject.id = myStory.optString("_id");
                    myStoreEntityObject.category = String.valueOf(myStory.optInt("category"));
                    myStoreEntityObject.nickname = myStory.optString("nickname");
                    myStoreEntityObject.postImg = myStory.optString("postImg");
                    myStoreEntityObject.postContent = myStory.optString("postContent");
                    myStoreEntityObject.postDate = myStory.optString("postDate");
                    myStoreEntityObject.replyCount = String.valueOf(myStory.optInt("replyCount"));
                    myStoreEntityObject.postLikeCount = String.valueOf(myStory.optInt("postLikeCount"));
                    myStoreEntityObject.myLike = String.valueOf(myStory.optInt("myLike"));
                    jsonAllList.add(myStoreEntityObject);
                }
            }


        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }
}
