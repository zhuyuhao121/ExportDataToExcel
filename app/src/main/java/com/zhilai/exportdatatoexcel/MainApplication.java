package com.zhilai.exportdatatoexcel;

import android.content.Context;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

public class MainApplication extends LitePalApplication {

    private static final String TAG = "MainApplication";
    public static MainApplication mMainApplication;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mMainApplication = this;
        mContext = this.getApplicationContext();
    }

    public static MainApplication getInstance() {
        return mMainApplication;
    }

    public static Context getContext() {
        return mContext;
    }

}
