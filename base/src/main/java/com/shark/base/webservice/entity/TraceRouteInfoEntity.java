package com.shark.base.webservice.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TraceRouteInfoEntity implements Serializable {


	@SerializedName("ip")
	private String ip;

	@SerializedName("responseTime")
	private float responseTime;


	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public float getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(float responseTime) {
		this.responseTime = responseTime;
	}
}
