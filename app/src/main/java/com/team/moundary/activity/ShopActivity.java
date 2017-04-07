package com.team.moundary.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.moundary.R;
import com.team.moundary.fragments.GirlsApplication;
import com.team.moundary.map.MoundaryInfoMap;
import com.team.moundary.network.LikeCountEntityObject;
import com.team.moundary.network.ShopActivityEntityObject;
import com.team.moundary.network.ShopActivityHandler;

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
 * Created by ccei on 2016-07-29.
 */
public class ShopActivity extends FontActivity {

    ImageView backBtnImage;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private int currentCategory;

    LinearLayout nofriend;

    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId
    LikeCountEntityObject likeCountEntityObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop);

        Intent intent = getIntent();
        currentCategory = intent.getIntExtra("category", -1);
        Log.e("현재 카테고리",String.valueOf(currentCategory));
        if( currentCategory != -1){
            if(currentCategory ==3 ) {
                new AsyncBloodJSONList().execute(String.valueOf(currentCategory));
            } else if(currentCategory ==4) {
                new AsyncBloodJSONList().execute(String.valueOf(currentCategory));
            } else if(currentCategory ==2) {
                new AsyncBloodJSONList().execute(String.valueOf(currentCategory));
            } else if(currentCategory ==1) {
                new AsyncBloodJSONList().execute(String.valueOf(currentCategory));
            }
        }
        nofriend=(LinearLayout)findViewById(R.id.no_friend);

        EventAdapter myStoryAdapter;
        backBtnImage =(ImageView)findViewById(R.id.back_img_btn);
        Toolbar toolbar = (Toolbar)findViewById(R.id.setup_toolbar);

        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.event_recycler);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myStoryAdapter = new EventAdapter(new ArrayList<ShopActivityEntityObject>(),getApplicationContext());
        mRecyclerView.setAdapter(myStoryAdapter);

        ImageView gps=(ImageView)findViewById(R.id.event_location_gps);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MoundaryInfoMap.class);
                intent.putExtra("category",String.valueOf(currentCategory));
                Log.e("인텐트 카테고리", String.valueOf(currentCategory));
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        new AsyncBloodJSONList().execute(String.valueOf(currentCategory));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class AsyncBloodJSONList extends AsyncTask<String, Integer,
            ArrayList<ShopActivityEntityObject>> {
        ProgressDialog dialog;

        @Override
        protected ArrayList<ShopActivityEntityObject> doInBackground(
                String... params) {
            String categoryValue = params[0];
            Response response = null;
            try {
                String targetURL = String.format(NetworkActivity.SHOP_NETWORK,userId,categoryValue);
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
                    return ShopActivityHandler.getJSONBloodRequestAllList(
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
            dialog = ProgressDialog.show(ShopActivity.this,
                    "", "잠시만 기다려 주세요 ...", true);
        }
        @Override
        protected void onPostExecute(ArrayList<ShopActivityEntityObject> result) {
            dialog.dismiss();

            if (result != null && result.size() > 0) {
                EventAdapter bloodListAdapter =
                        new EventAdapter(
                                result, ShopActivity.this);
                bloodListAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(bloodListAdapter);
                nofriend.setVisibility(View.GONE);
            }
            if(result == null) {
                nofriend.setVisibility(View.VISIBLE);
            }
        }
    }
    class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
        private ArrayList<ShopActivityEntityObject> eventEntityObjects;
        Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView likecount;
            public ImageView mImageView;
            public TextView mTextView;
            public TextView mTextView2;
            public TextView mTextView3;
            public TextView mTextView4;
            public LinearLayout eventRelative;
            public View mView;
            public TextView replyCountView;
            public ImageView likeImage;

            public ViewHolder(View view) {
                super(view);
                mView =view;
                likecount = (TextView)view.findViewById(R.id.like_count);
                mImageView = (ImageView)view.findViewById(R.id.image);
                mTextView = (TextView)view.findViewById(R.id.location);
                mTextView2 = (TextView)view.findViewById(R.id.content);
                mTextView3 = (TextView)view.findViewById(R.id.nickname);
                mTextView4 = (TextView)view.findViewById(R.id.date);
                eventRelative = (LinearLayout)view.findViewById(R.id.event_list_recycler);
                replyCountView = (TextView)view.findViewById(R.id.reple_count);
                likeImage = (ImageView)view.findViewById(R.id.like_image);

            }
        }
        public EventAdapter(ArrayList<ShopActivityEntityObject> myDataset, Context context) {
            eventEntityObjects = myDataset;
            this.context =context;
        }
        @Override
        public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.shop_list, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final int[] likeCheckInt = new int[1];
            final ShopActivityEntityObject shopEntityObject = eventEntityObjects.get(position);

            Glide.with(GirlsApplication.getGirlsContext()).load(shopEntityObject.postImg).override(510,300).centerCrop().into(holder.mImageView);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PostDetailActivity.class);

                    intent.putExtra("postId", shopEntityObject.id);

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                }
            });


            holder.mTextView2.setText(shopEntityObject.postContent);
            holder.mTextView3.setText(shopEntityObject.nickname);
            holder.mTextView.setText(shopEntityObject.postAddress);
            holder.mTextView4.setText(shopEntityObject.postDate);
//            holder.likecount.setText(shopEntityObject.postLikeCount);
            /*holder.likeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(shopEntityObject.myLike=="false") {
                        likeCheckInt[0] =Integer.parseInt(shopEntityObject.postLikeCount);
                        likeCheckInt[0]++;
                        holder.likecount.setText(shopEntityObject.postLikeCount);
                        shopEntityObject.myLike="true";
                        new AsyncLikeRequest(shopEntityObject.id).execute(likeCountEntityObject);
                    } else {
                        likeCheckInt[0] =Integer.parseInt(shopEntityObject.postLikeCount);
                        likeCheckInt[0]--;
                        holder.likecount.setText(shopEntityObject.postLikeCount);
                        shopEntityObject.myLike="false";
                        new AsyncLikeRequest(shopEntityObject.id).execute(likeCountEntityObject);
                    }
                }
            });*/

            holder.replyCountView.setText(shopEntityObject.replyCount);
        }

        public class AsyncLikeRequest extends AsyncTask<LikeCountEntityObject, Void, String> {
            String postId;

            private ProgressDialog progressDialog;
            String targetURL = String.format(NetworkActivity.MOUNDARY_INFO_LIKE_URL,userId);
            public AsyncLikeRequest(String postId) {
                this.postId = postId;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(ShopActivity.this, "서버입력중", "잠시만 기다려주세요", true);
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
                progressDialog.dismiss();
                if (result.equalsIgnoreCase("success")) {
                    new AsyncBloodJSONList().execute("");
                } else {
                    return;
                }

            }
        }

        @Override
        public int getItemCount() {
            return eventEntityObjects.size();
        }}

}

