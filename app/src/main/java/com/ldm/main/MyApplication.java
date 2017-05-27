package com.ldm.main;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;

/**
 * Created by jason_syf on 2017/5/25.
 * Email: jason_sunyf@163.com
 */

public class MyApplication extends Application {
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Utils.init(this);
    }
}
