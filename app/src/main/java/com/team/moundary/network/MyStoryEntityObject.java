/*
 *  혈액요청 환자의 정보를 나타냄
 */
package com.team.moundary.network;

public class MyStoryEntityObject {
	public String myLike;
	public String replyCount;
	public String postimg;
	public String postcontent;
	public String nickname;
	public String postdate;
	public String id;
	public String category;
	public String postLikeCount;

	public MyStoryEntityObject() {
	}

	public MyStoryEntityObject(String myLike, String postLikeCount,
							   String category, String postdate,
							   String id, String nickname, String postcontent,
							   String postimg, String replyCount) {
		this.myLike = myLike;
		this.postLikeCount = postLikeCount;
		this.category = category;
		this.postdate = postdate;
		this.id = id;
		this.nickname = nickname;
		this.postcontent = postcontent;
		this.postimg = postimg;
		this.replyCount = replyCount;
	}
}