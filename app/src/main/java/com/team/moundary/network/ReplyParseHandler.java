package com.team.moundary.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-12.
 */
public class ReplyParseHandler {
    public static ArrayList<ReplyEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {
        ArrayList<ReplyEntityObject> jsonAllList =null;
        JSONArray jsonArray =null;
        try {
            jsonAllList =new ArrayList<ReplyEntityObject>();
            jsonArray=new JSONArray(buf.toString());
            int jsonObjSize=jsonArray.length();
            for(int i =0 ; i<jsonObjSize; i++) {
                ReplyEntityObject entity= new ReplyEntityObject();

                JSONObject jData =jsonArray.getJSONObject(i);
                entity.postId=jData.optString("postId");
                entity.replyContent=jData.optString("replyContent");

            }
        } catch (JSONException je) {
            Log.e("DEBUG_TAG","getHttpURLConnection() --에러바생--",je);
        }
        return jsonAllList;
    }
}
