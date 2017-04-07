/*
 *  다양한 HTTP 통신을 관리하는 객체
 *  author PYO IN SOO
 */
package com.team.moundary.network;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnectionManager {
    private static final String DEBUG_TAG = "HttpConnectionManager";


    public static HttpURLConnection getHttpURLConnection(String targetURL) {
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL(targetURL);

            httpConnection = (HttpURLConnection) url.openConnection();

            httpConnection.setDoInput(true);
            httpConnection.setUseCaches(false);
            httpConnection.setConnectTimeout(15000);
            httpConnection.setReadTimeout(10000);

        } catch (Exception e) {
            Log.e("DEBUG_TAG", "getHttpURLConnection() -- 에러 발생 -- ", e);
        }
        return httpConnection;
    }
}
