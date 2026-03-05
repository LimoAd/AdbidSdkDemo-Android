package com.yiman.ad.adbid.platform;

import com.adbid.media.AdBidPlatform;

public class ItemModel {
    private String name;
    private boolean isSelected;
    private String adBidPlatform;

    public ItemModel(String name, boolean isSelected, String adBidPlatform) {
        this.name = name;
        this.isSelected = isSelected;
        this.adBidPlatform = adBidPlatform;
    }

    public String getAdBidPlatform() {
        return adBidPlatform;
    }

    public void setAdBidPlatform(String adBidPlatform) {
        this.adBidPlatform = adBidPlatform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
