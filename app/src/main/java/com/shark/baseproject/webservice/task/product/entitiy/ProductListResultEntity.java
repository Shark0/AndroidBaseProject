package com.shark.baseproject.webservice.task.product.entitiy;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Shark0 on 2016/8/12.
 */
public class ProductListResultEntity implements Serializable {

    @SerializedName("productItemList")
    private List<ProductItemEntity> productItemEntityList;

    @SerializedName("nextPage")
    private String nextPage;

    public List<ProductItemEntity> getProductItemEntityList() {
        return productItemEntityList;
    }

    public void setProductItemEntityList(List<ProductItemEntity> productItemEntityList) {
        this.productItemEntityList = productItemEntityList;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }
}
