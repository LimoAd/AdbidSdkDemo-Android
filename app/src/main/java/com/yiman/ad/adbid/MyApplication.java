package com.yiman.ad.adbid;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adbid.media.AdBidPlatform;
import com.adbid.sdk.AdbidCustomController;
import com.adbid.sdk.AdbidInitConfig;
import com.adbid.sdk.AdbidLocation;
import com.adbid.sdk.AdbidSdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        AdbidInitConfig config = AdbidInitConfig
                .builder(AdConfig.getAdConfig().getAppId())
                //设置App渠道
                .setAppChannel("xiaomi")
                //设置App版本
                .setAppVersion("1.0.0")
                //设置年龄
                .setAge(18)
                //设置用户ID
                .setUserId("xxxxxx")
                //设置用户性别，Male表示男性，Female表示女性
                .setGender(AdbidInitConfig.AdbidGender.Female)
                //设置隐私权限
                .addCustomController(new AdbidCustomController() {
                    //是否允许SDK主动使用手机硬件参数（如IMEI）
                    @Override public boolean isCanUsePhoneState() {
                        return true;
                    }

                    //是否允许SDK使用个性化广告（GDPR/CCPA合规需关闭）
                    @Override public boolean isSupportPersonalized() {
                        return false;
                    }

                    //是否允许SDK主动使用地理位置信息
                    @Override public boolean isCanUseLocation() {
                        return true;
                    }

                    //是否允许SDK主动获取OAID
                    @Override public boolean isCanUseWifiState() {
                        return true;
                    }

                    //是否允许SDK主动获取OAID
                    @Override public boolean isCanUseOaid() {
                        return true;
                    }

                    //开发者可传入OAID（当isCanUseOaid=false时生效）
                    @NonNull @Override public String getDevOaid() {
                        return "";
                    }

                    //是否允许SDK获取应用安装列表
                    @Override public boolean isCanUseAppList() {
                        return true;
                    }

                    //开发者可传入应用安装列表（当isCanUseAppList=false时生效）
                    @NonNull @Override public List<PackageInfo> getAppList() {
                        return Collections.emptyList();
                    }

                    //是否允许SDK获取ANDROID_ID
                    @Override public boolean isCanUseAndroidId() {
                        return true;
                    }

                    // 开发者可传入ANDROID_ID（当isCanUseAndroidId=false时生效）
                    @NonNull @Override public String getAndroidId() {
                        return "";
                    }

                    //是否允许SDK获取MAC地址
                    @Override public boolean isCanUseMacAddress() {
                        return true;
                    }

                    //开发者可传入MAC地址（当isCanUseMacAddress=false时生效）
                    @NonNull @Override public String getMacAddress() {
                        return "";
                    }

                    //是否允许写入存储卡权限
                    @Override public boolean isCanUseWriteExternal() {
                        return true;
                    }

                    // 是否允许加载摇一摇广告（需加速度传感器权限）
                    @Override public boolean isCanUseShakeAd() {
                        return true;
                    }

                    //是否允许SDK使用录音权限
                    @Override public boolean isCanUseRecordAudio() {
                        return true;
                    }

                    //开发者可传入IMEI（当isCanUsePhoneState=false时生效）
                    @NonNull @Override public String getDevImei() {
                        return "";
                    }

                    //开发者可传入IMEI列表（多卡设备）
                    @NonNull @Override public String[] getDevImeiList() {
                        return new String[0];
                    }

                    //开发者可传入定位信息
                    @Nullable @Override public AdbidLocation getLocation() {
                        return null;
                    }

                    //是否允许SDK主动获取IP地址
                    @Override public boolean isCanUseIP() {
                        return true;
                    }

                    //开发者可传入IP地址（当isCanUseIP=false时生效）
                    @NonNull @Override public String getIP() {
                        return "";
                    }
                })
                .build();
        AdbidSdk.getInstance(this).initialize(config, (isSuccess, adbidError) -> {
            if (isSuccess) {
                Log.i("AdbidSdk", "初始化成功");
            } else {
                Log.i("AdbidSdk", "初始化失败：" + adbidError.getMessage());
            }

        });

        AdbidSdk.getInstance(this).setDebugMode(true);

    }
}
