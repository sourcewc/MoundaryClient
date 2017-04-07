package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-23.
 */
public class PushActivityEntityObject {
    public String pushType;
    public String postId;
    public String category;
    public String pusherId;
    public String pusherNickname;
    public String img;
    public String pushDate;
    public String confirmed;
    public String pullerId;
    public String content;

    public PushActivityEntityObject() {
    }

    public PushActivityEntityObject(String category, String pushType, String pusherNickname,
                                    String pusherId, String pushDate, String postId, String img,
                                    String confirmed,String pullerId,String content) {
        this.category = category;
        this.pushType = pushType;
        this.pusherNickname = pusherNickname;
        this.pusherId = pusherId;
        this.pushDate = pushDate;
        this.postId = postId;
        this.img = img;
        this.confirmed = confirmed;
        this.pullerId =pullerId;
        this.content = content;
    }
}