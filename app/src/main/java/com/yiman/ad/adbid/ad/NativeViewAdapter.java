package com.yiman.ad.adbid.ad;


import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidError;
import com.adbid.media.nativeAd.AdbidNativeAd;
import com.adbid.media.nativeAd.AdbidNativeAdView;
import com.adbid.media.nativeAd.AdbidNativeEventListener;
import com.adbid.media.nativeAd.AdbidNativeVideoListener;
import com.adbid.utils.ViewUtils;
import com.bumptech.glide.Glide;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.bean.TestShopBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused") public class NativeViewAdapter extends RecyclerView.Adapter {
    public final static int NEWS = 0;
    public final static int NATIVE = 2;
    public final static int EXPRESS = 1;

    private final List<TestShopBean> list;

    private final ConsoleCallback consoleCallback;

    public NativeViewAdapter(List<TestShopBean> list, ConsoleCallback consoleCallback) {
        this.consoleCallback = consoleCallback;
        this.list = list;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == EXPRESS) {
            View view = View.inflate(parent.getContext(), R.layout.native_express_item, null);
            return new NativeExpressAdHolder(view);
        } else if (viewType == NATIVE) {
            View view = View.inflate(parent.getContext(), R.layout.activity_feedtest_item, null);
            return new NativeAdHolder(view);
        } else {
            View view = View.inflate(parent.getContext(), R.layout.activity_feedtest_item, null);
            return new InfoHolder(view);
        }
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                           @SuppressLint("RecyclerView") int position) {
        if (holder instanceof NativeExpressAdHolder) {
            /*((NativeExpressAdHolder) holder).nativeExpressView.removeAllViews();
            View target = getItem(holder.getAdapterPosition()).nativeExpressAd.getNativeExpressView();
            if (null != target.getParent())
                ((ViewGroup) target.getParent()).removeView(target);
            ((NativeExpressAdHolder) holder).nativeExpressView.addView(target, new ViewGroup.LayoutParams(-1, -2));*/
        } else if (holder instanceof NativeAdHolder) {
            AdbidNativeAd nativeAd = getItem(position).nativeAd;
            String imageUrl = null;
            View mediaView = nativeAd.getMediaView();
            List<View> clickViews = new ArrayList<>();
            if (mediaView != null) {
                ((NativeAdHolder) holder).videoLayout.removeAllViews();
                ViewUtils.removeFromParent(mediaView);
                ((NativeAdHolder) holder).videoLayout.addView(mediaView);
                nativeAd.setMuted(false);
                nativeAd.setVideoListener(new AdbidNativeVideoListener() {

                    @Override public void onVideoStart() {
                        if (null != consoleCallback)
                            consoleCallback.printMsg("onVideoStart " + nativeAd.getTitle());
                    }

                    @Override public void onVideoPause() {
                        if (null != consoleCallback)
                            consoleCallback.printMsg("onVideoPause " + nativeAd.getTitle());
                    }

                    @Override public void onVideoResume() {
                        if (null != consoleCallback)
                            consoleCallback.printMsg("onVideoResume " + nativeAd.getTitle());
                    }

                    @Override public void onVideoComplete() {
                        if (null != consoleCallback)
                            consoleCallback.printMsg("onVideoComplete " + nativeAd.getTitle());
                    }

                    @Override public void onVideoError(AdbidError var1) {
                        if (null != consoleCallback)
                            consoleCallback.printMsg("onVideoError " + var1.getMessage());
                    }


                    @Override public void onVideoProgressUpdate(long current, long total) {
                        if (null != consoleCallback) consoleCallback.printMsg(
                                "onVideoProgressUpdate " + total + "；" + current + " ;" + nativeAd);
                    }
                });
            } else {
                if (!TextUtils.isEmpty(nativeAd.getMainImageUrl())) {
                    imageUrl = nativeAd.getMainImageUrl();
                } else if (nativeAd.getImageUrlList() != null &&
                        !nativeAd.getImageUrlList().isEmpty()) {
                    imageUrl = nativeAd.getImageUrlList().get(0);
                }
                ImageView img = ((NativeAdHolder) holder).testImg;
                img.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(img).load(imageUrl).into(img);
                clickViews.add(img);
            }
            ((NativeAdHolder) holder).titleTv.setText(nativeAd.getTitle());
            ((NativeAdHolder) holder).timeTv.setText("广告");
            clickViews.add(((NativeAdHolder) holder).titleTv);

            (((NativeAdHolder) holder).closeBtn).setVisibility(View.VISIBLE);
            nativeAd.setEventListener(new AdbidNativeEventListener() {
                @Override public void onImpression(@NonNull AdbidNativeAdView view,
                                                   @NonNull AdbidAdInfo adInfo) {
                    if (null != consoleCallback) consoleCallback.printMsg("onImpression");
                }

                @Override public void onNativeAdClick(@NonNull AdbidNativeAdView view,
                                                      @NonNull AdbidAdInfo adInfo) {
                    if (null != consoleCallback) consoleCallback.printMsg("onAdClicked");
                }

                @Override public void onAdClose(@Nullable AdbidNativeAdView view) {
                    if (null != consoleCallback) consoleCallback.printMsg("onAdClosed");
                    list.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

            //信息流自渲染
            nativeAd.registerViews(((NativeAdHolder) holder).nativeLayout, clickViews,
                    ((NativeAdHolder) holder).closeBtn);
        } else {
            ((InfoHolder) holder).titleTv.setText(getItem(position).title);
            ((InfoHolder) holder).authorTv.setText(getItem(position).shareUser);
            ((InfoHolder) holder).timeTv.setText(
                    new Date(getItem(position).publishTime).toLocaleString());
            holder.itemView.setOnClickListener(v -> {
            });
            ImageView img = ((InfoHolder) holder).testImg;
            String[] imgUlrs =
                    {"https://q4.itc.cn/images01/20240305/07a6c1f603984e69a631288952f5347c.jpeg",
                            "https://q5.itc.cn/images01/20250630/06beb11d47414e19bbf6dc1a062194fe.jpeg",
                            "https://img0.baidu.com/it/u=68977182,2518794064&fm=253&fmt=auto&app=138&f=JPEG?w=439&h=552",
                            "https://q6.itc.cn/q_70/images03/20240411/96dd36d55e1645b083273924998ff7bc.png",
                            "https://img0.baidu.com/it/u=2062646163,2646513048&fm=253&fmt=auto&app=138&f=JPEG?w=916&h=516",
                            "https://pics6.baidu.com/feed/d009b3de9c82d1588ef78a2b4896a1d4bd3e4258.jpeg@f_auto?token=750f89c05337ce46175cccbe8350bc3f"};
            Glide.with(img).load(imgUlrs[position % 6]).into(img);
        }
    }

    public TestShopBean getItem(int position) {
        return list.get(position);
    }

    @Override public int getItemCount() {
        return list.size();
    }

    @Override public int getItemViewType(int position) {
        return list.get(position).itemType;
    }

    private static class InfoHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView authorTv;
        TextView timeTv;
        ImageView testImg;
        ImageView closeBtn;

        public InfoHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            authorTv = itemView.findViewById(R.id.authorTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            testImg = itemView.findViewById(R.id.testImg);
            closeBtn = itemView.findViewById(R.id.closeBtn);
        }
    }


    private static class NativeExpressAdHolder extends RecyclerView.ViewHolder {
        LinearLayout nativeExpressView;

        public NativeExpressAdHolder(View itemView) {
            super(itemView);
            nativeExpressView = itemView.findViewById(R.id.native_express_layout);
        }
    }

    private static class NativeAdHolder extends RecyclerView.ViewHolder {

        AdbidNativeAdView nativeLayout;
        TextView titleTv;
        TextView authorTv;
        TextView timeTv;
        ImageView testImg;
        ImageView closeBtn;

        RelativeLayout videoLayout;

        public NativeAdHolder(View itemView) {
            super(itemView);
            videoLayout = itemView.findViewById(R.id.videoLayout);
            nativeLayout = itemView.findViewById(R.id.parent_layout);
            titleTv = itemView.findViewById(R.id.titleTv);
            authorTv = itemView.findViewById(R.id.authorTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            testImg = itemView.findViewById(R.id.testImg);
            closeBtn = itemView.findViewById(R.id.closeBtn);
        }
    }

    public interface ConsoleCallback {

        void printMsg(String s);
    }
}
