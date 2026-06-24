package com.yiman.ad.adbid;

import android.content.Context;

import androidx.annotation.NonNull;

import com.yiman.ad.IAdLoad;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class AdConfig {
    private String appId;
    private final String interUnitId;
    private final String nativeUnitId;
    private final String rewardUnitId;
    private final String splashUnitId;
    private final String bannerUnitId;

    public static final String DEFAULT_APP_ID = "10005";
    private static final Map<String, AdConfig> configMap = new HashMap<>();

    static {
        configMap.put("10005", new AdConfig("10005", "MTc1MzkzMDgyNTk4MA==", "MTc1MzkzMTExNjA4NA==",
                "MTc1ODcwMDkyNjk1NA==", "MTc1MzkzMDY5NDkyOA==", "MTc1ODc5NjM5NTY4OA=="));
    }

    public static IAdLoad getAdLoad(@NonNull Context context) {
        return new AdbidAdLoad(context);
    }


    public static AdConfig getAdConfig() {
        return configMap.get("10005");
    }

    public AdConfig(String appId, String interUnitId, String nativeUnitId, String rewardUnitId,
                    String splashUnitId, String bannerUnitId) {
        this.appId = appId;
        this.interUnitId = interUnitId;
        this.nativeUnitId = nativeUnitId;
        this.rewardUnitId = rewardUnitId;
        this.splashUnitId = splashUnitId;
        this.bannerUnitId = bannerUnitId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }


    public String getInterUnitId() {
        return interUnitId;
    }


    public String getNativeUnitId() {
        return nativeUnitId;
    }


    public String getRewardUnitId() {
        return rewardUnitId;
    }


    public String getSplashUnitId() {
        return splashUnitId;
    }


    public String getBannerUnitId() {
        return bannerUnitId;
    }


    @NonNull
    @Override
    public String toString() {
        return "AdConfig{" + "appId='" + appId + '\'' + ", interUnitId='" + interUnitId + '\'' +
                ", nativeUnitId='" + nativeUnitId + '\'' + ", rewardUnitId='" + rewardUnitId +
                '\'' + ", splashUnitId='" + splashUnitId + '\'' + ", bannerUnitId='" +
                bannerUnitId + '\'' + '}';
    }
}
