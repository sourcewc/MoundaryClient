package com.team.moundary.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostDetailHandler {

    public static ArrayList<PostDetailEntityObject> getJSONBloodRequestAllList(StringBuilder buf) {

        ArrayList<PostDetailEntityObject> jsonAllList = null;
        JSONObject myStoryJSON = null;
        try {
            Log.e("서버데이터", buf.toString());
            jsonAllList = new ArrayList<PostDetailEntityObject>();
            myStoryJSON = new JSONObject(buf.toString());

            String msg = myStoryJSON.optString("msg");
            if(msg.equalsIgnoreCase("success")){
                Log.w("leelog json", myStoryJSON.toString());
                JSONObject data = myStoryJSON.getJSONObject("data");
                int dataSize = data.length();
                for(int i = 0 ; i < dataSize ;i++){
                    JSONObject  myStory = data;
                    PostDetailEntityObject myPostDetailRepleEntityObject = new PostDetailEntityObject();
                    myPostDetailRepleEntityObject._id = myStory.optString("_id");
                    myPostDetailRepleEntityObject.postImg = myStory.optString("postImg");
                    myPostDetailRepleEntityObject.postContent = myStory.optString("postContent");
                    myPostDetailRepleEntityObject.userId = myStory.optString("userId");
                    myPostDetailRepleEntityObject.nickname = myStory.optString("nickname");
                    myPostDetailRepleEntityObject.profileThumbnail = myStory.optString("profileThumbnail");
                    myPostDetailRepleEntityObject.postLikeCount = myStory.optString("postLikeCount");
                    myPostDetailRepleEntityObject.myLike = myStory.optString("myLike");
                    myPostDetailRepleEntityObject.replyCount = myStory.optString("replyCount");
                    myPostDetailRepleEntityObject.category = myStory.optString("category");
                    myPostDetailRepleEntityObject.postDate = myStory.optString("postDate");

                    JSONArray array = myStory.getJSONArray("reply");

                    for (int j = 0; j < array.length(); j++) {
                        JSONObject jsonObject = array.getJSONObject(j);
                        myPostDetailRepleEntityObject.reply.add(new PostReplyEntityObject(
                            jsonObject.getString("_id"),
                                jsonObject.getString("profileThumbnail"),
                                jsonObject.getString("replyContent"),
                                jsonObject.getString("userId"),
                                jsonObject.getString("nickname")
                        ));
                    }
                    //myPostDetailRepleEntityObject.reply=myStory.getJSONArray("reply");


                    jsonAllList.add(myPostDetailRepleEntityObject);
                }
            }

        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }
}