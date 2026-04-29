package com.yiman.ad.adbid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adbid.media.AdBidLossInfo;
import com.adbid.media.AdBidPlatform;
import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidBannerListener;
import com.adbid.media.AdbidError;
import com.adbid.media.AdbidListener;
import com.adbid.media.AdbidRewardListener;
import com.adbid.media.ad.AdbidAppOpen;
import com.adbid.media.ad.AdbidBannerView;
import com.adbid.media.ad.AdbidInterstitial;
import com.adbid.media.ad.AdbidRewarded;
import com.adbid.utils.ActivityUtils;
import com.adbid.utils.ViewUtils;
import com.yiman.ad.IAdLoad;
import com.yiman.ad.adbid.ad.NativeAdActivity;
import com.yiman.ad.adbid.ad.NativeAdRecycleActivity;
import com.yiman.ad.log.MainLogConsole;
import com.yiman.ad.log.ToastHub;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdbidAdLoad extends IAdLoad {
    @Nullable AdbidAppOpen appOpenAd;
    @Nullable AdbidRewarded rewardedAd;
    @Nullable AdbidInterstitial interstitialAd;
    private int size = 0;
    private SoftReference<ViewGroup> adContainer;

    public AdbidAdLoad(Context context) {
        super(context);
    }

    @Override public void loadSplash() {
        AdbidListener appOpenAdListener = new AdbidListener() {
            @Override public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
                logSuccess("开屏广告加载成功，eCPM " + adInfo.getPrice());
                toast("开屏广告加载成功");
                if (appOpenAd != null) {
                    if (size % 2 > 0) appOpenAd.winNotice(1000);
                    else appOpenAd.lossNotice(
                            new AdBidLossInfo(AdBidPlatform.GDT, 5000, "this is test " + "ad"));
                    size++;
                }
            }

            @Override
            public void onAdLoadFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                logError("开屏广告加载失败: " + error.getMessage());
                toast("开屏广告加载失败");
            }

            @Override public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                logSuccess("开屏广告展示成功");
                toast("开屏广告展示成功");
            }

            @Override public void onAdDisplayedFailed(@NonNull AdbidAdInfo adInfo,
                                                      @NonNull AdbidError error) {
                logError("开屏广告展示失败: " + error.getMessage());
                toast("开屏广告展示失败");
            }

            @Override public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                if (adContainer.get() != null) adContainer.get().removeAllViews();
                logInfo("开屏广告关闭");
                toast("开屏广告关闭");
            }

            @Override public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                logInfo("开屏广告被点击");
                toast("开屏广告被点击");
            }
        };
        if (appOpenAd != null) {
            appOpenAd.destroy();
        }
        appOpenAd = new AdbidAppOpen(AdConfig.getAdConfig().getSplashUnitId());
        appOpenAd.setAdListener(appOpenAdListener);
        appOpenAd.loadAd();
    }

    @Override public boolean isSplashReady() {
        return appOpenAd != null && appOpenAd.isReady();
    }

    @Override public void showSplash(@NonNull ViewGroup viewGroup) {
        adContainer = new SoftReference<>(viewGroup);
        if (isSplashReady()) {
            appOpenAd.showAd(viewGroup);
        }
    }

    @Override public void loadInterstitial() {
        AdbidListener interListener = new AdbidListener() {
            @Override public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
                logSuccess("插屏广告加载成功，eCPM " + adInfo.getPrice());
                toast("插屏广告加载成功");
                if (interstitialAd != null) {
                    if (size % 2 > 0) interstitialAd.winNotice(1000);
                    else interstitialAd.lossNotice(
                            new AdBidLossInfo(AdBidPlatform.GDT, 5000, "this is test " + "ad"));
                    size++;
                }
            }

            @Override
            public void onAdLoadFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                logError("插屏广告加载失败: " + error.getMessage());
                toast("插屏广告加载失败");
            }

            @Override public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                logSuccess("插屏广告展示成功");
                toast("插屏广告展示成功");
            }

            @Override public void onAdDisplayedFailed(@NonNull AdbidAdInfo adInfo,
                                                      @NonNull AdbidError error) {
                logError("插屏广告展示失败: " + error.getMessage());
                toast("插屏广告展示失败");
            }

            @Override public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                logInfo("插屏广告关闭");
                toast("插屏广告关闭");
            }

            @Override public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                logInfo("插屏广告被点击");
                toast("插屏广告被点击");
            }
        };
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        interstitialAd = new AdbidInterstitial(AdConfig.getAdConfig().getInterUnitId());
        interstitialAd.setAdListener(interListener);
        interstitialAd.loadAd();
    }

    @Override public boolean isInterstitialReady() {
        return interstitialAd != null && interstitialAd.isReady();
    }

    @Override public void showInterstitial() {
        if (isInterstitialReady()) {
            interstitialAd.showAd();
        }
    }

    @Override public void loadReward() {
        AdbidRewardListener adbidRewardListener = new AdbidRewardListener() {
            @Override public void onUserReward(@NonNull AdbidAdInfo adInfo) {
                logSuccess("激励广告发放奖励");
                toast("激励广告发放奖励");

            }

            @Override public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
                logSuccess("激励广告加载成功，eCPM " + adInfo.getPrice());
                toast("激励广告加载成功");
                if (rewardedAd != null) {
                    if (size % 2 > 0) rewardedAd.winNotice(1000);
                    else rewardedAd.lossNotice(
                            new AdBidLossInfo(AdBidPlatform.GDT, 5000, "this is test " + "ad"));

                    size++;
                }
            }

            @Override
            public void onAdLoadFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                logError("激励广告加载失败: " + error.getMessage());
                toast("激励广告加载失败");
            }

            @Override public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                logSuccess("激励广告展示成功");
                toast("激励广告展示成功");
            }

            @Override public void onAdDisplayedFailed(@NonNull AdbidAdInfo adInfo,
                                                      @NonNull AdbidError error) {
                logError("激励广告展示失败: " + error.getMessage());
                toast("激励广告展示失败");
            }

            @Override public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                logInfo("激励广告关闭");
                toast("激励广告关闭");
            }

            @Override public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                logInfo("激励广告被点击");
                toast("激励广告被点击");
            }
        };
        rewardedAd = new AdbidRewarded(AdConfig.getAdConfig().getRewardUnitId());
        rewardedAd.setAdListener(adbidRewardListener);

        Map<String, Object> extra = new HashMap<>();
        extra.put("customId", "user_custom_id_12345");  // 用户自定义ID
        extra.put("testId", 189978878);
        extra.put("testUserName", "zhangSan");
        extra.put("testAdInfo", new ArrayList<>());
        rewardedAd.setLocalExtra(extra);
        rewardedAd.loadAd();
    }

    @Override public boolean isRewardReady() {
        return rewardedAd != null && rewardedAd.isReady();
    }

    @Override public void showReward() {
        if (rewardedAd != null) rewardedAd.showAd();
    }

    @Override public void showBanner(@NonNull ViewGroup viewGroup) {
        AdbidBannerView bannerView = new AdbidBannerView(context);
        bannerView.setUnitId(AdConfig.getAdConfig().getBannerUnitId());
        int width = context.getResources().getDisplayMetrics().widthPixels;//定一个宽度值，比如屏幕宽度
        int height = (int) (width / (320 / 50f));//按照比例转换高度的值
        bannerView.setAdSize(width, height);
        if (viewGroup instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height);
            layoutParams.gravity = Gravity.CENTER;
            bannerView.setLayoutParams(layoutParams);
        } else {
            bannerView.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        }
        bannerView.setBannerAdListener(new AdbidBannerListener() {
            @Override public void onBannerLoad(@NonNull AdbidAdInfo adInfo) {
                logSuccess("横幅广告加载成功");
                toast("横幅广告加载成功");
                if (size % 2 > 0) bannerView.winNotice(1000);
                else bannerView.lossNotice(
                        new AdBidLossInfo(AdBidPlatform.GDT, 5000, "this is " + "test " + "ad"));
                size++;
            }

            @Override
            public void onBannerFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                logError("横幅广告加载失败: " + error.getMessage());
                toast("横幅广告加载失败");
            }

            @Override public void onBannerShow(@NonNull AdbidAdInfo adInfo) {
                logSuccess("横幅广告展示成功，eCPM " + adInfo.getPrice());
                toast("横幅广告展示成功");
            }

            @Override public void onBannerClose(@NonNull AdbidAdInfo adInfo) {
                ViewUtils.removeFromParent(bannerView);
                logInfo("横幅广告关闭");
                toast("横幅广告关闭");
            }

            @Override public void onBannerClicked(@NonNull AdbidAdInfo adInfo) {
                logInfo("横幅广告被点击");
                toast("横幅广告被点击");
            }
        });
        viewGroup.removeAllViews();
        viewGroup.addView(bannerView);
        bannerView.loadAd();
    }

    @Override public void destroy() {
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
        }

        if (rewardedAd != null) {
            rewardedAd.destroy();
        }
    }

    @Override public void loadNative() {
        context.startActivity(new Intent(context, NativeAdActivity.class));
    }

    @Override public void loadRecycleNative() {
        context.startActivity(new Intent(context, NativeAdRecycleActivity.class));
    }

    private void logInfo(String msg) {
        MainLogConsole.info(msg);
        Log.i("AdbidSdk", msg);
    }

    private void logSuccess(String msg) {
        MainLogConsole.success(msg);
        Log.i("AdbidSdk", msg);
    }

    private void logError(String msg) {
        MainLogConsole.error(msg);
        Log.e("AdbidSdk", msg);
    }

    private void toast(String msg) {
        ToastHub.show(context, msg);
    }
}
