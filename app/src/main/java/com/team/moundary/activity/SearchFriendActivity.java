package com.team.moundary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.team.moundary.R;

/**
 * Created by ccei on 2016-08-18.
 */
public class SearchFriendActivity extends FontActivity {

    ImageView backBtnImage;
    ImageView search_clear;
    EditText friend_search;
    String edit_save;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);


        friend_search=(EditText)findViewById(R.id.search_friend);
        search_clear=(ImageView)findViewById(R.id.search_clear);
        backBtnImage =(ImageView)findViewById(R.id.back_img_btn);
        Toolbar toolbar = (Toolbar)findViewById(R.id.setup_toolbar);

        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        search_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_save=friend_search.getText().toString();

                Intent intent=new Intent(getApplicationContext(),SearchFriendClearActivity.class);
                intent.putExtra("nickname",edit_save.toString());

                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
