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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.team.moundary.activity.NetworkActivity;
import com.team.moundary.activity.NetworkDialog;
import com.team.moundary.activity.PostDetailActivity;
import com.team.moundary.activity.PropertyManager;

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
 * Created by ccei on 2016-07-28.
 */
public class FriendPageGridView extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    static FriendPageActivity owner;
    String getOppnentId;

    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId

    public FriendPageGridView(){}

    public static FriendPageGridView newInstance() {
        FriendPageGridView fg = new FriendPageGridView();
        return fg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        owner = (FriendPageActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.friend_listview_fragment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(owner, 2));
        myStoryAdapter = new FriendPageAdapterGrid(owner, new ArrayList<FriendPageListViewEntityObject>());
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

    FriendPageAdapterGrid myStoryAdapter;

    public class FriendPageAdapterGrid extends RecyclerView.Adapter<FriendPageAdapterGrid.ViewHolder> {
        private ArrayList<FriendPageListViewEntityObject> myFriendPageListViewEntityObjects;
        Context context;

        public FriendPageAdapterGrid(Context context, ArrayList<FriendPageListViewEntityObject> resources) {
            myFriendPageListViewEntityObjects = resources;
            owner= (FriendPageActivity)context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.girls_group_item_grid,parent,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final FriendPageListViewEntityObject myStoryEntityObject = myFriendPageListViewEntityObjects.get(position);

            Glide.with(GirlsApplication.getGirlsContext()).load(myStoryEntityObject.postImg).override(510, 510).centerCrop().into(holder.girlsImage);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GirlsApplication.getGirlsContext(), PostDetailActivity.class);
                    intent.putExtra("postId", myStoryEntityObject.id);
                   // intent.putExtra("memberName", holder.postdate.getText().toString());


                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(owner, holder.girlsImage, ViewCompat.getTransitionName(holder.girlsImage));

                    ActivityCompat.startActivity(owner, intent, options.toBundle());
                }


            });

            //holder.postdate.setText(myStoryEntityObject.postdate);
           // holder.likeCountWD.setText(myStoryEntityObject.postLikeUsers);

        }

        @Override
        public int getItemCount() {
            return myFriendPageListViewEntityObjects.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView girlsImage;
           // public final TextView postdate;
            TextView likeCountWD;
            ImageView likeImageWD;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                girlsImage = (ImageView) view.findViewById(R.id.girls_group_member_image);
              //  postdate = (TextView) view.findViewById(R.id.post_date);
                likeImageWD = (ImageView) view.findViewById(R.id.like_image);
                likeCountWD = (TextView) view.findViewById(R.id.like_count);
            }
        }
    }


    class AsyncBloodJSONList extends AsyncTask<String, Integer,
            ArrayList<FriendPageListViewEntityObject>> {

        NetworkDialog netwrokDialog = new NetworkDialog(owner);
        String targetURL = String.format(NetworkActivity.FRIEND_PAGE_LISTVIEW_NETWORK,getOppnentId);
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
                FriendPageAdapterGrid bloodListAdapter = new FriendPageAdapterGrid(owner, result);
                bloodListAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(bloodListAdapter);
            }
        }
    }
}
