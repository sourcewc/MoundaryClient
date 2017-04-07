package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-09.
 */
public class PostDetailRepleEntityObject {
    public String replyContent;
    public String userId;
    public String replyDate;
    public String profileThumbnail;
    public String nickname;
    public String _id;
    public String myLike;
    public String replyLikeCount;

    public PostDetailRepleEntityObject() { }


    public PostDetailRepleEntityObject(String replyContent, String replyLikeCount,
                                       String myLike, String _id, String nickname,
                                       String profileThumbnail, String replyDate, String userId) {
        this.replyContent = replyContent;
        this.replyLikeCount = replyLikeCount;
        this.myLike = myLike;
        this._id = _id;
        this.nickname = nickname;
        this.profileThumbnail = profileThumbnail;
        this.replyDate = replyDate;
        this.userId = userId;
    }
}