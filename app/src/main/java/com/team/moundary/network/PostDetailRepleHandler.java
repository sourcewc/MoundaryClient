package com.team.moundary.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-09.
 */
public class PostDetailRepleHandler {

    public static ArrayList<PostDetailRepleEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {

        ArrayList<PostDetailRepleEntityObject> jsonAllList = null;
        JSONObject myStoryJSON = null;
        try {
            Log.e("서버데이터", buf.toString());
            jsonAllList = new ArrayList<PostDetailRepleEntityObject>();
            myStoryJSON = new JSONObject(buf.toString());

            String msg = myStoryJSON.optString("msg");
            if(msg.equalsIgnoreCase("success")){
                JSONArray data = myStoryJSON.getJSONArray("data");
                int dataSize = data.length();
                for(int i = 0 ; i < dataSize ;i++){
                    JSONObject  myStory = data.getJSONObject(i);
                    PostDetailRepleEntityObject myPostDetailRepleEntityObject = new PostDetailRepleEntityObject();
                    myPostDetailRepleEntityObject._id = myStory.optString("_id");
                    myPostDetailRepleEntityObject.nickname = myStory.optString("nickname");
                    myPostDetailRepleEntityObject.replyContent = myStory.optString("replyContent");
                    myPostDetailRepleEntityObject.profileThumbnail = myStory.optString("profileThumbnail");
                    myPostDetailRepleEntityObject.userId = myStory.optString("userId");
                    myPostDetailRepleEntityObject.replyDate = myStory.optString("replyDate");
                    myPostDetailRepleEntityObject.replyLikeCount = myStory.optString("replyLikeCount");
                    myPostDetailRepleEntityObject.myLike = myStory.optString("myLike");
                    jsonAllList.add(myPostDetailRepleEntityObject);
                }
            }

        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }
}