package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-09.
 */
public class MyInfoPageEntityObject {
    public String postLikeUsers;
    public String profileImg;
    public String id;
    public String coverImg;
    public String nickname;
    public String userAddress;
    public String babyAge;

    public MyInfoPageEntityObject() { }

    public MyInfoPageEntityObject(String babyAge, String profileImg, String nickname, String id, String coverImg, String postLikeUsers, String userAddress) {
        this.babyAge = babyAge;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.id = id;
        this.coverImg = coverImg;
        this.postLikeUsers = postLikeUsers;
        this.userAddress = userAddress;
    }
}