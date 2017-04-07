package com.team.moundary.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
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
import com.team.moundary.network.FriendManagerNetwork;
import com.team.moundary.network.LikeCountEntityObject;
import com.team.moundary.activity.MoundaryFriendNewsActivity;
import com.team.moundary.network.MyStoryDeleteEntityObject;
import com.team.moundary.network.MyStoryEntityObject;
import com.team.moundary.network.MystoryHandler;
import com.team.moundary.activity.NetworkActivity;
import com.team.moundary.activity.NetworkDialog;
import com.team.moundary.activity.PostDetailActivity;
import com.team.moundary.activity.PropertyManager;
import com.team.moundary.network.RecommendInforFragmentWrite;
import com.team.moundary.activity.What_Think;

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
 * Created by cccei on 2016-07-19.
 */
public class MyStoryFragment extends Fragment {

    private Context context;
    LinearLayout nofriend;
    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId();

    public static int increment;
    static MoundaryFriendNewsActivity owner;
    MyStoryDeleteEntityObject friendManagerEntityObject;
    String postId;
    LikeCountEntityObject likeCountEntityObject;

    public MyStoryFragment() {
    }

    public static MyStoryFragment newInstance(int initValue, Context context) {
        MyStoryFragment girlFragment = new MyStoryFragment();
        return girlFragment;
    }

    RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mystory_fragment, container, false);

        ImageView what_think = (ImageView) view.findViewById(R.id.what_think);

        owner = (MoundaryFriendNewsActivity)getActivity();

        nofriend=(LinearLayout)view.findViewById(R.id.no_friend);
        rv = (RecyclerView) view.findViewById(R.id.recyclerview_1);

        rv.setLayoutManager(new LinearLayoutManager(owner));
        myStoryAdapter = new GirlGroupRecyclerViewAdapter(owner, new ArrayList<MyStoryEntityObject>());
        rv.setAdapter(myStoryAdapter);

        what_think.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), What_Think.class);
                startActivity(intent);
            }
        });


        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncBloodJSONList().execute("");
    }

    @Override
    public void onResume() {
        super.onResume();
        new AsyncBloodJSONList().execute("");
    }

    GirlGroupRecyclerViewAdapter myStoryAdapter;
    public class GirlGroupRecyclerViewAdapter extends RecyclerView.Adapter<GirlGroupRecyclerViewAdapter.ViewHolder> {
        private ArrayList<MyStoryEntityObject> myStoryEntityObjects;
        Context context;

        public GirlGroupRecyclerViewAdapter(Context context, ArrayList<MyStoryEntityObject> resources) {
            this.context=context;
            this.myStoryEntityObjects = resources;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.girls_group_item, parent, false);
            return new ViewHolder(view);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView girlsImage;
            public final TextView memberName;
            TextView likeCountWD;
            public int likeCount;
            ImageView likeImageWD;
            ImageView repleImageWD;
            TextView membercontent;
            TextView replyCountWD;
            TextView writeDate;
            ImageView writedelete;
            ImageView writeEdit;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                girlsImage = (ImageView) view.findViewById(R.id.girls_group_member_image);
                memberName = (TextView) view.findViewById(R.id.member_name);
                likeImageWD = (ImageView) view.findViewById(R.id.like_image);
                writeDate=(TextView)view.findViewById(R.id.write_date);
                repleImageWD = (ImageView) view.findViewById(R.id.reple_image);
                likeCountWD = (TextView) view.findViewById(R.id.like_count);
                replyCountWD = (TextView) view.findViewById(R.id.reple_count);
                membercontent = (TextView) view.findViewById(R.id.member_content);
                writedelete=(ImageView)view.findViewById(R.id.delete_post);
                writeEdit = (ImageView)view.findViewById(R.id.rewrite_post);
            }
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final MyStoryEntityObject myStoryEntityObject = myStoryEntityObjects.get(position);
            final int[] likeCheckInt = new int[1];

            Glide.with(GirlsApplication.getGirlsContext()).load(myStoryEntityObject.postimg).override(510, 300).centerCrop().into(holder.girlsImage);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PostDetailActivity.class);


                    intent.putExtra("postId", myStoryEntityObject.id);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(owner, holder.girlsImage, ViewCompat.getTransitionName(holder.girlsImage));

                    ActivityCompat.startActivity(owner, intent, options.toBundle());
                }
            });

            holder.memberName.setText(myStoryEntityObject.nickname);
            holder.membercontent.setText(myStoryEntityObject.postcontent);
            holder.likeCountWD.setText(myStoryEntityObject.postLikeCount);
            holder.replyCountWD.setText(myStoryEntityObject.replyCount);
            holder.writeDate.setText(myStoryEntityObject.postdate);

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

            holder.writedelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.custom_alert_layout, null);
                    TextView customTitle = (TextView)view.findViewById(R.id.customtitle);
                    customTitle.setText("글을 삭제하시겠습니까?");
                    customTitle.setTextColor(Color.BLACK);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(view);
                    builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {

                                myStoryEntityObjects.remove(position);
                                notifyItemRemoved(position);
                                new AsyncDeleteMyStory(myStoryEntityObject.id,getActivity()).execute(friendManagerEntityObject);
                            } catch(IndexOutOfBoundsException ex) {

                                ex.printStackTrace();

                            }
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            holder.writeEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myStoryEntityObject.category == "0") {
                        Intent intent = new Intent(owner, What_Think.class);
                        intent.putExtra("postId",myStoryEntityObject.id);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(owner, RecommendInforFragmentWrite.class);
                        intent.putExtra("postId",myStoryEntityObject.id);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return myStoryEntityObjects.size();
        }
    }

    public class AsyncBloodJSONList extends AsyncTask<String, Integer,
            ArrayList<MyStoryEntityObject>> {

        NetworkDialog netwrokDialog = new NetworkDialog(owner);

        @Override
        protected ArrayList<MyStoryEntityObject> doInBackground(
                String... params) {
            String targetURL = String.format(NetworkActivity.MY_STORY_NETWORK,userId);
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
                    return MystoryHandler.getJSONBloodRequestAllList(
                            new StringBuilder(responseBody.string()));
                }
            } catch (UnknownHostException une) {
                e("aaa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("bbb", uee.toString());
            } catch (Exception e) {
                e("ccctototto", e.toString());
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
        protected void onPostExecute(ArrayList<MyStoryEntityObject> result) {
            netwrokDialog.dismiss();

            if (result != null && result.size() > 0) {
                GirlGroupRecyclerViewAdapter bloodListAdapter =
                        new GirlGroupRecyclerViewAdapter(owner, result);
                bloodListAdapter.notifyDataSetChanged();
                rv.setAdapter(bloodListAdapter);
                nofriend.setVisibility(View.GONE);
            }if(result == null) {
                nofriend.setVisibility(View.VISIBLE);
            }
        }
    }

    public class AsyncDeleteMyStory extends AsyncTask<MyStoryDeleteEntityObject, Void, String> {
        String postId;
        Context context;
        String targetURL = String.format(FriendManagerNetwork.MOUNDARY_MYSTORY_DELETE_URL,userId);
        NetworkDialog netwrokDialog = new NetworkDialog(owner);

        public AsyncDeleteMyStory(String postId,Context context) {
            this.postId = postId;
            owner = (MoundaryFriendNewsActivity) context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netwrokDialog.show();
        }



        @Override
        protected String doInBackground(MyStoryDeleteEntityObject... entity) {

            Response response=null;
            try{
                /*
                 연결설정에 대한 부분
                 */
                OkHttpClient toServer = new OkHttpClient().newBuilder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                /*
                 요청에 대한 설정
                 */
                FormBody.Builder requestBody = new FormBody.Builder();
                requestBody.add("userId",userId);
                requestBody.add("postId", postId);
                FormBody reqBody = requestBody.build();

                Request request = new Request.Builder()
                        .url(targetURL)
                        .delete(reqBody)
                        .build();
                Log.e("삭제항목","serverStart");
                //응답에 대한 코드
                response = toServer.newCall(request).execute();
                Log.e("삭제항목","serverEnd");
                boolean flag = response.isSuccessful();
                ResponseBody responseBody = response.body();
                if(flag){
                    JSONObject resultfromServer = new JSONObject(responseBody.string());
                    return resultfromServer.getString("msg");
                }

            }catch(Exception e){
                Log.e("deleterror", "글 삭제중 에러발생", e);
            }finally{
                //종료
                if(response != null){
                    response.close();
                }
            }
            return "fail";
        }

        @Override
        protected void onPostExecute(String result) {
            netwrokDialog.dismiss();
            if(result.equalsIgnoreCase("delete success")){
                new  AsyncBloodJSONList().execute("");
            }else{
                return;
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