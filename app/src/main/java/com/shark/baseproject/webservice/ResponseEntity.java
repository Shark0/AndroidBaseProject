package com.shark.baseproject.webservice;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Shark0 on 2016/8/10.
 */
public class ResponseEntity implements Serializable {

    @SerializedName("resultCode")
    private int resultCode;

    @SerializedName("description")
    private String description;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
