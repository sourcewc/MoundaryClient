package com.team.moundary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team.moundary.R;
import com.team.moundary.fragments.GirlsApplication;
import com.team.moundary.network.FriendNewEntityObject;
import com.team.moundary.network.LikeCountEntityObject;
import com.team.moundary.network.PostDetailEntityObject;
import com.team.moundary.network.PostDetailHandler;
import com.team.moundary.network.PostReplyEntityObject;
import com.team.moundary.network.ReplyDeleteEntityObject;
import com.team.moundary.network.ReplyEditEntityObject;
import com.team.moundary.network.ReplyEntityObject;

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
 * Created by ccei on 2016-08-27.
 */
public class PostDetailInfoActivity extends FontActivity {
    //하드코딩 밭,, 교체해줘야 합니다

    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<PostDetailEntityObject> myDatasets;
    private ImageView backBtnImage;
    private EditText replyEdit;
    private Button repleInput;
    ImageView member_image;
    TextView likeCountWD;
    TextView member_title;
    TextView member_content;
    ImageView likeImageWD;
    TextView reple_count;
    String myLikeChecked;
    String postId;
    ReplyEntityObject replyEntityObject;
    LikeCountEntityObject likeCountEntityObject;
    FriendNewEntityObject friendNewEntityObject;
    final int[] likeCheckInt = new int[1];
    DAdapter myStoryAdapter;
    ArrayList<PostDetailEntityObject> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_post_detail);

        myStoryAdapter = new DAdapter(new ArrayList<PostReplyEntityObject>(), this);

        mRecyclerView = (RecyclerView) findViewById(R.id.member_detail);
        mRecyclerView.setAdapter(myStoryAdapter);
        myDatasets = new ArrayList<>();

        //likeImageWD = (ImageView) findViewById(R.id.like_image);
        //likeCountWD = (TextView) findViewById(R.id.like_count);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

