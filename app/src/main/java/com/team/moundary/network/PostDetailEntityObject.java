package com.team.moundary.network;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-23.
 */
public class PostDetailEntityObject  {
    public String profileThumbnail;
    public String postImg;
    public String postContent;
    public String userId;
    public String nickname;
    public String _id;
    public String postDate;
    public String postLikeCount;
    public String myLike;
    public String replyCount;
    public String category;
    public String postId;
    public ArrayList<PostReplyEntityObject> reply = new ArrayList<>();



    public PostDetailEntityObject() { }


}

class PostReplyEntityObject {
    public String reply_id;
    public String reply_profileThumbnail;
    public String reply_Content;
    public String reply_userId;
    public String reply_nickname;

    public PostReplyEntityObject(String reply_id, String reply_profileThumbnail, String reply_Content, String reply_userId, String reply_nickname) {
        this.reply_id = reply_id;
        this.reply_profileThumbnail = reply_profileThumbnail;
        this.reply_Content = reply_Content;
        this.reply_userId = reply_userId;
        this.reply_nickname = reply_nickname;
    }
}