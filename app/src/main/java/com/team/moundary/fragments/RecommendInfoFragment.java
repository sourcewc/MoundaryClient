package com.team.moundary.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.team.moundary.R;
import com.team.moundary.activity.MoundaryFriendNewsActivity;
import com.team.moundary.activity.NetworkActivity;
import com.team.moundary.activity.NetworkDialog;
import com.team.moundary.activity.PropertyManager;
import com.team.moundary.network.RecommendHandler;
import com.team.moundary.network.RecommendInforFragmentWrite;
import com.team.moundary.activity.SearchPostActivity;
import com.team.moundary.activity.ShopActivity;
import com.team.moundary.network.ShopActivityEntityObject;

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
 * Created by Shin on 2016-07-27.
 */
public class RecommendInfoFragment extends Fragment {

    static MoundaryFriendNewsActivity owner;
    String myAddress;
    private static final int MAP_ADDRESS = 3;

    TextView map, infoCountText;
    PropertyManager propertyManager = PropertyManager.getInstance();
    final String userId = propertyManager.getUserId(); //userId
    String address;
    String tokenArea;
    String area1 = "";
    String area2 = "";
    String area3 = "";
    String area4 = "";
    String area5 = "";

    public RecommendInfoFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recommend_info, null);

        ImageView disscount = (ImageView) rootView.findViewById(R.id.disscount_info_list_btn);
        ImageView nanum = (ImageView) rootView.findViewById(R.id.nanum_info_list_btn);
        ImageView envet = (ImageView) rootView.findViewById(R.id.event_info_list_btn);
        ImageView shop = (ImageView) rootView.findViewById(R.id.shop_info_list_btn);
        TextView write = (TextView) rootView.findViewById(R.id.jeong);
        ImageView search = (ImageView) rootView.findViewById(R.id.search);

        map = (TextView) rootView.findViewById(R.id.mylocation_text);
        infoCountText = (TextView) rootView.findViewById(R.id.infocount_text);

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecommendInforFragmentWrite.class);
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });

        disscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                intent.putExtra("category", 1);
                startActivity(intent);
            }
        });

        nanum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                intent.putExtra("category", 2);
                startActivity(intent);
            }
        });

        envet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                intent.putExtra("category", 3);
                startActivity(intent);
            }
        });

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                intent.putExtra("category", 4);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchPostActivity.class);
                startActivity(intent);
            }
        });


        map.setText(myAddress);

        owner = (MoundaryFriendNewsActivity) getActivity();


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new AsyncRecoJSONList().execute("");
    }


    class AsyncRecoJSONList extends AsyncTask<String, Integer, ArrayList<ShopActivityEntityObject>> {
        NetworkDialog netwrokDialog = new NetworkDialog(owner);

        @Override
        protected ArrayList<ShopActivityEntityObject> doInBackground(
                String... params) {

            Response response = null;
            try {
                String targetURL = String.format(NetworkActivity.RECOMMEND_NETWORK, userId);
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
                    return RecommendHandler.getJSONBloodRequestAllList(
                            new StringBuilder(responseBody.string()));
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
        protected void onPostExecute(ArrayList<ShopActivityEntityObject>
                                             result) {
            netwrokDialog.dismiss();

            if (result != null) {
                if (result.get(0).postCount != null) {
                    infoCountText.setText("주변에 " + result.get(0).postCount + "개의 소식이 있습니다.");
                    Log.e("소식", result.get(0).postCount);
                } else {
                    result.get(0).postCount = "0";
                    infoCountText.setText("주변에  0 개의 소식이 있습니다");

                }

                if (result.get(1).postAddress != null) {
                    map.setText(result.get(1).postAddress);
                } else {
                    Toast.makeText(owner, "아직 친구가 없습니다 친구를 만들어보세요", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }
}
