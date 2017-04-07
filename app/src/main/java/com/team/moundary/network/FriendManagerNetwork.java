package com.team.moundary.network;

/**
 * Created by Shin on 2016-08-17.
 */
public class FriendManagerNetwork {
    public static final String HOST_URL = "52.78.98.25";
    //웹서버 포트번호
    public static final int PORT_NUMBER = 3000;
    //저장관련 URL 주소
    public static final String SERVER_URL_BLOOD_JSON_ALL_SELECT =
            "http://" + HOST_URL +
                    ":3000/reply?postId=57ab18be071e240b7f05bdce";
    public static final String MOUNDARY_FRIEND_REQUEST_URL = "http://" + HOST_URL + ":3000/friend/apply?userId=%s";
    public static final String MOUNDARY_FRIEND_CANCEL_URL = "http://" + HOST_URL + ":3000/friend/cancel?userId=%s";
    public static final String MOUNDARY_FRIEND_ALLOW_URL ="http://" + HOST_URL + ":3000/friend/allow?userId=%s";
    public static final String MOUNDARY_FRIEND_REJECT_URL ="http://" + HOST_URL + ":3000/friend/reject?userId=%s";
    public static final String MOUNDARY_FRIEND_DELETE_URL ="http://" + HOST_URL + ":3000/friend/delete?userId=%s";
    //public static final String MOUNDARY_MYSTORY_DELETE_URL ="http://" + HOST_URL + ":3000/post?userId=57aac4ccb735b12422b29b72";
    //public static final String MOUNDARY_POST_LIKE_URL ="http://" + HOST_URL + ":3000/post/like?userId=57aac4ccb735b12422b29b72";
    public static final String MOUNDARY_MYSTORY_DELETE_URL ="http://" + HOST_URL + ":3000/post?userId=%s"; //유저아이디 조금다름
    public static final String MOUNDARY_INFO_DELETE_URL ="http://" + HOST_URL + ":3000/info?userId=57aab6ef918317581eb11017"; //유저아이디 조금다름
    public static final String MOUNDARY_POST_LIKE_URL ="http://" + HOST_URL + ":3000/post/like?userId=57aab6ef918317581eb11017";
}

