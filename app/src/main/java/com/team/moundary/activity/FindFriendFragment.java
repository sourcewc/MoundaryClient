package com.team.moundary.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.moundary.R;
import com.team.moundary.fragments.GirlsApplication;
import com.team.moundary.network.FindFriendActivityEntityObject;
import com.team.moundary.network.FindFriendActivityHandler;
import com.team.moundary.network.FriendManagerEntityObject;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class FindFriendFragment extends FontActivity implements AdapterView.OnItemSelectedListener {

    ImageView change_view_image;
    LinearLayout nofriend;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<FindFriendActivityEntityObject> myDataset;
    private LinearLayoutManager mLayoutManager;

    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_find);
        nofriend=(LinearLayout)findViewById(R.id.no_friend);
        ImageView backBtn = (ImageView) findViewById(R.id.live_back_button);
        change_view_image=(ImageView)findViewById(R.id.change_view_image);

        change_view_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SearchFriendActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });


        FindFriendAdapter myStoryAdapter;

        mRecyclerView = (RecyclerView) findViewById(R.id.friend_find_recycler);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myStoryAdapter = new FindFriendAdapter(new ArrayList<FindFriendActivityEntityObject>());
        mRecyclerView.setAdapter(myStoryAdapter);
        myDataset = new ArrayList<>();

        mAdapter = new FindFriendAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        new AsyncBloodJSONList().execute("");

    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) { }

    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class AsyncBloodJSONList extends AsyncTask<String, Integer, ArrayList<FindFriendActivityEntityObject>> {
        NetworkDialog netwrokDialog = new NetworkDialog(FindFriendFragment.this);

        @Override
        protected ArrayList<FindFriendActivityEntityObject> doInBackground(
                String... params) {
            String categoryValue = params[0];
            Response response = null;
            try {
                String targetURL = String.format(NetworkActivity.FIND_FRIEND_NETWORK,/*categoryValue*/userId);
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
                    return FindFriendActivityHandler.getJSONBloodRequestAllList(
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
        protected void onPostExecute(ArrayList<FindFriendActivityEntityObject>
                                             result) {
            netwrokDialog.dismiss();

            if (result != null && result.size() > 0) {
                FindFriendAdapter bloodListAdapter =
                        new FindFriendAdapter(result);
                bloodListAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(bloodListAdapter);
                nofriend.setVisibility(View.GONE);
            }if(result == null){
                nofriend.setVisibility(View.VISIBLE);

            }
        }
    }

    class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.ViewHolder> {
        private ArrayList<FindFriendActivityEntityObject> eventEntityObjects;
        FriendManagerEntityObject friendManagerEntityObject;

        public FindFriendAdapter(ArrayList<FindFriendActivityEntityObject> myDataset) {
            eventEntityObjects = myDataset;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_find_list,parent,false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            final FindFriendActivityEntityObject shopEntityObject = eventEntityObjects.get(position);

            Glide.with(GirlsApplication.getGirlsContext()).load(shopEntityObject.profileThumbnail).into(holder.mImageView);
            holder.mTextView2.setText(" 주소 : "+shopEntityObject.userAddress);
            holder.mTextView.setText(shopEntityObject.nickname);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent(v.getContext(), FriendPageActivity.class);
                    intent.putExtra("oppositeUserId",shopEntityObject._id);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                }
            });
        }

        @Override
        public int getItemCount() {
            return eventEntityObjects.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImageView;
            public TextView mTextView;
            public TextView mTextView2;
            public TextView mTextView3;
            public LinearLayout eventRelative;
            public View mView;

            public ViewHolder(View view) {
                super(view);
                mView =view;
                mImageView = (ImageView)view.findViewById(R.id.image_friend);
                mTextView = (TextView)view.findViewById(R.id.friend_name_text);
                mTextView2 = (TextView)view.findViewById(R.id.friend_location_text);
                eventRelative = (LinearLayout)view.findViewById(R.id.event_list_recycler);
            }
        }
    }
}