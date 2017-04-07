package com.team.moundary.network;

/**
 * Created by Shin on 2016-08-20.
 */
public class ReplyEditEntityObject {
    String postId;
    String replyId;
    String replyContent;

    public ReplyEditEntityObject() {
    }

    public ReplyEditEntityObject(String postId, String replyId,String replyContent) {
        this.postId = postId;
        this.replyId = replyId;
        this.replyContent = replyContent;
    }
}
