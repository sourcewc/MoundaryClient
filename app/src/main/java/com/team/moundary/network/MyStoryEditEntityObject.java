package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-25.
 */
public class MyStoryEditEntityObject {
    public String postId;
    public String postContent;
    public String postImg;

    public MyStoryEditEntityObject() {
    }

    public MyStoryEditEntityObject(String postId, String postContent, String postImg) {
        super();
        this.postId = postId;
        this.postContent = postContent;
        this.postImg = postImg;
    }
}
