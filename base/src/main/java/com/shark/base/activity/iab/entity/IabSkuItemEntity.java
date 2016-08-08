package com.shark.base.activity.iab.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Shark0 on 2016/4/26.
 */
public class IabSkuItemEntity implements Serializable {

    @SerializedName("productId")
    private String sku;
    @SerializedName("type")
    private String type;
    @SerializedName("price")
    private String price;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("price_currency_code")
    private String currency;
    @SerializedName("price_amount_micros")
    private double microsPrice;

    private double realPrice;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getMicrosPrice() {
        return microsPrice;
    }

    public void setMicrosPrice(double microsPrice) {
        this.microsPrice = microsPrice;
    }

    public double getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(double realPrice) {
        this.realPrice = realPrice;
    }
}
