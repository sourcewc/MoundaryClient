package com.team.moundary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.team.moundary.R;

public class SearchPostActivity extends FontActivity {

    ImageView backBtnImage;
    ImageView search_clear;
    String edit_save;
    ArrayAdapter adapter;

    EditText friend_search;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_post);

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

                Intent intent=new Intent(getApplicationContext(),SearchPostClearActivity.class);
                intent.putExtra("word",edit_save.toString());

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
