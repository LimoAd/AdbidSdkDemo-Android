package com.yiman.ad;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.yiman.ad.adbid.AdbidAdLoad;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.view.TitleBar;
import com.yiman.ad.log.MainLogConsole;
import com.yiman.ad.log.ToastHub;

public class MainActivity extends BaseActivity {

    private AdbidAdLoad adLoad;
    private MainPanelController panelController;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adLoad = new AdbidAdLoad(this);
        TitleBar titleBar = findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setTitle(R.string.app_name);
            titleBar.setListener(view -> finish());
        }

        panelController = new MainPanelController(this);
        panelController.bind();

        initAdActions();
    }


    private void initAdActions() {
        findViewById(R.id.btn_native_load).setOnClickListener(view -> {
            MainLogConsole.info("信息流广告开始加载单条自渲染...");
            adLoad.loadNative();
        });
        findViewById(R.id.btn_native_load_list).setOnClickListener(view -> {
            MainLogConsole.info("信息流广告开始加载列表自渲染...");
            adLoad.loadRecycleNative();
        });

        findViewById(R.id.btn_banner_load).setOnClickListener(view -> {
            MainLogConsole.info("横幅广告开始加载并展示...");
            adLoad.showBanner(findViewById(R.id.frame_ad_banner));
        });

        findViewById(R.id.btn_reward_Load).setOnClickListener(view -> {
            MainLogConsole.info("激励广告开始加载...");
            adLoad.loadReward();
        });
        findViewById(R.id.btn_reward_ready).setOnClickListener(
                view -> showReadyToast("激励广告", adLoad.isRewardReady()));
        findViewById(R.id.btn_reward_show).setOnClickListener(view -> {
            MainLogConsole.info("激励广告开始展示...");
            adLoad.showReward();
        });

        findViewById(R.id.btn_inter_load).setOnClickListener(view -> {
            MainLogConsole.info("插屏广告开始加载...");
            adLoad.loadInterstitial();
        });
        findViewById(R.id.btn_inter_ready).setOnClickListener(
                view -> showReadyToast("插屏广告", adLoad.isInterstitialReady()));
        findViewById(R.id.btn_inter_show).setOnClickListener(view -> {
            MainLogConsole.info("插屏广告开始展示...");
            adLoad.showInterstitial();
        });

        findViewById(R.id.btn_app_open_load).setOnClickListener(view -> {
            MainLogConsole.info("开屏广告开始加载...");
            adLoad.loadSplash();
        });
        findViewById(R.id.btn_app_open_ready).setOnClickListener(
                view -> showReadyToast("开屏广告", adLoad.isSplashReady()));
        findViewById(R.id.btn_app_open_show).setOnClickListener(view -> {
            MainLogConsole.info("开屏广告开始展示...");
            adLoad.showSplash(findViewById(R.id.frame_ad));
        });
    }

    private void showReadyToast(String adName, boolean ready) {
        if (ready) {
            MainLogConsole.success(adName + " isReady: true");
            ToastHub.show(this, adName + " 就绪验证 true");
        } else {
            MainLogConsole.warning(adName + " isReady: false");
            ToastHub.show(this, adName + " 就绪验证 false");
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (panelController != null) {
            panelController.unbind();
        }
        if (adLoad != null) {
            adLoad.destroy();
        }
    }
}
