package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-18.
 */
public class SearchPostClearActivityEntityObject {

    public String _id;
    public String userId;
    public String category;
    public String due;
    public String postContent;
    public String postImg;
    public String postThumbnail;
    public String nickname;
    public String profileThumbnail;
    public String replyCount;
    public String postLikeCount;
    public String postAddress;
    public String myLike;
    public String postDate;


    public SearchPostClearActivityEntityObject() { }

    public SearchPostClearActivityEntityObject(String category, String _id, String due, String myLike,
                                               String nickname, String postAddress, String postContent,
                                               String postImg, String postLikeCount, String postThumbnail,
                                               String replyCount, String profileThumbnail, String userId,String postDate) {
        this.category = category;
        this._id = _id;
        this.due = due;
        this.myLike = myLike;
        this.nickname = nickname;
        this.postAddress = postAddress;
        this.postContent = postContent;
        this.postImg = postImg;
        this.postLikeCount = postLikeCount;
        this.postThumbnail = postThumbnail;
        this.replyCount = replyCount;
        this.profileThumbnail = profileThumbnail;
        this.userId = userId;
        this.postDate =postDate;
    }
}

