package com.team.moundary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.team.moundary.R;

/**
 * Created by ccei on 2016-07-28.
 */
public class SuggestMenuActivity extends FontActivity {
        ImageView backBtnImage;
                protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.suggest);

                final EditText suggest_title = (EditText)findViewById(R.id.suggest_title);
                final EditText suggest_content = (EditText)findViewById(R.id.suggest_content);
                final ImageView sugesst_text_upload = (ImageView)findViewById(R.id.sugesst_text_upload);

                sugesst_text_upload.setOnClickListener(new Button.OnClickListener(){


                        @Override

                        public void onClick(View arg0) {

                                // TODO Auto-generated method stub

                                String emailSubject = suggest_title.getText().toString();
                                String emailText = suggest_content.getText().toString();

                                Intent intent = new Intent(Intent.ACTION_SEND);

                                String[] strEmailTo  = {"dmsvhfl29@naver.com"};

                                intent.setType("plain/text");
                                intent.putExtra(Intent.EXTRA_EMAIL, strEmailTo);
                                intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);

                                intent.putExtra(Intent.EXTRA_TEXT, emailText);

                                startActivity(Intent.createChooser(intent, " mail 클라이언트를 선택하세요. "));
                        }});

                backBtnImage =(ImageView)findViewById(R.id.back_img_btn);
                Toolbar toolbar = (Toolbar)findViewById(R.id.setup_toolbar);

                backBtnImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                onBackPressed();
                }
                });
                }
                @Override
                public void onBackPressed() {
                        super.onBackPressed();
                        }
        }
