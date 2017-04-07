package com.team.moundary.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shin on 2016-08-17.
 */
public class FriendManagerHandler {
    public static ArrayList<FriendManagerEntityObject> getJSONAllManager(StringBuilder buf) {
        ArrayList<FriendManagerEntityObject> jsonAllMagnager =null;
        JSONArray jsonArray =null;
        try {
            jsonAllMagnager =new ArrayList<FriendManagerEntityObject>();
            jsonArray=new JSONArray(buf.toString());
            int jsonObjSize=jsonArray.length();
            for(int i =0 ; i<jsonObjSize; i++) {
                FriendManagerEntityObject entity= new FriendManagerEntityObject();

                JSONObject jData =jsonArray.getJSONObject(i);
                entity.oppositeUserId=jData.optString("oppositeUserId");

            }
        } catch (JSONException je) {
            Log.e("DEBUG_TAG","getHttpURLConnection() --에러바생--",je);
        }
        return jsonAllMagnager;
    }
}
