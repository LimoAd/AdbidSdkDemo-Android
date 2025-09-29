package com.yiman.ad.adbid.ad;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidBannerListener;
import com.adbid.media.AdbidError;
import com.adbid.media.ad.AdbidBannerView;
import com.yiman.ad.adbid.AdConfig;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.view.TitleBar;

public class BannerActivity extends ComponentActivity {
    private @Nullable AdbidBannerView bannerView;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_layout);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.app_banner_name);
        titleBar.setListener(view -> finish());

        bannerView = new AdbidBannerView(BannerActivity.this);
        int width = getResources().getDisplayMetrics().widthPixels;//定一个宽度值，比如屏幕宽度
        int height = (int) (width / (320 / 50f));//按照比例转换高度的值
        //bannerView.setAdSize(width, height);
        bannerView.setUnitId(AdConfig.getAdConfig().getBannerUnitId());
        ViewGroup bannerContainer = findViewById(R.id.banner_container);
        bannerView.setBannerAdListener(new AdbidBannerListener() {
            @Override public void onBannerLoad(@NonNull AdbidAdInfo adInfo) {

                //获取广告价格，单位分
                double price = adInfo.getPrice();
                log("横幅广告加载成功，ecpm: " + price);

                //展示广告
                if (bannerContainer != null) {
                    bannerContainer.removeAllViews();
                    bannerContainer.addView(bannerView);
                }
            }

            @Override
            public void onBannerFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                log("横幅广告加载失败：" + error.getMessage());
            }

            @Override public void onBannerShow(@NonNull AdbidAdInfo adInfo) {
                log("插屏广告加展示");

            }

            @Override public void onBannerClose(@NonNull AdbidAdInfo adInfo) {
                log("插屏广告消失");
                //销毁广告, 调用销毁后需要重新新建
                if (bannerView != null) {
                    bannerView.destroy();
                }
            }

            @Override public void onBannerClicked(@NonNull AdbidAdInfo adInfo) {
                log("插屏广告触发点击");
            }
        });

        //加载广告
        bannerView.loadAd();
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        //销毁广告, 调用销毁后需要重新新建
        if (bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }
    }

    StringBuilder stringBuilder = new StringBuilder("广告日志：\n");

    private void log(String msg) {
        stringBuilder.append(msg + ";\n");
        TextView textView = findViewById(R.id.text_layout);
        textView.setText(stringBuilder.toString());
    }
}
