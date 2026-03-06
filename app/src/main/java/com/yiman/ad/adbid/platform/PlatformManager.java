package com.yiman.ad.adbid.platform;

import android.text.TextUtils;
import android.widget.TextView;

import com.adbid.media.AdBidPlatform;
import com.adbid.sdk.AdbidSdk;
import com.adbid.sdk.AdbidSdkConfiguration;
import com.adbid.utils.GsonUtils;
import com.adbid.utils.sp.AdBidSpUtil;

import java.util.ArrayList;
import java.util.List;

public class PlatformManager {
    private static List<ItemModel> mItemList = new ArrayList<>();

    public static void init() {
        String data = AdBidSpUtil.getString("mItemList");
        if (!TextUtils.isEmpty(data)) {
            mItemList = GsonUtils.fromJson(data, GsonUtils.getListType(ItemModel.class));
        } else {
            mItemList.add(new ItemModel("领摩", true, AdBidPlatform.LM.getLabel()));
            mItemList.add(new ItemModel("优量汇", true, AdBidPlatform.GDT.getLabel()));
            mItemList.add(new ItemModel("快手", true, AdBidPlatform.KS.getLabel()));
            mItemList.add(new ItemModel("汇川", true, AdBidPlatform.HuiChuan.getLabel()));
            mItemList.add(new ItemModel("TopOn", true, AdBidPlatform.TaKu.getLabel()));
            mItemList.add(new ItemModel("倍孜", true, AdBidPlatform.AMPS.getLabel()));
            mItemList.add(new ItemModel("优必客思", true, AdBidPlatform.UBX.getLabel()));
            mItemList.add(new ItemModel("Sigmob", true, AdBidPlatform.Sigmob.getLabel()));
            mItemList.add(new ItemModel("美数", true, AdBidPlatform.MS.getLabel()));
        }
        setConfig();
    }

    private static void setConfig() {
        List<AdBidPlatform> notSelect = new ArrayList<>();
        for (ItemModel itemModel : mItemList) {
            if (!itemModel.isSelected()) {
                notSelect.add(AdBidPlatform.getByLabel(itemModel.getAdBidPlatform()));
            }
        }
        AdbidSdk.getInstance(AdbidSdkConfiguration.getApplication()).getSetting()
                .setForbidShowPlatformList(notSelect);
    }

    public static List<ItemModel> getList() {
        return mItemList;
    }

    public static List<ItemModel> getDialogList() {
        return GsonUtils.fromJson(GsonUtils.toJson(mItemList),
                GsonUtils.getListType(ItemModel.class));
    }


    public static void save(List<ItemModel> listModel) {
        mItemList = listModel;
        setConfig();
        AdBidSpUtil.put("mItemList", GsonUtils.toJson(listModel));
    }


    public static String getString() {
        List<AdBidPlatform> notSelect = new ArrayList<>();
        StringBuilder select = new StringBuilder();
        for (ItemModel itemModel : mItemList) {
            if (!itemModel.isSelected()) {
                notSelect.add(AdBidPlatform.getByLabel(itemModel.getAdBidPlatform()));
            } else {
                if (!TextUtils.isEmpty(select.toString())) {
                    select.append(",");
                }
                select.append(itemModel.getName());
            }
        }
        if (notSelect.isEmpty()) {
            return "全部";
        } else {
            return select.toString();
        }
    }
}