//        likeImageWD.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (myLikeChecked == "false") {
//                    new AsyncLikeRequest(postId).execute(likeCountEntityObject);
//
//                } else {
//                    new AsyncLikeRequest(postId).execute(likeCountEntityObject);
//                }
//            }
//        });

        backBtnImage = (ImageView) findViewById(R.id.back_img_btn);

        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            Transition exitTrans = new Explode();

            Transition reenterTrans = new Explode();

            window.setExitTransition(exitTrans);
            window.setEnterTransition(reenterTrans);
            window.setReenterTransition(reenterTrans);
        }


        member_image = (ImageView) findViewById(R.id.member_image);
        member_title = (TextView) findViewById(R.id.member_title);
        member_content = (TextView) findViewById(R.id.post_detail_content);
        reple_count = (TextView) findViewById(R.id.reple_count);

        replyEdit = (EditText) findViewById(R.id.input_reply_editext);
        repleInput = (Button) findViewById(R.id.reple_sendbtn);

        repleInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String repleData = replyEdit.getText().toString().trim();
                if (repleData != null && repleData.length() > 0) {
                    new AsyncReplyInsert(repleData, postId).execute(replyEntityObject);
                } else {
                    Toast.makeText(PostDetailInfoActivity.this, "입력된 값이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    replyEdit.requestFocus();
                }

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navi_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class AsyncPostDetailList extends AsyncTask<String, Integer, ArrayList<PostDetailEntityObject>> {
        NetworkDialog netwrokDialog = new NetworkDialog(PostDetailInfoActivity.this);

        @Override
        protected ArrayList<PostDetailEntityObject> doInBackground(String... params) {
            Intent intent = getIntent();
            final String postId = intent.getStringExtra("postId");
            Response response = null;
            try {
                String targetURL = String.format(NetworkActivity.POST_DETAIL, postId,userId);
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
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
                    ArrayList<PostDetailEntityObject> data = PostDetailHandler.getJSONBloodRequestAllList(new StringBuilder(responseBody.string()));
                    /*Log.i("sizedata", data.size() + "");
                    Log.i("data", data.get(0).reply.get(0).reply_Content);*/
                    return data;
                }
            } catch (UnknownHostException une) {
                e("aaa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("bbb", uee.toString());
            } catch (Exception e) {
                e("ccc", e.toString());
            } finally {
                if (response != null) {
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
        protected void onPostExecute(final ArrayList<PostDetailEntityObject> result) {
            netwrokDialog.dismiss();

            if (result != null) {
                if (result.size() > 0) {
                    Glide.with(PostDetailInfoActivity.this).load(result.get(0).postImg).override(510, 300).centerCrop().into(member_image);

                    member_title.setText(result.get(0).nickname);
                    member_content.setText(result.get(0).postContent);
                 //   likeCountWD.setText(result.get(0).postLikeCount);
                    reple_count.setText(result.get(0).replyCount);

      /*              if(result.get(0).myLike=="false") {
                        myLikeChecked = result.get(0).myLike;
                        likeCheckInt[0] = Integer.parseInt(result.get(0).postLikeCount);
                        likeCheckInt[0]++;
                        result.get(0).myLike="true";
                    } else {
                        myLikeChecked = result.get(0).myLike;
                        likeCheckInt[0] = Integer.parseInt(result.get(0).postLikeCount);
                        likeCheckInt[0]--;
                        result.get(0).myLike="false";
                    }*/

                    mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    data.addAll(result);
                    if (data.size() > 0) {
                        Log.i("leelogDataSize", data.size() + "");
                        myStoryAdapter.postDetailEntityObjcects.clear();
                        myStoryAdapter.postDetailEntityObjcects.addAll(result.get(0).reply);
                        myStoryAdapter.notifyDataSetChanged();

                    }


                }
            }
        }
    }

    Context mContext = this;


    public class AsyncReplyInsert extends AsyncTask<ReplyEntityObject, Void, String> {
        String setReply;
        String postId;

        String targetURL = String.format(NetworkActivity.MOUNDARY_REPLY_WRETE_URL, userId);
        NetworkDialog netwrokDialog = new NetworkDialog(PostDetailInfoActivity.this);

        public AsyncReplyInsert(String setReply, String postId) {
            this.setReply = setReply;
            this.postId = postId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netwrokDialog.show();
        }


        @Override
        protected String doInBackground(ReplyEntityObject... entity) {
            //resultInfo =HttpApiHelperHandler.RequestServer(entitiyObject);
            ReplyEntityObject httpParams = entity[0];

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

                requestBody.add("postId", postId);
                requestBody.add("replyContent", setReply);
                FormBody reqBody = requestBody.build();

                Request request = new Request.Builder()
                        .url(targetURL)
                        .post(reqBody)
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
                Log.e("doInBac", "댓글전송중 에러발생", e);
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
            if (result != null && result.equalsIgnoreCase("success")) {
                new AsyncPostDetailList().execute("");
                Toast.makeText(PostDetailInfoActivity.this, " 댓글 정상 입력.", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(PostDetailInfoActivity.this, "댓글 입력 실패.", Toast.LENGTH_SHORT).show();
            }
        }
    }
/*

    public class AsyncLikeRequest extends AsyncTask<LikeCountEntityObject, Void, String> {
        String postId;
        String targetURL = String.format(NetworkActivity.MOUNDARY_POST_LIKE_URL, userId);
        NetworkDialog netwrokDialog = new NetworkDialog(PostDetailInfoActivity.this);

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
            //resultInfo =HttpApiHelperHandler.RequestServer(entitiyObject);
            LikeCountEntityObject httpParams = entity[0];

            Response response = null;
            try {


                OkHttpClient toServer = new OkHttpClient().newBuilder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build();


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
            if (result.equalsIgnoreCase("delete success")) {
                new AsyncPostDetailList().execute("");
                Toast.makeText(GirlsApplication.getGirlsContext(), "댓글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
            } else {
                return;
            }
        }
    }
*/

    public class AsyncDeleteReply extends AsyncTask<PostDetailEntityObject, Void, String> {
        String postId;
        String targetURL =String.format(NetworkActivity.MOUNDARY_REPLY_DELETE_URL,userId);
        String replyId;
        NetworkDialog netwrokDialog = new NetworkDialog(PostDetailInfoActivity.this);

        public AsyncDeleteReply(String postId, String replyId) {
            this.postId = postId;
            this.replyId = replyId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netwrokDialog.show();
        }



        @Override
        protected String doInBackground(PostDetailEntityObject... entity) {

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
                requestBody.add("replyId", replyId); //리플 삭제 받아올 변수 필요함
                FormBody reqBody = requestBody.build();
                Log.e("확인", "댓글 삭제 연결 날라감");

                Request request = new Request.Builder()
                        .url(targetURL)
                        .delete(reqBody)
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
                Log.e("doInBac", "댓글 삭제 전송중 에러발생", e);
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
            if (result.equalsIgnoreCase("delete success")) {
                new AsyncPostDetailList().execute("");
                Toast.makeText(GirlsApplication.getGirlsContext(), "댓글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
            } else {
                return;
            }
        }

    }

    class DAdapter extends RecyclerView.Adapter<DAdapter.ViewHolder> {
        ArrayList<PostReplyEntityObject> postDetailEntityObjcects;
        ReplyDeleteEntityObject replyDeleteEntityObject;
        ReplyEditEntityObject replyEditEntityObject;
        PostDetailActivity girlActivity = new PostDetailActivity();
        Context context;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_detail_reple, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        public DAdapter(ArrayList<PostReplyEntityObject> myDataset, Context context) {

            postDetailEntityObjcects = myDataset;
            this.context = context;
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final PostReplyEntityObject postDetailRepleEntityObject = postDetailEntityObjcects.get(position);
            final int[] likeCheckInt = new int[1];


            if (!postDetailEntityObjcects.get(position).reply_userId.equals(userId)) {
                holder.replyDeleteBtn.setVisibility(View.GONE);
                holder.replyLikeBtn.setVisibility(View.VISIBLE);
            }
            Glide.with(GirlsApplication.getGirlsContext()).load(postDetailRepleEntityObject.reply_profileThumbnail).override(510,300).centerCrop().into(holder.mImageView);

            holder.mTextView2.setText(postDetailRepleEntityObject.reply_Content);
            holder.mTextView.setText(postDetailRepleEntityObject.reply_nickname);

            holder.replyDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    new AsyncDeleteReply(postId, postDetailRepleEntityObject.reply_id).execute();  //수정필요
                    new AsyncPostDetailList().execute("");

                }
            });


            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), FriendPageActivity.class);
                    intent.putExtra("oppositeUserId",postDetailRepleEntityObject.reply_userId);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return postDetailEntityObjcects.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView replyDeleteBtn;
            public ImageView replyLikeBtn;
            public ImageView mImageView;
            public TextView mTextView;
            public TextView mTextView2;
            public TextView replyLikeCount;

            public ViewHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.image);
                mTextView = (TextView) view.findViewById(R.id.reply_user);
                mTextView2 = (TextView) view.findViewById(R.id.reply_content);
                replyDeleteBtn = (ImageView) view.findViewById(R.id.reply_delete);
                replyLikeBtn = (ImageView) view.findViewById(R.id.reply_like_image);
                replyLikeCount = (TextView) view.findViewById(R.id.reply_like_text);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncPostDetailList().execute("");
    }
}


