package com.team.moundary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.moundary.R;
import com.team.moundary.fragments.GirlsApplication;
import com.team.moundary.network.LikeCountEntityObject;
import com.team.moundary.network.SearchPostClearActivityEntityObject;
import com.team.moundary.network.SearchPostClearActivityHandler;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class SearchPostClearActivity extends FontActivity {

    ImageView backBtnImage;
    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId();

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<SearchPostClearActivityEntityObject> myDataset;
    LikeCountEntityObject likeCountEntityObject;
    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_clear);

        backBtnImage =(ImageView)findViewById(R.id.back_img_btn);
        Toolbar toolbar = (Toolbar)findViewById(R.id.setup_toolbar);

        Intent intent = getIntent();

        word=intent.getStringExtra("word");


        // nickname.setText(nickname);

        SearchClearAdapter myStoryAdapter;
        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.search_recycler);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myDataset = new ArrayList<>();
        myStoryAdapter = new SearchClearAdapter(new ArrayList<SearchPostClearActivityEntityObject>(),getApplicationContext());
        mRecyclerView.setAdapter(myStoryAdapter);


        new AsyncBloodJSONList().execute("");
    }
    class AsyncBloodJSONList extends AsyncTask<String, Integer,
            ArrayList<SearchPostClearActivityEntityObject>> {
        NetworkDialog netwrokDialog = new NetworkDialog(SearchPostClearActivity.this);

        @Override
        protected ArrayList<SearchPostClearActivityEntityObject> doInBackground(
                String... params) {
            String categoryValue = params[0];
            Response response = null;
            try {
                String targetURL = String.format(NetworkActivity.SEARCH_POST_NETWORK,word,userId);
                //OKHttp3사용
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
                    return SearchPostClearActivityHandler.getJSONBloodRequestAllList(
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
        protected void onPostExecute(ArrayList<SearchPostClearActivityEntityObject>
                                             result) {
            netwrokDialog.dismiss();

            if (result != null && result.size() > 0) {
                SearchClearAdapter bloodListAdapter =
                        new SearchClearAdapter(
                                result, SearchPostClearActivity.this);
                bloodListAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(bloodListAdapter);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    class SearchClearAdapter extends RecyclerView.Adapter<SearchClearAdapter.ViewHolder> {
        private ArrayList<SearchPostClearActivityEntityObject> searchFriendClearActivityEntityObjects;
        Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public ImageView postImageView;
            public TextView nickname;
            public TextView postAddress;
            public TextView content;
            public TextView date;
            public RelativeLayout recyclerItem;
        //    TextView likeCountWD;
            TextView replyCountWD;
        //    public int likeCount;
         //   ImageView likeImageWD;
            ImageView repleImageWD;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                postImageView = (ImageView)view.findViewById(R.id.image);
                nickname = (TextView)view.findViewById(R.id.nickname);
                content = (TextView)view.findViewById(R.id.content);
                postAddress = (TextView)view.findViewById(R.id.location);
                date = (TextView)view.findViewById(R.id.date);
             //   likeCountWD = (TextView) view.findViewById(R.id.like_count);
                replyCountWD = (TextView) view.findViewById(R.id.reple_count);
             //   likeImageWD = (ImageView) view.findViewById(R.id.like_image);

                recyclerItem= (RelativeLayout)view.findViewById(R.id.push_recycler_item);
            }
        }
        public SearchClearAdapter(ArrayList<SearchPostClearActivityEntityObject> myDataset, Context context) {
            searchFriendClearActivityEntityObjects = myDataset;
            this.context =context;
        }
        @Override
        public SearchClearAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.shop_list, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder,final int position) {
            final SearchPostClearActivityEntityObject searchFriendClearActivityEntityObject = searchFriendClearActivityEntityObjects.get(position);
            final int[] likeCheckInt = new int[1];


            Glide.with(GirlsApplication.getGirlsContext()).load(searchFriendClearActivityEntityObject.postImg).into(holder.postImageView);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);

                    intent.putExtra("postId", searchFriendClearActivityEntityObject._id);
                    startActivity(intent);
                }
            });
            holder.nickname.setText(searchFriendClearActivityEntityObject.nickname);
            holder.content.setText(searchFriendClearActivityEntityObject.postContent);
            holder.date.setText(searchFriendClearActivityEntityObject.postDate);
            holder.postAddress.setText(searchFriendClearActivityEntityObject.postAddress);
//            holder.likeCountWD.setText(searchFriendClearActivityEntityObject.postLikeCount);
            holder.replyCountWD.setText(searchFriendClearActivityEntityObject.replyCount);

/*            holder.likeImageWD.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(searchFriendClearActivityEntityObject.myLike=="false") {
                        likeCheckInt[0] =Integer.parseInt(searchFriendClearActivityEntityObject.postLikeCount);
                        likeCheckInt[0]++;
                        holder.likeCountWD.setText(searchFriendClearActivityEntityObject.postLikeCount);
                        searchFriendClearActivityEntityObject.myLike="true";
                        new AsyncLikeRequest(searchFriendClearActivityEntityObject._id).execute(likeCountEntityObject);
                    } else {
                        likeCheckInt[0] =Integer.parseInt(searchFriendClearActivityEntityObject.postLikeCount);
                        likeCheckInt[0]--;
                        holder.likeCountWD.setText(searchFriendClearActivityEntityObject.postLikeCount);
                        searchFriendClearActivityEntityObject.myLike="false";
                        new AsyncLikeRequest(searchFriendClearActivityEntityObject._id).execute(likeCountEntityObject);
                    }
                }
            });*/
        }

        @Override
        public int getItemCount() {
            return searchFriendClearActivityEntityObjects.size();
        }}

/*    public class AsyncLikeRequest extends AsyncTask<LikeCountEntityObject, Void, String> {
        String postId;

        private ProgressDialog progressDialog;
        String targetURL = String.format(NetworkActivity.MOUNDARY_INFO_LIKE_URL,userId);
        public AsyncLikeRequest(String postId) {
            this.postId = postId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SearchPostClearActivity.this, "서버입력중", "잠시만 기다려주세요", true);
        }


        @Override
        protected String doInBackground(LikeCountEntityObject... entity) {

            Response response = null;
            try {
                *//*
                 연결설정에 대한 부분
                 *//*
                OkHttpClient toServer = new OkHttpClient().newBuilder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build();

                *//*
                 요청에 대한 설정
                 *//*
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
            progressDialog.dismiss();
            if (result.equalsIgnoreCase("success")) {
                new AsyncBloodJSONList().execute("");
            } else {
                return;
            }

        }
    }*/

}