package com.yiman.ad.adbid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidBannerListener;
import com.adbid.media.AdbidError;
import com.adbid.media.AdbidListener;
import com.adbid.media.AdbidRewardListener;
import com.adbid.media.ad.AdbidAppOpen;
import com.adbid.media.ad.AdbidBannerView;
import com.adbid.media.ad.AdbidInterstitial;
import com.adbid.media.ad.AdbidRewarded;
import com.adbid.utils.ViewUtils;
import com.yiman.ad.adbid.ad.BannerActivity;
import com.yiman.ad.adbid.ad.InterstitialActivity;
import com.yiman.ad.adbid.ad.NativeAdActivity;
import com.yiman.ad.adbid.ad.NativeAdRecycleActivity;
import com.yiman.ad.adbid.ad.RewardActivity;
import com.yiman.ad.adbid.ad.SplashActivity;
import com.yiman.ad.adbid.view.TitleBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity {
    @Nullable AdbidAppOpen appOpenAd;
    @Nullable AdbidRewarded rewardedAd;
    @Nullable AdbidInterstitial interstitialAd;
    @Nullable private AdbidListener appOpenAdListener;
    @Nullable private AdbidRewardListener adbidRewardListener;
    @Nullable private AdbidListener interListener;


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.app_name);
        titleBar.setListener(view -> finish());
        initAd();
    }

    private void initAd() {
        initSplash();
        initReward();
        initInter();
        initBanner();

        findViewById(R.id.btn_native_load).setOnClickListener(
                view -> startActivity(new Intent(MainActivity.this, NativeAdActivity.class)));

        findViewById(R.id.btn_native_load_list).setOnClickListener(view -> startActivity(
                new Intent(MainActivity.this, NativeAdRecycleActivity.class)));

        findViewById(R.id.btn_app_open_jump).setOnClickListener(
                view -> startActivity(new Intent(MainActivity.this, SplashActivity.class)));

        findViewById(R.id.btn_reeard_jump).setOnClickListener(
                view -> startActivity(new Intent(MainActivity.this, RewardActivity.class)));

        findViewById(R.id.btn_inter_jump).setOnClickListener(
                view -> startActivity(new Intent(MainActivity.this, InterstitialActivity.class)));

        findViewById(R.id.btn_banner_jump).setOnClickListener(
                view -> startActivity(new Intent(MainActivity.this, BannerActivity.class)));
    }


    private void initBanner() {
        findViewById(R.id.btn_banner_load).setOnClickListener(view -> {
            AdbidBannerView bannerView = new AdbidBannerView(MainActivity.this);
            bannerView.setUnitId(AdConfig.getAdConfig().getBannerUnitId());
            ViewGroup viewGroup = findViewById(R.id.frame_ad_banner);
            int width = getResources().getDisplayMetrics().widthPixels;//定一个宽度值，比如屏幕宽度
            int height = (int) (width / (320 / 50f));//按照比例转换高度的值
            bannerView.setAdSize(width, height);
            bannerView.setBannerAdListener(new AdbidBannerListener() {
                @Override public void onBannerLoad(@NonNull AdbidAdInfo adInfo) {
                    logToast("onBannerLoad");
                }

                @Override
                public void onBannerFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                    logToast("onBannerFail: " + error.getMessage());
                }

                @Override public void onBannerShow(@NonNull AdbidAdInfo adInfo) {
                    logToast("onBannerShow");
                }

                @Override public void onBannerClose(@NonNull AdbidAdInfo adInfo) {
                    ViewUtils.removeFromParent(bannerView);
                    logToast("onBannerClose");
                }

                @Override public void onBannerClicked(@NonNull AdbidAdInfo adInfo) {
                    logToast("onBannerClicked");
                }
            });
            viewGroup.removeAllViews();
            viewGroup.addView(bannerView);

            bannerView.loadAd();
        });
    }

    private void initReward() {
        adbidRewardListener = new AdbidRewardListener() {
            @Override public void onUserReward(@NonNull AdbidAdInfo adInfo) {
                logToast("onUserReward");

            }

            @Override public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
                logToast("onAdLoadSuccess");
            }

            @Override
            public void onAdLoadFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                logToast("onAdLoadFail: " + error.getMessage());
            }

            @Override public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                logToast("onAdDisplayed");
            }

            @Override public void onAdDisplayedFailed(@NonNull AdbidAdInfo adInfo,
                                                      @NonNull AdbidError error) {
                logToast("onAdDisplayedFailed: " + error.getMessage());
            }

            @Override public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                logToast("onAdHidden");
            }

            @Override public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                logToast("onAdClicked");
            }
        };
        findViewById(R.id.btn_reward_Load).setOnClickListener(view -> {
            rewardedAd = new AdbidRewarded(AdConfig.getAdConfig().getRewardUnitId());
            rewardedAd.setAdListener(adbidRewardListener);
            Map<String,Object> extra=new HashMap<>();
            extra.put("testId",189978878);
            extra.put("testUserName","zhangSan");
            extra.put("testAdInfo",new ArrayList<>());
            rewardedAd.setLocalExtra(extra);
            rewardedAd.loadAd();
        });
        findViewById(R.id.btn_reward_show).setOnClickListener(view -> {
            if (rewardedAd != null) rewardedAd.showAd();
        });
    }

    private void initInter() {
        interListener = new AdbidListener() {
            @Override public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
                logToast("onAdLoadSuccess");
            }

            @Override
            public void onAdLoadFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                logToast("onAdLoadFail: " + error.getMessage());
            }

            @Override public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                logToast("onAdDisplayed");
            }

            @Override public void onAdDisplayedFailed(@NonNull AdbidAdInfo adInfo,
                                                      @NonNull AdbidError error) {
                logToast("onAdDisplayedFailed: " + error.getMessage());
            }

            @Override public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                FrameLayout frameLayout = findViewById(R.id.frame_ad);
                frameLayout.removeAllViews();
                logToast("onAdHidden");
            }

            @Override public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                logToast("onAdClicked");
            }
        };
        findViewById(R.id.btn_inter_load).setOnClickListener(view -> {
            if (interstitialAd != null) {
                interstitialAd.destroy();
            }
            interstitialAd = new AdbidInterstitial(AdConfig.getAdConfig().getInterUnitId());
            interstitialAd.setAdListener(interListener);
            interstitialAd.loadAd();
        });
        findViewById(R.id.btn_inter_show).setOnClickListener(view -> {
            if (interstitialAd != null) interstitialAd.showAd();
        });
    }

    private void initSplash() {
        appOpenAdListener = new AdbidListener() {
            @Override public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
                logToast("onAdLoadSuccess");
            }

            @Override
            public void onAdLoadFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                Toast.makeText(MainActivity.this, "load fail " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                logToast("onAdLoadFail: " + error.getMessage());
            }

            @Override public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                logToast("onAdDisplayed");
            }

            @Override public void onAdDisplayedFailed(@NonNull AdbidAdInfo adInfo,
                                                      @NonNull AdbidError error) {
                logToast("onAdDisplayedFailed: " + error.getMessage());
            }

            @Override public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                FrameLayout frameLayout = findViewById(R.id.frame_ad);
                frameLayout.removeAllViews();
                logToast("onAdHidden");
            }

            @Override public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                logToast("onAdClicked");
            }
        };
        findViewById(R.id.btn_app_open_load).setOnClickListener(view -> {
            if (appOpenAd != null) {
                appOpenAd.destroy();
            }
            appOpenAd = new AdbidAppOpen(AdConfig.getAdConfig().getSplashUnitId());
            appOpenAd.setAdListener(appOpenAdListener);
            appOpenAd.loadAd();
        });
        findViewById(R.id.btn_app_open_show).setOnClickListener(view -> {
            if (appOpenAd != null) appOpenAd.showAd(findViewById(R.id.frame_ad));
        });
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (appOpenAd != null) {
            appOpenAd.destroy();
            appOpenAd = null;
        }
        if (rewardedAd != null) {
            rewardedAd.destroy();
            rewardedAd = null;
        }

        if (interstitialAd != null) {
            interstitialAd.destroy();
            interListener = null;
        }

        if (rewardedAd != null) {
            rewardedAd.destroy();
        }

    }


    private void logToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        Log.i("AdbidSdk", msg);
    }
}
