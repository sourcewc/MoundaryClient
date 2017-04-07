package com.team.moundary.network;

/**
 * Created by ccei on 2016-08-16.
 */
public class FriendAskActivityEntityObject {

    public String _id;
    public String profileThumbnail;
    public String nickname;

    public FriendAskActivityEntityObject() {
    }

    public FriendAskActivityEntityObject(String _id, String profileThumbnail, String nickname) {
        this._id = _id;
        this.profileThumbnail = profileThumbnail;
        this.nickname = nickname;
    }
}
