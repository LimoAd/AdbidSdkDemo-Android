package com.yiman.ad.adbid;

import android.content.Context;

import androidx.annotation.NonNull;

import com.yiman.ad.AppIdStore;
import com.yiman.ad.IAdLoad;
import com.yiman.ad.log.MainLogConsole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class AdConfig {
    private String appId;
    private final String interUnitId;
    private final String nativeUnitId;
    private final String rewardUnitId;
    private final String splashUnitId;
    private final String bannerUnitId;
    private String nativeUnitId2;

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
        configMap.put("10006",
                new AdConfig("10006", "MTc1ODc4MzcyODk3Ng==", "MTc1NDAzMjI5MTk4OQ==", "",
                        "MTc1NDAzMTYwOTk3OQ==", ""));

        configMap.put("10007_LM", new AdConfig("10007", "MTc1MzkzMDgyNTk4MA==",
                "MTc3OTI0ODU5MjcxNQ==",
                "MTc3OTI0ODU2ODYxNQ==", "MTc3OTI0ODU0OTgxMg==", "MTc1ODc5NjM5NTY4OA=="));

        configMap.put("10007_FL", new AdConfig("10007", "MTc1MzkzMDgyNTk4MA==",
                "MTc3OTI0NjMyODQzNA==",
                "MTc3OTI0NjM4MzE5Ng==", "MTc3OTI0NjQxNTkxOA==", "MTc1ODc5NjM5NTY4OA=="));

        configMap.put("10007_Sigmob", new AdConfig("10007", "MTc1MzkzMDgyNTk4MA==",
                "MTc3OTcwNTk5MjcxMg==",
                "MTc3OTcwNTk1ODkxNQ==", "MTc3OTcwNTkyNzA5Mg==", "MTc1ODc5NjM5NTY4OA=="));

        configMap.put("10007_Ezviz", new AdConfig("10007", "MTc1MzkzMDgyNTk4MA==",
                "MTc3OTcwNjA5MjgyMw==",
                //  "MTc3OTcwNjEwNTAyNg==",
                "MTc3OTcwNjA1NjI2Nw==", "MTc3OTcwNjA0NDAxNg==", "MTc1ODc5NjM5NTY4OA=="));

        configMap.put("10028",
                new AdConfig("10028", "", "", "",
                        "MTc3OTg1ODEyOTc5Nw==", ""));
    }

    public static IAdLoad getAdLoad(@NonNull Context context, @NonNull MainLogConsole logConsole) {
        return new AdbidAdLoad(context, logConsole);
    }

    @NonNull
    public static String resolveSelectionKey(String appId) {
        if (appId != null && configMap.containsKey(appId)) {
            return appId;
        }
        return DEFAULT_APP_ID;
    }

    public void setNativeUnitId2(String nativeUnitId2) {
        this.nativeUnitId2 = nativeUnitId2;
    }

    public String getNativeUnitId2() {
        return nativeUnitId2;
    }

    @NonNull
    public static String resolveAppId(String appId) {
        return configMap.get(resolveSelectionKey(appId)).appId;
    }

    @NonNull
    public static List<String> getAvailableAppIds() {
        return new ArrayList<>(configMap.keySet());
    }

    public static AdConfig getAdConfig() {
        String selected = AppIdStore.getSelectedAppKey();
        return configMap.get(resolveSelectionKey(selected));
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
