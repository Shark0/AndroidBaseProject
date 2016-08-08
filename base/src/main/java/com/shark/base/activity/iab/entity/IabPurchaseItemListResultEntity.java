package com.shark.base.activity.iab.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Shark0 on 2016/4/26.
 */
public class IabPurchaseItemListResultEntity implements Serializable {

    private int response;

    private List<IabSkuItemEntity> inAppSkuItemList;

    private List<IabSkuItemEntity> subSkuItemList;

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public List<IabSkuItemEntity> getInAppSkuItemList() {
        return inAppSkuItemList;
    }

    public void setInAppSkuItemList(List<IabSkuItemEntity> inAppSkuItemList) {
        this.inAppSkuItemList = inAppSkuItemList;
    }

    public List<IabSkuItemEntity> getSubSkuItemList() {
        return subSkuItemList;
    }

    public void setSubSkuItemList(List<IabSkuItemEntity> subSkuItemList) {
        this.subSkuItemList = subSkuItemList;
    }
}
