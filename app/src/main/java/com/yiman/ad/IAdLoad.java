package com.yiman.ad;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public abstract class IAdLoad {
    public Context context;

    public IAdLoad(Context context) {
        this.context = context;
    }

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
}
