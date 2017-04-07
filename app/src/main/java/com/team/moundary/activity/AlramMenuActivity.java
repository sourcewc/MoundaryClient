package com.team.moundary.activity;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.team.moundary.R;

public class AlramMenuActivity extends FontActivity {

    boolean select = false;

    private ImageView backBtnImage;

    private TextView recevie_alram;

    private Switch parent_swtich,
                   sale_alarm_switch,
                   nanum_alarm_switch,
                   shop_alarm_switch,
                   event_alarm_switch;


    boolean  select3 = false,
             select4 = false,
             select5 = false,
             select6 = false;

    private RelativeLayout sixth_alarm_container,
                           seventh_alarm_container, eighth_alarm_container, nine_alarm_container;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alram);

        recevie_alram=(TextView)findViewById(R.id.recieve_alaram_text);

        sixth_alarm_container = (RelativeLayout)findViewById(R.id.sixth_alarm_container);
        seventh_alarm_container = (RelativeLayout)findViewById(R.id.seventh_alarm_container);
        eighth_alarm_container=(RelativeLayout)findViewById(R.id.eighth_alarm_container);
        nine_alarm_container=(RelativeLayout)findViewById(R.id.nine_alarm_container);

        parent_swtich=(Switch)findViewById(R.id.parent_receive_alarm_switch);

        sale_alarm_switch=(Switch)findViewById(R.id.sale_alarm_switch);
        nanum_alarm_switch=(Switch)findViewById(R.id.nanum_alarm_switch);
        shop_alarm_switch=(Switch)findViewById(R.id.shop_alarm_switch);
        event_alarm_switch=(Switch)findViewById(R.id.event_alarm_switch);

        backBtnImage =(ImageView)findViewById(R.id.back_img_btn);
        Toolbar toolbar = (Toolbar)findViewById(R.id.setup_toolbar);

        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        Log.e("click!", parent_swtich+"");
        Log.e("click!", PropertyManager.getInstance().getSetting3()+"");
        Log.e("click!", PropertyManager.getInstance().getSetting4()+"");
        Log.e("click!", PropertyManager.getInstance().getSetting5()+"");
        Log.e("click!", PropertyManager.getInstance().getSetting6()+"");

        Log.e("click!", ((PropertyManager.getInstance().getSetting3().compareTo("T")==0)?true:false)+"");
        Log.e("click!", ((PropertyManager.getInstance().getSetting4().compareTo("T")==0)?true:false)+"");
        Log.e("click!", ((PropertyManager.getInstance().getSetting5().compareTo("T")==0)?true:false)+"");
        Log.e("click!", ((PropertyManager.getInstance().getSetting6().compareTo("T")==0)?true:false)+"");

        boolean t3 = (PropertyManager.getInstance().getSetting3().compareTo("T")==0)?true:false;
        boolean t4 = (PropertyManager.getInstance().getSetting4().compareTo("T")==0)?true:false;
        boolean t5 = (PropertyManager.getInstance().getSetting5().compareTo("T")==0)?true:false;
        boolean t6 = (PropertyManager.getInstance().getSetting6().compareTo("T")==0)?true:false;

        sale_alarm_switch.setChecked(t3);
        nanum_alarm_switch.setChecked(t4);
        shop_alarm_switch.setChecked(t5);
        event_alarm_switch.setChecked(t6);

        if(t3||t4||t5||t6 ){
            parent_swtich.setChecked(true);
            sale_alarm_switch.setVisibility(View.VISIBLE);
            nanum_alarm_switch.setVisibility(View.VISIBLE);
            shop_alarm_switch.setVisibility(View.VISIBLE);
            event_alarm_switch.setVisibility(View.VISIBLE);
        }

        parent_swtich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select==false){
                    parent_swtich.setChecked(true);
                    sale_alarm_switch.setChecked(true);
                    nanum_alarm_switch.setChecked(true);
                    shop_alarm_switch.setChecked(true);
                    event_alarm_switch.setChecked(true);

                    select = true;

                    PropertyManager.getInstance().setSetting3("T");
                    PropertyManager.getInstance().setSetting4("T");
                    PropertyManager.getInstance().setSetting5("T");
                    PropertyManager.getInstance().setSetting6("T");


                    sixth_alarm_container.setVisibility(View.VISIBLE);
                    seventh_alarm_container.setVisibility(View.VISIBLE);
                    eighth_alarm_container.setVisibility(View.VISIBLE);
                    nine_alarm_container.setVisibility(View.VISIBLE);

                }else if(select==true){
                    parent_swtich.setChecked(false);
                    sale_alarm_switch.setChecked(false);
                    nanum_alarm_switch.setChecked(false);
                    shop_alarm_switch.setChecked(false);
                    event_alarm_switch.setChecked(false);

                    select=false;

                    PropertyManager.getInstance().setSetting3("F");
                    PropertyManager.getInstance().setSetting4("F");
                    PropertyManager.getInstance().setSetting5("F");
                    PropertyManager.getInstance().setSetting6("F");


                    sixth_alarm_container.setVisibility(View.GONE);
                    seventh_alarm_container.setVisibility(View.GONE);
                    eighth_alarm_container.setVisibility(View.GONE);
                    nine_alarm_container.setVisibility(View.GONE);
                }
            }
        });

        sale_alarm_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select3==false){
                    sale_alarm_switch.setChecked(true);
                    PropertyManager.getInstance().setSetting3("T");
                    select3=true;
                }else if (select3==true){
                    sale_alarm_switch.setChecked(false);
                    PropertyManager.getInstance().setSetting3("F");
                    select3=false;
                }
            }
        });

        nanum_alarm_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select4==false){
                    nanum_alarm_switch.setChecked(true);
                    PropertyManager.getInstance().setSetting4("T");
                    select4=true;
                }else if (select4==true){
                    nanum_alarm_switch.setChecked(false);
                    PropertyManager.getInstance().setSetting4("F");
                    select4=false;
                }
            }
        });
        shop_alarm_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select5==false){
                    shop_alarm_switch.setChecked(true);
                    PropertyManager.getInstance().setSetting5("T");
                    select5=true;
                }else if (select5==true){
                    shop_alarm_switch.setChecked(false);
                    PropertyManager.getInstance().setSetting5("F");
                    select5=false;
                }
            }
        });

        event_alarm_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select6==false){
                    event_alarm_switch.setChecked(true);
                    PropertyManager.getInstance().setSetting6("T");
                    select6=true;
                }else if (select6==true){
                    event_alarm_switch.setChecked(false);
                    PropertyManager.getInstance().setSetting6("F");
                    select6=false;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

