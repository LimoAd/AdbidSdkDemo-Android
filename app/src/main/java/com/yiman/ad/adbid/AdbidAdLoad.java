package com.yiman.ad.adbid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adbid.adx.util.StringUtils;
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
import com.adbid.sdk.AdbidCustomController;
import com.adbid.sdk.AdbidInitConfig;
import com.adbid.sdk.AdbidLocation;
import com.adbid.sdk.AdbidSdk;
import com.adbid.sdk.AdbidSdkInitListener;
import com.adbid.utils.ViewUtils;
import com.yiman.ad.AppIdStore;
import com.yiman.ad.DemoRequestUtils;
import com.yiman.ad.IAdLoad;
import com.yiman.ad.MyApplication;
import com.yiman.ad.adbid.ad.NativeAdActivity;
import com.yiman.ad.adbid.ad.NativeAdDrawActivity;
import com.yiman.ad.adbid.ad.NativeAdRecycleActivity;
import com.yiman.ad.log.MainLogConsole;
import com.yiman.ad.log.ToastHub;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdbidAdLoad extends IAdLoad {
    @Nullable
    AdbidAppOpen appOpenAd;
    @Nullable
    AdbidRewarded rewardedAd;
    @Nullable
    AdbidInterstitial interstitialAd;
    private int size = 0;
    private SoftReference<ViewGroup> adContainer;
    private String token;

    public AdbidAdLoad(Context context, MainLogConsole logConsole) {
        super(context, logConsole);
    }

    @Override
    protected void init() {
        // Reserved for manual initialization logic.
        AdbidSdk.getInstance(MyApplication.myApplication).setDebugMode(true);
        //广告sdk初始化
        AdbidInitConfig config = AdbidInitConfig.builder(AppIdStore.getSelectedAppId())
                //设置App渠道
                .setAppChannel("xiaomi")
                //设置App版本
                .setAppVersion("1.0.0")
                //设置用户ID
                .setUserId("xxxxxx")
                //设置隐私权限
                .addCustomController(new AdbidCustomController() {
                    //是否允许SDK主动使用手机硬件参数（如IMEI）
                    @Override
                    public boolean isCanUsePhoneState() {
                        return true;
                    }

                    //是否允许SDK使用个性化广告（GDPR/CCPA合规需关闭）
                    @Override
                    public boolean isSupportPersonalized() {
                        return false;
                    }

                    //是否允许SDK主动使用地理位置信息
                    @Override
                    public boolean isCanUseLocation() {
                        return true;
                    }

                    //是否允许SDK主动获取OAID
                    @Override
                    public boolean isCanUseWifiState() {
                        return true;
                    }

                    //是否允许SDK主动获取OAID
                    @Override
                    public boolean isCanUseOaid() {
                        return true;
                    }

                    //开发者可传入OAID（当isCanUseOaid=false时生效）
                    @Nullable
                    @Override
                    public String getDevOaid() {
                        return "";
                    }

                    //是否允许SDK获取应用安装列表
                    @Override
                    public boolean isCanUseAppList() {
                        return true;
                    }

                    //开发者可传入应用安装列表（当isCanUseAppList=false时生效）
                    @Nullable
                    @Override
                    public List<PackageInfo> getAppList() {
                        return Collections.emptyList();
                    }

                    //是否允许SDK获取ANDROID_ID
                    @Override
                    public boolean isCanUseAndroidId() {
                        return true;
                    }

                    // 开发者可传入ANDROID_ID（当isCanUseAndroidId=false时生效）
                    @Nullable
                    @Override
                    public String getAndroidId() {
                        return "";
                    }

                    //是否允许SDK获取MAC地址
                    @Override
                    public boolean isCanUseMacAddress() {
                        return true;
                    }

                    //开发者可传入MAC地址（当isCanUseMacAddress=false时生效）
                    @Nullable
                    @Override
                    public String getMacAddress() {
                        return "";
                    }

                    //是否允许写入存储卡权限
                    @Override
                    public boolean isCanUseWriteExternal() {
                        return true;
                    }

                    // 是否允许加载摇一摇广告（需加速度传感器权限）
                    @Override
                    public boolean isCanUseShakeAd() {
                        return true;
                    }

                    //是否允许SDK使用录音权限
                    @Override
                    public boolean isCanUseRecordAudio() {
                        return true;
                    }

                    //开发者可传入IMEI（当isCanUsePhoneState=false时生效）
                    @Nullable
                    @Override
                    public String getDevImei() {
                        return "";
                    }

                    //开发者可传入IMEI列表（多卡设备）
                    @Nullable
                    @Override
                    public String[] getDevImeiList() {
                        return new String[0];
                    }

                    //开发者可传入定位信息
                    @Nullable
                    @Override
                    public AdbidLocation getLocation() {
                        return null;
                    }

                    //是否允许SDK主动获取IP地址
                    @Override
                    public boolean isCanUseIP() {
                        return true;
                    }

                    //开发者可传入IP地址（当isCanUseIP=false时生效）
                    @Nullable
                    @Override
                    public String getIP() {
                        return "";
                    }
                }).build();
        AdbidSdk.getInstance(MyApplication.myApplication).initialize(config, new AdbidSdkInitListener() {
            @Override
            public void onSdkInitCallback(boolean isSuccess, AdbidError adbidError) {
                if (isSuccess) {
                    logSuccess("初始化成功");
                    toast("初始化成功");
                } else {
                    logError("初始化失败");
                    toast("初始化失败");
                }

            }
        });
    }

    public void checkS2SBiddingToken(String adUnitId, Runnable callback) {
        if (!AdConfig.isS2SBiddingEnabled()) {
            callback.run();
            return;
        }
        DemoRequestUtils.requestBiddingToken(adUnitId, new DemoRequestUtils.RequestCallBack() {
            @Override
            public void onSuccess(String result) {
                if (!StringUtils.isEmpty(result)) {
                    token = result;
                }
                callback.run();
            }

            @Override
            public void onFailure() {
                token = null;
                callback.run();
            }
        });
    }

    @Override
    public void loadSplash() {
        checkS2SBiddingToken(AdConfig.getAdConfig().getSplashUnitId(), new Runnable() {
            @Override
            public void run() {
                AdbidListener appOpenAdListener = new AdbidListener() {
                    @Override
                    public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
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

                    @Override
                    public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                        logSuccess("开屏广告展示成功");
                        toast("开屏广告展示成功");
                    }

                    @Override
                    public void onAdDisplayedFailed(@NonNull AdbidAdInfo adInfo,
                                                    @NonNull AdbidError error) {
                        logError("开屏广告展示失败: " + error.getMessage());
                        toast("开屏广告展示失败");
                    }

                    @Override
                    public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                        if (adContainer.get() != null) adContainer.get().removeAllViews();
                        logInfo("开屏广告关闭");
                        toast("开屏广告关闭");
                    }

                    @Override
                    public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                        logInfo("开屏广告被点击");
                        toast("开屏广告被点击");
                    }
                };
                if (appOpenAd != null) {
                    appOpenAd.destroy();
                }
                if (StringUtils.isEmpty(token)) {
                    appOpenAd = new AdbidAppOpen(AdConfig.getAdConfig().getSplashUnitId());
                } else {
                    appOpenAd = new AdbidAppOpen(AdConfig.getAdConfig().getSplashUnitId(), token);
                }
                appOpenAd.setAdListener(appOpenAdListener);
                appOpenAd.loadAd();
            }
        });
    }


    @Override
    public boolean isSplashReady() {
        return appOpenAd != null && appOpenAd.isReady();
    }

    @Override
    public void showSplash(@NonNull ViewGroup viewGroup) {
        adContainer = new SoftReference<>(viewGroup);
        if (isSplashReady()) {
            appOpenAd.showAd(viewGroup);
        }
    }

    @Override
    public void loadInterstitial() {
        checkS2SBiddingToken(AdConfig.getAdConfig().getInterUnitId(), () -> {
            AdbidListener interListener = new AdbidListener() {
                @Override
                public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
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

                @Override
                public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                    logSuccess("插屏广告展示成功");
                    toast("插屏广告展示成功");
                }

                @Override
                public void onAdDisplayedFailed(@NonNull AdbidAdInfo adInfo,
                                                @NonNull AdbidError error) {
                    logError("插屏广告展示失败: " + error.getMessage());
                    toast("插屏广告展示失败");
                }

                @Override
                public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                    logInfo("插屏广告关闭");
                    toast("插屏广告关闭");
                }

                @Override
                public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                    logInfo("插屏广告被点击");
                    toast("插屏广告被点击");
                }
            };
            if (interstitialAd != null) {
                interstitialAd.destroy();
            }
            interstitialAd = new AdbidInterstitial(AdConfig.getAdConfig().getInterUnitId(), token);
            interstitialAd.setAdListener(interListener);
            interstitialAd.loadAd();
        });
    }

    @Override
    public boolean isInterstitialReady() {
        return interstitialAd != null && interstitialAd.isReady();
    }

    @Override
    public void showInterstitial() {
        if (isInterstitialReady()) {
            interstitialAd.showAd();
        }
    }

    @Override
    public void loadReward() {
        checkS2SBiddingToken(AdConfig.getAdConfig().getRewardUnitId(), () -> {
            AdbidRewardListener adbidRewardListener = new AdbidRewardListener() {
                @Override
                public void onUserReward(@NonNull AdbidAdInfo adInfo) {
                    logSuccess("激励广告发放奖励");
                    toast("激励广告发放奖励");

                }

                @Override
                public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
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

                @Override
                public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                    logSuccess("激励广告展示成功");
                    toast("激励广告展示成功");
                }

                @Override
                public void onAdDisplayedFailed(@NonNull AdbidAdInfo adInfo,
                                                @NonNull AdbidError error) {
                    logError("激励广告展示失败: " + error.getMessage());
                    toast("激励广告展示失败");
                }

                @Override
                public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                    logInfo("激励广告关闭");
                    toast("激励广告关闭");
                }

                @Override
                public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                    logInfo("激励广告被点击");
                    toast("激励广告被点击");
                }
            };
            rewardedAd = new AdbidRewarded(AdConfig.getAdConfig().getRewardUnitId(), token);
            rewardedAd.setAdListener(adbidRewardListener);

            Map<String, Object> extra = new HashMap<>();
            extra.put("customId", "user_custom_id_12345");  // 用户自定义ID
            extra.put("testId", 189978878);
            extra.put("testUserName", "zhangSan");
            extra.put("testAdInfo", new ArrayList<>());
            rewardedAd.setLocalExtra(extra);
            rewardedAd.loadAd();
        });
    }

    @Override
    public boolean isRewardReady() {
        return rewardedAd != null && rewardedAd.isReady();
    }

    @Override
    public void showReward() {
        if (rewardedAd != null) rewardedAd.showAd();
    }

    @Override
    public void showBanner(@NonNull ViewGroup viewGroup) {
        checkS2SBiddingToken(AdConfig.getAdConfig().getBannerUnitId(), () -> {
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
                @Override
                public void onBannerLoad(@NonNull AdbidAdInfo adInfo) {
                    logSuccess("横幅广告加载成功");
                    toast("横幅广告加载成功");
                    viewGroup.removeAllViews();
                    viewGroup.addView(bannerView);
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

                @Override
                public void onBannerShow(@NonNull AdbidAdInfo adInfo) {
                    logSuccess("横幅广告展示成功，eCPM " + adInfo.getPrice());
                    toast("横幅广告展示成功");
                }

                @Override
                public void onBannerClose(@NonNull AdbidAdInfo adInfo) {
                    ViewUtils.removeFromParent(bannerView);
                    logInfo("横幅广告关闭");
                    toast("横幅广告关闭");
                }

                @Override
                public void onBannerClicked(@NonNull AdbidAdInfo adInfo) {
                    logInfo("横幅广告被点击");
                    toast("横幅广告被点击");
                }
            });

            bannerView.loadAd(token);
        });
    }

    @Override
    protected void destroy() {
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

    @Override
    protected void loadNative() {
        checkS2SBiddingToken(AdConfig.getAdConfig().getNativeUnitId(), () -> {
            Intent intent = new Intent(context, NativeAdActivity.class);
            if (!StringUtils.isEmpty(token)) {
                intent.putExtra("s2s_token", token);
            }
            context.startActivity(intent);
        });
    }

    @Override
    protected void loadRecycleNative() {
        checkS2SBiddingToken(AdConfig.getAdConfig().getNativeUnitId(), () -> {
            Intent intent = new Intent(context, NativeAdRecycleActivity.class);
            if (!StringUtils.isEmpty(token)) {
                intent.putExtra("s2s_token", token);
            }
            context.startActivity(intent);
        });
    }

    @Override
    protected void loadNativeDraw() {
        checkS2SBiddingToken(AdConfig.getAdConfig().getNativeUnitId(), () -> {
            Intent intent = new Intent(context, NativeAdDrawActivity.class);
            if (!StringUtils.isEmpty(token)) {
                intent.putExtra("s2s_token", token);
            }
            context.startActivity(intent);
        });
    }

    private void logInfo(String msg) {
        logConsole.info(msg);
        Log.i("AdbidSdk", msg);
    }

    private void logSuccess(String msg) {
        logConsole.success(msg);
        Log.i("AdbidSdk", msg);
    }

    private void logError(String msg) {
        logConsole.error(msg);
        Log.e("AdbidSdk", msg);
    }

    private void toast(String msg) {
        ToastHub.show(context, msg);
    }
}
