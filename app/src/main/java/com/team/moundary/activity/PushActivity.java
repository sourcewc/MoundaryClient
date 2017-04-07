package com.team.moundary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.moundary.R;
import com.team.moundary.fragments.GirlsApplication;
import com.team.moundary.network.PushActivityEntityObject;
import com.team.moundary.network.PushActivityHandler;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class PushActivity extends FontActivity {

    ImageView backBtnImage;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private int currentCategory;
    LinearLayout nofriend;
    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId
    String checkCategory;
    String pushType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push);
        nofriend=(LinearLayout)findViewById(R.id.no_friend);
        backBtnImage =(ImageView)findViewById(R.id.back_img_btn);

        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onBackPressed();}});

        PushAdapter mypushAdapter = null;

        Intent intent = getIntent();

        mRecyclerView = (RecyclerView) findViewById(R.id.push_recycler);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mypushAdapter = new PushAdapter(new ArrayList<PushActivityEntityObject>(),getApplicationContext());
        mRecyclerView.setAdapter(mypushAdapter);

        currentCategory = intent.getIntExtra("category", -1);
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
        }else{
            new AsyncBloodJSONList().execute(String.valueOf(1));
        }

    }


    class AsyncBloodJSONList extends AsyncTask<String, Integer,
            ArrayList<PushActivityEntityObject>> {
        NetworkDialog netwrokDialog = new NetworkDialog(PushActivity.this);

        @Override
        protected ArrayList<PushActivityEntityObject> doInBackground(
                String... params) {
            String categoryValue = params[0];
            Response response = null;
            try {
                String targetURL = String.format(NetworkActivity.PUSH_NETWORK,/*categoryValue*/userId);

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
                    return PushActivityHandler.getJSONBloodRequestAllList(
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
        protected void onPostExecute(ArrayList<PushActivityEntityObject>
                                             result) {
            netwrokDialog.dismiss();

            if (result != null && result.size() > 0) {
                PushAdapter bloodListAdapter =
                        new PushAdapter(result, PushActivity.this);
                bloodListAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(bloodListAdapter);
                nofriend.setVisibility(View.GONE);
            }
            if(result==null){
                nofriend.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onBackPressed() {super.onBackPressed();}

    class PushAdapter extends RecyclerView.Adapter<PushAdapter.ViewHolder> {
        private ArrayList<PushActivityEntityObject> mDataset;

        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView push_image;
            public TextView push_nickname;
            public TextView push_date;
            public RelativeLayout recyclerItem;

            public ViewHolder(View view) {
                super(view);
                push_image = (ImageView)view.findViewById(R.id.push_image);
                push_nickname = (TextView)view.findViewById(R.id.push_nickname);
                push_date = (TextView)view.findViewById(R.id.push_date);
                recyclerItem= (RelativeLayout)view.findViewById(R.id.push_recycler_item);
            }
        }
        public PushAdapter(ArrayList<PushActivityEntityObject> myDataset, Context context) {
            mDataset = myDataset;
            this.context=context;
        }
        @Override
        public PushAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.push_list, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final PushActivityEntityObject pushActivityEntityObject = mDataset.get(position);
            Glide.with(GirlsApplication.getGirlsContext()).load(pushActivityEntityObject.img).into(holder.push_image);

            Intent intent = getIntent();
            /*checkCategory=intent.getStringExtra("category");
            pushType=intent.getStringExtra("pushType");*/

            holder.recyclerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context ,PostDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("postId",pushActivityEntityObject.postId);
                    context.startActivity(intent);
                }
            });
            holder.push_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context ,FriendPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("oppositeUserId",pushActivityEntityObject.pusherId);
                    context.startActivity(intent);
                }
            });

            if(pushActivityEntityObject.pushType=="1") {
//                if (checkCategory == "0") {
//                    holder.push_nickname.setText(pushActivityEntityObject.pusherNickname + " 님이 내 글에 상태를 남겼습니다.");
                /*}*/ if (pushActivityEntityObject.category == "1") {
                    holder.push_nickname.setText(pushActivityEntityObject.pusherNickname + " 님이 세일 정보글에 글을 업로드했습니다.");
                } else if (pushActivityEntityObject.category == "2") {
                    holder.push_nickname.setText(pushActivityEntityObject.pusherNickname + " 님이 나눔 정보글에 글을 업로드했습니다.");
                } else if (pushActivityEntityObject.category == "3") {
                    holder.push_nickname.setText(pushActivityEntityObject.pusherNickname + " 님이 이벤트 정보글에 글을 업로드했습니다.");
                } else if (pushActivityEntityObject.category == "4") {
                    holder.push_nickname.setText(pushActivityEntityObject.pusherNickname + " 님이 상점 정보글에 글을 업로드했습니다.");
                }
            } else if(pushActivityEntityObject.pushType == "0") {
                if(pushActivityEntityObject.category == "0") {
                    holder.push_nickname.setText(pushActivityEntityObject.pusherNickname + " 님이 댓글을 남겼습니다.");
                } else if(pushActivityEntityObject.category == "1") {
                    holder.push_nickname.setText(pushActivityEntityObject.pusherNickname + " 님이 좋아요를 보냈습니다.");
                }
            }
            holder.push_date.setText(pushActivityEntityObject.pushDate);
        }
        @Override
        public int getItemCount() {
            return mDataset.size();
        }}


}