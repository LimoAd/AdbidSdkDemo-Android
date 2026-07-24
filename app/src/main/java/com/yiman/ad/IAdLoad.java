package com.yiman.ad;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.yiman.ad.log.MainLogConsole;

public abstract class IAdLoad {
    protected Context context;
    protected MainLogConsole logConsole;

    public IAdLoad(Context context, MainLogConsole logConsole) {
        this.context = context;
        this.logConsole = logConsole;
    }

    public void update(@NonNull Context context, @NonNull MainLogConsole logConsole) {
        this.context = context;
        this.logConsole = logConsole;
    }

    public void updateContext(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    public MainLogConsole getLogConsole() {
        return logConsole;
    }

    public abstract void init();

    public abstract void loadSplash();

    public abstract boolean isSplashReady();

    public abstract void showSplash(@NonNull ViewGroup viewGroup);

    public abstract void loadInterstitial();

    public abstract boolean isInterstitialReady();

    public abstract void showInterstitial();

    public abstract void loadReward();

    public abstract boolean isRewardReady();

    public abstract void showReward();

    public abstract void showBanner(@NonNull ViewGroup viewGroup);

    public abstract void destroy();

    public abstract void loadNative();

    public abstract void loadRecycleNative();

    public abstract void loadNativeDraw();
}
