package com.yiman.ad;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public abstract class IAdLoad {
    protected Context context;

    public IAdLoad(Context context) {
        this.context = context;
    }

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
}
