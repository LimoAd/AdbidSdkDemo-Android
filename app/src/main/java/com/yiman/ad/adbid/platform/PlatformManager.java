package com.yiman.ad.adbid.platform;

import android.text.TextUtils;

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
            mItemList.add(new ItemModel("领摩新", true, AdBidPlatform.LMX.getLabel()));
            mItemList.add(new ItemModel("酷盈S2S", true, AdBidPlatform.KuYing.getLabel()));
            mItemList.add(new ItemModel("穿山甲", true, AdBidPlatform.CSJ.getLabel()));
            mItemList.add(new ItemModel("优量汇", true, AdBidPlatform.GDT.getLabel()));
            mItemList.add(new ItemModel("快手", true, AdBidPlatform.KS.getLabel()));
            mItemList.add(new ItemModel("汇川", true, AdBidPlatform.HuiChuan.getLabel()));
            mItemList.add(new ItemModel("TopOn", true, AdBidPlatform.TaKu.getLabel()));
            mItemList.add(new ItemModel("倍孜", true, AdBidPlatform.AMPS.getLabel()));
            mItemList.add(new ItemModel("优必客思", true, AdBidPlatform.UBX.getLabel()));
            mItemList.add(new ItemModel("Sigmob", true, AdBidPlatform.Sigmob.getLabel()));
            mItemList.add(new ItemModel("美数", true, AdBidPlatform.MS.getLabel()));
            mItemList.add(new ItemModel("Funlink", true, AdBidPlatform.FL.getLabel()));
            mItemList.add(new ItemModel("萤石", true, AdBidPlatform.Ezviz.getLabel()));
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

    public static int getSelectedCount() {
        int count = 0;
        for (ItemModel itemModel : mItemList) {
            if (itemModel.isSelected()) {
                count++;
            }
        }
        return count;
    }

    public static int getTotalCount() {
        return mItemList.size();
    }

    public static String getSummary() {
        int selectedCount = getSelectedCount();
        int totalCount = getTotalCount();
        if (selectedCount == 0) {
            return "未选择平台";
        }
        if (selectedCount == totalCount) {
            return "全部平台";
        }
        return "已选 " + selectedCount + " 个平台";
    }

    public static String getDisplayText() {
        return getDisplayText(mItemList);
    }

    public static String getDisplayText(List<ItemModel> itemList) {
        String value = getString(itemList);
        if ("全部".equals(value)) {
            return "已选: 全部平台";
        }
        if (TextUtils.isEmpty(value)) {
            return "已选: 暂无";
        }
        return "已选: " + value;
    }

    public static String getSelectedNamesText() {
        String value = getString();
        if ("全部".equals(value)) {
            return "全部平台";
        }
        if (TextUtils.isEmpty(value)) {
            return "暂无";
        }
        return value;
    }

    private static String getString(List<ItemModel> itemList) {
        List<AdBidPlatform> notSelect = new ArrayList<>();
        StringBuilder select = new StringBuilder();
        for (ItemModel itemModel : itemList) {
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
