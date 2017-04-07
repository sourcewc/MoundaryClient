package com.team.moundary.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-10.
 */
public class FriendNewHandler  {

    public static ArrayList<FriendNewEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {

        ArrayList<FriendNewEntityObject> jsonAllList = null;
        JSONObject myStoryJSON = null;
        try {
            Log.e("서버데이터", buf.toString());
            jsonAllList = new ArrayList<FriendNewEntityObject>();
            myStoryJSON = new JSONObject(buf.toString());

            String msg = myStoryJSON.optString("msg");
            if(msg.equalsIgnoreCase("success")){
                JSONArray data = myStoryJSON.getJSONArray("data");
                int dataSize = data.length();
                for(int i = 0 ; i < dataSize ;i++){
                    JSONObject  myStory = data.getJSONObject(i);
                    FriendNewEntityObject myFriendNewEntityObject = new FriendNewEntityObject();
                    myFriendNewEntityObject._id = myStory.optString("_id");
                    myFriendNewEntityObject.category = String.valueOf(myStory.optInt("category"));
                    myFriendNewEntityObject.nickname = myStory.optString("nickname");
                    myFriendNewEntityObject.postImg = myStory.optString("postImg");
                    myFriendNewEntityObject.postContent = myStory.optString("postContent");
                    myFriendNewEntityObject.postDate = myStory.optString("postDate");
                    myFriendNewEntityObject.replyCount = String.valueOf(myStory.optInt("replyCount"));
                    myFriendNewEntityObject.postLikeCount = String.valueOf(myStory.optInt("postLikeCount"));
                    myFriendNewEntityObject.myLike = String.valueOf(myStory.optInt("myLike"));

                    jsonAllList.add(myFriendNewEntityObject);
                }
            }


        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }
}
