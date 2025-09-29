package com.yiman.ad.adbid.ad;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidError;
import com.adbid.media.AdbidRewardListener;
import com.adbid.media.ad.AdbidRewarded;
import com.yiman.ad.adbid.AdConfig;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.view.TitleBar;

public class RewardActivity extends ComponentActivity {
    @Nullable AdbidRewarded rewardedAd;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_layout);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.app_reward_name);
        titleBar.setListener(view -> finish());

        //创建激励广告
        rewardedAd = new AdbidRewarded(AdConfig.getAdConfig().getRewardUnitId());
        rewardedAd.setAdListener(new AdbidRewardListener() {
            @Override public void onUserReward(@NonNull AdbidAdInfo adInfo) {
                log( "激励视频领取奖励");
            }

            @Override public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
                //获取广告价格，单位分
                double price = adInfo.getPrice();
                log( "激励视频广告加载完成, ecpm: "+price);

                //展示广告
                if (rewardedAd != null)
                    rewardedAd.showAd();
            }

            @Override
            public void onAdLoadFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                log( "激励视频广告加载失败：" + error.getMessage());
            }

            @Override public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                log( "激励视频广告展示成功");
            }

            @Override
            public void onAdDisplayedFailed(@Nullable AdbidAdInfo adInfo,@NonNull AdbidError error) {
                log( "激励视频广告展示失败: " + error.getMessage());
            }

            @Override public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                log( "激励视频广告关闭");
            }

            @Override public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                log( "激励视频广告触发点击");
            }
        });

        //加载广告
        rewardedAd.loadAd();
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        //销毁广告
        if (rewardedAd != null) {
            rewardedAd.destroy();
            rewardedAd = null;
        }
    }

    StringBuilder stringBuilder = new StringBuilder("广告日志：\n");

    private void log(String msg) {
        stringBuilder.append(msg + ";\n");
        TextView textView = findViewById(R.id.text_layout);
        textView.setText(stringBuilder.toString());
    }
}
