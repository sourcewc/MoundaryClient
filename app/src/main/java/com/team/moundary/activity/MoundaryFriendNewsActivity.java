package com.team.moundary.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.team.moundary.R;
import com.team.moundary.fragments.FriendNewFragment;
import com.team.moundary.fragments.MyStoryFragment;
import com.team.moundary.fragments.RecommendInfoFragment;
import com.team.moundary.network.AlarmEntityObject;
import com.team.moundary.network.AlarmHandler;
import com.team.moundary.network.MyInfoPageEntityObject;
import com.team.moundary.network.MyInfoPageHandler;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class MoundaryFriendNewsActivity extends FontActivity {

    private DrawerLayout mDrawerLayout;
    private Fragment recommendInfoFragment;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    public static boolean firstConnect = false;
    public ImageView profileimg;
    public ImageView coverimg;
    public TextView name;
    public ImageView pushImage;
    public ImageView redAlarm;


    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moundary_friend_news);


        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();
        pushImage=(ImageView) findViewById(R.id.push);
        pushImage.setOnClickListener(mClickListener);

        redAlarm=(ImageView)findViewById(R.id.red_alarm);

        profileimg=(ImageView) findViewById(R.id.navi_header_image);
        coverimg=(ImageView)findViewById(R.id.navi_header_cover);
        name=(TextView)findViewById(R.id.navi_header_name);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.navi);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();

        if (navigationView != null){
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            mDrawerLayout.closeDrawers();

                            if (menuItem.getItemId() == R.id.friendmenu) {

                                Intent i = new Intent(MoundaryFriendNewsActivity.this, FriendMenuActivity.class);
                                startActivity(i);
                            }
                            /*if (menuItem.getItemId() == R.id.alrammenu) {

                                Intent i = new Intent(MoundaryFriendNewsActivity.this, AlramMenuActivity.class);
                                startActivity(i);
                            }*/

                            if (menuItem.getItemId() == R.id.noticemenu) {

                                Intent i = new Intent(MoundaryFriendNewsActivity.this, NoticeMenuActivity.class);
                                startActivity(i);
                            }
                            if (menuItem.getItemId() == R.id.qamenu) {

                                Intent i = new Intent(MoundaryFriendNewsActivity.this, SuggestMenuActivity.class);
                                startActivity(i);
                            }
                            if (menuItem.getItemId() == R.id.profilemenu) {

                                Intent i = new Intent(MoundaryFriendNewsActivity.this, MyInfoPageActivity.class);
                                startActivity(i);
                            }
                            if (menuItem.getItemId() == R.id.findmenu) {

                                Intent i = new Intent(MoundaryFriendNewsActivity.this, FindFriendFragment.class);
                                startActivity(i);
                            }

                            return true;
                        }
                    });
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupGirlsViewPager(viewPager);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(0xFFcc9999, 0xFF330000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncBloodJSONList().execute("");
        new AsyncAlarmCheckJSONList().execute("");
    }
    private void setupGirlsViewPager(ViewPager viewPager) {

        GirlGroupPagerAdapter girlsAdapter = new GirlGroupPagerAdapter(getSupportFragmentManager());
        girlsAdapter.appendFragment(new FriendNewFragment().newInstance(1), "친구 소식");
        girlsAdapter.appendFragment(new MyStoryFragment().newInstance(1 ,this), "내 이야기");
        girlsAdapter.appendFragment(new RecommendInfoFragment(), "동네소식");
        viewPager.setAdapter(girlsAdapter);
    }
/*    @Override
    protected void onResume() {
        super.onResume();
        Log.e("test", "onResume()");
    }*/

    private static class GirlGroupPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> girsFragment = new ArrayList<>();

        private final ArrayList<String> tabTitles = new ArrayList<>();

        public GirlGroupPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void appendFragment(Fragment fragment, String title) {
            girsFragment.add(fragment);
            tabTitles.add(title);

        }

        @Override
        public Fragment getItem(int position) {
            return girsFragment.get(position);
        }

        @Override
        public int getCount() {
            return girsFragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        long intervalTime = currentTime - backPressedTime;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                super.onBackPressed();
            } else {
                backPressedTime = currentTime;
                Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    ImageView.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MoundaryFriendNewsActivity.this, PushActivity.class);
            startActivity(i);
        }
    };
    class AsyncBloodJSONList extends AsyncTask<String, Integer, MyInfoPageEntityObject> {
        NetworkDialog netwrokDialog = new NetworkDialog(MoundaryFriendNewsActivity.this);
        String targetURL = String.format(NetworkActivity.MY_PAGE_NETWORK,userId,userId);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netwrokDialog.show();
        }


        @Override
        protected MyInfoPageEntityObject doInBackground(
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
                    return MyInfoPageHandler.getJSONBloodRequestAllList(new StringBuilder(responseBody.string()));
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
        protected void onPostExecute(MyInfoPageEntityObject result) {
            netwrokDialog.dismiss();
            if (result != null) {
                Glide.with(MoundaryFriendNewsActivity.this).load(result.profileImg).override(510, 510).centerCrop().into(profileimg);
                Glide.with(MoundaryFriendNewsActivity.this).load(result.coverImg).override(510, 510).centerCrop().into(coverimg);

                name.setText(result.nickname);
            }
        }
    }

    class AsyncAlarmCheckJSONList extends AsyncTask<String, Integer, AlarmEntityObject> {
        NetworkDialog netwrokDialog = new NetworkDialog(MoundaryFriendNewsActivity.this);
        String targetURL = String.format(NetworkActivity.MOUNDARY_ALARM_CKECK,userId);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netwrokDialog.show();
        }


        @Override
        protected AlarmEntityObject doInBackground(
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
                    return AlarmHandler.getJSONBloodRequestAllList(new StringBuilder(responseBody.string()));
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
        protected void onPostExecute(AlarmEntityObject result) {
            netwrokDialog.dismiss();
            if (result != null) {
//                Log.e("알람체크",result.alarmBoolean);
                if(result.alarmBoolean !="0") {
                    redAlarm.setVisibility(View.VISIBLE);
                } else {
                    redAlarm.setVisibility(View.GONE);
                }
            }
        }
    }


}
