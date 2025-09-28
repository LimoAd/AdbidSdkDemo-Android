package com.yiman.ad.adbid.ad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidError;
import com.adbid.media.NativeAdLoadListener;
import com.adbid.media.ad.AdbidNativeLoader;
import com.adbid.media.nativeAd.AdbidAppDownLoadListener;
import com.adbid.media.nativeAd.AdbidNativeAd;
import com.adbid.media.nativeAd.AdbidNativeAdView;
import com.adbid.media.nativeAd.AdbidNativeEventListener;
import com.adbid.media.nativeAd.AdbidNativePrepareInfo;
import com.yiman.ad.adbid.AdConfig;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.utils.SelfRenderViewUtil;
import com.yiman.ad.adbid.view.TitleBar;

import java.util.HashMap;
import java.util.Map;

public class NativeAdActivity extends Activity implements View.OnClickListener {

    private AdbidNativeLoader mATNative;
    private AdbidNativeAd mNativeAd;

    private AdbidNativeAdView mATNativeView;
    private ViewGroup mSelfRenderView;
    private TextView mTVLoadAdBtn;
    private TextView mTVIsAdReadyBtn;
    private TextView mTVShowAdBtn;
    private View mPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        initView();
        initListener();
        initPanel();
        initATNativeAd(AdConfig.getAdConfig().getNativeUnitId());
    }


    protected void initView() {
        mTVLoadAdBtn = findViewById(R.id.load_ad_btn);
        mTVIsAdReadyBtn = findViewById(R.id.is_ad_ready_btn);
        mTVShowAdBtn = findViewById(R.id.show_ad_btn);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setListener(view -> finish());
        initPanel();
    }

    protected void initListener() {
        mTVLoadAdBtn.setOnClickListener(this);
        mTVIsAdReadyBtn.setOnClickListener(this);
        mTVShowAdBtn.setOnClickListener(this);
    }

    private void initPanel() {
        mPanel = findViewById(R.id.rl_panel);
        mATNativeView = findViewById(R.id.native_ad_view);
        mSelfRenderView = findViewById(R.id.self_render_view);
        RecyclerView rvButtonList = findViewById(R.id.rv_button);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        rvButtonList.setLayoutManager(manager);
    }

    private void initATNativeAd(String placementId) {
        mATNative = new AdbidNativeLoader(this, placementId, new NativeAdLoadListener() {
            @Override
            public void onNativeAdLoaded() {
                Toast.makeText(NativeAdActivity.this, "load success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdLoadFail(@NonNull AdbidError adError) {
                Toast.makeText(NativeAdActivity.this, "load fail", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadAd() {
        Map<String,Object> map=new HashMap<>();
        map.put("test","this is test");
        mATNative.setLocalExtra(map);
        mATNative.loadAd();
    }

    private boolean isAdReady() {
        boolean isReady = mATNative.getNativeAd() != null && mATNative.getNativeAd().isReady();
        Toast.makeText(NativeAdActivity.this, "load isReady " + isReady, Toast.LENGTH_SHORT).show();
        return isReady;
    }

    private void showAd() {
        AdbidNativeAd nativeAd = mATNative.getNativeAd();
        if (nativeAd != null) {
            if (mNativeAd != null) {
                mNativeAd.destroy();
            }
            mNativeAd = nativeAd;

            mNativeAd.setEventListener(new AdbidNativeEventListener() {

                @Override
                public void onImpression(@NonNull AdbidNativeAdView view,
                                         @NonNull AdbidAdInfo adInfo) {
                    Toast.makeText(NativeAdActivity.this, "ad impress", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNativeAdClick(@NonNull AdbidNativeAdView view,
                                            @NonNull AdbidAdInfo adInfo) {
                    Toast.makeText(NativeAdActivity.this, "ad click", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdVideoStart(@NonNull AdbidNativeAdView view) {
                    Toast.makeText(NativeAdActivity.this, "video start", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdVideoEnd(@NonNull AdbidNativeAdView view) {
                    Toast.makeText(NativeAdActivity.this, "video end", Toast.LENGTH_SHORT).show();
                }

                @Override public void onAdClose(@Nullable AdbidNativeAdView view) {
                    Toast.makeText(NativeAdActivity.this, "ad close", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdVideoProgress(@Nullable AdbidNativeAdView view, int progress) {

                }
            });

            mNativeAd.setDislikeCallbackListener(
                    info -> Toast.makeText(NativeAdActivity.this, "dislike click", Toast.LENGTH_SHORT)
                            .show());

            mATNativeView.removeAllViews();

            AdbidNativePrepareInfo mNativePrepareInfo;

            mNativePrepareInfo = new AdbidNativePrepareInfo();

            SelfRenderViewUtil.bindSelfRenderView(this, mNativeAd.getAdMaterial(), mSelfRenderView,
                    mNativePrepareInfo);

            mNativeAd.renderAdContainer(mATNativeView, mSelfRenderView);

            if (mNativePrepareInfo.getCtaView() != null &&
                    mNativePrepareInfo.getCtaView() instanceof TextView) {
                TextView ctaTextView = (TextView) mNativePrepareInfo.getCtaView();
                mNativeAd.setDownLoadListener(new AdbidAppDownLoadListener() {

                    @Override
                    public void onDownloadPaused(int progress) {
                        Toast.makeText(NativeAdActivity.this, "download pause", Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onDownloadStarted() {
                        Toast.makeText(NativeAdActivity.this, "download start", Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onDownloadProgressUpdate(int progress) {

                    }

                    @Override
                    public void onDownloadFinished() {
                        Toast.makeText(NativeAdActivity.this, "download finish", Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override public void onDownloadResume(int progress) {

                    }

                    @Override public void onDownloadFailed(AdbidError error) {
                        Toast.makeText(NativeAdActivity.this, "download fail", Toast.LENGTH_SHORT)
                                .show();
                    }


                    @Override
                    public void onInstalled() {
                        Toast.makeText(NativeAdActivity.this, "download install",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            mNativeAd.prepare(mATNativeView, mNativePrepareInfo);
            mATNativeView.setVisibility(View.VISIBLE);
            mPanel.setVisibility(View.VISIBLE);
        } else {
            Log.e("nativeActivity", "this placement no cache!");
        }
    }

//    public void changeBg(View view,boolean selected) {
//        view.setBackgroundResource(selected ? R.drawable.bg_white_selected : R.drawable.bg_white);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyAd();
        if (mATNative != null) {
            mATNative.setAdListener(null);
        }
    }

    private void destroyAd() {
        if (mNativeAd != null) {
            mNativeAd.destroy();
        }
    }

    @Override
    protected void onPause() {
        if (mNativeAd != null) {
            mNativeAd.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mNativeAd != null) {
            mNativeAd.onResume();
        }
        super.onResume();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v == null) return;
        if (v.getId() == R.id.load_ad_btn) {
            loadAd();
        } else if (v.getId() == R.id.is_ad_ready_btn) {
            isAdReady();
        } else if (v.getId() == R.id.show_ad_btn) {
            showAd();
        }
    }


}
