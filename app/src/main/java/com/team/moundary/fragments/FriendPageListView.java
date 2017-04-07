package com.team.moundary.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.moundary.R;
import com.team.moundary.activity.FriendPageActivity;
import com.team.moundary.network.FriendPageListViewEntityObject;
import com.team.moundary.network.FriendPageListViewHandler;
import com.team.moundary.network.LikeCountEntityObject;
import com.team.moundary.activity.NetworkActivity;
import com.team.moundary.activity.NetworkDialog;
import com.team.moundary.activity.PostDetailActivity;
import com.team.moundary.activity.PropertyManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei on 2016-07-28.
 */
public class FriendPageListView extends Fragment {

    private RecyclerView mRecyclerView;
    static FriendPageActivity owner;
    LikeCountEntityObject likeCountEntityObject;
    String getOppnentId;
    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId


    public static FriendPageListView newInstance() {
        FriendPageListView lv = new FriendPageListView();
        return lv;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        owner = (FriendPageActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_listview_fragment, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(owner));
        myStoryAdapter = new FriendPageAdapter(owner, new ArrayList<FriendPageListViewEntityObject>());
        mRecyclerView.setAdapter(myStoryAdapter);

        Bundle args = getActivity().getIntent().getExtras();
        getOppnentId= args.getString("oppositeUserId");
//        Log.e("getid",getOppnentId);

        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncBloodJSONList().execute("");
    }
    FriendPageAdapter myStoryAdapter;

    public class FriendPageAdapter extends RecyclerView.Adapter<FriendPageAdapter.ViewHolder> {
        private ArrayList<FriendPageListViewEntityObject> myFriendPageListViewEntityObjects;
        Context context;

        public FriendPageAdapter(Context context, ArrayList<FriendPageListViewEntityObject> resources) {
            myFriendPageListViewEntityObjects = resources;
            owner =(FriendPageActivity)context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_news_item,parent,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final int[] likeCheckInt = new int[1];
            final FriendPageListViewEntityObject myStoryEntityObject = myFriendPageListViewEntityObjects.get(position);

            Glide.with(GirlsApplication.getGirlsContext()).load(myStoryEntityObject.postImg).override(510,300).centerCrop().into(holder.girlsImage);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PostDetailActivity.class);

                    intent.putExtra("postId", myStoryEntityObject.id);
                    /*intent.putExtra("memberName", holder.memberName.getText().toString());
                    intent.putExtra("memberContent", holder.membercontent.getText().toString());*/
                    //intent.putExtra("")-호스트주소마다 호스트아이디를 주어서 각기 다른 댓글보이게 하는 부분 해야함!
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(owner, holder.girlsImage, ViewCompat.getTransitionName(holder.girlsImage));

