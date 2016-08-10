package com.shark.baseproject.webservice;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Shark0 on 2016/8/10.
 */
public class ResponseDataEntity<Data> extends ResponseEntity implements Serializable{

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
