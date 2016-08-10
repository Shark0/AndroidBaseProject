package com.shark.baseproject.webservice.task.login.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shark0 on 2016/8/10.
 */
public class LoginResultEntity {

    @SerializedName("memberId")
    private String memberId;

    @SerializedName("nickName")
    private String nickName;

    @SerializedName("profileImageUrl")
    private String profileImageUrl;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
