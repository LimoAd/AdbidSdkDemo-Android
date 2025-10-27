package com.yiman.ad.adbid;

import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.adbid.adx.util.immersionbar.ImmersionBar;


public class BaseActivity extends ComponentActivity {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this).fullScreen(true).init();
        super.onCreate(savedInstanceState);
    }
}
