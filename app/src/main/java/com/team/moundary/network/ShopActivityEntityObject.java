package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-08.
 */
public class ShopActivityEntityObject {
    public String postLikeUsers;
    public String replyCount;
    public String id;
    public String due;
    public String postThumbnail;
    public String postImg;
    public String postContent;
    public String nickname;
    public String postDate;
    public String category;
    public String endPost;
    public String postAddress;
    public String postCount;
    public String postLikeCount;
    public String myLike;
    public String area1;
    public String area2;
    public String area3;
    public String area4;
    public String area5;

    public ShopActivityEntityObject() { }

    public ShopActivityEntityObject(String category, String replyCount,
                                    String postThumbnail, String postImg,
                                    String postDate, String postCount, String postContent,
                                    String postAddress, String nickname, String id, String endPost, String due,
                                    String area1,String area2,String area3,String area4,String area5) {
        this.category = category;
        this.replyCount = replyCount;
        this.postThumbnail = postThumbnail;
        this.postImg = postImg;
        this.postDate = postDate;
        this.postCount = postCount;
        this.postContent = postContent;
        this.postAddress = postAddress;
        this.nickname = nickname;
        this.id = id;
        this.endPost = endPost;
        this.due = due;
        this.area1=area1;
        this.area2=area2;
        this.area3=area3;
        this.area4=area4;
        this.area5=area5;
    }
}