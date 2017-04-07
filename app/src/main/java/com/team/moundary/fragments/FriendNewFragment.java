package com.team.moundary.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.moundary.R;
import com.team.moundary.network.FriendNewEntityObject;
import com.team.moundary.network.FriendNewHandler;
import com.team.moundary.network.LikeCountEntityObject;
import com.team.moundary.activity.MoundaryFriendNewsActivity;
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

public class FriendNewFragment extends Fragment {
    public static int increment;
    Handler mHandler = new Handler(Looper.getMainLooper());
    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId
    LinearLayout nofriend;
    LikeCountEntityObject likeCountEntityObject;


    static MoundaryFriendNewsActivity owner;

    public FriendNewFragment() {
    }

    public static FriendNewFragment newInstance(int initValue){
        FriendNewFragment girlFragment = new FriendNewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("value",initValue);
        girlFragment.setArguments(bundle);
        return girlFragment;
    }

    RecyclerView rv;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_news_fragment,container,false);

        nofriend=(LinearLayout)view.findViewById(R.id.no_friend);

        Bundle initBundle =getArguments();
        increment += initBundle.getInt("value");

        owner = (MoundaryFriendNewsActivity) getActivity();
        rv = (RecyclerView)view.findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(owner));
        myStoryAdapter = new GirlGroupRecyclerViewAdapter(owner, new ArrayList<FriendNewEntityObject>());
        rv.setAdapter(myStoryAdapter);
        // rv.setOnScrollListener(new EndlessScrollListener());

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncBloodJSONList().execute("");
        new AsyncBloodJSONList2().execute("");
    }
    GirlGroupRecyclerViewAdapter myStoryAdapter;

    public class GirlGroupRecyclerViewAdapter extends RecyclerView.Adapter<GirlGroupRecyclerViewAdapter.ViewHolder>{
        private ArrayList<FriendNewEntityObject> myFriendNewEntityObjects;

        public GirlGroupRecyclerViewAdapter(Context context, ArrayList<FriendNewEntityObject> resources) {
            this.myFriendNewEntityObjects  = resources;
        }



        public class ViewHolder extends RecyclerView.ViewHolder{
            public final View mView;
            public int likeCount;
            public final ImageView girlsImage;
            public final TextView memberName;
            public final TextView likeCountWD;
            public final ImageView likeImageWD;
            public final TextView writeDate;
            public final TextView membercontent;
            public final TextView replyCountWD;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                girlsImage = (ImageView)view.findViewById(R.id.girls_group_member_image);
                memberName = (TextView)view.findViewById(R.id.member_name);
                likeImageWD = (ImageView) view.findViewById(R.id.like_image);
                likeCountWD = (TextView)view. findViewById(R.id.like_count);
                writeDate=(TextView)view.findViewById(R.id.write_date);
                replyCountWD = (TextView)view. findViewById(R.id.reple_count);
                membercontent = (TextView) view.findViewById(R.id.member_content);


            }
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_news_item,parent,false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final int[] likeCheckInt = new int[1];
            final FriendNewEntityObject myStoryEntityObject = myFriendNewEntityObjects.get(position);

            Glide.with(GirlsApplication.getGirlsContext()).load(myStoryEntityObject.postImg).override(510,300).centerCrop().into(holder.girlsImage);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PostDetailActivity.class);

/*                    intent.putExtra("memberImage", myStoryEntityObject.postImg);
                    intent.putExtra("memberName", holder.memberName.getText().toString());
                    intent.putExtra("memberContent", holder.membercontent.getText().toString());
                    intent.putExtra("likeCount",holder.likeCountWD.getText().toString());
                    intent.putExtra("replyCount",holder.replyCountWD.getText().toString());
                    intent.putExtra("postdate",holder.writeDate.getText().toString());*/
                    //intent.putExtra("")-호스트주소마다 호스트아이디를 주어서 각기 다른 댓글보이게 하는 부분 해야함!
                    intent.putExtra("postId", myStoryEntityObject._id);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(owner, holder.girlsImage, ViewCompat.getTransitionName(holder.girlsImage));

                    ActivityCompat.startActivity(owner, intent, options.toBundle());
                }
            });

            holder.memberName.setText(myStoryEntityObject.nickname);
            holder.membercontent.setText(myStoryEntityObject.postContent);
            holder.likeCountWD.setText(myStoryEntityObject.postLikeCount);
            holder.replyCountWD.setText(myStoryEntityObject.replyCount);
            holder.writeDate.setText(myStoryEntityObject.postDate);
            holder.likeImageWD.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(myStoryEntityObject.myLike=="false") {
                        likeCheckInt[0] =Integer.parseInt(myStoryEntityObject.postLikeCount);
                        likeCheckInt[0]++;
                        holder.likeCountWD.setText(myStoryEntityObject.postLikeCount);
                        myStoryEntityObject.myLike="true";
                        new AsyncLikeRequest(myStoryEntityObject._id).execute(likeCountEntityObject);
                    } else {
                        likeCheckInt[0] =Integer.parseInt(myStoryEntityObject.postLikeCount);
                        likeCheckInt[0]--;
                        holder.likeCountWD.setText(myStoryEntityObject.postLikeCount);
                        myStoryEntityObject.myLike="false";
                        new AsyncLikeRequest(myStoryEntityObject._id).execute(likeCountEntityObject);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return myFriendNewEntityObjects.size();
        }
    }
    public class AsyncBloodJSONList extends AsyncTask<String, Integer, ArrayList<FriendNewEntityObject>> {
        String targetURL=String.format(NetworkActivity.FRIEND_NEWS_NETWORK,userId) ;
        NetworkDialog netwrokDialog = new NetworkDialog(owner);

        @Override
        protected ArrayList<FriendNewEntityObject> doInBackground(
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
                    return FriendNewHandler.getJSONBloodRequestAllList(
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
        protected void onPostExecute(ArrayList<FriendNewEntityObject> result) {

            netwrokDialog.dismiss();

            if(result != null && result.size() > 0){

                GirlGroupRecyclerViewAdapter bloodListAdapter = new GirlGroupRecyclerViewAdapter(owner, result);
                bloodListAdapter.notifyDataSetChanged();
                rv.setAdapter(bloodListAdapter);
                nofriend.setVisibility(View.GONE);
            }if(result == null) {
                nofriend.setVisibility(View.VISIBLE);
            }
        }
    }

    public class AsyncBloodJSONList2 extends AsyncTask<String, Integer, ArrayList<FriendNewEntityObject>> {

        NetworkDialog netwrokDialog = new NetworkDialog(owner);

        @Override
        protected ArrayList<FriendNewEntityObject> doInBackground(String... params) {
            Response response = null;


            try {
                //OKHttp3사용ㄴ
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(NetworkActivity.FRIEND_NEWS_NETWORK )
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                // responseBody.string();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return FriendNewHandler.getJSONBloodRequestAllList(
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
        protected void onPostExecute(ArrayList<FriendNewEntityObject> result) {

            netwrokDialog.dismiss();

            if(result != null && result.size() > 0){

                GirlGroupRecyclerViewAdapter bloodListAdapter = new GirlGroupRecyclerViewAdapter(owner, result);
                bloodListAdapter.notifyDataSetChanged();
                rv.setAdapter(bloodListAdapter);
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