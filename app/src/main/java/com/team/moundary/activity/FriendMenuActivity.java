package com.team.moundary.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team.moundary.R;
import com.team.moundary.fragments.GirlsApplication;
import com.team.moundary.network.FriendManagerEntityObject;
import com.team.moundary.network.FriendManagerNetwork;
import com.team.moundary.network.FriendMenuActivityEntityObject;
import com.team.moundary.network.FriendMenuActivityHandler;

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
public class FriendMenuActivity extends FontActivity {
    LinearLayout nofriend;
    private ImageView backBtnImage;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend);
        nofriend=(LinearLayout)findViewById(R.id.no_friend);
        backBtnImage = (ImageView) findViewById(R.id.back_img_btn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.setup_toolbar);

        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_views);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        new AsyncBloodJSONList().execute("");

    }
    class AsyncBloodJSONList extends AsyncTask<String, Integer,
            ArrayList<FriendMenuActivityEntityObject>> {
        NetworkDialog netwrokDialog = new NetworkDialog(FriendMenuActivity.this);

        @Override
        protected ArrayList<FriendMenuActivityEntityObject> doInBackground(
                String... params) {
            String categoryValue = params[0];
            Response response = null;
            try {
                String targetURL = String.format(NetworkActivity.FRIEND_MENU_NETWORK,/*categoryValue*/userId);
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
                    return FriendMenuActivityHandler.getJSONBloodRequestAllList(
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
        protected void onPostExecute(ArrayList<FriendMenuActivityEntityObject>
                                             result) {
            netwrokDialog.dismiss();

            if (result != null && result.size() > 0) {
                MyAdapter bloodListAdapter = new MyAdapter(result, FriendMenuActivity.this);
                bloodListAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(bloodListAdapter);
                nofriend.setVisibility(View.GONE);
            }if(result == null){
                nofriend.setVisibility(View.VISIBLE);
            }
        }
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<FriendMenuActivityEntityObject> mDataset;
        private FriendManagerEntityObject friendManagerEntityObject;
        Context context;
        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public ImageView mImageView;
            public TextView name;
            public TextView babyage;
            public TextView address;
            public RelativeLayout friend_id;
            public ImageView delete;

            public ViewHolder(View view) {
                super(view);
                mView =view;
                mImageView = (ImageView) view.findViewById(R.id.friend_image);
                delete=(ImageView)view.findViewById(R.id.delete_friend);
                name = (TextView) view.findViewById(R.id.name);
                address=(TextView)view.findViewById(R.id.address);
                friend_id = (RelativeLayout) view.findViewById(R.id.friend_id);
            }
        }
        public MyAdapter(ArrayList<FriendMenuActivityEntityObject> myDataset,Context context) {
            mDataset = myDataset;
            this.context =context;
        }
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final FriendMenuActivityEntityObject friendmenuEntityObject = mDataset.get(position);


            Glide.with(GirlsApplication.getGirlsContext()).load(friendmenuEntityObject.profileThumbnail).override(510, 510).centerCrop().into(holder.mImageView);
            holder.name.setText(friendmenuEntityObject.nickname);
            holder.address.setText(" 주소 : "+friendmenuEntityObject.userAddress);

            holder.friend_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FriendMenuActivity.this, FriendPageActivity.class);
                    intent.putExtra("oppositeUserId",friendmenuEntityObject._id);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {




                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.custom_alert_layout, null);
                    TextView customTitle = (TextView)view.findViewById(R.id.customtitle);
                    customTitle.setText("친구삭제를 하시겠습니까?");
                    customTitle.setTextColor(Color.BLACK);
                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendMenuActivity.this);
                    builder.setView(view);
                    builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                mDataset.remove(position);
                                notifyItemRemoved(position);
                                new AsyncDeleteFriend(friendmenuEntityObject._id).execute(friendManagerEntityObject);
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
        }

        public class AsyncDeleteFriend extends AsyncTask<FriendManagerEntityObject, Void, String> {
            String oppnentId;
            String targetURL = String.format(FriendManagerNetwork.MOUNDARY_FRIEND_DELETE_URL,userId);
            NetworkDialog netwrokDialog = new NetworkDialog(context);

            public AsyncDeleteFriend(String oppnentId) {
                this.oppnentId = oppnentId;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                netwrokDialog.show();
            }



            @Override
            protected String doInBackground(FriendManagerEntityObject... entity) {
                //resultInfo =HttpApiHelperHandler.RequestServer(entitiyObject);
                FriendManagerEntityObject httpParams = entity[0];

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
                    requestBody.add("oppositeUserId", oppnentId);
                    FormBody reqBody = requestBody.build();

                    Request request = new Request.Builder()
                            .url(targetURL)
                            .put(reqBody)
                            .build();
                    //응답에 대한 코드
                    response = toServer.newCall(request).execute();
                    boolean flag = response.isSuccessful();
                    ResponseBody responseBody = response.body();
                    if(flag){
                        JSONObject resultfromServer = new JSONObject(responseBody.string());
                        return resultfromServer.getString("msg");
                    }

                }catch(Exception e){
                    Log.e("doInBac", "친구삭제중 에러발생", e);
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
                if (result != null && result.equalsIgnoreCase("success")) {
                    Toast.makeText(FriendMenuActivity.this, " 친구삭제 정상 입력.", Toast.LENGTH_SHORT).show();
                    new AsyncBloodJSONList().execute("");
                } else {
                    Toast.makeText(FriendMenuActivity.this, "친구삭제 입력 실패.", Toast.LENGTH_SHORT).show();
                }
            }

        }
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}