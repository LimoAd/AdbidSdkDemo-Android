package com.yiman.ad.adbid.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adbid.media.nativeAd.AdbidNativeAd;
import com.adbid.media.nativeAd.AdbidNativeAdView;
import com.adbid.media.nativeAd.AdbidNativeAppInfo;
import com.bumptech.glide.Glide;
import com.yiman.ad.adbid.R;

import java.util.ArrayList;
import java.util.List;

public class BindViewUtils {

    public static void registerView(Context context, @NonNull AdbidNativeAd nativeAd,
                                    @NonNull AdbidNativeAdView nativeAdView) {


        TextView titleView = nativeAdView.findViewById(R.id.native_ad_title);
        nativeAdView.setTitleView(titleView);
        TextView descView = nativeAdView.findViewById(R.id.native_ad_desc);
        nativeAdView.setDescView(descView);
        TextView ctaView = nativeAdView.findViewById(R.id.native_ad_install_btn);
        nativeAdView.setCtaView(ctaView);
        TextView adFromView = nativeAdView.findViewById(R.id.native_ad_from);
        nativeAdView.setAdFromView(adFromView);
        FrameLayout iconArea = nativeAdView.findViewById(R.id.native_ad_image);
        nativeAdView.setAdIconView(iconArea);
        FrameLayout contentArea = nativeAdView.findViewById(R.id.native_ad_content_image_area);
        nativeAdView.setMutiImageView(contentArea);
        final ImageView logoView = nativeAdView.findViewById(R.id.native_ad_logo);
        nativeAdView.setLogoView(logoView);
        View closeView = nativeAdView.findViewById(R.id.native_ad_close);
        FrameLayout adLogoContainer =
                nativeAdView.findViewById(R.id.native_ad_logo_container);   //v6.1.52+
        //click view
        List<View> clickViewList = new ArrayList<>();
        // title
        String title = nativeAd.getTitle();

        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
            clickViewList.add(titleView);
            titleView.setVisibility(View.VISIBLE);
        } else {
            titleView.setVisibility(View.GONE);
        }

        // desc
        String descriptionText = nativeAd.getDescriptionText();
        if (!TextUtils.isEmpty(descriptionText)) {
            descView.setText(descriptionText);
            clickViewList.add(descView);
            descView.setVisibility(View.VISIBLE);
        } else {
            descView.setVisibility(View.GONE);
        }

        // icon
        View adIconView = nativeAd.getIconView();
        String iconImageUrl = nativeAd.getIconImgUrl();
        iconArea.removeAllViews();
        final ImageView iconView = new ImageView(context);
        if (adIconView != null) {
            iconArea.addView(adIconView);
            clickViewList.add(adIconView);
            iconArea.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(iconImageUrl)) {
            iconArea.addView(iconView);
            Glide.with(context).load(iconImageUrl).fitCenter()// 圆形裁剪
                    .into(iconView);
            clickViewList.add(iconView);
            iconArea.setVisibility(View.VISIBLE);
        } else {
            iconArea.setVisibility(View.INVISIBLE);
        }

        // cta button
        String callToActionText = nativeAd.getCallToAction();
        if (!TextUtils.isEmpty(callToActionText)) {
            ctaView.setText(callToActionText);
            clickViewList.add(ctaView);
            ctaView.setVisibility(View.VISIBLE);
        } else {
            ctaView.setVisibility(View.GONE);
        }

