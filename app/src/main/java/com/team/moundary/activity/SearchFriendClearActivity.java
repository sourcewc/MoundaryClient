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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.moundary.R;
import com.team.moundary.fragments.GirlsApplication;
import com.team.moundary.network.SearchFriendClearActivityEntityObject;
import com.team.moundary.network.SearchFriendClearActivityHandler;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei on 2016-08-18.
 */
public class SearchFriendClearActivity extends FontActivity {

    ImageView backBtnImage;
    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId();
    LinearLayout nofriend;
    String oppnentId;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<SearchFriendClearActivityEntityObject> myDataset;
    private String nicknames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_clear);
        nofriend=(LinearLayout)findViewById(R.id.no_friend);
        backBtnImage =(ImageView)findViewById(R.id.back_img_btn);
        Toolbar toolbar = (Toolbar)findViewById(R.id.setup_toolbar);

        Intent intent = getIntent();

        nicknames=intent.getStringExtra("nickname");


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
        myStoryAdapter = new SearchClearAdapter(new ArrayList<SearchFriendClearActivityEntityObject>(),getApplicationContext());
        mRecyclerView.setAdapter(myStoryAdapter);



        new AsyncBloodJSONList().execute("");
    }
    class AsyncBloodJSONList extends AsyncTask<String, Integer,
            ArrayList<SearchFriendClearActivityEntityObject>> {
        NetworkDialog netwrokDialog = new NetworkDialog(SearchFriendClearActivity.this);

        @Override
        protected ArrayList<SearchFriendClearActivityEntityObject> doInBackground(
                String... params) {
            String categoryValue = params[0];
            Response response = null;
            try {
                String targetURL = String.format(NetworkActivity.SEARCH_FRIEND_NETWORK,nicknames,userId);
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
                    return SearchFriendClearActivityHandler.getJSONBloodRequestAllList(
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
        protected void onPostExecute(ArrayList<SearchFriendClearActivityEntityObject> result) {
            netwrokDialog.dismiss();

            if (result != null && result.size() > 0) {
                SearchClearAdapter bloodListAdapter =
                        new SearchClearAdapter(
                                result, SearchFriendClearActivity.this);
                bloodListAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(bloodListAdapter);
                nofriend.setVisibility(View.GONE);
            }
            if(result == null) {
                nofriend.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    class SearchClearAdapter extends RecyclerView.Adapter<SearchClearAdapter.ViewHolder> {
        private ArrayList<SearchFriendClearActivityEntityObject> searchFriendClearActivityEntityObjects;
        Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public ImageView mImageView;
            public TextView nickname;
            public TextView address;
            public TextView babyAge;
            public RelativeLayout recyclerItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView)view.findViewById(R.id.image_friend);
                nickname = (TextView)view.findViewById(R.id.friend_name_text);
                address = (TextView)view.findViewById(R.id.friend_location_text);
                recyclerItem= (RelativeLayout)view.findViewById(R.id.push_recycler_item);


            }
        }
        public SearchClearAdapter(ArrayList<SearchFriendClearActivityEntityObject> myDataset, Context context) {
            searchFriendClearActivityEntityObjects = myDataset;
            this.context =context;
        }
        @Override
        public SearchClearAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_clear_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final SearchFriendClearActivityEntityObject searchFriendClearActivityEntityObject = searchFriendClearActivityEntityObjects.get(position);

            Glide.with(GirlsApplication.getGirlsContext()).load(searchFriendClearActivityEntityObject.profileThumbnail).into(holder.mImageView);
            holder.nickname.setText(searchFriendClearActivityEntityObject.nickname);
            holder.address.setText(searchFriendClearActivityEntityObject.userAddress);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), FriendPageActivity.class);


                    oppnentId=searchFriendClearActivityEntityObject._id;
                    intent.putExtra("oppositeUserId",oppnentId);

                    startActivity(intent);
                }
            });
        }
        @Override
        public int getItemCount() {
            return searchFriendClearActivityEntityObjects.size();
        }}

}
