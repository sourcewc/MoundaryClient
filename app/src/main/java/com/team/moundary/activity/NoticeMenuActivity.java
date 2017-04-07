package com.team.moundary.activity;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.team.moundary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shin on 2016-07-27.
 */
public class NoticeMenuActivity extends FontActivity {

    ImageView backBtnImage;
    private RecyclerView recyclerview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);
        backBtnImage =(ImageView)findViewById(R.id.back_img_btn);
        Toolbar toolbar = (Toolbar)findViewById(R.id.setup_toolbar);

        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<NoticeMenuActivity_Adapter.Item> data = new ArrayList<>();



        NoticeMenuActivity_Adapter.Item places = new NoticeMenuActivity_Adapter.Item(NoticeMenuActivity_Adapter.HEADER, "마운더리는 어떤 서비스 인가요?");
        places.invisibleChildren = new ArrayList<>();
        places.invisibleChildren.add(new NoticeMenuActivity_Adapter.Item(NoticeMenuActivity_Adapter.CHILD, "동네에 있는 육아맘들을 위해 친구를 찾아주고,내 생각과 감정을 표현하는 공간입니다. 한줄 일기로 당신의 하루를 기록하고 하나의 추억록을 만들어 보세요"));
        data.add(places);


        NoticeMenuActivity_Adapter.Item places2 = new NoticeMenuActivity_Adapter.Item(NoticeMenuActivity_Adapter.HEADER, "지켜야되는 규칙이 있나요?");
        places2.invisibleChildren = new ArrayList<>();
        places2.invisibleChildren.add(new NoticeMenuActivity_Adapter.Item(NoticeMenuActivity_Adapter.CHILD, "당신의 친구는 가까운 곳에 있습니다. 서로에게 예의를 지키고 신뢰할 만 한 이야기를 올려주세요."));
        data.add(places2);

        NoticeMenuActivity_Adapter.Item places3 = new NoticeMenuActivity_Adapter.Item(NoticeMenuActivity_Adapter.HEADER, "친구 소식에 대해서 이야기해주세요");
        places3.invisibleChildren = new ArrayList<>();
        places3.invisibleChildren.add(new NoticeMenuActivity_Adapter.Item(NoticeMenuActivity_Adapter.CHILD, "친구가 올린 소식은 폴라로이드 사진으로 올라옵니다. 댓글로 친구와 함께 즐거운 수다를 떨어보세요"));
        data.add(places3);

        NoticeMenuActivity_Adapter.Item places4 = new NoticeMenuActivity_Adapter.Item(NoticeMenuActivity_Adapter.HEADER, "동네 소식에 대해 설명해주세요");
        places4.invisibleChildren = new ArrayList<>();
        places4.invisibleChildren.add(new NoticeMenuActivity_Adapter.Item(NoticeMenuActivity_Adapter.CHILD, "당신의 동네친구가 올린 소식들을 찾아서 추천 해 줍니다. 동네에서 제공하는 육아용품 할인,동네 육아 용품 무료나눔,동네에서 일어나느 행사,동네 유용한 상점까지, 동네 친구가 추천해 준 유용한 소식을 실시간으로 받아보세요."));
        data.add(places4);

       NoticeMenuActivity_Adapter.Item places5 = new NoticeMenuActivity_Adapter.Item(NoticeMenuActivity_Adapter.HEADER, "친구의 글을 좀 더 보고싶어요");
        places5.invisibleChildren = new ArrayList<>();
        places5.invisibleChildren.add(new NoticeMenuActivity_Adapter.Item(NoticeMenuActivity_Adapter.CHILD, "친구사진을 누르면 친구 상세 페이지로 넘어갑니다.친구신천을 하면 자동으로 메인화면에서 알람 아이콘에 붉은 점이 뜹니다. 친구가 글을 올리거나 덧글을 달 때, 알람을 받으실 수 있습니다."));
        data.add(places5);


        recyclerview.setAdapter(new NoticeMenuActivity_Adapter(data));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