        // media view
        View mediaView = nativeAd.getMediaView();
        FrameLayout.LayoutParams mainImageParam =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
        if (mediaView == null) {
            ViewTreeObserver viewTreeObserver = nativeAdView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override public void onGlobalLayout() {
                            // 移除监听器
                            nativeAdView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int realMainImageWidth = nativeAdView.getWidth() - dip2px(context, 10);
                            mainImageParam.width = FrameLayout.LayoutParams.MATCH_PARENT;
                            mainImageParam.height = realMainImageWidth * 600 / 1024;
                        }
                    });
        } else {
            int realMainImageWidth =
                    context.getResources().getDisplayMetrics().widthPixels - dip2px(context, 10);
            if (context.getResources().getDisplayMetrics().widthPixels >
                    context.getResources().getDisplayMetrics().heightPixels) {//Horizontal screen
                realMainImageWidth = context.getResources().getDisplayMetrics().widthPixels -
                        dip2px(context, 10) - dip2px(context, 330) - dip2px(context, 130);
            }

            mainImageParam.width = FrameLayout.LayoutParams.MATCH_PARENT;
            mainImageParam.height = realMainImageWidth * 600 / 1024;
        }

        List<String> imageList = nativeAd.getImageUrlList();
        contentArea.removeAllViews();
        if (mediaView != null) {
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }
            mainImageParam.gravity = Gravity.CENTER;
            mediaView.setLayoutParams(mainImageParam);
            contentArea.addView(mediaView, mainImageParam);
            clickViewList.add(mediaView);
            contentArea.setVisibility(View.VISIBLE);
        } else if (imageList != null && imageList.size() > 1) {
            createDynamicImageLayout(contentArea, imageList, context, 3);
        } else if (!TextUtils.isEmpty(nativeAd.getMainImageUrl())) {
            ImageView imageView = new ImageView(context);
            Glide.with(context).load(nativeAd.getMainImageUrl())// 圆形裁剪
                    .into(imageView);
            imageView.setLayoutParams(mainImageParam);
            contentArea.addView(imageView, mainImageParam);
            clickViewList.add(imageView);
            contentArea.setVisibility(View.VISIBLE);
        } else {
            contentArea.removeAllViews();
            contentArea.setVisibility(View.GONE);
        }

        //Ad Logo
        View adLogoView = nativeAd.getLogoView();
        if (adLogoView != null) {
            adLogoContainer.setVisibility(View.VISIBLE);
            adLogoContainer.removeAllViews();
            adLogoContainer.addView(adLogoView);
        } else {
            adLogoContainer.setVisibility(View.GONE);
            String adChoiceIconUrl = nativeAd.getAdChoiceIconUrl();
            Bitmap adLogoBitmap = nativeAd.getAdLogoBitmap();
            if (!TextUtils.isEmpty(adChoiceIconUrl)) {
                Glide.with(context).load(adChoiceIconUrl)// 圆形裁剪
                        .into(logoView);
                logoView.setVisibility(View.VISIBLE);
            } else if (adLogoBitmap != null) {
                logoView.setImageBitmap(adLogoBitmap);
                logoView.setVisibility(View.VISIBLE);
            } else {
                logoView.setImageBitmap(null);
                logoView.setVisibility(View.GONE);
            }
        }

        // ad from
        String adFrom = nativeAd.getAdFrom();
        if (!TextUtils.isEmpty(adFrom)) {
            adFromView.setText(adFrom);
            adFromView.setVisibility(View.VISIBLE);
        } else {
            adFromView.setVisibility(View.GONE);
        }

        nativeAd.registerViews(nativeAdView, clickViewList, closeView);

        //应用六要素
        initSixAppInfo(nativeAd.getNativeAppInfo(), nativeAdView);
    }

    private static void initSixAppInfo(AdbidNativeAppInfo adAppInfo, View nativeAdView) {
        View sixInfoView = nativeAdView.findViewById(R.id.six_info);
        if (adAppInfo != null) {
            sixInfoView.setVisibility(View.VISIBLE);
            TextView functionTextView = sixInfoView.findViewById(R.id.function_test);
            TextView developerTextView = sixInfoView.findViewById(R.id.developer_test);
            TextView appnameTextView = sixInfoView.findViewById(R.id.app_name_test);
            TextView versionTextView = sixInfoView.findViewById(R.id.version_test);
            TextView privacyTextView = sixInfoView.findViewById(R.id.privacy_test);
            TextView permissionTextView = sixInfoView.findViewById(R.id.permission_test);

            //获取应用名称，可以使用TitleView显示AppName
            appnameTextView.setText(
                    TextUtils.isEmpty(adAppInfo.getAppName()) ? "" : adAppInfo.getAppName());
            //获取获取开发者公司名称
            developerTextView.setText(
                    TextUtils.isEmpty(adAppInfo.getPublisher()) ? "" : adAppInfo.getPublisher());
            //获取应用版本号
            versionTextView.setText(
                    TextUtils.isEmpty(adAppInfo.getAppVersion()) ? "" : adAppInfo.getAppVersion());

            if (!TextUtils.isEmpty(adAppInfo.getFunctionUrl())) {
                functionTextView.setVisibility(View.VISIBLE);
                setOpenUrlClickListener(functionTextView, adAppInfo.getFunctionUrl());
            } else {
                functionTextView.setOnClickListener(null);
                functionTextView.setVisibility(View.GONE);
            }
            //获取隐私协议url
            if (!TextUtils.isEmpty(adAppInfo.getAppPrivacyUrl())) {
                privacyTextView.setVisibility(View.VISIBLE);
                setOpenUrlClickListener(privacyTextView, adAppInfo.getAppPrivacyUrl());
            } else {
                privacyTextView.setVisibility(View.GONE);
                privacyTextView.setOnClickListener(null);
            }
            //获取权限列表url
            if (!TextUtils.isEmpty(adAppInfo.getAppPermissonUrl())) {
                permissionTextView.setVisibility(View.VISIBLE);
                setOpenUrlClickListener(permissionTextView, adAppInfo.getAppPermissonUrl());
            } else {
                permissionTextView.setVisibility(View.GONE);
                permissionTextView.setOnClickListener(null);
            }
        } else {
            sixInfoView.setVisibility(View.GONE);
        }
    }


    private static void setOpenUrlClickListener(View view, final String url) {
        view.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Context context = view.getContext();
                if (context != null) {
                    context.startActivity(intent);
                }
            } catch (Throwable e2) {
                System.err.println(e2.getMessage());
            }
        });

    }


    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    public static void createDynamicImageLayout(ViewGroup container, List<String> imgUrls,
                                                Context context, int columnCount) {
        // 清除容器原有视图
        container.removeAllViews();
        // 设置默认列数为3
        if (columnCount <= 0) {
            columnCount = 3;
        }
        // 计算屏幕宽度和每张图片的宽度
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int imageWidth = screenWidth / columnCount;

        // 创建外层垂直布局
        LinearLayout verticalLayout = new LinearLayout(context);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);
        verticalLayout.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        // 计算需要的行数
        int rowCount = (int) Math.ceil((double) imgUrls.size() / columnCount);

        for (int i = 0; i < rowCount; i++) {
            // 创建水平行布局
            LinearLayout horizontalLayout = new LinearLayout(context);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < columnCount; j++) {
                int position = i * columnCount + j;
                if (position >= imgUrls.size()) {
                    break;
                }

                // 创建ImageView
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(imageWidth, imageWidth); // 正方形图片
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // 使用Glide加载图片
                Glide.with(context).load(imgUrls.get(position)).into(imageView);


                horizontalLayout.addView(imageView);
            }

            verticalLayout.addView(horizontalLayout);
        }

        container.addView(verticalLayout);
    }

   /* public static void registerCustomViewView(NativeAdActivity context, AdbidNativeAd nativeAd,
                                              AdbidNativeAdView nativeAdView) {
        TextView titleView = nativeAdView.findViewById(R.id.native_ad_title);
        TextView descView = nativeAdView.findViewById(R.id.native_ad_desc);
        TextView ctaView = nativeAdView.findViewById(R.id.native_ad_install_btn);
        TextView adFromView = nativeAdView.findViewById(R.id.native_ad_from);
        FrameLayout iconArea = nativeAdView.findViewById(R.id.native_ad_image);
        FrameLayout contentArea = nativeAdView.findViewById(R.id.native_ad_content_image_area);
        final ImageView logoView = nativeAdView.findViewById(R.id.native_ad_logo);
        View closeView = nativeAdView.findViewById(R.id.native_ad_close);
        FrameLayout adLogoContainer =
                nativeAdView.findViewById(R.id.native_ad_logo_container);   //v6.1.52+
        //click view
        List<View> clickViewList = new ArrayList<>();
        // title
        String title = nativeAd.getTitle();

        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
            clickViewList.add(titleView);
            titleView.setVisibility(View.VISIBLE);
        } else {
            titleView.setVisibility(View.GONE);
        }

        // desc
        String descriptionText = nativeAd.getDescriptionText();
        if (!TextUtils.isEmpty(descriptionText)) {
            descView.setText(descriptionText);
            clickViewList.add(descView);
            descView.setVisibility(View.VISIBLE);
        } else {
            descView.setVisibility(View.GONE);
        }

        // icon
        View adIconView = nativeAd.getIconView();
        String iconImageUrl = nativeAd.getIconImgUrl();
        iconArea.removeAllViews();
        final ImageView iconView = new ImageView(context);
        if (adIconView != null) {
            iconArea.addView(adIconView);
            clickViewList.add(adIconView);
            iconArea.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(iconImageUrl)) {
            iconArea.addView(iconView);
            Glide.with(context).load(iconImageUrl).fitCenter()// 圆形裁剪
                    .into(iconView);
            clickViewList.add(iconView);
            iconArea.setVisibility(View.VISIBLE);
        } else {
            iconArea.setVisibility(View.INVISIBLE);
        }

        // cta button
        String callToActionText = nativeAd.getCallToAction();
        if (!TextUtils.isEmpty(callToActionText)) {
            ctaView.setText(callToActionText);
            clickViewList.add(ctaView);
            ctaView.setVisibility(View.VISIBLE);
        } else {
            ctaView.setVisibility(View.GONE);
        }

        // media view
        View mediaView = nativeAd.getMediaView();
        FrameLayout.LayoutParams mainImageParam =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
        if (mediaView == null) {
            ViewTreeObserver viewTreeObserver = nativeAdView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override public void onGlobalLayout() {
                            // 移除监听器
                            nativeAdView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int realMainImageWidth = nativeAdView.getWidth() - dip2px(context, 10);
                            mainImageParam.width = FrameLayout.LayoutParams.MATCH_PARENT;
                            mainImageParam.height = realMainImageWidth * 600 / 1024;
                        }
                    });
        } else {
            int realMainImageWidth =
                    context.getResources().getDisplayMetrics().widthPixels - dip2px(context, 10);
            if (context.getResources().getDisplayMetrics().widthPixels >
                    context.getResources().getDisplayMetrics().heightPixels) {//Horizontal screen
                realMainImageWidth = context.getResources().getDisplayMetrics().widthPixels -
                        dip2px(context, 10) - dip2px(context, 330) - dip2px(context, 130);
            }

            mainImageParam.width = FrameLayout.LayoutParams.MATCH_PARENT;
            mainImageParam.height = realMainImageWidth * 600 / 1024;
        }

        List<String> imageList = nativeAd.getImageUrlList();
        contentArea.removeAllViews();
        if (mediaView != null) {
            contentArea.setBackgroundColor(Color.parseColor("#00000000"));
            //自定义视频组件
            if (nativeAd.getCustomVideo() != null) {
                mainImageParam.gravity = Gravity.CENTER;
                LimoVideoPlayerView videoPlayerView = new LimoVideoPlayerView(context);
                videoPlayerView.setDataSource(Uri.parse(LimoVideoCache.getInstance(context)
                        .getCacheUrl(nativeAd.getCustomVideo().getVideoUrl())));

                videoPlayerView.setVideoPlayerListener(
                        new LimoVideoPlayerView.VideoPlayerListener() {
                            @Override public void onPrepared(MediaPlayer mediaPlayer) {

                            }
                            @Override public void onStart(MediaPlayer mediaPlayer) {
                                nativeAd.getCustomVideo().reportVideoStart();
                            }

                            @Override public void onPause(MediaPlayer mediaPlayer) {
                                nativeAd.getCustomVideo().reportVideoPause();
                            }

                            @Override public void onStop(MediaPlayer mediaPlayer) {
                                nativeAd.getCustomVideo().reportVideoPause();
                            }

                            @Override public void onCompletion(MediaPlayer mediaPlayer) {
                                nativeAd.getCustomVideo().reportVideoComplete();
                            }

                            @Override public void onError(MediaPlayer mediaPlayer, int i, int i1) {
                                nativeAd.getCustomVideo().reportVideoError();
                            }

                            @Override
                            public void onProgressUpdate(MediaPlayer mediaPlayer, int i, int i1) {
                                nativeAd.getCustomVideo().reportVideoProgress(i,i1);
                            }

                            @Override
                            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

                            }
                        });
                videoPlayerView.play();
                contentArea.addView(videoPlayerView, mainImageParam);
                clickViewList.add(mediaView);
                contentArea.setVisibility(View.VISIBLE);
            }
        } else if (imageList != null && imageList.size() > 1) {
            createDynamicImageLayout(contentArea, imageList, context, 3);
        } else if (!TextUtils.isEmpty(nativeAd.getMainImageUrl())) {
            ImageView imageView = new ImageView(context);
            Glide.with(context).load(nativeAd.getMainImageUrl())// 圆形裁剪
                    .into(imageView);
            imageView.setLayoutParams(mainImageParam);
            contentArea.addView(imageView, mainImageParam);
            clickViewList.add(imageView);
            contentArea.setVisibility(View.VISIBLE);
        } else {
            contentArea.removeAllViews();
            contentArea.setVisibility(View.GONE);
        }

        //Ad Logo
        View adLogoView = nativeAd.getLogoView();
        if (adLogoView != null) {
            adLogoContainer.setVisibility(View.VISIBLE);
            adLogoContainer.removeAllViews();
            adLogoContainer.addView(adLogoView);
        } else {
            adLogoContainer.setVisibility(View.GONE);
            String adChoiceIconUrl = nativeAd.getAdChoiceIconUrl();
            Bitmap adLogoBitmap = nativeAd.getAdLogoBitmap();
            if (!TextUtils.isEmpty(adChoiceIconUrl)) {
                Glide.with(context).load(adChoiceIconUrl)// 圆形裁剪
                        .into(logoView);
                logoView.setVisibility(View.VISIBLE);
            } else if (adLogoBitmap != null) {
                logoView.setImageBitmap(adLogoBitmap);
                logoView.setVisibility(View.VISIBLE);
            } else {
                logoView.setImageBitmap(null);
                logoView.setVisibility(View.GONE);
            }
        }

        // ad from
        String adFrom = nativeAd.getAdFrom();
        if (!TextUtils.isEmpty(adFrom)) {
            adFromView.setText(adFrom);
            adFromView.setVisibility(View.VISIBLE);
        } else {
            adFromView.setVisibility(View.GONE);
        }

        nativeAd.registerViews(nativeAdView, clickViewList, closeView);

        //应用六要素
        initSixAppInfo(nativeAd.getNativeAppInfo(), nativeAdView);
    }*/
}


