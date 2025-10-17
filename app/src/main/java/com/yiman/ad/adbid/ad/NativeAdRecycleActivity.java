package com.yiman.ad.adbid.ad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adbid.media.AdbidError;
import com.adbid.media.ad.AdbidNativeLoader;
import com.adbid.media.nativeAd.AdbidNativeAd;
import com.adbid.media.nativeOverseas.NativeAdbidLoadListener;
import com.yiman.ad.adbid.AdConfig;
import com.yiman.ad.adbid.BaseActivity;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.bean.TestShopBean;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NativeAdRecycleActivity extends BaseActivity implements NativeViewAdapter.ConsoleCallback {

    protected RecyclerView recyclerView;
    protected NativeViewAdapter adapter;

    protected List<TestShopBean> list = new ArrayList<>();


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_rcy);
        recyclerView = findViewById(R.id.recycle);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new NativeViewAdapter(list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isScrollToBottom(recyclerView)) {
                    loadData();
                }
            }

            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        loadData();
    }

    public boolean isScrollToBottom(RecyclerView container) {
        LinearLayoutManager manager = (LinearLayoutManager) container.getLayoutManager();
        //当前屏幕所看到的子项个数
        int visibleCount = manager.findLastVisibleItemPosition();
        //当前RecyclerView的所有子项个数
        int totalCount = manager.getItemCount();
        if (visibleCount == list.size()) return true;
        //屏幕中最后一个可见子项的position
        int lastVisiblePosition = manager.findLastVisibleItemPosition();
        //RecyclerView的滑动状态
        int state = container.getScrollState();
        return lastVisiblePosition == totalCount - 1 && state == RecyclerView.SCROLL_STATE_IDLE;
    }

    JSONArray jsonArray = null;
    private boolean isLoading;

    public void loadData() {
        try {
            if (isLoading) {
                return;
            }
            isLoading = true;
            List<TestShopBean> feedBeanList = new ArrayList<>();
            if (jsonArray == null) {
//                打开存放在assets文件夹下面的json格式的文件并且放在文件输入流里面
                InputStreamReader inputStreamReader =
                        new InputStreamReader(getAssets().open("test.json"),
                                StandardCharsets.UTF_8);
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
            //新建一个json对象，用它对数据进行操作
            for (int i = 0; i < jsonArray.length(); i++) {
                TestShopBean testFeedBean = new TestShopBean();
                testFeedBean.desc = jsonArray.getJSONObject(i).getString("desc");
                testFeedBean.title = jsonArray.getJSONObject(i).getString("title");
                testFeedBean.link = jsonArray.getJSONObject(i).getString("link");
                testFeedBean.author = jsonArray.getJSONObject(i).getString("author");
                testFeedBean.publishTime = jsonArray.getJSONObject(i).getLong("publishTime");
                testFeedBean.envelopePic = jsonArray.getJSONObject(i).getString("envelopePic");
                feedBeanList.add(testFeedBean);
            }
            list.addAll(feedBeanList);
            isLoading = false;
            requestAd();

        } catch (Exception e) {

        }
    }

    @SuppressLint("NotifyDataSetChanged") protected void requestAd() {
        AdbidNativeLoader nativeLoader =
                new AdbidNativeLoader(this, AdConfig.getAdConfig().getNativeUnitId(),
                        new NativeAdbidLoadListener() {
                            @Override
                            public void onNativeAdLoaded(@NonNull AdbidNativeAd nativeAd) {
                                final TestShopBean tempNativeBean = new TestShopBean();
                                tempNativeBean.itemType = NativeViewAdapter.NATIVE;
                                tempNativeBean.nativeAd = nativeAd;

                                if (adapter.getItemCount() > 13) {
                                    NativeAdRecycleActivity.this.list.add(
                                            adapter.getItemCount() - 2, tempNativeBean);
                                    tempNativeBean.pos = adapter.getItemCount() - 2;
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override public void onNativeAdLoadFail(@NonNull AdbidError adError) {

                            }
                        });
        nativeLoader.loadAd();
    }

    @Override public void printMsg(String s) {
        Log.i("AdbidSdkDemo", s);
    }
}
