package com.team.moundary.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-24.
 */
public class RecommendHandler  {

    public static ArrayList<ShopActivityEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {
        ShopActivityEntityObject jsonAllList2;

        ArrayList<ShopActivityEntityObject> jsonAllList = null;
        JSONObject myStoryJSON = null;
        try {
            Log.e("서버데이터", buf.toString());
            jsonAllList = new ArrayList<ShopActivityEntityObject>();
            myStoryJSON = new JSONObject(buf.toString());

            String msg = myStoryJSON.optString("msg");
            if(msg.equalsIgnoreCase("success")){

                JSONObject postcount=myStoryJSON.getJSONObject("page");
                jsonAllList2 = new ShopActivityEntityObject();

                jsonAllList2.postCount = postcount.optString("postCount");
                jsonAllList2.endPost = postcount.optString("endPost");
                jsonAllList.add(jsonAllList2);

                ShopActivityEntityObject shopActivityEntityObject = new ShopActivityEntityObject();
                JSONObject addressObj = myStoryJSON.getJSONObject("myAddress");

                String area1 = addressObj.optString("area1");
                String area2 = addressObj.optString("area2");
                String area3 = addressObj.optString("area3");
                String area4 = addressObj.optString("area4");
                String area5 = addressObj.optString("area5");

                shopActivityEntityObject.postAddress = area1 + " " + area2 + " " + area3 + " " + area4 + " " + area5;

                jsonAllList.add(shopActivityEntityObject);
            }


        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }

}