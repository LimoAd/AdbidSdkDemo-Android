package com.yiman.ad.adbid.ad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adbid.media.AdMaterialType;
import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidError;
import com.adbid.media.ad.AdbidNativeLoader;
import com.adbid.media.nativeAd.AdbidAppDownLoadListener;
import com.adbid.media.nativeAd.AdbidCustomDownloadConfirmListener;
import com.adbid.media.nativeAd.AdbidNativeAd;
import com.adbid.media.nativeAd.AdbidNativeAdView;
import com.adbid.media.nativeAd.AdbidNativeEventListener;
import com.adbid.media.nativeAd.AdbidNativeMaterial;
import com.adbid.media.nativeAd.AdbidNativeVideoListener;
import com.adbid.media.nativeOverseas.NativeAdbidLoadListener;
import com.yiman.ad.adbid.AdConfig;
import com.yiman.ad.adbid.BaseActivity;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.utils.BindViewUtils;
import com.yiman.ad.adbid.view.TitleBar;

public class NativeAdActivity extends BaseActivity implements View.OnClickListener {

    private AdbidNativeLoader mATNative;
    private AdbidNativeAd mNativeAd;

    private AdbidNativeAdView mATNativeView;
    private TextView mTVLoadAdBtn;
    private TextView mTVIsAdReadyBtn;
    private TextView mTVShowAdBtn;
    private View mPanel;
    private View videoControl;
    private View videoStart;
    private View videoPause;
    private Button videoMuteChange;
    private TextView videoLog;
    private TextView videoProgress;
    //是否自定义video展示
    private boolean isCustomVideo = false;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        initView();
        initListener();
        initATNativeAd(AdConfig.getAdConfig().getNativeUnitId());
    }


    protected void initView() {
        mTVLoadAdBtn = findViewById(R.id.load_ad_btn);
        mTVIsAdReadyBtn = findViewById(R.id.is_ad_ready_btn);
        mTVShowAdBtn = findViewById(R.id.show_ad_btn);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setListener(view -> finish());

        //广告布局
        mPanel = findViewById(R.id.rl_panel);
        mATNativeView = findViewById(R.id.native_ad_view);
        RecyclerView rvButtonList = findViewById(R.id.rv_button);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        rvButtonList.setLayoutManager(manager);

        //视频广告操作布局
        videoControl = findViewById(R.id.layout_video_control);
        videoPause = findViewById(R.id.btn_pause);
        videoStart = findViewById(R.id.btn_start);
        videoLog = findViewById(R.id.text_video_msg);
        videoProgress = findViewById(R.id.text_video_progress);
        videoMuteChange = findViewById(R.id.btn_mute);

    }

    protected void initListener() {
        mTVLoadAdBtn.setOnClickListener(this);
        mTVIsAdReadyBtn.setOnClickListener(this);
        mTVShowAdBtn.setOnClickListener(this);
    }


    private void initATNativeAd(String placementId) {
        mATNative = new AdbidNativeLoader(this, placementId, new NativeAdbidLoadListener() {

            @Override public void onNativeAdLoaded(@NonNull AdbidNativeAd nativeAd) {
                Toast.makeText(NativeAdActivity.this, "load success", Toast.LENGTH_SHORT).show();
            }

            @Override public void onNativeAdLoadFail(@NonNull AdbidError adError) {
                Toast.makeText(NativeAdActivity.this, "load fail", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadAd() {
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
            //设置事件监听
            mNativeAd.setEventListener(new AdbidNativeEventListener() {
                @Override public void onImpression(@NonNull AdbidNativeAdView view,
                                                   @NonNull AdbidAdInfo adInfo) {
                    videoControl.setVisibility(
                            nativeAd.getAdMaterialType() == AdMaterialType.VIDEO ? View.VISIBLE :
                                    View.INVISIBLE);
                    Toast.makeText(NativeAdActivity.this, "ad impress", Toast.LENGTH_SHORT).show();
                }

                @Override public void onNativeAdClick(@NonNull AdbidNativeAdView view,
                                                      @NonNull AdbidAdInfo adInfo) {
                    Toast.makeText(NativeAdActivity.this, "ad click", Toast.LENGTH_SHORT).show();
                }

                @Override public void onAdClose(@Nullable AdbidNativeAdView view) {
                    Toast.makeText(NativeAdActivity.this, "ad close", Toast.LENGTH_SHORT).show();
                }
            });

            mNativeAd.setDislikeCallbackListener(
                    info -> Toast.makeText(NativeAdActivity.this, "dislike click",
                            Toast.LENGTH_SHORT).show());


            if (!isCustomVideo) {
                BindViewUtils.registerView(this, mNativeAd, mATNativeView);
            }/*else {
                BindViewUtils.registerCustomViewView(this, mNativeAd, mATNativeView);
            }*/
            initAdNativeListener(mNativeAd);

            mATNativeView.setVisibility(View.VISIBLE);
            mPanel.setVisibility(View.VISIBLE);
        }


    }

    private boolean isMute = true;

    private void initAdNativeListener(AdbidNativeAd mNativeAd) {
        videoStart.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mNativeAd.startVideo();
            }
        });
        videoPause.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mNativeAd.pauseVideo();
            }
        });

        /*mNativeAd.setCustomDownloadConfirmListener(new AdbidCustomDownloadConfirmListener() {
            @Override public void onDownloadConfirm(Context context, Bundle bundle,
                                                    AdbidDownloadConfirmCallback callback) {
                new AlertDialog.Builder(context)
                        .setTitle("这是一个广告测试弹框")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 处理“确定”
                            if (callback!=null){
                                callback.onConfirm();
                            }
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                            // 处理“取消”
                            if (callback!=null){
                                callback.onCancel();
                            }
                        })
                        .setNeutralButton("关闭", (dialog, which) -> {
                            // 处理“关闭”
                            if (callback!=null){
                                callback.onConfirm();
                            }
                        })
                        .setCancelable(false) // 可选：点击外部不关闭
                        .show();
            }
        });*/
        videoMuteChange.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                isMute = !isMute;
                videoMuteChange.setText(isMute ? "打开声音" : "关闭声音");
                mNativeAd.setMuted(isMute);
            }
        });
        mNativeAd.setVideoListener(new AdbidNativeVideoListener() {
            @Override public void onVideoStart() {
                logText("video status: start");
            }

            @Override public void onVideoPause() {
                logText("video status: pause");
            }

            @Override public void onVideoResume() {
                logText("video status: resume");
            }

            @Override public void onVideoComplete() {
                logText("video status: end");
            }

            @Override public void onVideoError(AdbidError var1) {
                logText("video status: error " + var1.getMessage());
            }

            @Override public void onVideoProgressUpdate(long var1, long var3) {
                if (var3 == 0) {
                    return;
                }
                int progress = (int) (var1 * 100F / var3);
                progressText("video progress：" + progress + "%");
            }
        });
        mNativeAd.setDownLoadListener(new AdbidAppDownLoadListener() {
            @Override public void onDownloadPaused(int progress) {
                logText("download status: pause");
            }

            @Override public void onDownloadStarted() {
                logText("download status: start");
            }

            @Override public void onDownloadProgressUpdate(int progress) {
                progressText("download progress：" + progress + "%");
            }

            @Override public void onDownloadFinished() {
                logText("download status: finish");
            }

            @Override public void onDownloadResume(int progress) {
                logText("download status: resume");
            }

            @Override public void onDownloadFailed(AdbidError error) {
                logText("download status: fail");
            }


            @Override public void onInstalled() {
                logText("download status: apk install");
            }
        });
    }

    private void logText(String msg) {
        videoLog.setText(msg);
    }

    private void progressText(String msg) {
        videoProgress.setText(msg);
    }


    @Override protected void onDestroy() {
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


    @SuppressLint("NonConstantResourceId") @Override public void onClick(View v) {
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

