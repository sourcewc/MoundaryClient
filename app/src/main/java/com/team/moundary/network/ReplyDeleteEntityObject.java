package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-19.
 */
public class ReplyDeleteEntityObject {
    String postId;
    String replyId;

    public ReplyDeleteEntityObject() {
    }

    public ReplyDeleteEntityObject(String postId, String replyId) {
        this.postId = postId;
        this.replyId = replyId;
    }
}
