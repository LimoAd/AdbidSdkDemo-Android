package com.yiman.ad.adbid.ad;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.adbid.media.AdbidError;
import com.adbid.media.ad.AdbidNativeLoader;
import com.adbid.media.nativeAd.AdbidNativeAd;
import com.adbid.media.nativeOverseas.NativeAdbidLoadListener;
import com.yiman.ad.BaseActivity;
import com.yiman.ad.adbid.AdConfig;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.bean.TestShopBean;
import com.yiman.ad.adbid.view.TitleBar;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NativeAdDrawActivity extends BaseActivity implements NativeDrawPagerAdapter.ConsoleCallback {
    private static final int AD_INTERVAL = 3;
    private static final int AD_PRELOAD_RANGE = 1;

    private final List<TestShopBean> drawList = new ArrayList<>();
    private final Set<Integer> pendingAdSlots = new HashSet<>();
    private ViewPager2 viewPager;
    private NativeDrawPagerAdapter adapter;
    private JSONArray jsonArray;
    private boolean isRequestingAd;
    private int pendingAdRequestCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_draw);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.adbid_title_native_draw);
        TextView titleView = titleBar.findViewById(R.id.tv_title);
        if (titleView != null) {
            titleView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
        titleBar.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewPager = findViewById(R.id.view_pager_draw);
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager.setOffscreenPageLimit(1);
        adapter = new NativeDrawPagerAdapter(drawList, this);
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ensureAdPreload(position);
            }
        });

        loadData();
    }

    private void loadData() {
        try {
            if (jsonArray == null) {
                InputStreamReader inputStreamReader = new InputStreamReader(
                        getAssets().open("test.json"), StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                inputStreamReader.close();
                jsonArray = new JSONArray(new JSONTokener(stringBuilder.toString()));
            }
            List<TestShopBean> feedBeanList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                TestShopBean testFeedBean = new TestShopBean();
                testFeedBean.itemType = NativeDrawPagerAdapter.NEWS;
                testFeedBean.desc = jsonArray.getJSONObject(i).getString("desc");
                testFeedBean.title = jsonArray.getJSONObject(i).getString("title");
                testFeedBean.link = jsonArray.getJSONObject(i).getString("link");
                testFeedBean.author = jsonArray.getJSONObject(i).getString("author");
                testFeedBean.shareUser = jsonArray.getJSONObject(i).optString("shareUser",
                        testFeedBean.author);
                testFeedBean.publishTime = jsonArray.getJSONObject(i).getLong("publishTime");
                testFeedBean.envelopePic = jsonArray.getJSONObject(i).getString("envelopePic");
                feedBeanList.add(testFeedBean);
            }
            rebuildDrawList(feedBeanList);
            adapter.notifyDataSetChanged();
            ensureAdPreload(0);
        } catch (Exception e) {
            Log.e("AdbidSdkDemo", "load draw data failed", e);
        }
    }

    private void rebuildDrawList(@NonNull List<TestShopBean> contentList) {
        drawList.clear();
        int contentIndex = 0;
        int displayIndex = 0;
        while (contentIndex < contentList.size()) {
            if ((displayIndex + 1) % AD_INTERVAL == 0) {
                TestShopBean adSlot = new TestShopBean();
                adSlot.itemType = NativeDrawPagerAdapter.NATIVE;
                adSlot.pos = displayIndex;
                drawList.add(adSlot);
            } else {
                TestShopBean contentItem = contentList.get(contentIndex++);
                contentItem.itemType = NativeDrawPagerAdapter.NEWS;
                contentItem.pos = displayIndex;
                drawList.add(contentItem);
            }
            displayIndex++;
        }
    }

    private void ensureAdPreload(int currentPosition) {
        int start = Math.max(0, currentPosition - AD_PRELOAD_RANGE);
        int end = Math.min(drawList.size() - 1, currentPosition + AD_PRELOAD_RANGE);
        for (int i = start; i <= end; i++) {
            TestShopBean item = drawList.get(i);
            if (item.itemType == NativeDrawPagerAdapter.NATIVE
                    && item.nativeAd == null
                    && !pendingAdSlots.contains(i)) {
                requestAd(i);
            }
        }
    }

    private void requestAd(final int slotIndex) {
        if (slotIndex < 0 || slotIndex >= drawList.size()) {
            return;
        }
        pendingAdSlots.add(slotIndex);
        pendingAdRequestCount++;
        isRequestingAd = true;
        AdbidNativeLoader nativeLoader = new AdbidNativeLoader(this,
                AdConfig.getAdConfig().getNativeUnitId(), new NativeAdbidLoadListener() {
            @Override
            public void onNativeAdLoaded(@NonNull AdbidNativeAd nativeAd) {
                pendingAdSlots.remove(slotIndex);
                pendingAdRequestCount = Math.max(0, pendingAdRequestCount - 1);
                if (slotIndex < drawList.size()
                        && drawList.get(slotIndex).itemType == NativeDrawPagerAdapter.NATIVE
                        && drawList.get(slotIndex).nativeAd == null) {
                    drawList.get(slotIndex).nativeAd = nativeAd;
                    adapter.notifyItemChanged(slotIndex);
                } else {
                    nativeAd.destroy();
                }
                isRequestingAd = pendingAdRequestCount > 0;
                ensureAdPreload(viewPager.getCurrentItem());
            }

            @Override
            public void onNativeAdLoadFail(@NonNull AdbidError adError) {
                pendingAdSlots.remove(slotIndex);
                pendingAdRequestCount = Math.max(0, pendingAdRequestCount - 1);
                isRequestingAd = pendingAdRequestCount > 0;
                printMsg("draw load fail " + adError.getMessage());
            }
        });
        String token = getIntent().getStringExtra("s2s_token");
        nativeLoader.loadAd(token);
    }

    @Override
    public void printMsg(String s) {
        Log.i("AdbidSdkDemo", s);
    }

    @Override
    public void onAdClosed() {
        if (!isRequestingAd) {
            ensureAdPreload(viewPager.getCurrentItem());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pendingAdSlots.clear();
        for (TestShopBean item : drawList) {
            if (item.nativeAd != null) {
                item.nativeAd.destroy();
                item.nativeAd = null;
            }
        }
    }
}
