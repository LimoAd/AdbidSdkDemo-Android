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

        configMap.put("10001", new AdConfig("10001", "MTc2MTEyMjM5NTQwNA==", "MTc2MTEyMjI5MjI0OA==",
                "MTc2MTEyMjMzNTUyMQ==", "MTc1MjcyMDQyMjgwOQ==", "MTc2MTU0NjgxNjEyOQ=="));
        configMap.put("10003", new AdConfig("10003", "MTc1MjgxODg5NzQwNg==", "MTc1MjgzNDIzODkwMA==",
                "MTc1NTI0NzIwMTI1Ng==", "MTc1MjgxODg1MjYzNA==", "MTc1NTI0NzIyMDA4Nw=="));
        configMap.put("10005", new AdConfig("10005", "MTc1MzkzMDgyNTk4MA==", "MTc1MzkzMTExNjA4NA==",
                "MTc1ODcwMDkyNjk1NA==", "MTc1MzkzMDY5NDkyOA==", "MTc1ODc5NjM5NTY4OA=="));
        configMap.put("10006",
                new AdConfig("10006", "MTc1ODc4MzcyODk3Ng==", "MTc1NDAzMjI5MTk4OQ==", "",
                        "MTc1NDAzMTYwOTk3OQ==", ""));

        configMap.put("10007", new AdConfig("10007", "MTc1MzkzMDgyNTk4MA==",
                "MTc2NjExMTYxODc3MQ==",
                "MTc3OTI0NzA4NDQ0Mw==", "MTc3OTI0NzA0OTgxNg==", "MTc1ODc5NjM5NTY4OA==","MTc2NjExMTU5ODcxNA=="));

        configMap.put("10007_UBIX", new AdConfig("10007", "MTc1MzkzMDgyNTk4MA==",
                "MTc3OTI0OTI2MzA1OA==",
                "MTc3OTI0ODU2ODYxNQ==", "MTc3OTI0ODU0OTgxMg==", "MTc1ODc5NjM5NTY4OA==", "MTc3OTI0NzExMzgyOQ=="));

        configMap.put("10007_LM", new AdConfig("10007", "MTc1MzkzMDgyNTk4MA==",
                "MTc3OTI0ODU5MjcxNQ==",
                "MTc3OTI0ODU2ODYxNQ==", "MTc3OTI0ODU0OTgxMg==", "MTc1ODc5NjM5NTY4OA==", "MTc3OTI0ODU4MDQ1NQ=="));

        configMap.put("10007_FL", new AdConfig("10007", "MTc1MzkzMDgyNTk4MA==",
                "MTc3OTI0NjMyODQzNA==",
                "MTc3OTI0NjM4MzE5Ng==", "MTc3OTI0NjQxNTkxOA==", "MTc1ODc5NjM5NTY4OA==", "MTc3OTI0NjIyOTk2NQ=="));

        configMap.put("10007_Sigmob", new AdConfig("10007", "MTc1MzkzMDgyNTk4MA==",
                "MTc3OTcwNTk5MjcxMg==",
                "MTc3OTcwNTk1ODkxNQ==", "MTc3OTcwNTkyNzA5Mg==", "MTc1ODc5NjM5NTY4OA==", "MTc3OTcwNTk3ODMzOA=="));

        configMap.put("10007_Ezviz", new AdConfig("10007", "MTc1MzkzMDgyNTk4MA==",
                "MTc3OTcwNjA5MjgyMw==",

                "MTc3OTcwNjA1NjI2Nw==", "MTc3OTcwNjA0NDAxNg==", "MTc1ODc5NjM5NTY4OA==", "MTc3OTcwNjEwNTAyNg=="));

        configMap.put("10008",
                new AdConfig("10008", "", "MTc2NDkwNTA2ODE0NQ==", "MTc2MTI3MjI5MTE4Mw==",
                        "MTc2MTI3MjIxNzUzNg==", ""));
        configMap.put("10017",
                new AdConfig("10017", "MTc3MjY5MzYyNzU3NQ==", "", "MTc3MjY5NDAzODIyMA==",
                        "MTc3MjY5MzUxMTUyNQ==", ""));

        configMap.put("10019",
                new AdConfig("10019", "MTc3MjY5MzYyNzU3NQ==", "", "MTc3NTE4NjQ2ODgyOQ==",
                        "MTc3MjY5MzUxMTUyNQ==", ""));
        configMap.put("10028",
                new AdConfig("10028", "", "", "",
                        "MTc3OTg1ODEyOTc5Nw==", ""));
    }

    public static IAdLoad getAdLoad(@NonNull Context context, @NonNull MainLogConsole logConsole) {
        return AdbidAdLoad.getInstance(context, logConsole);
    }

    @NonNull
    public static String resolveSelectionKey(String appId) {
        if (appId != null && configMap.containsKey(appId)) {
            return appId;
        }
        return DEFAULT_APP_ID;
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

    public AdConfig(String appId, String interUnitId, String nativeUnitId, String rewardUnitId,
                    String splashUnitId, String bannerUnitId, String nativeUnitId2) {
        this.appId = appId;
        this.interUnitId = interUnitId;
        this.nativeUnitId = nativeUnitId;
        this.nativeUnitId2 = nativeUnitId2;
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
