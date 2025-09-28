package com.yiman.ad.adbid.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adbid.media.nativeAd.AdbidNativeAdView;
import com.adbid.media.nativeAd.AdbidNativeAppInfo;
import com.adbid.media.nativeAd.AdbidNativeMaterial;
import com.adbid.media.nativeAd.AdbidNativePrepareInfo;
import com.bumptech.glide.Glide;
import com.yiman.ad.adbid.R;


import java.util.ArrayList;
import java.util.List;

public class SelfRenderViewUtil {
    private static final String TAG = SelfRenderViewUtil.class.getSimpleName();

    public static void bindSelfRenderView(Context context, AdbidNativeMaterial adMaterial,
                                          ViewGroup selfRenderView,
                                          AdbidNativePrepareInfo nativePrepareInfo) {

        int padding = dip2px(context, 5);
        selfRenderView.setPadding(padding, padding, padding, padding);
        TextView titleView = (TextView) selfRenderView.findViewById(R.id.native_ad_title);
        TextView descView = (TextView) selfRenderView.findViewById(R.id.native_ad_desc);
        TextView ctaView = (TextView) selfRenderView.findViewById(R.id.native_ad_install_btn);
        TextView adFromView = (TextView) selfRenderView.findViewById(R.id.native_ad_from);
        FrameLayout iconArea = (FrameLayout) selfRenderView.findViewById(R.id.native_ad_image);
        FrameLayout contentArea =
                (FrameLayout) selfRenderView.findViewById(R.id.native_ad_content_image_area);
        final ImageView logoView = (ImageView) selfRenderView.findViewById(R.id.native_ad_logo);
        View closeView = selfRenderView.findViewById(R.id.native_ad_close);
        FrameLayout shakeViewContainer =
                (FrameLayout) selfRenderView.findViewById(R.id.native_ad_shake_view_container);
        FrameLayout slideViewContainer =
                (FrameLayout) selfRenderView.findViewById(R.id.native_ad_slide_view_container);
        FrameLayout adLogoContainer =
                selfRenderView.findViewById(R.id.native_ad_logo_container);   //v6.1.52+

        // bind view
        if (nativePrepareInfo == null) {
            nativePrepareInfo = new AdbidNativePrepareInfo();
        }
        nativePrepareInfo.setAdContainerView(selfRenderView);
        List<View> clickViewList = new ArrayList<>();//click views


        String title = adMaterial.getTitle();
        // title
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
            nativePrepareInfo.setTitleView(titleView);//bind title
            clickViewList.add(titleView);
            titleView.setVisibility(View.VISIBLE);
        } else {
            titleView.setVisibility(View.GONE);
        }


        String descriptionText = adMaterial.getDescriptionText();
        if (!TextUtils.isEmpty(descriptionText)) {
            // desc
            descView.setText(descriptionText);
            nativePrepareInfo.setDescView(descView);//bind desc
            clickViewList.add(descView);
            descView.setVisibility(View.VISIBLE);
        } else {
            descView.setVisibility(View.GONE);
        }

        // icon
        View adIconView = adMaterial.getIconView();
        String iconImageUrl = adMaterial.getIconImgUrl();
        iconArea.removeAllViews();
        final ImageView iconView = new ImageView(context);
        if (adIconView != null) {
            iconArea.addView(adIconView);
            nativePrepareInfo.setIconView(adIconView);//bind icon
            clickViewList.add(adIconView);
            iconArea.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(iconImageUrl)) {
            iconArea.addView(iconView);
            Glide.with(context).load(iconImageUrl).fitCenter()// 圆形裁剪
                    .into(iconView);
            nativePrepareInfo.setIconView(iconView);//bind icon
            clickViewList.add(iconView);
            iconArea.setVisibility(View.VISIBLE);
        } else {
            iconArea.setVisibility(View.INVISIBLE);
        }

        // cta button
        String callToActionText = adMaterial.getCallToAction();
        if (!TextUtils.isEmpty(callToActionText)) {
            ctaView.setText(callToActionText);
            nativePrepareInfo.setCtaView(ctaView);//bind cta button
            clickViewList.add(ctaView);
            ctaView.setVisibility(View.VISIBLE);
        } else {
            ctaView.setVisibility(View.GONE);
        }


