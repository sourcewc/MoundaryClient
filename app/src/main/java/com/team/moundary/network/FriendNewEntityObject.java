package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-10.
 */
public class FriendNewEntityObject  {
    public String myLike;
    public String replyCount;
    public String postImg;
    public String userId;
    public String postContent;
    public String nickname;
    public String postDate;
    public String _id;
    public String category;
    public String postLikeCount;

    public FriendNewEntityObject() {
    }

    public FriendNewEntityObject(String myLike, String category, String postLikeCount,
                                 String _id, String postDate, String nickname,
                                 String postContent, String userId, String postImg,
                                 String replyCount) {
        this.myLike = myLike;
        this.category = category;
        this.postLikeCount = postLikeCount;
        this._id = _id;
        this.postDate = postDate;
        this.nickname = nickname;
        this.postContent = postContent;
        this.userId = userId;
        this.postImg = postImg;
        this.replyCount = replyCount;
    }
}