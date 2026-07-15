package com.yiman.ad;

import android.app.Application;

import com.yiman.ad.adbid.platform.PlatformManager;

public class MyApplication extends Application {
    public static Application myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        //此处是demo的业务，不是广告初始化的功能，和广告无关
        PlatformManager.init();

        //广告初始化参照：AdbidAdLoad.init
    }
}
