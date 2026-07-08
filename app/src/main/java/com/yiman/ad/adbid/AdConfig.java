package com.yiman.ad.adbid;

import android.content.Context;

import androidx.annotation.NonNull;

import com.yiman.ad.AppIdStore;
import com.yiman.ad.IAdLoad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") public class AdConfig {
    private String appId;
    private final String interUnitId;
    private final String nativeUnitId;
    private final String rewardUnitId;
    private final String splashUnitId;
    private final String bannerUnitId;

    public static final String DEFAULT_APP_ID = "10005";
    private static final Map<String, AdConfig> configMap = new HashMap<>();

    private static boolean s2sBiddingEnabled = false;

    public static boolean isS2SBiddingEnabled() {
        return s2sBiddingEnabled;
    }

    public static void setS2SBiddingEnabled(boolean enabled) {
        s2sBiddingEnabled = enabled;
    }


    static {
        configMap.put("10005", new AdConfig("10005", "MTc1MzkzMDgyNTk4MA==", "MTc1MzkzMTExNjA4NA==",
                "MTc1ODcwMDkyNjk1NA==", "MTc1MzkzMDY5NDkyOA==", "MTc1ODc5NjM5NTY4OA=="));
        configMap.put("10007", new AdConfig("10007", "MTc1MzkzMDgyNTk4MA==",
                "MTc3OTcwNjA5MjgyMw==",
                "MTc3OTcwNjA1NjI2Nw==", "MTc3OTcwNjA0NDAxNg==", "MTc1ODc5NjM5NTY4OA=="));
    }

    public static IAdLoad getAdLoad(@NonNull Context context) {
        return new AdbidAdLoad(context);
    }

    @NonNull public static String resolveAppId(String appId) {
        if (appId != null && configMap.containsKey(appId)) {
            return appId;
        }
        return DEFAULT_APP_ID;
    }

    @NonNull public static List<String> getAvailableAppIds() {
        return new ArrayList<>(configMap.keySet());
    }

    public static AdConfig getAdConfig() {
        String selected = AppIdStore.getSelectedAppId();
        return configMap.get(resolveAppId(selected));
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


    @NonNull @Override public String toString() {
        return "AdConfig{" + "appId='" + appId + '\'' + ", interUnitId='" + interUnitId + '\'' +
                ", nativeUnitId='" + nativeUnitId + '\'' + ", rewardUnitId='" + rewardUnitId +
                '\'' + ", splashUnitId='" + splashUnitId + '\'' + ", bannerUnitId='" +
                bannerUnitId + '\'' + '}';
    }
}
