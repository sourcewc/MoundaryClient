package com.team.moundary.network;

import android.util.Log;

import com.team.moundary.network.ShopActivityEntityObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-08.
 */
public class ShopActivityHandler {

    public static ArrayList<ShopActivityEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {

        ArrayList<ShopActivityEntityObject> jsonAllList = null;
        JSONObject myStoryJSON = null;
        try {
            Log.e("서버데이터", buf.toString());
            jsonAllList = new ArrayList<ShopActivityEntityObject>();
            myStoryJSON = new JSONObject(buf.toString());

            String msg = myStoryJSON.optString("msg");
            if(msg.equalsIgnoreCase("success")){
                JSONArray data = myStoryJSON.getJSONArray("data");
                int dataSize = data.length();
                for(int i = 0 ; i < dataSize ;i++){
                    JSONObject  myStory = data.getJSONObject(i);

                    ShopActivityEntityObject myshopEntityObject = new ShopActivityEntityObject();

                    myshopEntityObject.id = myStory.optString("_id");
                    myshopEntityObject.category = String.valueOf(myStory.optInt("category"));
                    myshopEntityObject.nickname = myStory.optString("nickname");
                    myshopEntityObject.postImg = myStory.optString("postImg");
                    myshopEntityObject.postContent = myStory.optString("postContent");
                    myshopEntityObject.postDate = myStory.optString("postDate");
                    myshopEntityObject.replyCount = String.valueOf(myStory.optInt("replyCount"));
                    myshopEntityObject.due=myStory.optString("due");

                    myshopEntityObject.postCount = myStory.optString("postCount");

                    JSONObject addressObj = myStory.optJSONObject("postAddress");
                    String area1 = addressObj.optString("area1");
                    String area2 = addressObj.optString("area2");
                    String area3 = addressObj.optString("area3");
                    String area4 = addressObj.optString("area4");
                    String area5 = addressObj.optString("area5");

                    myshopEntityObject.postAddress = area1 + " " + area2 + " " + area3 + " " + area4 + " " + area5;
                    myshopEntityObject.postLikeCount = String.valueOf(myStory.optInt("postLikeCount"));
                    myshopEntityObject.myLike = String.valueOf(myStory.optInt("myLike"));

                    /*JSONArray likeUsers = myStory.getJSONArray("postLikeUsers");
                    if(likeUsers.length() > 0 ){
                        myshopEntityObject.postLikeUsers = String.valueOf(likeUsers.length());
                    }else{
                        myshopEntityObject.postLikeUsers = "0";
                    }*/

                    jsonAllList.add(myshopEntityObject);
                }
            }


        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }

}
