package com.team.moundary.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ccei on 2016-08-28.
 */
public class AlarmHandler {
    public static AlarmEntityObject getJSONBloodRequestAllList(StringBuilder buf) {


        JSONObject myStoryJSON = null;
        AlarmEntityObject alarmEntityObject = null;
        try {
            Log.e("서버데이터", buf.toString());

            myStoryJSON = new JSONObject(buf.toString());

            String msg = myStoryJSON.optString("msg");
            if(msg.equalsIgnoreCase("success")){
                JSONObject data = myStoryJSON.optJSONObject("data");
                alarmEntityObject = new AlarmEntityObject();
                alarmEntityObject.alarmBoolean = data.optString("notificationCount");

            }


        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return alarmEntityObject;
    }
}
