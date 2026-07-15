package com.yiman.ad;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.yiman.ad.log.MainLogConsole;

public abstract class IAdLoad {
    protected final Context context;
    protected final MainLogConsole logConsole;

    public IAdLoad(Context context, MainLogConsole logConsole) {
        this.context = context;
        this.logConsole = logConsole;
    }

    protected abstract void init();

    protected abstract void loadSplash();

    protected abstract boolean isSplashReady();

    protected abstract void showSplash(@NonNull ViewGroup viewGroup);

    protected abstract void loadInterstitial();

    protected abstract boolean isInterstitialReady();

    protected abstract void showInterstitial();

    protected abstract void loadReward();

    protected abstract boolean isRewardReady();

    protected abstract void showReward();

    protected abstract void showBanner(@NonNull ViewGroup viewGroup);

    protected abstract void destroy();

    protected abstract void loadNative();

    protected abstract void loadRecycleNative();

    protected abstract void loadNativeDraw();
}