                    ActivityCompat.startActivity(owner, intent, options.toBundle());
                }
            });

            holder.likeImageWD.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(myStoryEntityObject.myLike=="false") {
                        likeCheckInt[0] =Integer.parseInt(myStoryEntityObject.postLikeCount);
                        likeCheckInt[0]++;
                        holder.likeCountWD.setText(myStoryEntityObject.postLikeCount);
                        myStoryEntityObject.myLike="true";
                        new AsyncLikeRequest(myStoryEntityObject.id).execute(likeCountEntityObject);
                    } else {
                        likeCheckInt[0] =Integer.parseInt(myStoryEntityObject.postLikeCount);
                        likeCheckInt[0]--;
                        holder.likeCountWD.setText(myStoryEntityObject.postLikeCount);
                        myStoryEntityObject.myLike="false";
                        new AsyncLikeRequest(myStoryEntityObject.id).execute(likeCountEntityObject);
                    }
                }
            });

            holder.memberName.setText(myStoryEntityObject.nickname);
            holder.membercontent.setText(myStoryEntityObject.postContent);

            holder.likeCountWD.setText(myStoryEntityObject.postLikeCount);
            holder.replyCountWD.setText(myStoryEntityObject.replyCount);
            holder.writeDate.setText(myStoryEntityObject.postDate);


        }

        @Override
        public int getItemCount() {
            return myFriendPageListViewEntityObjects.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public int likeCount;
            TextView writeDate;
            public final ImageView girlsImage;
            public final TextView memberName;
            public final TextView membercontent;
            TextView likeCountWD;
            ImageView likeImageWD;
            TextView replyCountWD;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                girlsImage = (ImageView) view.findViewById(R.id.girls_group_member_image);
                memberName = (TextView) view.findViewById(R.id.member_name);
                writeDate=(TextView)view.findViewById(R.id.write_date);
                replyCountWD = (TextView)view. findViewById(R.id.reple_count);
                membercontent = (TextView) view.findViewById(R.id.member_content);
                likeImageWD = (ImageView) view.findViewById(R.id.like_image);
                likeCountWD = (TextView) view.findViewById(R.id.like_count);



            }
        }
    }

    public class AsyncBloodJSONList extends AsyncTask<String, Integer,
            ArrayList<FriendPageListViewEntityObject>> {

        NetworkDialog netwrokDialog = new NetworkDialog(owner);
        String targetURL =String.format(NetworkActivity.FRIEND_PAGE_LISTVIEW_NETWORK,getOppnentId);
        @Override
        protected ArrayList<FriendPageListViewEntityObject> doInBackground(
                String... params) {
            Response response = null;
            try {
                //OKHttp3사용ㄴ
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(targetURL)
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                // responseBody.string();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return FriendPageListViewHandler.getJSONBloodRequestAllList(
                            new StringBuilder(responseBody.string()));
                }
            } catch (UnknownHostException une) {
                e("aaa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("bbb", uee.toString());
            } catch (Exception e) {
                e("ccc", e.toString());
            }finally{
                if(response != null){
                    response.close();
                }
            }
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netwrokDialog.show();
        }


        @Override
        protected void onPostExecute(ArrayList<FriendPageListViewEntityObject>
                                             result) {
            netwrokDialog.dismiss();

            if (result != null && result.size() > 0) {
                FriendPageAdapter bloodListAdapter = new FriendPageAdapter(owner, result);
                bloodListAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(bloodListAdapter);
            }
        }
    }

    public class AsyncLikeRequest extends AsyncTask<LikeCountEntityObject, Void, String> {
        String postId;
        NetworkDialog netwrokDialog = new NetworkDialog(owner);
        String targetURL = String.format(NetworkActivity.MOUNDARY_INFO_LIKE_URL,userId);
        public AsyncLikeRequest(String postId) {
            this.postId = postId;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netwrokDialog.show();
        }



        @Override
        protected String doInBackground(LikeCountEntityObject... entity) {

            Response response = null;
            try {
                /*
                 연결설정에 대한 부분
                 */
                OkHttpClient toServer = new OkHttpClient().newBuilder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build();

                /*
                 요청에 대한 설정
                 */
                FormBody.Builder requestBody = new FormBody.Builder();
                requestBody.add("postId", postId);
                FormBody reqBody = requestBody.build();
                Log.e("확인", "연결 날라감");

                Request request = new Request.Builder()
                        .url(targetURL)
                        .put(reqBody)
                        .build();
                Log.e("확인", "연결 날라감2");
                //응답에 대한 코드
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();
                ResponseBody responseBody = response.body();
                Log.e("확인", "연결 날라감3");
                if (flag) {
                    JSONObject resultfromServer = new JSONObject(responseBody.string());
                    Log.e("확인", "연결 날라감4");
                    return resultfromServer.getString("msg");
                }

            } catch (Exception e) {
                Log.e("doInBac", "좋아요전송중 에러발생", e);
            } finally {
                //종료
                if (response != null) {
                    response.close();
                }
            }
            return "fail";
        }

        @Override
        protected void onPostExecute(String result) {
            netwrokDialog.dismiss();
            if (result.equalsIgnoreCase("success")) {
                new AsyncBloodJSONList().execute("");
            } else {
                return;
            }
        }
    }
}
