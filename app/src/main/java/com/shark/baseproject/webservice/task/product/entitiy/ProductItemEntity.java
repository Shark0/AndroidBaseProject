package com.shark.baseproject.webservice.task.product.entitiy;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Shark0 on 2016/8/12.
 */
public class ProductItemEntity implements Serializable {

    @SerializedName("title")
    private String title;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("productId")
    private String productId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
