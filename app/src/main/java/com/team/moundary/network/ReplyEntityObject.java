package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-12.
 */
public class ReplyEntityObject {
    String postId = "postId";
    String replyContent = "replyContent";

    public ReplyEntityObject() {
    }

    public ReplyEntityObject(String postId, String postContent) {
        super();
        this.postId = postId;
        this.replyContent = postContent;
    }
}
