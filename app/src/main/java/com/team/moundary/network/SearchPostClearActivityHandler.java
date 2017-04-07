package com.team.moundary.network;

import android.util.Log;

import com.team.moundary.network.SearchPostClearActivityEntityObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-18.
 */
public class SearchPostClearActivityHandler {

    public static ArrayList<SearchPostClearActivityEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {

        ArrayList<SearchPostClearActivityEntityObject> jsonAllList = null;
        JSONObject myStoryJSON = null;
        try {
            Log.e("서버데이터", buf.toString());
            jsonAllList = new ArrayList<SearchPostClearActivityEntityObject>();
            myStoryJSON = new JSONObject(buf.toString());

            String msg = myStoryJSON.getString("msg");
            if(msg.equalsIgnoreCase("success")){
                JSONArray data = myStoryJSON.getJSONArray("data");
                int dataSize = data.length();
                for(int i = 0 ; i < dataSize ;i++){
                    JSONObject  myStory = data.getJSONObject(i);

                    SearchPostClearActivityEntityObject mySearchPostClearActivityEntityObject = new SearchPostClearActivityEntityObject();

                    mySearchPostClearActivityEntityObject._id = myStory.getString("_id");
                    mySearchPostClearActivityEntityObject.userId = myStory.getString("userId");
                    mySearchPostClearActivityEntityObject.due = myStory.optString("due");
                    mySearchPostClearActivityEntityObject.postContent = myStory.getString("postContent");
                    mySearchPostClearActivityEntityObject.postImg = myStory.getString("postImg");
                    mySearchPostClearActivityEntityObject.nickname = myStory.getString("nickname");
                    mySearchPostClearActivityEntityObject.profileThumbnail = myStory.getString("profileThumbnail");
                    mySearchPostClearActivityEntityObject.replyCount = myStory.getString("replyCount");
                    mySearchPostClearActivityEntityObject.postLikeCount = myStory.getString("postLikeCount");
                    mySearchPostClearActivityEntityObject.myLike = myStory.getString("myLike");
                    mySearchPostClearActivityEntityObject.postDate=myStory.getString("postDate");

                    JSONObject addressObj = myStory.getJSONObject("postAddress");
                    String area1 = addressObj.optString("area1");
                    String area2 = addressObj.optString("area2");
                    String area3 = addressObj.optString("area3");
                    String area4 = addressObj.optString("area4");
                    String area5 = addressObj.optString("area5");

                    mySearchPostClearActivityEntityObject.postAddress = area1 + " " + area2 + " " + area3 + " " + area4 + " " + area5;

                    jsonAllList.add(mySearchPostClearActivityEntityObject);
                }
            }


        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }

}

