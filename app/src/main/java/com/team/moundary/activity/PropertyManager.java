package com.team.moundary.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.team.moundary.fragments.GirlsApplication;

public class PropertyManager {
    private static PropertyManager instance;

    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    private PropertyManager() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(GirlsApplication.getGirlsContext());
        mEditor = mPrefs.edit();
    }
    /*
     서버로 넘길 ID 또는 토큰 값
     */
    private static final String UUID = "uuid";
    public static final String USER_ID = "userId";
    private static final String USER_NICK_NAME = "nickname";

    private static final String USER_SETTING3 = "setting3";
    private static final String USER_SETTING4 = "setting4";
    private static final String USER_SETTING5 = "setting5";
    private static final String USER_SETTING6 = "setting6";



    public void setSetting3(String setting3){
        mEditor.putString(USER_SETTING3, setting3);
        mEditor.commit();
    }

    public String getSetting3(){
        return mPrefs.getString(USER_SETTING3, "");
    }

    public void setSetting4(String setting4){
        mEditor.putString(USER_SETTING4, setting4);
        mEditor.commit();
    }

    public String getSetting4(){
        return mPrefs.getString(USER_SETTING4, "");
    }

    public void setSetting5(String setting5){
        mEditor.putString(USER_SETTING5, setting5);
        mEditor.commit();
    }

    public String getSetting5(){
        return mPrefs.getString(USER_SETTING5, "");
    }

    public void setSetting6(String setting6){
        mEditor.putString(USER_SETTING6, setting6);
        mEditor.commit();
    }

    public String getSetting6(){
        return mPrefs.getString(USER_SETTING6, "");
    }



    public void setUUID(String uuid){
        mEditor.putString(UUID, uuid);
        mEditor.commit();
    }
    public void getUUID(){
        mEditor.putString(UUID, "");
    }
    /*
      실제 서버에서 넘어온 userId
     */
    public void setUserId(String id) {
        mEditor.putString(USER_ID, id);
        mEditor.commit();
    }
    public String getUserId() {

        return mPrefs.getString(USER_ID, "");
    }

    public void setUserNickName(String token) {
        mEditor.putString(USER_NICK_NAME, token);
    }
    public String setUserNickName() {
        return mPrefs.getString(USER_NICK_NAME, "");
    }
}