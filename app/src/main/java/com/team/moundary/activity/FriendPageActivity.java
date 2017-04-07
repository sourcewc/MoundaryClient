package com.team.moundary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.moundary.R;
import com.team.moundary.fragments.FriendPageGridView;
import com.team.moundary.fragments.FriendPageListView;
import com.team.moundary.network.FriendManagerEntityObject;
import com.team.moundary.network.FriendManagerNetwork;
import com.team.moundary.network.FriendPageEntityObject;
import com.team.moundary.network.FriendPageHandler;

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
public class FriendPageActivity extends FontActivity {
    ImageView criclephoto;
    ImageView backBtnImage;
    ImageView backgroundimage;
    TextView friendname;
    TextView friendaddress;
    TextView invisibleParser;
    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId

    FriendPageEntityObject myFriendPageEntityObject;
    FriendManagerEntityObject friendManagerEntityObject;
    String oppnentId;
    String isFriendCheck;
    Button button1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_page);

        backgroundimage = (ImageView) findViewById(R.id.coverimg);
        criclephoto = (ImageView) findViewById(R.id.friend_photo_imageview);
        friendname = (TextView) findViewById(R.id.friend_nickname);
        friendaddress = (TextView) findViewById(R.id.friend_location);
        invisibleParser = (TextView) findViewById(R.id.invisible_parser);
        button1 = (Button) findViewById(R.id.friend_requestbtn);

        Intent intent = getIntent();
        oppnentId = intent.getStringExtra("oppositeUserId");


        backBtnImage = (ImageView) findViewById(R.id.back_img_btn2);
        CollapsingToolbarLayout toolbar = (CollapsingToolbarLayout) findViewById(R.id.info_toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new FriendViewFragmentAdapter(getSupportFragmentManager()));


        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.friend_list_page);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.showcatalog);
        tabLayout.getTabAt(1).setIcon(R.drawable.navi);




        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncBloodJSONList().execute("");



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFriendCheck = invisibleParser.getText().toString();
                switch (isFriendCheck) {
                    case "-1":
                        new AsyncFriendRequest(oppnentId).execute();
                        new AsyncBloodJSONList().execute();
                        break;
                    case "0":
                        new AsyncFriendCancel(oppnentId).execute();
                        new AsyncBloodJSONList().execute();
                }

            }
        });


    }

    private void setupViewPager(ViewPager viewPager) {

        FriendPageGridView infoFragment = new FriendPageGridView().newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("oppositeUserId", oppnentId);
        infoFragment.setArguments(bundle);
        Log.e("넘겨지는값", String.valueOf(bundle));

        FriendPageListView infolistFragment = new FriendPageListView().newInstance();
        Bundle listBundle = new Bundle();
        bundle.putString("oppositeUserId", oppnentId);
        infoFragment.setArguments(bundle);
        Log.e("넘겨지는값", String.valueOf(bundle));

        FriendViewFragmentAdapter friendAdapter = new FriendViewFragmentAdapter(getSupportFragmentManager());
        friendAdapter.appendFragment(new FriendPageGridView().newInstance(), "");
        friendAdapter.appendFragment(new FriendPageListView().newInstance(), "");

        viewPager.setAdapter(friendAdapter);
    }

    public class FriendViewFragmentAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private Context context;
        private ArrayList<Fragment> friendFragment = new ArrayList<>();
        private ArrayList<String> tabTitle = new ArrayList<>();

        public void appendFragment(Fragment fragment, String title) {
            friendFragment.add(fragment);
            tabTitle.add(title);

        }

        public FriendViewFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return friendFragment.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitle.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friendpage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class AsyncBloodJSONList extends AsyncTask<String, Integer, FriendPageEntityObject> {
        NetworkDialog netwrokDialog = new NetworkDialog(FriendPageActivity.this);
        String targetURL = String.format(NetworkActivity.FRIEND_PAGE_NETWORK, oppnentId, userId);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netwrokDialog.show();
        }
        @Override
        protected FriendPageEntityObject doInBackground(
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
                    return FriendPageHandler.getJSONBloodRequestAllList(new StringBuilder(responseBody.string()));
//                    Log.e("서버데이터",responseBody.string());
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
        protected void onPostExecute(FriendPageEntityObject result) {
            netwrokDialog.dismiss();
            if (result != null) {
                Glide.with(FriendPageActivity.this).load(result.profileImg).override(510, 300).fitCenter().into(criclephoto);
                Glide.with(FriendPageActivity.this).load(result.coverImg).override(510, 300).fitCenter().into(backgroundimage);
                friendname.setText(result.nickname);
                friendaddress.setText(result.userAddress);

                /*if (result.isFriend == "-1") {
                    isFriendCheck = result.isFriend;
                } else if (result.isFriend == "0") {
                    isFriendCheck = result.isFriend;
                } else {
                    isFriendCheck = result.isFriend;
                }*/
                final int image[] = {
                        R.drawable.friendsrequest,
                        R.drawable.friendsrefusal
                };
                Button button1 = (Button) findViewById(R.id.friend_requestbtn);

                switch (result.isFriend) {
                    case "-1":
                        button1.setBackgroundResource(image[0]);
                        invisibleParser.setText(result.isFriend);
                        break;
                    case "0":
                        button1.setBackgroundResource(image[1]);
                        invisibleParser.setText(result.isFriend);
                        break;
                    case "1":
                        button1.setVisibility(View.INVISIBLE);
                        invisibleParser.setText(result.isFriend);
                        break;
                    default:
                }
            }
        }
    }

    public class AsyncFriendRequest extends AsyncTask<FriendManagerEntityObject, Void, String> {
        String oppnentId;
        String targetURL = String.format(NetworkActivity.MOUNDARY_FRIEND_REQUEST_URL, userId);
        NetworkDialog netwrokDialog = new NetworkDialog(FriendPageActivity.this);

        public AsyncFriendRequest(String oppnentId) {
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
                Log.e("doInBac", "친구요청 전송중 에러발생", e);
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
        }
    }

    public class AsyncFriendCancel extends AsyncTask<FriendManagerEntityObject, Void, String> {
        String oppnentId;
        String targetURL = String.format(FriendManagerNetwork.MOUNDARY_FRIEND_CANCEL_URL, userId);
        NetworkDialog netwrokDialog = new NetworkDialog(FriendPageActivity.this);

        public AsyncFriendCancel(String oppnentId) {
            this.oppnentId = oppnentId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netwrokDialog.show();
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
            netwrokDialog.dismiss();
            /*if (result != null && result.equalsIgnoreCase("success")) {
                Toast.makeText(GirlsMemberDetailActivity.this, " 댓글 정상 입력.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GirlsMemberDetailActivity.this, "댓글 입력 실패.", Toast.LENGTH_SHORT).show();
            }*/
        }

    }


}