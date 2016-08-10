package com.shark.baseproject.webservice.task.login.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by Shark0 on 2016/8/10.
 */
public class LoginInputEntity implements Serializable {

    @SerializedName("account")
    private String account;

    @SerializedName("password")
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
