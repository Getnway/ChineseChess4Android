package com.luojiawei.chinesechess.chinesechess4android;

import android.app.Application;
import android.content.Context;

/**
 * Created by L1 on 15-08-26.
 * �洢ȫ��Context
 */
public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate(){
//        LogUtil.i("MyApplication","onCreate");
        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
