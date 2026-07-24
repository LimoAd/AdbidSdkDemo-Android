package com.yiman.ad.adbid.ad;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.adbid.media.AdMaterialType;
import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidError;
import com.adbid.media.nativeAd.AdbidNativeAppInfo;
import com.adbid.media.nativeAd.AdbidNativeAd;
import com.adbid.media.nativeAd.AdbidNativeAdView;
import com.adbid.media.nativeAd.AdbidNativeEventListener;
import com.adbid.media.nativeAd.AdbidNativeVideoListener;
import com.adbid.utils.ViewUtils;
import com.bumptech.glide.Glide;
import com.yiman.ad.adbid.AdbidAdLoad;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.bean.TestShopBean;
import com.yiman.ad.log.ToastHub;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public class NativeDrawPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int NEWS = 0;
    public static final int NATIVE = 2;

    private static final int PLACEHOLDER_BG_COLOR = Color.parseColor("#E6EAF2");

    private final List<TestShopBean> list;
    private final ConsoleCallback consoleCallback;

    public NativeDrawPagerAdapter(List<TestShopBean> list, ConsoleCallback consoleCallback) {
        this.list = list;
        this.consoleCallback = consoleCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_native_draw_page, parent, false);
        if (viewType == NATIVE) {
            return new NativeAdHolder(view);
        }
        return new InfoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        TestShopBean item = list.get(position);
        if (holder instanceof NativeAdHolder) {
            bindNativeAdHolder((NativeAdHolder) holder, item);
        } else if (holder instanceof InfoHolder) {
            bindInfoHolder((InfoHolder) holder, item, position);
        }
    }

    private void bindNativeAdHolder(@NonNull NativeAdHolder holder, @NonNull TestShopBean item) {
        bindAssetViews(holder);
        resetMediaArea(holder);
        showRewardEntry(holder.rewardEntry, holder.rewardBtn, true);
        holder.authorTv.setVisibility(VISIBLE);
        holder.authorTv.setText("广告推荐");
        holder.timeTv.setText("广告");
        holder.itemView.setOnClickListener(null);
        if (item.nativeAd == null) {
            showAdPlaceholder(holder);
            return;
        }

        AdbidNativeAd nativeAd = item.nativeAd;
        String imageUrl;
        View mediaView = nativeAd.getMediaView();
        List<View> clickViews = new ArrayList<>();
        if (nativeAd.getAdMaterialType() == AdMaterialType.VIDEO && mediaView != null) {
            holder.testImg.setVisibility(GONE);
            holder.videoLayout.setVisibility(VISIBLE);
            ViewUtils.removeFromParent(mediaView);
            holder.videoLayout.addView(mediaView, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            clickViews.add(holder.videoLayout);
            nativeAd.setVideoListener(new AdbidNativeVideoListener() {
                @Override
                public void onVideoStart() {
                    if (consoleCallback != null) {
                        consoleCallback.printMsg("视频开始播放: " + nativeAd.getTitle());
                    }
                }

                @Override
                public void onVideoPause() {
                    if (consoleCallback != null) {
                        consoleCallback.printMsg("视频暂停播放: " + nativeAd.getTitle());
                    }
                }

                @Override
                public void onVideoResume() {
                    if (consoleCallback != null) {
                        consoleCallback.printMsg("视频继续播放: " + nativeAd.getTitle());
                    }
                }

                @Override
                public void onVideoComplete() {
                    if (consoleCallback != null) {
                        consoleCallback.printMsg("视频播放完成: " + nativeAd.getTitle());
                    }
                }

                @Override
                public void onVideoError(AdbidError error) {
                    if (consoleCallback != null) {
                        consoleCallback.printMsg("视频播放出错: " + error.getMessage());
                    }
                }

                @Override
                public void onVideoProgressUpdate(long current, long total) {
                    if (consoleCallback != null) {
                        consoleCallback.printMsg("视频播放进度: " + current + "/" + total);
                    }
                }
            });
        } else if (nativeAd.getAdMaterialType() == AdMaterialType.IMAGE
                && !TextUtils.isEmpty(nativeAd.getMainImageUrl())) {
            holder.testImg.setVisibility(VISIBLE);
            holder.videoLayout.setVisibility(GONE);
            holder.testImg.setBackground(null);
            imageUrl = nativeAd.getMainImageUrl();
            holder.testImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(holder.testImg).load(imageUrl).into(holder.testImg);
            clickViews.add(holder.testImg);
        } else if (nativeAd.getAdMaterialType() == AdMaterialType.MULTIPLE_IMAGE
                && nativeAd.getImageUrlList() != null
                && !nativeAd.getImageUrlList().isEmpty()) {
            holder.testImg.setVisibility(VISIBLE);
            holder.videoLayout.setVisibility(GONE);
            holder.testImg.setBackground(null);
            imageUrl = nativeAd.getImageUrlList().get(0);
            holder.testImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(holder.testImg).load(imageUrl).into(holder.testImg);
            clickViews.add(holder.testImg);
        } else {
            holder.testImg.setVisibility(VISIBLE);
            holder.videoLayout.setVisibility(GONE);
            holder.testImg.setBackgroundColor(PLACEHOLDER_BG_COLOR);
        }

        holder.titleTv.setText(nativeAd.getTitle());
        bindAdMeta(holder, nativeAd);
        clickViews.add(holder.titleTv);
        if (holder.ctaTv.getVisibility() == VISIBLE) {
            clickViews.add(holder.ctaTv);
        }
        if (holder.iconContainer.getVisibility() == VISIBLE) {
            clickViews.add(holder.iconContainer);
        }
        nativeAd.setEventListener(new AdbidNativeEventListener() {
            @Override
            public void onImpression(@NonNull AdbidNativeAdView view, @NonNull AdbidAdInfo adInfo) {
                if (consoleCallback != null) {
                    consoleCallback.printMsg("信息流曝光成功");
                }
            }

            @Override
            public void onNativeAdClick(@NonNull AdbidNativeAdView view,
                                        @NonNull AdbidAdInfo adInfo) {
                if (consoleCallback != null) {
                    consoleCallback.printMsg("信息流被点击");
                }
            }

            @Override
            public void onAdClose(@Nullable AdbidNativeAdView view) {
                int adapterPosition = holder.getBindingAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    list.get(adapterPosition).nativeAd = null;
                    notifyItemChanged(adapterPosition);
                }
                if (consoleCallback != null) {
                    consoleCallback.onAdClosed();
                    consoleCallback.printMsg("信息流已关闭");
                }
            }
        });
        nativeAd.registerViews(holder.nativeLayout, clickViews, null);
    }

    private void bindInfoHolder(@NonNull InfoHolder holder, @NonNull TestShopBean item,
                                int position) {
        showRewardEntry(holder.rewardEntry, holder.rewardBtn, false);
        holder.videoLayout.setVisibility(GONE);
        holder.videoLayout.removeAllViews();
        holder.authorTv.setVisibility(VISIBLE);
        holder.descTv.setVisibility(GONE);
        holder.adFromTv.setVisibility(GONE);
        holder.ctaTv.setVisibility(GONE);
        holder.iconContainer.setVisibility(GONE);
        holder.sixInfo.setVisibility(GONE);
        holder.itemView.setOnClickListener(v -> {
        });
        holder.titleTv.setText(item.title);
        holder.authorTv.setText(TextUtils.isEmpty(item.shareUser) ? item.author : item.shareUser);
        holder.timeTv.setText(new Date(item.publishTime).toLocaleString());
        holder.testImg.setVisibility(VISIBLE);
        holder.testImg.setBackground(null);
        holder.testImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String imageUrl ="";
        if (TextUtils.isEmpty(imageUrl)) {
            String[] imgUrls = {
                    "https://q4.itc.cn/images01/20240305/07a6c1f603984e69a631288952f5347c.jpeg",
                    "https://q5.itc.cn/images01/20250630/06beb11d47414e19bbf6dc1a062194fe.jpeg",
                    "https://img0.baidu.com/it/u=68977182,2518794064&fm=253&fmt=auto&app=138&f=JPEG?w=439&h=552",
                    "https://q6.itc.cn/q_70/images03/20240411/96dd36d55e1645b083273924998ff7bc.png",
                    "https://img0.baidu.com/it/u=2062646163,2646513048&fm=253&fmt=auto&app=138&f=JPEG?w=916&h=516",
                    "https://pics6.baidu.com/feed/d009b3de9c82d1588ef78a2b4896a1d4bd3e4258.jpeg@f_auto?token=750f89c05337ce46175cccbe8350bc3f"
            };
            imageUrl = imgUrls[position % imgUrls.length];
        }
        Glide.with(holder.testImg).load(imageUrl).into(holder.testImg);
    }

    private void showRewardEntry(@Nullable View rewardEntry, @Nullable View rewardBtn,
                                 boolean visible) {
        if (rewardEntry == null) {
            return;
        }
        rewardEntry.setVisibility(visible ? VISIBLE : GONE);
        if (!visible || rewardBtn == null) {
            return;
        }
        rewardBtn.setOnClickListener(v -> {
            try {
                Context context = v.getContext();
                AdbidAdLoad.getInstance().updateContext(context);
                ToastHub.show(context, "激励广告开始加载...");
                AdbidAdLoad.getInstance().loadReward(true);
            } catch (Exception e) {
                Log.e("AdbidSdkDemo", "load reward failed", e);
                ToastHub.show(v.getContext(), "请先在首页完成广告初始化");
            }
        });
    }

    private void showAdPlaceholder(@NonNull NativeAdHolder holder) {
        holder.videoLayout.setVisibility(GONE);
        holder.testImg.setVisibility(VISIBLE);
        holder.testImg.setImageDrawable(null);
        holder.testImg.setBackgroundColor(PLACEHOLDER_BG_COLOR);
        holder.testImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.titleTv.setText("广告预加载中...");
        holder.authorTv.setText("广告推荐");
        holder.timeTv.setText("广告占位");
        holder.descTv.setVisibility(VISIBLE);
        holder.descTv.setText("全屏素材加载中，请稍候");
        holder.adFromTv.setVisibility(GONE);
        holder.ctaTv.setVisibility(GONE);
        holder.iconContainer.removeAllViews();
        holder.iconContainer.setVisibility(GONE);
        holder.sixInfo.setVisibility(GONE);
    }
    private void bindAssetViews(@NonNull NativeAdHolder holder) {
        holder.nativeLayout.setTitleView(holder.titleTv);
        holder.nativeLayout.setDescView(holder.descTv);
        holder.nativeLayout.setAdIconView(holder.iconContainer);
        holder.nativeLayout.setCtaView(holder.ctaTv);
        holder.nativeLayout.setMainImageView(holder.testImg);
        holder.nativeLayout.setMutiImageView(holder.videoLayout);
        holder.nativeLayout.setAdFromView(holder.adFromTv);
    }

    private void resetMediaArea(@NonNull NativeAdHolder holder) {
        Glide.with(holder.testImg).clear(holder.testImg);
        holder.testImg.setImageDrawable(null);
        holder.testImg.setVisibility(VISIBLE);
        holder.testImg.setBackground(null);
        holder.videoLayout.removeAllViews();
        holder.videoLayout.setVisibility(GONE);
        holder.descTv.setText(null);
        holder.descTv.setVisibility(GONE);
        holder.adFromTv.setText(null);
        holder.adFromTv.setVisibility(GONE);
        holder.ctaTv.setText(null);
        holder.ctaTv.setVisibility(GONE);
        holder.iconContainer.removeAllViews();
        holder.iconContainer.setVisibility(GONE);
        holder.sixInfo.setVisibility(GONE);
    }

    private void bindAdMeta(@NonNull NativeAdHolder holder, @NonNull AdbidNativeAd nativeAd) {
        String descriptionText = nativeAd.getDescriptionText();
        if (TextUtils.isEmpty(descriptionText)) {
            holder.descTv.setVisibility(GONE);
        } else {
            holder.descTv.setText(descriptionText);
            holder.descTv.setVisibility(VISIBLE);
        }

        String adFrom = nativeAd.getAdFrom();
        if (TextUtils.isEmpty(adFrom)) {
            holder.adFromTv.setVisibility(GONE);
        } else {
            holder.adFromTv.setText(adFrom);
            holder.adFromTv.setVisibility(VISIBLE);
        }

        String callToAction = nativeAd.getCallToAction();
        if (TextUtils.isEmpty(callToAction)) {
            holder.ctaTv.setVisibility(GONE);
        } else {
            holder.ctaTv.setText(callToAction);
            holder.ctaTv.setVisibility(VISIBLE);
        }

        bindAdIcon(holder, nativeAd);
        bindSixElements(holder, nativeAd.getNativeAppInfo());
    }

    private void bindAdIcon(@NonNull NativeAdHolder holder, @NonNull AdbidNativeAd nativeAd) {
        holder.iconContainer.removeAllViews();
        View adIconView = nativeAd.getIconView();
        if (adIconView != null) {
            ViewUtils.removeFromParent(adIconView);
            holder.iconContainer.addView(adIconView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            holder.iconContainer.setVisibility(VISIBLE);
            return;
        }
        String iconImageUrl = nativeAd.getIconImgUrl();
        if (TextUtils.isEmpty(iconImageUrl)) {
            holder.iconContainer.setVisibility(GONE);
            return;
        }
        ImageView iconView = new ImageView(holder.iconContainer.getContext());
        iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.iconContainer.addView(iconView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Glide.with(holder.iconContainer).load(iconImageUrl).into(iconView);
        holder.iconContainer.setVisibility(VISIBLE);
    }

    private void bindSixElements(@NonNull NativeAdHolder holder, @Nullable AdbidNativeAppInfo appInfo) {
        if (appInfo == null) {
            holder.sixInfo.setVisibility(GONE);
            return;
        }
        bindChip(holder.appNameTv, appInfo.getAppName());
        bindChip(holder.developerTv, appInfo.getPublisher());
        bindChip(holder.versionTv, appInfo.getAppVersion());
        bindChip(holder.functionTv, "功能", appInfo.getFunctionUrl());
        bindChip(holder.privacyTv, "隐私", appInfo.getAppPrivacyUrl());
        bindChip(holder.permissionTv, "权限", appInfo.getAppPermissonUrl());

        boolean hasVisibleChip = holder.appNameTv.getVisibility() == VISIBLE
                || holder.developerTv.getVisibility() == VISIBLE
                || holder.versionTv.getVisibility() == VISIBLE
                || holder.functionTv.getVisibility() == VISIBLE
                || holder.privacyTv.getVisibility() == VISIBLE
                || holder.permissionTv.getVisibility() == VISIBLE;
        holder.sixInfo.setVisibility(hasVisibleChip ? VISIBLE : GONE);
    }

    private void bindChip(@NonNull TextView view, @Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            view.setText(null);
            view.setVisibility(GONE);
        } else {
            view.setText(text);
            view.setVisibility(VISIBLE);
        }
        view.setOnClickListener(null);
    }

    private void bindChip(@NonNull TextView view, @NonNull String text, @Nullable String actionUrl) {
        view.setText(text);
        if (TextUtils.isEmpty(actionUrl)) {
            view.setVisibility(GONE);
            view.setOnClickListener(null);
        } else {
            view.setVisibility(VISIBLE);
            view.setOnClickListener(v -> openUrl(v.getContext(), actionUrl));
        }
    }

    private void openUrl(@Nullable Context context, @NonNull String url) {
        if (context == null) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Throwable ignored) {
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).itemType;
    }

    static class InfoHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView authorTv;
        TextView timeTv;
        TextView descTv;
        TextView adFromTv;
        TextView ctaTv;
        FrameLayout iconContainer;
        HorizontalScrollView sixInfo;
        ImageView testImg;
        RelativeLayout videoLayout;
        View rewardEntry;
        View rewardBtn;

        InfoHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            authorTv = itemView.findViewById(R.id.authorTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            descTv = itemView.findViewById(R.id.descTv);
            adFromTv = itemView.findViewById(R.id.adFromTv);
            ctaTv = itemView.findViewById(R.id.ctaTv);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            sixInfo = itemView.findViewById(R.id.six_info);
            testImg = itemView.findViewById(R.id.testImg);
            videoLayout = itemView.findViewById(R.id.videoLayout);
            rewardEntry = itemView.findViewById(R.id.fl_reward_entry);
            rewardBtn = itemView.findViewById(R.id.btn_reward_video);
        }
    }

    static class NativeAdHolder extends RecyclerView.ViewHolder {
        AdbidNativeAdView nativeLayout;
        TextView titleTv;
        TextView authorTv;
        TextView timeTv;
        TextView descTv;
        TextView adFromTv;
        TextView ctaTv;
        TextView appNameTv;
        TextView functionTv;
        TextView privacyTv;
        TextView permissionTv;
        TextView developerTv;
        TextView versionTv;
        FrameLayout iconContainer;
        HorizontalScrollView sixInfo;
        ImageView testImg;
        RelativeLayout videoLayout;
        View rewardEntry;
        View rewardBtn;

        NativeAdHolder(@NonNull View itemView) {
            super(itemView);
            nativeLayout = itemView.findViewById(R.id.parent_layout);
            titleTv = itemView.findViewById(R.id.titleTv);
            authorTv = itemView.findViewById(R.id.authorTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            descTv = itemView.findViewById(R.id.descTv);
            adFromTv = itemView.findViewById(R.id.adFromTv);
            ctaTv = itemView.findViewById(R.id.ctaTv);
            appNameTv = itemView.findViewById(R.id.app_name_test);
            functionTv = itemView.findViewById(R.id.function_test);
            privacyTv = itemView.findViewById(R.id.privacy_test);
            permissionTv = itemView.findViewById(R.id.permission_test);
            developerTv = itemView.findViewById(R.id.developer_test);
            versionTv = itemView.findViewById(R.id.version_test);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            sixInfo = itemView.findViewById(R.id.six_info);
            testImg = itemView.findViewById(R.id.testImg);
            videoLayout = itemView.findViewById(R.id.videoLayout);
            rewardEntry = itemView.findViewById(R.id.fl_reward_entry);
            rewardBtn = itemView.findViewById(R.id.btn_reward_video);
        }
    }

    public interface ConsoleCallback {
        void printMsg(String s);

        void onAdClosed();
    }
}
