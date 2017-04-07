package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-09.
 */

public class FriendPageListViewEntityObject  {
    public String myLike;
    public String replyCount;
    public String postImg;
    public String userId;
    public String postContent;
    public String nickname;
    public String postDate;
    public String id;
    public String category;
    public String postLikeCount;

    public FriendPageListViewEntityObject() {
    }

    public FriendPageListViewEntityObject(String myLike, String category, String postLikeCount,
                                 String id, String postDate, String nickname,
                                 String postContent, String userId, String postImg,
                                 String replyCount) {
        this.myLike = myLike;
        this.category = category;
        this.postLikeCount = postLikeCount;
        this.id = id;
        this.postDate = postDate;
        this.nickname = nickname;
        this.postContent = postContent;
        this.userId = userId;
        this.postImg = postImg;
        this.replyCount = replyCount;
    }
}