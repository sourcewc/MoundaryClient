/*
 *   Http 통신을 컨트롤하는 역할을 담당
 */
package com.team.moundary.network;

import android.util.Log;

import com.team.moundary.activity.NetworkActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class HttpAPIHelperHandler {
    private static final String DEBUG_TAG = "HttpAPIHelperHandler";

    public static ArrayList<MyStoryEntityObject> bloodJSONAllSelect() {
        ArrayList<MyStoryEntityObject> myStoryEntityObjects = null;
        BufferedReader jsonStreamData = null;
        HttpURLConnection connection = null;
        try {
            connection = HttpConnectionManager.getHttpURLConnection(
                    NetworkActivity.MY_STORY_NETWORK);

            int responseCode = connection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                jsonStreamData = new BufferedReader(new
                        InputStreamReader(connection.getInputStream()));
                StringBuilder jsonBuf = new StringBuilder();
                String jsonLine = "";
                while ((jsonLine = jsonStreamData.readLine()) != null) {
                    jsonBuf.append(jsonLine);
                }
                myStoryEntityObjects = MystoryHandler.getJSONBloodRequestAllList(jsonBuf);
            }

        } catch (IOException e) {
            Log.e("bloodJSONAllSelect()", e.toString());
        } finally {
            if (jsonStreamData != null) {
                try {
                    jsonStreamData.close();
                } catch (IOException ioe) {
                }
            }
            if (connection != null) connection.disconnect();
        }
        return myStoryEntityObjects;
    }
}