package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-10.
 */
public class FriendPageEntityObject {
    public String postLikeUsers;
    public String profileImg;
    public String id;
    public String coverImg;
    public String nickname;
    public String userAddress;
    public String isFriend;

    public FriendPageEntityObject() { }

    public FriendPageEntityObject(String coverImg, String userAddress,
                                  String profileImg, String postLikeUsers,
                                  String nickname, String id,String isFriend) {
        this.coverImg = coverImg;
        this.userAddress = userAddress;
        this.profileImg = profileImg;
        this.postLikeUsers = postLikeUsers;
        this.nickname = nickname;
        this.id = id;
        this.isFriend=isFriend;
    }
}
