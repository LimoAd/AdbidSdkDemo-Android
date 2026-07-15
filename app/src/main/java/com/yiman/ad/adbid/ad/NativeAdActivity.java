package com.yiman.ad.adbid.ad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adbid.media.AdBidLossInfo;
import com.adbid.media.AdBidPlatform;
import com.adbid.media.AdMaterialType;
import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidError;
import com.adbid.media.ad.AdbidNativeLoader;
import com.adbid.media.nativeAd.AdbidAppDownLoadListener;
import com.adbid.media.nativeAd.AdbidNativeAd;
import com.adbid.media.nativeAd.AdbidNativeAdView;
import com.adbid.media.nativeAd.AdbidNativeEventListener;
import com.adbid.media.nativeAd.AdbidNativeVideoListener;
import com.adbid.media.nativeOverseas.NativeAdbidLoadListener;
import com.yiman.ad.adbid.AdConfig;
import com.yiman.ad.BaseActivity;
import com.yiman.ad.adbid.AdbidAdLoad;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.utils.BindViewUtils;
import com.yiman.ad.adbid.view.TitleBar;
import com.yiman.ad.log.MainLogConsole;

public class NativeAdActivity extends BaseActivity implements View.OnClickListener {

    private final MainLogConsole logConsole = new MainLogConsole();
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
        titleBar.setTitle(R.string.app_section_native);
        titleBar.setListener(view -> finish());

        //广告布局
        mPanel = findViewById(R.id.rl_panel);
        recreateNativeAdView();


        //视频广告操作布局
        videoControl = findViewById(R.id.layout_video_control);
        videoPause = findViewById(R.id.btn_pause);
        videoStart = findViewById(R.id.btn_start);
        videoMuteChange = findViewById(R.id.btn_mute);
        ScrollView logScroll = findViewById(R.id.scroll_log);
        TextView logText = findViewById(R.id.text_log);
        logConsole.bind(logScroll, logText);
        logConsole.clear();
        logConsole.info("自渲染信息流页面已打开");

    }

    protected void initListener() {
        mTVLoadAdBtn.setOnClickListener(this);
        mTVIsAdReadyBtn.setOnClickListener(this);
        mTVShowAdBtn.setOnClickListener(this);
    }

    int size=0;
    private void initATNativeAd(String placementId) {
        mATNative = new AdbidNativeLoader(this, placementId,new NativeAdbidLoadListener() {

            @Override public void onNativeAdLoaded(@NonNull AdbidNativeAd nativeAd) {
                AdbidAdInfo adinfo = nativeAd.getAdbidAdInfo();
                String msg = "load success ecpm" + (adinfo == null ? "adinfo null" :
                        adinfo.getPrice());
                showToast(msg);
                logConsole.success("信息流加载成功: " + msg);

                if (size % 2 > 0)
                    mATNative.winNotice(1000);
                else
                    mATNative.lossNotice(new AdBidLossInfo(AdBidPlatform.GDT,5000,"this is test " +
                            "ad"));
                size++;
            }

            @Override public void onNativeAdLoadFail(@NonNull AdbidError adError) {
                showToast("load fail");
                logConsole.error("信息流加载失败: " + adError.getMessage());
            }
        });

    }

    private void loadAd() {
        destroyAd();
        recreateNativeAdView();
        mPanel.setVisibility(View.INVISIBLE);
        videoControl.setVisibility(View.INVISIBLE);
        String token = getIntent().getStringExtra("s2s_token");
        logConsole.info("开始加载自渲染信息流");
        mATNative.loadAd(token);
    }

    private boolean isAdReady() {
        boolean isReady = mATNative.getNativeAd() != null && mATNative.getNativeAd().isReady();
        showToast("load isReady " + isReady);
        if (isReady) {
            logConsole.success("自渲染信息流就绪: true");
        } else {
            logConsole.warning("自渲染信息流就绪: false");
        }
        return isReady;
    }

    private void showAd() {
        AdbidNativeAd nativeAd = mATNative.getNativeAd();
        if (nativeAd != null) {
            logConsole.info("开始展示自渲染信息流");
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
                    showToast("ad impress");
                    logConsole.success("信息流曝光成功");
                }

                @Override public void onNativeAdClick(@NonNull AdbidNativeAdView view,
                                                      @NonNull AdbidAdInfo adInfo) {
                    showToast("ad click");
                    logConsole.info("信息流被点击");
                }

                @Override public void onAdClose(@Nullable AdbidNativeAdView view) {
                    showToast("ad close");
                    logConsole.warning("信息流已关闭");
                }
            });

            mNativeAd.setDislikeCallbackListener(info -> {
                showToast("dislike click");
                logConsole.warning("触发不感兴趣回调");
            });

            if (!isCustomVideo) {
                BindViewUtils.registerView(this, mNativeAd, mATNativeView);
            }/*else {
                BindViewUtils.registerCustomViewView(this, mNativeAd, mATNativeView);
            }*/
            initAdNativeListener(mNativeAd);

            mATNativeView.setVisibility(View.VISIBLE);
            mPanel.setVisibility(View.VISIBLE);
            logConsole.success("广告视图已绑定到预览区");
        } else {
            logConsole.warning("当前无可展示的信息流广告，请先加载");
        }


    }

    private void recreateNativeAdView() {
        if (!(mPanel instanceof ViewGroup)) {
            return;
        }
        ViewGroup panelGroup = (ViewGroup) mPanel;
        panelGroup.removeAllViews();
        AdbidNativeAdView nativeAdView = new AdbidNativeAdView(this);
        nativeAdView.setId(R.id.native_ad_view);
        nativeAdView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        nativeAdView.setBackgroundResource(android.R.color.transparent);
        LayoutInflater.from(this).inflate(R.layout.layout_native_self, nativeAdView, true);
        panelGroup.addView(nativeAdView);
        mATNativeView = nativeAdView;
    }

    private boolean isMute = true;

    private void initAdNativeListener(AdbidNativeAd mNativeAd) {
        videoStart.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                logConsole.info("点击视频播放");
                mNativeAd.startVideo();
            }
        });
        videoPause.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                logConsole.info("点击视频暂停");
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
                logConsole.info("视频静音切换: " + (isMute ? "静音" : "有声"));
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
        logConsole.info(msg);
    }

    private void progressText(String msg) {
        logConsole.info(msg);
    }

    private void showToast(String msg) {
        Toast.makeText(NativeAdActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        destroyAd();
        if (mATNative != null) {
            mATNative.setAdListener(null);
        }
        logConsole.unbind();
    }

    private void destroyAd() {
        if (mNativeAd != null) {
            mNativeAd.destroy();
            mNativeAd = null;
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