        // media view
        View mediaView = adMaterial.getMediaView();
        FrameLayout.LayoutParams mainImageParam =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
        if (mediaView == null) {
            ViewTreeObserver viewTreeObserver = selfRenderView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override public void onGlobalLayout() {
                            // 移除监听器
                            selfRenderView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            int realMainImageWidth =
                                    selfRenderView.getWidth() - dip2px(context, 10);
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

        List<String> imageList = adMaterial.getImageUrlList();

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
        } else if (!TextUtils.isEmpty(adMaterial.getMainImageUrl())) {
            ImageView imageView = new ImageView(context);
            Glide.with(context).load(adMaterial.getMainImageUrl())// 圆形裁剪
                    .into(imageView);
            imageView.setLayoutParams(mainImageParam);
            contentArea.addView(imageView, mainImageParam);

            nativePrepareInfo.setMainImageView(imageView);//bind main image
            clickViewList.add(imageView);
            contentArea.setVisibility(View.VISIBLE);
        } else {
            contentArea.removeAllViews();
            contentArea.setVisibility(View.GONE);
        }


        //Ad Logo
        View adLogoView = adMaterial.getLogoView();
        if (adLogoView != null) {
            adLogoContainer.setVisibility(View.VISIBLE);
            adLogoContainer.removeAllViews();
            adLogoContainer.addView(adLogoView);
        } else {
            adLogoContainer.setVisibility(View.GONE);

            String adChoiceIconUrl = adMaterial.getAdChoiceIconUrl();
            Bitmap adLogoBitmap = adMaterial.getAdLogoBitmap();
            if (!TextUtils.isEmpty(adChoiceIconUrl)) {
                Glide.with(context).load(adChoiceIconUrl)// 圆形裁剪
                        .into(logoView);
                nativePrepareInfo.setAdLogoView(logoView);//bind ad choice
                logoView.setVisibility(View.VISIBLE);
            } else if (adLogoBitmap != null) {
                logoView.setImageBitmap(adLogoBitmap);
                logoView.setVisibility(View.VISIBLE);
            } else {
                logoView.setImageBitmap(null);
                logoView.setVisibility(View.GONE);
            }
        }

        String adFrom = adMaterial.getAdFrom();

        // ad from
        if (!TextUtils.isEmpty(adFrom)) {
            adFromView.setText(adFrom);
            adFromView.setVisibility(View.VISIBLE);
        } else {
            adFromView.setVisibility(View.GONE);
        }
        nativePrepareInfo.setAdFromView(adFromView);//bind ad from

        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(dip2px(context, 40), dip2px(context, 10));//ad choice
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        nativePrepareInfo.setChoiceViewLayoutParams(layoutParams);//bind layout params for ad choice
        nativePrepareInfo.setCloseView(closeView);//bind close button

        nativePrepareInfo.setClickViewList(clickViewList);//bind click view list


        View sixInfoView = selfRenderView.findViewById(R.id.six_info);
        AdbidNativeAppInfo adAppInfo = adMaterial.getNativeAppInfo();
        if (adAppInfo != null) {
            sixInfoView.setVisibility(View.VISIBLE);
            TextView functionTextView = sixInfoView.findViewById(R.id.function_test);
            TextView developerTextView = sixInfoView.findViewById(R.id.developer_test);
            TextView appnameTextView = sixInfoView.findViewById(R.id.app_name_test);
            TextView versionTextView = sixInfoView.findViewById(R.id.version_test);
            TextView privacyTextView = sixInfoView.findViewById(R.id.privacy_test);
            TextView permissionTextView = sixInfoView.findViewById(R.id.permission_test);
            List<View> appInfoClickViewList = new ArrayList<>();
            List<View> privacyClickViewList = new ArrayList<>();
            List<View> permissionClickViewList = new ArrayList<>();
            //获取应用名称，可以使用TitleView显示AppName
            appnameTextView.setText(
                    TextUtils.isEmpty(adAppInfo.getAppName()) ? "" : adAppInfo.getAppName());
            //获取获取开发者公司名称
            developerTextView.setText(
                    TextUtils.isEmpty(adAppInfo.getPublisher()) ? "" : adAppInfo.getPublisher());
            //获取应用版本号
            versionTextView.setText(
                    TextUtils.isEmpty(adAppInfo.getAppVersion()) ? "" : adAppInfo.getAppVersion());
            appInfoClickViewList.add(developerTextView);
            appInfoClickViewList.add(versionTextView);
            //获取产品功能url
            appInfoClickViewList.add(functionTextView);
            if (!TextUtils.isEmpty(adAppInfo.getFunctionUrl())) {
                functionTextView.setVisibility(View.VISIBLE);
                setOpenUrlClickListener(functionTextView, adAppInfo.getFunctionUrl());
            } else {
                functionTextView.setOnClickListener(null);
                functionTextView.setVisibility(View.GONE);
            }
            //获取隐私协议url
            privacyClickViewList.add(privacyTextView);
            if (!TextUtils.isEmpty(adAppInfo.getAppPrivacyUrl())) {
                privacyTextView.setVisibility(View.VISIBLE);
                setOpenUrlClickListener(privacyTextView, adAppInfo.getAppPrivacyUrl());
            } else {
                privacyTextView.setVisibility(View.GONE);
                privacyTextView.setOnClickListener(null);
            }
            //获取权限列表url
            permissionClickViewList.add(permissionTextView);
            if (!TextUtils.isEmpty(adAppInfo.getAppPermissonUrl())) {
                permissionTextView.setVisibility(View.VISIBLE);
                setOpenUrlClickListener(permissionTextView, adAppInfo.getAppPermissonUrl());
            } else {
                permissionTextView.setVisibility(View.GONE);
                permissionTextView.setOnClickListener(null);
            }
            //绑定查看权限、隐私、产品功能点击View集合
            nativePrepareInfo.setAppInfoClickViewList(
                    appInfoClickViewList);
            nativePrepareInfo.setPermissionClickViewList(
                    permissionClickViewList);
            nativePrepareInfo.setPrivacyClickViewList(
                    privacyClickViewList);
        } else {
            sixInfoView.setVisibility(View.GONE);
        }
    }


    private static void setOpenUrlClickListener(View view, final String url) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Context context = view.getContext();
                    if (context != null) {
                        context.startActivity(intent);
                    }
                } catch (Throwable e2) {
                    e2.printStackTrace();
                }
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

}
