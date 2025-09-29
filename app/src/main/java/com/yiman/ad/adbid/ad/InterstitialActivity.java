package com.yiman.ad.adbid.ad;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidError;
import com.adbid.media.AdbidListener;
import com.adbid.media.ad.AdbidInterstitial;
import com.yiman.ad.adbid.AdConfig;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.view.TitleBar;

public class InterstitialActivity extends ComponentActivity {
    @Nullable AdbidInterstitial interstitialAd;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_layout);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.app_inter_name);
        titleBar.setListener(view -> finish());

        //创建插屏广告
        interstitialAd = new AdbidInterstitial(AdConfig.getAdConfig().getInterUnitId());
        interstitialAd.setAdListener(new AdbidListener() {
            @Override public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
                //获取广告价格，单位分
                double price = adInfo.getPrice();
                log( "插屏广告加载完成，ecpm: " + price);

                //展示广告
                if (interstitialAd != null) {
                    interstitialAd.showAd();
                }
            }

            @Override
            public void onAdLoadFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                log( "插屏广告加载失败：" + error.getMessage());
            }

            @Override public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                log( "插屏广告展示成功");
            }

            @Override public void onAdDisplayedFailed(@Nullable AdbidAdInfo adInfo,
                                                      @NonNull AdbidError error) {
                log( "插屏广告展示失败，error: " + error.getMessage());
            }

            @Override public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                log( "插屏广告关闭");
            }

            @Override public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                log( "插屏广告触发点击");
            }
        });

        //加载广告
        interstitialAd.loadAd();
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        //销毁广告
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
    }

    StringBuilder stringBuilder = new StringBuilder("广告日志：\n");

    private void log(String msg) {
        stringBuilder.append(msg + ";\n");
        TextView textView = findViewById(R.id.text_layout);
        textView.setText(stringBuilder.toString());
    }
}