package com.luojiawei.chinesechess.chinesechess4android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Setting extends Activity {
    final String TAG = "Setting";

    final static String LEVEL = "level";    //等级
    final static String IS_OFFENSIVE = "is_offensive";  //是否先手Key
    final static String IS_RED_COLOR = "is_red_color";  //是否红色Key
    final static String IS_BT_CLIENT = "is_bt_client";  //是否蓝牙客户端Key

    //等级
    final static int BLUETOOTH = 0;
    final static int LOW_RANK = 1;
    final static int MIDDLE_RANK = 2;
    final static int HIGH_RANK = 3;

    boolean AI;     //是否人机对弈
    int level;

    //View
    RadioButton mBtnRedColor;
    RadioButton mBtnOffensiveSize;
    RadioButton mBtnLowRank;
    RadioButton mBtnMiddleRank;
    RadioButton mBtnClient;
    Button mBtnOk;
    Button mBtnCancel;
    TextView mTxtLevel;
    TextView mTxtBt;
    RadioGroup mRgLevel;
    RadioGroup mRgBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Intent intent = getIntent();
        level = intent.getIntExtra("level", LOW_RANK);
        AI = intent.getBooleanExtra("AI", true);
        LogUtil.d(TAG, level + " " + AI);

        mBtnRedColor = (RadioButton)findViewById(R.id.red_color);
        mBtnRedColor.setChecked(true);
        mBtnOffensiveSize = (RadioButton)findViewById(R.id.offensive_side);
        mBtnOffensiveSize.setChecked(true);
        if(AI) {
            mTxtLevel = (TextView)findViewById(R.id.txt_level);
            mTxtLevel.setVisibility(View.VISIBLE);
            mRgLevel = (RadioGroup)findViewById(R.id.rg_level);
            mRgLevel.setVisibility(View.VISIBLE);
            mBtnLowRank = (RadioButton) findViewById(R.id.low_rank);
            mBtnMiddleRank = (RadioButton) findViewById(R.id.middle_rank);
            switch (level){
                case LOW_RANK:
                    mBtnLowRank.setChecked(true);break;
                case MIDDLE_RANK:
                    mBtnMiddleRank.setChecked(true);break;
                case HIGH_RANK:
                    ((RadioButton)findViewById(R.id.high_rank)).setChecked(true);break;
            }
        }else{
            mTxtBt = (TextView)findViewById(R.id.txt_bt);
            mTxtBt.setVisibility(View.VISIBLE);
            mRgBt = (RadioGroup)findViewById(R.id.rg_bt);
            mRgBt.setVisibility(View.VISIBLE);
            mBtnClient = (RadioButton) findViewById(R.id.bt_client);
            mBtnClient.setChecked(true);
        }
        mBtnOk = (Button)findViewById(R.id.btn_ok);
        mBtnCancel = (Button)findViewById(R.id.btn_cancel);

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //我方颜色
                if(mBtnRedColor.isChecked()){
                    intent.putExtra(IS_RED_COLOR, true);
                }else{
                    intent.putExtra(IS_RED_COLOR, false);
                }

                //我方先后手
                if(mBtnOffensiveSize.isChecked()){
                    intent.putExtra(IS_OFFENSIVE, true);
                }else{
                    intent.putExtra(IS_OFFENSIVE, false);
                }

                if(AI){
                    //电脑等级
                    if(mBtnLowRank.isChecked()){
                        intent.putExtra(LEVEL,LOW_RANK);
                    }else if(mBtnMiddleRank.isChecked()){
                        intent.putExtra(LEVEL,MIDDLE_RANK);
                    }else{
                        intent.putExtra(LEVEL,HIGH_RANK);
                    }
                }else{
                    //蓝牙
                    intent.putExtra(LEVEL, BLUETOOTH);
                    if(mBtnClient.isChecked()){
                        intent.putExtra(IS_BT_CLIENT, true);
                    }else{
                        intent.putExtra(IS_BT_CLIENT, false);
                    }
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
