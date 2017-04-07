package com.team.moundary.activity;

/**
 * Created by ccei on 2016-08-18.
 */
public class NetworkActivity {

            public static final String HOST_URL = "52.78.98.25";

            public static final String POST_ID = "57ac34fd071e240b7f05bddd";

            public static  String FIND_FRIEND_NETWORK = "http://" + HOST_URL + ":3000/user/list?userId=%s";

            public static final String FRIEND_ASK_NETWORK = "http://" + HOST_URL + ":3000/friend/candidate?userId=%s";

            public static final String FRIEND_MENU_NETWORK = "http://" + HOST_URL + ":3000/friend?userId=%s";

            public static final String FRIEND_NEWS_NETWORK = "http://" + HOST_URL + ":3000/post?userId=%s";

            public static final String FRIEND_PAGE_LISTVIEW_NETWORK = "http://" + HOST_URL + ":3000/post/mine?userId=%s";

            public static final String MY_STORY_NETWORK = "http://" + HOST_URL + ":3000/post/mine?userId=%s";

            public static  String SHOP_NETWORK = "http://" + HOST_URL + ":3000/info?userId=%s&category=%s";

            public static  String RECOMMEND_NETWORK = "http://" + HOST_URL + ":3000/info?userId=%s";


            public static  String SEARCH_FRIEND_NETWORK = "http://" + HOST_URL + ":3000/user/search?nickname=%s&userId=%s";

            public static  String SEARCH_POST_NETWORK = "http://" + HOST_URL + ":3000/search?word=%s&userId=%s";

            public static final String MY_PAGE_NETWORK = "http://" + HOST_URL + ":3000/user/mine?profileUserId=%s&userId=%s";

            public static final String FRIEND_PAGE_NETWORK = "http://" + HOST_URL + ":3000/user?profileUserId=%s&userId=%s";

            public static final String SERVER_URL_BLOOD_JSON_ALL_SELECT = "http://" + HOST_URL + ":3000/reply?postId=%s";

            public static final String MOUNDARY_FRIEND_REQUEST_URL = "http://" + HOST_URL + ":3000/friend/apply?userId=%s";

            public static final String MOUNDARY_FRIEND_CANCEL_URL = "http://" + HOST_URL + ":3000/friend/cancel?userId=57aab6ef918317581eb11017";

            public static final String MOUNDARY_POST_LIKE_URL = "http://" + HOST_URL + ":3000/post/like?userId=%s";

            public static final String MOUNDARY_INFO_LIKE_URL ="http://" + HOST_URL + ":3000/info/like?userId=%s";

            public static final String MYSTORY = "http://" + HOST_URL + ":3000/user?profileUserId=57aab6ef918317581eb11017&userId=57aab6ef918317581eb11017";

            public static final String SERVER_LOGIN_URL = "http://" + HOST_URL + ":3000/auth/signup";

            public static final String PUSH_NETWORK = "http://" + HOST_URL + ":3000/user/notification?userId=%s";

            public static final String POST_DETAIL = "http://" + HOST_URL + ":3000/post/detail?postId=%s&userId=%s";

            public static final String MOUNDARY_REPLY_WRETE_URL = "http://" + HOST_URL + ":3000/reply?userId=%s";

            public static final String MOUNDARY_REPLY_DELETE_URL = "http://" + HOST_URL + ":3000/reply?userId=%s";

            public static final String MOUNDARY_REPLY_LIKE_URL = "http://" + HOST_URL + ":3000/reply/like?userId=%s";

            public static final String MOUNDARY_REPLY_EDIT_URL = "http://" + HOST_URL + ":3000/reply?userId=%s";

            public static final String INFO_WRITE_URL  ="http://"+HOST_URL+":3000/info?userId=%s";

            public static final String MOUNDARY_WRITE_POST = "http://52.78.98.25:3000/post?userId=%s";

            public static final String MOUNDARY_NEW_FRIEND_REQUEST_URL = "http://" + HOST_URL + ":3000/friend/candidate?userId=%s";

            public static final String MOUNDARY_MY_INFO_EDIT = "http://"+HOST_URL+":3000/user?userId=%s";

            public static final String MOUNDARY_MY_POST_EDIT = "http://" +HOST_URL+ ":3000/post?userId=%s";

            public static final String MOUNDARY_ALARM_CKECK ="http://"+HOST_URL+":3000/user/notification/count?userId=%s";
}
