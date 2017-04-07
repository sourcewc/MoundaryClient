package com.team.moundary.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.team.moundary.fragments.GirlsApplication;
import com.team.moundary.network.FriendAskActivityEntityObject;
import com.team.moundary.network.FriendAskActivityHandler;
import com.team.moundary.network.FriendManagerEntityObject;
import com.team.moundary.network.FriendManagerNetwork;

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

public class FriendAskActivity extends Fragment {
    static PushActivity owner;
    RecyclerView rv;
    FriendManagerEntityObject friendManagerEntityObject;

    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_recycler, container, false);

        rv= (RecyclerView)view.findViewById(R.id.recyclerview);
        myStoryAdapter = new Recycler_View_Adapter(owner, new ArrayList<FriendAskActivityEntityObject>());
        rv.setAdapter(myStoryAdapter);
        rv.setLayoutManager(new LinearLayoutManager(owner));

        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncBloodJSONList().execute("");
    }

    public class AsyncBloodJSONList extends AsyncTask<String, Integer,
            ArrayList<FriendAskActivityEntityObject>> {
        ProgressDialog dialog;
        String targetURL=String.format(NetworkActivity.FRIEND_ASK_NETWORK,userId);

        @Override
        protected ArrayList<FriendAskActivityEntityObject> doInBackground(
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
                    return FriendAskActivityHandler.getJSONBloodRequestAllList(
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
            // dialog = ProgressDialog.show(getContext(),
            //       "", "잠시만 기다려 주세요 ...", true);
        }

        @Override
        protected void onPostExecute(ArrayList<FriendAskActivityEntityObject>
                                             result) {
//            dialog.dismiss();

            if (result != null && result.size() > 0) {
                Recycler_View_Adapter bloodListAdapter = new Recycler_View_Adapter(owner, result);
                bloodListAdapter.notifyDataSetChanged();
                rv.setAdapter(bloodListAdapter);
            }
        }
    }
    Recycler_View_Adapter myStoryAdapter;

    class Recycler_View_Adapter extends RecyclerView.Adapter<Recycler_View_Adapter.ViewHolder> {

        private ArrayList<FriendAskActivityEntityObject> myFriendPageListViewEntityObjects;
        Context context;

        public Recycler_View_Adapter(Context context, ArrayList<FriendAskActivityEntityObject> resources) {
            myFriendPageListViewEntityObjects = resources;
            owner =(PushActivity)context;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_ask_list,parent,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final FriendAskActivityEntityObject myStoryEntityObject = myFriendPageListViewEntityObjects.get(position);

            Glide.with(GirlsApplication.getGirlsContext()).load(myStoryEntityObject.profileThumbnail).into(holder.girlsImage);
            holder.memberName.setText(myStoryEntityObject.nickname+" 님이 친구요청을 보냈습니다");

            holder.pushdetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(),FriendPageActivity.class);
                    intent.putExtra("oppositeUserId",myStoryEntityObject._id);
                    startActivity(intent);

                }
            });
            holder.pushYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myFriendPageListViewEntityObjects.remove(position);
                    notifyItemRemoved(position);
                    new AsyncFriendAllow(myStoryEntityObject._id).execute();
                    new AsyncBloodJSONList().execute("");
                }
            });
            holder.pushNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myFriendPageListViewEntityObjects.remove(position);
                    notifyItemRemoved(position);
                    new AsyncFriendReject(myStoryEntityObject._id).execute();
                    new AsyncBloodJSONList().execute("");
                }
            });

        }
        @Override
        public int getItemCount() {
            return myFriendPageListViewEntityObjects.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView girlsImage;
            public final TextView memberName;
            public final ImageView pushdetail;
            public final ImageView pushYes;
            public final ImageView pushNo;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                girlsImage = (ImageView) view.findViewById(R.id.imageView);
                memberName = (TextView) view.findViewById(R.id.title_news);
                pushdetail=(ImageView)view.findViewById(R.id.pushdetail);
                pushYes = (ImageView)view.findViewById(R.id.pushyes);
                pushNo = (ImageView)view.findViewById(R.id.pushno);
            }
        }
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

    public class AsyncFriendAllow extends AsyncTask<FriendManagerEntityObject, Void, String> {
        String oppnentId;
        Context context;
        PushActivity pushActivity;
        String targetURL = String.format(FriendManagerNetwork.MOUNDARY_FRIEND_ALLOW_URL,userId);
        private ProgressDialog progressDialog;

        public AsyncFriendAllow(String oppnentId) {
            this.oppnentId = oppnentId;
//            pushActivity = (PushActivity) context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(pushActivity, "서버입력중", "잠시만 기다려주세요", true);
        }


        @Override
        protected String doInBackground(FriendManagerEntityObject... entity) {
            //resultInfo =HttpApiHelperHandler.RequestServer(entitiyObject);
//            FriendManagerEntityObject httpParams = entity[0];

            Response response = null;
            try {
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
                if (flag) {
                    JSONObject resultfromServer = new JSONObject(responseBody.string());
                    return resultfromServer.getString("msg");
                }

            } catch (Exception e) {
                Log.e("doInBac", "친구요청 에러발생", e);
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
//            progressDialog.dismiss();
            /*if (result != null && result.equalsIgnoreCase("success")) {
                Toast.makeText(GirlsMemberDetailActivity.this, " 댓글 정상 입력.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GirlsMemberDetailActivity.this, "댓글 입력 실패.", Toast.LENGTH_SHORT).show();
            }*/
        }

    }

    public class AsyncFriendReject extends AsyncTask<FriendManagerEntityObject, Void, String> {
        String oppnentId;
        String targetURL=String.format(FriendManagerNetwork.MOUNDARY_FRIEND_REJECT_URL,userId);
        private ProgressDialog progressDialog;

        public AsyncFriendReject(String oppnentId) {
            this.oppnentId = oppnentId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(owner, "서버입력중", "잠시만 기다려주세요", true);
        }


        @Override
        protected String doInBackground(FriendManagerEntityObject... entity) {

            Response response = null;
            try {
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
                if (flag) {
                    JSONObject resultfromServer = new JSONObject(responseBody.string());
                    return resultfromServer.getString("msg");
                }

            } catch (Exception e) {
                Log.e("doInBac", "친구취소 에러발생", e);
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
//            progressDialog.dismiss();
            /*if (result != null && result.equalsIgnoreCase("success")) {
                Toast.makeText(GirlsMemberDetailActivity.this, " 댓글 정상 입력.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GirlsMemberDetailActivity.this, "댓글 입력 실패.", Toast.LENGTH_SHORT).show();
            }*/
        }
    }

}